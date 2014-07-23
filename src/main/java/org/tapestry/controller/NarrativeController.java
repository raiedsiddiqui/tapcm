package org.tapestry.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.NarrativeDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.UserDao;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Patient;

@Controller
public class NarrativeController {
	protected static Logger logger = Logger.getLogger(NarrativeController.class);
	
	private NarrativeDao narrativeDao = null;
	private UserDao userDao = null;	
	private AppointmentDao appointmentDao = null;
	private PatientDao patientDao = null;
	
	@PostConstruct
	public void readDatabaseConfig(){
		Utils.setDatabaseConfig();
		
		Properties props = System.getProperties();
		String DB = props.getProperty("db");
		String UN = props.getProperty("un");
		String PW = props.getProperty("pwd");
				
		userDao = new UserDao(DB, UN, PW);
		narrativeDao = new NarrativeDao(DB, UN, PW);	
		appointmentDao = new AppointmentDao(DB, UN, PW);
		patientDao = new PatientDao(DB, UN, PW);
		
	}
	
	@RequestMapping(value="/view_narratives", method=RequestMethod.GET)
	public String getNarrativesByUser(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		int loggedInUserId = 0;
		List<Narrative> narratives = new ArrayList<Narrative>();
		HttpSession  session = request.getSession();					
		loggedInUserId = Utils.getLoggedInUserId(request);	
		
		narratives = narrativeDao.getAllNarrativesByUser(loggedInUserId);		

		//check if there is message should be displayed
		if (session.getAttribute("narrativeMessage") != null)
		{
			String message = session.getAttribute("narrativeMessage").toString();
					
			if ("C".equals(message)){
				model.addAttribute("narrativeCreated", true);
				session.removeAttribute("narrativeMessage");
			}
			else if ("D".equals(message)){
				model.addAttribute("narrativeDeleted", true);
				session.removeAttribute("narrativeMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("narrativeUpdate", true);
				session.removeAttribute("narrativeMessage");
			}			
		}
		
		model.addAttribute("narratives", narratives);	
		return "/volunteer/view_narrative";
	}
	
	//loading a existing narrative to view detail or make a change
	@RequestMapping(value="/modify_narrative/{narrativeId}", method=RequestMethod.GET)
	public String modifyNarrative(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("narrativeId") int id, ModelMap model){		
		Narrative narrative = new Narrative();
		narrative = narrativeDao.getNarrativeById(id);		
		
		//set Date format for editDate
		String editDate = narrative.getEditDate();
		
		if(!Utils.isNullOrEmpty(editDate))
			editDate = editDate.substring(0,10);
		
		narrative.setEditDate(editDate);
		
		model.addAttribute("narrative", narrative);	
		
		return "/volunteer/modify_narrative";
	}
	
	@RequestMapping(value="/new_narrative", method=RequestMethod.GET)
	public String newNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession session = request.getSession();
		int appointmentId = getAppointmentId(session);
		
		Appointment appt = appointmentDao.getAppointmentById(appointmentId);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		return "/volunteer/new_narrative";
	}
	
	//Modify a narrative and save the change in the DB
	@RequestMapping(value="/update_narrative", method=RequestMethod.POST)
	public String updateNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		String narrativeId = null;
		int iNarrativeId;	
		Narrative narrative = new Narrative();		
		HttpSession  session = request.getSession();			
					
		if (request.getParameter("narrativeId") != null){
			narrativeId = request.getParameter("narrativeId").toString();	
			iNarrativeId = Integer.parseInt(narrativeId);
			
			narrative = narrativeDao.getNarrativeById(iNarrativeId);
						
			String title = null;
			if (request.getParameter("mNarrativeTitle") != null){
				title = request.getParameter("mNarrativeTitle").toString();
				narrative.setTitle(title);
			}
			
			String editDate = null;
			//convert current date to the format matched in DB
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			editDate = sdf.format(new Date()); 
			narrative.setEditDate(editDate);
			
			String contents = null;
			if (request.getParameter("mNarrativeContent") != null){
				contents = request.getParameter("mNarrativeContent").toString();
				narrative.setContents(contents);			
			}
			else{
				logger.error("Narrative ID can not be null");
			}
			
			narrativeDao.updateNarrative(narrative);			
			session.setAttribute("narrativeMessage","U");
		}		
		
		return "redirect:/view_narratives";

	}
	
	//create a new narrative and save it in DB
	@RequestMapping(value="/add_narrative", method=RequestMethod.POST)
	public String addNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
	
		int loggedInUserId = 0;
		int patientId = 0;
		int appointmentId = 0;
		
		HttpSession  session = request.getSession();	
		session.setAttribute("userDao", userDao);		
		
		loggedInUserId = Utils.getLoggedInUserId(request);		
		
		patientId = getPatientId(session);
		appointmentId = getAppointmentId(session);
		
		if ((patientId != 0) && (appointmentId != 0)){
			String title = request.getParameter("narrativeTitle");
			String content = request.getParameter("narrativeContent");	
			
			Narrative narrative = new Narrative();
			//convert current date to the format matched in DB
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sdf.format(new Date()); 
			
			String editDate = date.toString();		
			
			narrative.setUserId(loggedInUserId);
			narrative.setContents(content);
			narrative.setTitle(title);
			narrative.setEditDate(editDate);
			narrative.setPatientId(patientId);
			narrative.setAppointmentId(appointmentId);
			
			//add new narrative in narrative table in DB
			narrativeDao.addNarrative(narrative);
			//set complete narrative in Appointment table in DB
			appointmentDao.completeNarrative(appointmentId);
			
//			session.setAttribute("narrativeMessage","C");			
		}	
		else
		{			
			System.out.println("Please select a patient or appointment first ");
			logger.error("Please select a patient or appointment first===");
		}		

		return "redirect:/";
	}
	
	@RequestMapping(value="/delete_narrative/{narrativeId}", method=RequestMethod.GET)
	public String deleteNarrativeById(@PathVariable("narrativeId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		narrativeDao.deleteNarrativeById(id);
				
		HttpSession  session = request.getSession();		
		session.setAttribute("narrativeMessage","D");
				
		return "redirect:/view_narratives";
	}	
	
	/*
	 * get selected patient id which is stored in the session
	 * when a patient is selected from clients list in the main page	
	 */
	private int getPatientId(HttpSession session){
		int patientId = 0;		
		
		if (session.getAttribute("patientId") != null){			
			patientId = Integer.parseInt(session.getAttribute("patientId").toString());
		}
		
		return patientId;
	}
	
	/*
	 * get selected appointment id which is stored in the session
	 *  when an appointment is selected in the main page
	 */
	private int getAppointmentId(HttpSession session){
		int appointmentId = 0;		
		
		if (session.getAttribute("appointmentId") != null){			
			appointmentId = Integer.parseInt(session.getAttribute("appointmentId").toString());
		}
		
		return appointmentId;
	}
}
