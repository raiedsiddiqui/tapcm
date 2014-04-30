package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyMap;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.PictureDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.dao.MessageDao;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Message;
import org.tapestry.objects.Patient;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Availability;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.objects.Picture;
import org.yaml.snakeyaml.Yaml;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.objects.SurveyResult;
import org.tapestry.dao.ActivityDao;
import org.tapestry.objects.SurveyTemplate;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyMap;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.services.SurveyServiceIndivo;

import java.util.Properties;
import java.util.Collections;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.servlet.http.HttpSession;

@Controller
public class PatientController {
protected static Logger logger = Logger.getLogger(AppointmentController.class);
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private UserDao userDao;
	private PatientDao patientDao;
	private AppointmentDao appointmentDao;
	private ActivityDao activityDao;
	private VolunteerDao volunteerDao;
	private MessageDao messageDao;
	private SurveyTemplateDao surveyTemplateDao;
   	private SurveyResultDao surveyResultDao;
   	private PictureDao pictureDao;
	
   	//Mail-related settings;
   	private Properties props;
   	private String mailAddress = "";
   	private Session session;
	
	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 */
   	@PostConstruct
   	public void readDatabaseConfig(){
   		String DB = "";
   		String UN = "";
   		String PW = "";
   		String mailHost = "";
   		String mailUser = "";
   		String mailPassword = "";
   		String mailPort = "";
   		String useTLS = "";
   		String useAuth = "";
		try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			DB = config.get("url");
			UN = config.get("username");
			PW = config.get("password");
			mailHost = config.get("mailHost");
			mailUser = config.get("mailUser");
			mailPassword = config.get("mailPassword");
			mailAddress = config.get("mailFrom");
			mailPort = config.get("mailPort");
			useTLS = config.get("mailUsesTLS");
			useAuth = config.get("mailRequiresAuth");
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());
		}
		userDao = new UserDao(DB, UN, PW);
		patientDao = new PatientDao(DB, UN, PW);
		appointmentDao = new AppointmentDao(DB, UN, PW);
		activityDao = new ActivityDao(DB, UN, PW);
		volunteerDao = new VolunteerDao(DB, UN, PW);
		messageDao = new MessageDao(DB, UN, PW);
		surveyTemplateDao = new SurveyTemplateDao(DB, UN, PW);
		surveyResultDao = new SurveyResultDao(DB, UN, PW);
		pictureDao = new PictureDao(DB, UN, PW);
		
		//Mail-related settings
		final String username = mailUser;
		final String password = mailPassword;
		props = System.getProperties();
		session = Session.getDefaultInstance(props, 
				 new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
		  		});
		props.setProperty("mail.smtp.host", mailHost);
		props.setProperty("mail.smtp.socketFactory.port", mailPort);
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.auth", useAuth);
		props.setProperty("mail.smtp.starttls.enable", useTLS);
		props.setProperty("mail.user", mailUser);
		props.setProperty("mail.password", mailPassword);
   	}
   	
   	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
	
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();		
		model.addAttribute("volunteers", volunteers);
		
	    ArrayList<Patient> patientList = patientDao.getAllPatients();
        model.addAttribute("patients", patientList);
		return "admin/manage_patients";
	}
   	
   	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception{
		//Add a new patient
		Patient p = new Patient();
		
		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		Volunteer v1 = volunteerDao.getVolunteerById(vId1);
		Volunteer v2 = volunteerDao.getVolunteerById(vId2);
		
		if (Utils.isMatchVolunteer(v1, v2))
		{
			p.setFirstName(request.getParameter("firstname").trim());
			p.setLastName(request.getParameter("lastname").trim());
			if(request.getParameter("preferredname") != "") {
				p.setPreferredName(request.getParameter("preferredname").trim());
			}		
			p.setVolunteer(vId1);
			p.setPartner(vId2);		
			p.setMyoscarVerified(request.getParameter("myoscar_verified"));		
			p.setGender(request.getParameter("gender"));
			p.setNotes(request.getParameter("notes"));
			p.setAlerts(request.getParameter("alerts"));
			p.setClinic(request.getParameter("clinic"));
			
			String availableTime = Utils.getAvailableTime(request);		
			p.setAvailability(availableTime);
			
			patientDao.createPatient(p);
			
			Patient newPatient = patientDao.getNewestPatient();
			
			//Auto assign all existing surveys
			ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
	   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
	   		SurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
	   		
	   		for(SurveyTemplate st: surveyTemplates) {
			List<PHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID()));
			
			SurveyFactory surveyFactory = new SurveyFactory();
			PHRSurvey template = surveyFactory.getSurveyTemplate(st);
				SurveyResult sr = new SurveyResult();
	            sr.setSurveyID(st.getSurveyID());
	            sr.setPatientID(newPatient.getPatientID());
	            
	            //set today as startDate
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	        	String startDate = sdf.format(new Date());            
	            
	            sr.setStartDate(startDate);
	          //if requested survey that's already done
	    		if (specificSurveys.size() < template.getMaxInstances())
	    		{
	    			PHRSurvey blankSurvey = template;
	    			blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
	    			sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
	    			String documentId = surveyResultDao.assignSurvey(sr);
	    			blankSurvey.setDocumentId(documentId);
	    			surveys.addSurvey(blankSurvey);
	    			specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
	    		}
	    		else
	    		{
	    			return "redirect:/manage_patients";
	    		}
			}
	   		return "redirect:/manage_patients";
		}
		else
		{System.out.println("no matched volunteer pair");
			
			model.addAttribute("misMatchedVolunteer",true);
			List<Volunteer> volunteers = volunteerDao.getAllVolunteers();		
			model.addAttribute("volunteers", volunteers);
			
		    ArrayList<Patient> patientList = patientDao.getAllPatients();
	        model.addAttribute("patients", patientList);
	        
			return "admin/manage_patients";
		}
	}
   	
   	@RequestMapping(value="/edit_patient/{id}", method=RequestMethod.GET)
	public String editPatientForm(@PathVariable("id") int patientID, ModelMap model){   		
		Patient p = patientDao.getPatientByID(patientID);		
		model.addAttribute("patient", p);		
		
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();			
		model.addAttribute("volunteers", volunteers);
		
		String strAvailibilities = p.getAvailability();	
		boolean mondayNull = false;
		boolean tuesdayNull = false;
		boolean wednesdayNull = false;
		boolean thursdayNull = false;
		boolean fridayNull = false;
		
		if (!Utils.isNullOrEmpty(strAvailibilities))
		{
			if (strAvailibilities.contains("1non"))
				mondayNull = true;
			if (strAvailibilities.contains("2non"))
				tuesdayNull = true;
			if (strAvailibilities.contains("3non"))
				wednesdayNull = true;
			if (strAvailibilities.contains("4non"))
				thursdayNull = true;
			if (strAvailibilities.contains("5non"))
				fridayNull = true;
			
			String[] arrayAvailibilities = strAvailibilities.split(",");
			
			Utils.getPosition("1","monDropPosition",arrayAvailibilities,mondayNull, model);
			Utils.getPosition("2","tueDropPosition",arrayAvailibilities,tuesdayNull, model);
			Utils.getPosition("3","wedDropPosition",arrayAvailibilities,wednesdayNull, model);
			Utils.getPosition("4","thuDropPosition",arrayAvailibilities,thursdayNull, model);
			Utils.getPosition("5","friDropPosition",arrayAvailibilities,fridayNull, model);
			
			model.addAttribute("mondayNull", mondayNull);
			model.addAttribute("tuesdayNull", tuesdayNull);
			model.addAttribute("wednesdayNull", wednesdayNull);
			model.addAttribute("thursdayNull", thursdayNull);
			model.addAttribute("fridayNull", fridayNull);
		}
		
		return "/admin/edit_patient"; //Why this one requires a slash when none of the others do, I do not know.
	}
   	@RequestMapping(value="/submit_edit_patient/{id}", method=RequestMethod.POST)
	public String modifyPatient(@PathVariable("id") int patientID, SecurityContextHolderAwareRequestWrapper request){
		Patient p = new Patient();
		p.setPatientID(patientID);
		p.setFirstName(request.getParameter("firstname"));
		p.setLastName(request.getParameter("lastname"));
		p.setPreferredName(request.getParameter("preferredname"));		
		p.setVolunteer(Integer.valueOf(request.getParameter("volunteer1")));
		p.setPartner(Integer.valueOf(request.getParameter("volunteer2")));
		p.setGender(request.getParameter("gender"));
		p.setNotes(request.getParameter("notes"));
		p.setClinic(request.getParameter("clinic"));
		p.setAlerts(request.getParameter("alerts"));
		p.setMyoscarVerified(request.getParameter("myoscar_verified"));
		
		String strAvailableTime = Utils.getAvailableTime(request);
		p.setAvailability(strAvailableTime);
		
		patientDao.updatePatient(p);
		return "redirect:/manage_patients";
	}
   	
   	
   	@RequestMapping(value="/remove_patient/{patient_id}", method=RequestMethod.GET)
	public String removePatient(@PathVariable("patient_id") int id){
		patientDao.removePatientWithId(id);
		return "redirect:/manage_patients";
	}

	@RequestMapping(value="/patient/{patient_id}", method=RequestMethod.GET)
	public String viewPatient(@PathVariable("patient_id") int id, @RequestParam(value="complete", required=false)
					String completedSurvey, @RequestParam(value="aborted", required=false) String inProgressSurvey, 
					@RequestParam(value="appointmentId", required=false) Integer appointmentId, 
					SecurityContextHolderAwareRequestWrapper request, ModelMap model){
				
		Patient patient = patientDao.getPatientByID(id);
		//Find the name of the current user
		User u = userDao.getUserByUsername(request.getUserPrincipal().getName());
		String loggedInUser = u.getName();
		//Make sure that the user is actually responsible for the patient in question
		int volunteerForPatient = patient.getVolunteer();
		if (!(u.getUserID() == patient.getVolunteer())){
			model.addAttribute("loggedIn", loggedInUser);
			model.addAttribute("patientOwner", volunteerForPatient);
			return "redirect:/403";
		}
		model.addAttribute("patient", patient);
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(u.getUserID());
		model.addAttribute("unread", unreadMessages);
		ArrayList<SurveyResult> completedSurveyResultList = surveyResultDao.getCompletedSurveysByPatientID(id);
		ArrayList<SurveyResult> incompleteSurveyResultList = surveyResultDao.getIncompleteSurveysByPatientID(id);
		Collections.sort(completedSurveyResultList);
		Collections.sort(incompleteSurveyResultList);
		ArrayList<SurveyTemplate> surveyList = surveyTemplateDao.getAllSurveyTemplates();
		
//		ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(u.getUserID());
		//use volunteerId to replace userId		
		int volunteerId= volunteerDao.getVolunteerIdByUsername(u.getUsername());
		ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(volunteerId);		
		
	
		Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
		model.addAttribute("appointment", appointment);
		model.addAttribute("patients", patientsForUser);
		model.addAttribute("completedSurveys", completedSurveyResultList);
		model.addAttribute("inProgressSurveys", incompleteSurveyResultList);
		model.addAttribute("surveys", surveyList);
		ArrayList<Picture> pics = pictureDao.getPicturesForPatient(id);
		model.addAttribute("pictures", pics);
		if(patient.getPreferredName() != null && patient.getPreferredName() != ""){
			activityDao.logActivity(u.getName() + " viewing patient: " + patient.getPreferredName(), u.getUserID(), patient.getPatientID());
		} else {
			activityDao.logActivity(u.getName() + " viewing patient: " + patient.getDisplayName(), u.getUserID(), patient.getPatientID());
		}
				
		//save selected appointmentId in the session for other screen, like narrative
		HttpSession  session = request.getSession();		
		session.setAttribute("appointmentId", appointmentId);
		session.setAttribute("patientId", id);
				
		return "/patient";
	}
	
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.GET)
	public String viewPatientsFromAdmin(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<Patient> patients = getAllPatients();
		
		model.addAttribute("patients", patients);
		return "redirect:/manage_patients";
	}
	
	private List<Patient> getAllPatients(){
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientDao.getAllPatients();
		
		return patients;
	}

}
