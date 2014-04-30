package org.tapestry.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.objects.Activity;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;

import org.tapestry.controller.Utils;

@Controller
public class ActivityController {
protected static Logger logger = Logger.getLogger(ActivityController.class);
	
	private ActivityDao activityDao = null;
	private UserDao userDao = null;
	private VolunteerDao volunteerDao = null;
	
	@PostConstruct
	public void readDatabaseConfig(){
		Utils.setDatabaseConfig();
		
		Properties props = System.getProperties();
		String DB = props.getProperty("db");
		String UN = props.getProperty("un");
		String PW = props.getProperty("pwd");
				
		userDao = new UserDao(DB, UN, PW);
		activityDao = new ActivityDao(DB, UN, PW);
		volunteerDao = new VolunteerDao(DB, UN, PW);
	}
		
	//display all activity logs input by volunteers
	@RequestMapping(value="/view_activitylogs_admin", method=RequestMethod.GET)
	public String viewActivityLogByAdmin( SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		
		List<Activity> activities = new ArrayList<Activity>();
		activities = activityDao.getAllActivitiesForAdmin();
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();
		
		model.addAttribute("activityLogs", activities);	
		model.addAttribute("volunteers", volunteers);	
		
		return "/admin/view_activityLogs";
	}
	
	//display all activity logs for selected volunteer
	@RequestMapping(value="/view_activitylogs_admin", method=RequestMethod.POST)
	public String viewActivityLogBySelectedVolunteerByAdmin( SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		
		String strVId = request.getParameter("search_volunteer");		
		List<Activity> activities = new ArrayList<Activity>();
		
		if (!Utils.isNullOrEmpty(strVId))
			activities = activityDao.getAllActivitiesForVolunteer(Integer.parseInt(strVId));		
		
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();
		
		model.addAttribute("activityLogs", activities);	
		model.addAttribute("volunteers", volunteers);	
		model.addAttribute("selectedVolunteer", strVId);
		
		return "/admin/view_activityLogs";
	}
	
	@RequestMapping(value="/view_activityLogs", method=RequestMethod.GET)
	public String viewActivityByVolunteer( SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		HttpSession  session = request.getSession();		
		int volunteerId = getVolunteerIdFromLoginUser(request);	
		List<Activity> activities = new ArrayList<Activity>();
		activities = activityDao.getAllActivitiesForVolunteer(volunteerId, true);
		
		//check if there is message should be displayed
		if (session.getAttribute("ActivityMessage") != null)
		{
			String message = session.getAttribute("ActivityMessage").toString();
			
			if ("C".equals(message)){
				model.addAttribute("activityCreated", true);
				session.removeAttribute("ActivityMessage");
			}
			else if ("D".equals(message)){
				model.addAttribute("activityDeleted", true);
				session.removeAttribute("ActivityMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("activityUpdate", true);
				session.removeAttribute("ActivityMessage");
			}			
		}
		model.addAttribute("activityLogs", activities);	
		
		return "/volunteer/view_activityLogs";
	}
	
	@RequestMapping(value="/new_activityLogs", method=RequestMethod.GET)
	public String newActivityByVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		return "/volunteer/new_activityLogs";
	}
	
	@RequestMapping(value="/add_activityLogs", method=RequestMethod.POST)
	public String addActivityByVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		HttpSession  session = request.getSession();		
		int volunteerId = getVolunteerIdFromLoginUser(request);	
		
		String date = request.getParameter("activityDate");
		String startTime = request.getParameter("activityStartTime");	
		String endTime = request.getParameter("activityEndTime");	
		String description = request.getParameter("activityDesc");
		
		Activity activity = new Activity();
		activity.setDescription(description);
		activity.setVolunteer(String.valueOf(volunteerId));				
		activity.setDate(date);		
		
		//format start_Time and end_Time to match data type in DB
		StringBuffer sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(startTime);
		startTime = sb.toString();
		activity.setStartTime(startTime);
		
		sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(endTime);
		endTime = sb.toString();
		
		activity.setEndTime(endTime);
		
		activityDao.logActivity(activity);
		
		//update view activity page with new record		
		session.setAttribute("ActivityMessage", "C");
		return "redirect:/view_activityLogs";
	}	
	
	@RequestMapping(value="/delete_activityLog/{activityId}", method=RequestMethod.GET)
	public String deleteActivityById(@PathVariable("activityId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		activityDao.deleteActivityById(id);	
				
		HttpSession  session = request.getSession();		
		session.setAttribute("ActivityMessage", "D");		
	
		return "redirect:/view_activityLogs";		
	}
	
	@RequestMapping(value="/modify_activityLog/{activityId}", method=RequestMethod.GET)
	public String modifyActivityLog(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("activityId") int id, ModelMap model){
		Activity activity = new Activity();
		activity = activityDao.getActivityLogById(id);
		
		model.addAttribute("activityLog", activity);
		
		return "/volunteer/modify_activityLog";
	}
	
	@RequestMapping(value="/update_activityLog", method=RequestMethod.POST)
	public String updateActivityById(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		String activityId = null;
		int iActivityId = 0;		
		Activity activity = new Activity();
		
		HttpSession  session = request.getSession();		
		int volunteerId = getVolunteerIdFromLoginUser(request);	
		
		if (!Utils.isNullOrEmpty(request.getParameter("activityId")))
		{
			activityId = request.getParameter("activityId");
			iActivityId = Integer.parseInt(activityId);
			
			activity = activityDao.getActivityLogById(iActivityId);
			activity.setVolunteer(String.valueOf(volunteerId));
			
			String date = null;
			if(!Utils.isNullOrEmpty(request.getParameter("activityDate")))
				date = request.getParameter("activityDate");
				activity.setDate(date + " 00:00:00");
			
			String startTime = null;
			if (!Utils.isNullOrEmpty(request.getParameter("activityStartTime")))
				startTime = request.getParameter("activityStartTime");
				
			if (startTime.length() < 6)//format 
				activity.setStartTime(date +" " + startTime + ":00"); 
			else
				activity.setStartTime(date +" " + startTime); 
			
			String endTime = null;
			if (!Utils.isNullOrEmpty(request.getParameter("activityEndTime")))
				endTime = request.getParameter("activityEndTime");
			
			if (endTime.length() < 6)//format
				activity.setEndTime(date + " " + endTime + ":00");
			else
				activity.setEndTime(date + " " + endTime);
			
			String desc = null;
			desc = request.getParameter("activityDesc");
			if (!Utils.isNullOrEmpty(desc))
				activity.setDescription(desc);						
		}
		
		activityDao.updateActivity(activity);
		
		session.setAttribute("ActivityMessage","U");
		return "redirect:/view_activityLogs";	
	}
	
	private int getVolunteerIdFromLoginUser(SecurityContextHolderAwareRequestWrapper request){
		String name = null;		
		int volunteerId = 0;
		String username = null;
		User loggedInUser = null;
	
		if (request.getUserPrincipal() != null){			
			name = request.getUserPrincipal().getName();	
					
			if (name != null){							
				loggedInUser = userDao.getUserByUsername(name);
				username = loggedInUser.getUsername();
				
				volunteerId = volunteerDao.getVolunteerIdByUsername(username);
			}
		}
		
		return volunteerId;
	}

}
