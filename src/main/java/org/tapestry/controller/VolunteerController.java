package org.tapestry.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tapestry.dao.NarrativeDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.controller.Utils;

import java.util.Properties;

@Controller
public class VolunteerController {
	
protected static Logger logger = Logger.getLogger(VolunteerController.class);
	
	private VolunteerDao volunteerDao = null;
	private UserDao userDao = null;	
	
	@PostConstruct
	public void readDatabaseConfig(){
		Utils.setDatabaseConfig();
		
		Properties props = System.getProperties();
		String DB = props.getProperty("db");
		String UN = props.getProperty("un");
		String PW = props.getProperty("pwd");
				
		userDao = new UserDao(DB, UN, PW);
		volunteerDao = new VolunteerDao(DB, UN, PW);		
		
	}
	//display all volunteers
	@RequestMapping(value="/view_volunteers", method=RequestMethod.GET)
	public String getAllVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		HttpSession  session = request.getSession();	
		
		volunteers = volunteerDao.getAllVolunteers();		
		model.addAttribute("volunteers", volunteers);	
		
		if (session.getAttribute("volunteerMessage") != null)
		{
			String message = session.getAttribute("volunteerMessage").toString();
			
			if ("C".equals(message)){
				model.addAttribute("volunteerCreated", true);
				session.removeAttribute("volunteerMessage");
			}
			else if ("D".equals(message)){
				model.addAttribute("volunteerDeleted", true);
				session.removeAttribute("volunteerMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("volunteerUpdate", true);
				session.removeAttribute("volunteerMessage");
			}			
		}
		
		return "/admin/view_volunteers";
	}
	
	//display all volunteers with search criteria
	@RequestMapping(value="/view_volunteers", method=RequestMethod.POST)
	public String viewFilteredVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		
		String name = request.getParameter("searchName");
		if(!Utils.isNullOrEmpty(name)) {
			volunteers = volunteerDao.getVolunteersByName(name);			
		} 
		
		model.addAttribute("searchName", name);
		model.addAttribute("volunteers", volunteers);
		return "/admin/view_volunteers";
	}
	
	@RequestMapping(value="/new_volunteer", method=RequestMethod.GET)
	public String newVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){

		return "/admin/add_volunteer";
	}
		
	//create a new volunteer and save in DB
	@RequestMapping(value="/add_volunteer", method=RequestMethod.POST)
	public String addVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		Volunteer volunteer = new Volunteer();
		
		volunteer.setFirstName(request.getParameter("firstname").trim());
		volunteer.setLastName(request.getParameter("lastname").trim());
		volunteer.setGender(request.getParameter("gender"));		
		volunteer.setEmail(request.getParameter("email"));		
		volunteer.setExperienceLevel(request.getParameter("level"));		
		volunteer.setAgeType(request.getParameter("type"));
		volunteer.setProvince(request.getParameter("province"));		
		
		String preferredName = request.getParameter("preferredname");
		
		if(!Utils.isNullOrEmpty(preferredName)) 
			volunteer.setPreferredName(preferredName.trim());	
		
		String city = request.getParameter("city");
		if(!Utils.isNullOrEmpty(city))
			volunteer.setCity(city.trim());
					 
		String phone = request.getParameter("phoneNum");
		if(!Utils.isNullOrEmpty(phone))
			volunteer.setPhoneNumber(phone.trim());
		
		volunteerDao.addVolunteer(volunteer);				
		
		HttpSession session = request.getSession();
		session.setAttribute("volunteerMessage", "C");
		
		return "redirect:/view_volunteers";

	}
	
	@RequestMapping(value="/modify_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String modifyVolunteer(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("volunteerId") int id, ModelMap model){
		Volunteer volunteer = new Volunteer();
		volunteer = volunteerDao.getVolunteerById(id);
		
		model.addAttribute("volunteer", volunteer);
		
		return "/admin/modify_volunteer";
	}
	
	//update 
	@RequestMapping(value="/update_volunteer", method=RequestMethod.POST)
	public String updateVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		
		HttpSession  session = request.getSession();
		Volunteer volunteer = new Volunteer();
		
		if (!Utils.isNullOrEmpty(request.getParameter("volunteerId")))
		{
			String volunteerId = request.getParameter("volunteerId").toString();
			int iVolunteerId = Integer.parseInt(volunteerId);
			volunteer.setVolunteerId(iVolunteerId);
			
			if (!Utils.isNullOrEmpty(request.getParameter("firstname")))
				volunteer.setFirstName(request.getParameter("firstname"));
		
			if (!Utils.isNullOrEmpty(request.getParameter("lastname")))
				volunteer.setLastName(request.getParameter("lastname"));				
			
			if (!Utils.isNullOrEmpty(request.getParameter("preferredname")))
				volunteer.setPreferredName(request.getParameter("preferredname"));
			
			if (!Utils.isNullOrEmpty(request.getParameter("gender")))
				volunteer.setGender(request.getParameter("gender"));
		
			if (!Utils.isNullOrEmpty(request.getParameter("email")))
				volunteer.setEmail(request.getParameter("email"));
			
			
			if (!Utils.isNullOrEmpty(request.getParameter("level")))					
				volunteer.setExperienceLevel((request.getParameter("level")));			
			
			if (!Utils.isNullOrEmpty(request.getParameter("type")))
				volunteer.setAgeType((request.getParameter("type")));
		
			if (!Utils.isNullOrEmpty(request.getParameter("city")))
				volunteer.setCity(request.getParameter("city"));
			
			if (!Utils.isNullOrEmpty(request.getParameter("province")))
				volunteer.setProvince(request.getParameter("province"));
			
			if (!Utils.isNullOrEmpty(request.getParameter("phoneNum")))
				volunteer.setPhoneNumber(request.getParameter("phoneNum"));
			
			volunteerDao.updateVolunteer(volunteer);
		}
		else 
			logger.error("Volunteer ID is null");
		
		session.setAttribute("volunteerMessage","U");
		return "redirect:/view_volunteers";
	
	}
	
	@RequestMapping(value="/delete_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String deleteActivityById(@PathVariable("volunteerId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		volunteerDao.deleteVolunteerById(id);
				
		HttpSession  session = request.getSession();		
		session.setAttribute("volunteerMessage", "D");		
	
		return "redirect:/view_volunteers";
		
	}
	

}
