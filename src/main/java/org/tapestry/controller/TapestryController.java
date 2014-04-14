package org.tapestry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.MessageDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.dao.PictureDao;
import org.tapestry.objects.SurveyResult;
import org.tapestry.dao.ActivityDao;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Message;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Picture;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.objects.Volunteer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyMap;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.services.SurveyServiceIndivo;

import javax.annotation.PostConstruct;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import java.util.Collections;


/**
* Main controller class
* This class is responsible for interpreting URLs and returning the appropriate pages.
* It is the 'brain' of the application. Each function is tagged with @RequestMapping and
* one of either RequestMethod.GET or RequestMethod.POST, which determines which requests
* the function will be triggered in response to.
* The function returns a string, which is the name of a web page to render. For example,
* the login() function returns "login" when an HTTP request like "HTTP 1.1 GET /login"
* is received. The application then loads the page "login.jsp" (the extension is added
* automatically).
*/
@Controller
public class TapestryController{
	protected static Logger logger = Logger.getLogger(TapestryController.class);
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
		
	private UserDao userDao;
   	private PatientDao patientDao;
   	private AppointmentDao appointmentDao;
   	private MessageDao messageDao;
   	private PictureDao pictureDao;
   	private SurveyTemplateDao surveyTemplateDao;
   	private SurveyResultDao surveyResultDao;
   	private ActivityDao activityDao;
   	private VolunteerDao volunteerDao;
 
   	//Mail-related settings;
   	private Properties props;
   	private String mailAddress = "";
   	private Session session;
   	
   	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 */
   	@PostConstruct
   	public void readConfig(){
   		String database = "";
   		String dbUsername = "";
   		String dbPassword = "";
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
			database = config.get("url");
			dbUsername = config.get("username");
			dbPassword = config.get("password");
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
			
			logger.error("Error reading from config file");
			e.printStackTrace();
		}
		
		//Create the DAOs
		userDao = new UserDao(database, dbUsername, dbPassword);
		patientDao = new PatientDao(database, dbUsername, dbPassword);
		appointmentDao = new AppointmentDao(database, dbUsername, dbPassword);
		messageDao = new MessageDao(database, dbUsername, dbPassword);
		pictureDao = new PictureDao(database, dbUsername, dbPassword);
		surveyTemplateDao = new SurveyTemplateDao(database, dbUsername, dbPassword);
		surveyResultDao = new SurveyResultDao(database, dbUsername, dbPassword);
		activityDao = new ActivityDao(database, dbUsername, dbPassword);
		volunteerDao = new VolunteerDao(database, dbUsername, dbPassword);
		
		
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
   	
   	//Everything below this point is a RequestMapping

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam(value="usernameChanged", required=false) Boolean usernameChanged, ModelMap model){
		if (usernameChanged != null)
			model.addAttribute("usernameChanged", usernameChanged);
		return "login";
	}
	
	@RequestMapping(value="/loginsuccess", method=RequestMethod.GET)
	public String loginSuccess(SecurityContextHolderAwareRequestWrapper request){
		String username = request.getUserPrincipal().getName();
		User u = userDao.getUserByUsername(username);
		if (request.isUserInRole("ROLE_USER")){
			activityDao.logActivity(u.getName() + " logged in", u.getUserID());
		}
		return "redirect:/";
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	//Note that messageSent is Boolean, not boolean, to allow it to be null
	public String welcome(@RequestParam(value="booked", required=false) Boolean booked, @RequestParam(value="patientId", required=false) Integer patientId, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		if (request.isUserInRole("ROLE_USER")){
			String username = request.getUserPrincipal().getName();
			User u = userDao.getUserByUsername(username);
			
			//get volunteer Id from login user
			int volunteerId = volunteerDao.getVolunteerIdByUsername(u.getUsername());			
			ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(volunteerId);		
			
//			ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(u.getUserID());
			ArrayList<Activity> activityLog = activityDao.getLastNActivitiesForVolunteer(u.getUserID(), 5); //Cap recent activities at 5
			ArrayList<Message> announcements = messageDao.getAnnouncementsForUser(u.getUserID());
			ArrayList<Appointment> approvedAppointments = new ArrayList<Appointment>();
			ArrayList<Appointment> pendingAppointments = new ArrayList<Appointment>();
			ArrayList<Appointment> declinedAppointments = new ArrayList<Appointment>();
			if(patientId != null) {
				
				approvedAppointments = appointmentDao.getAllApprovedAppointmentsForPatient(patientId);
				pendingAppointments = appointmentDao.getAllPendingAppointmentsForPatient(patientId);
				declinedAppointments = appointmentDao.getAllDeclinedAppointmentsForPatient(patientId);
				Patient patient = patientDao.getPatientByID(patientId);
				model.addAttribute("patient", patient);
				
				//set patientId in the session for other screen, like narratives 
				HttpSession  session = request.getSession();		
				session.setAttribute("patientId", patientId);
				
			} else {
				approvedAppointments = appointmentDao.getAllApprovedAppointmentsForVolunteer(u.getUserID());
				pendingAppointments = appointmentDao.getAllPendingAppointmentsForVolunteer(u.getUserID());
				declinedAppointments = appointmentDao.getAllDeclinedAppointmentsForVolunteer(u.getUserID());
				/*ArrayList<Appointment> appointmentsForToday = appointmentDao.getAllAppointmentsForVolunteerForToday(u.getUserID());
				ArrayList<Appointment> allAppointments = appointmentDao.getAllAppointmentsForVolunteer(u.getUserID());
				model.addAttribute("appointments_today", appointmentsForToday);
				model.addAttribute("appointments_all", allAppointments);*/
			}
			
			model.addAttribute("approved_appointments", approvedAppointments);
			model.addAttribute("pending_appointments", pendingAppointments);
			model.addAttribute("declined_appointments", declinedAppointments);
			model.addAttribute("name", u.getName());
			model.addAttribute("patients", patientsForUser);
			int unreadMessages = messageDao.countUnreadMessagesForRecipient(u.getUserID());
			model.addAttribute("unread", unreadMessages);
			model.addAttribute("activities", activityLog);
			model.addAttribute("announcements", announcements);
			if (booked != null)
				model.addAttribute("booked", booked);
			return "volunteer/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN")){
			User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
			int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
			model.addAttribute("name", loggedInUser.getName());
			return "admin/index";
		}
		else{ //This should not happen, but catch any unforseen behavior
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/loginfailed", method=RequestMethod.GET)
	public String failed(ModelMap model){
		model.addAttribute("error", "true");
		return "login";
	}

	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(SecurityContextHolderAwareRequestWrapper request){
		String username = request.getUserPrincipal().getName();
		User currentUser = userDao.getUserByUsername(username);
		activityDao.logActivity(currentUser.getName() + " logged out", currentUser.getUserID());
		return "confirm-logout";
	}
	
	@RequestMapping(value="/client", method=RequestMethod.GET)
	public String getClients(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		//get volunteer Id from login user		
		int volunteerId= volunteerDao.getVolunteerIdByUsername(loggedInUser.getUsername());
		ArrayList<Patient> clients = patientDao.getPatientsForVolunteer(volunteerId);		
		
//		ArrayList<Patient> clients = patientDao.getPatientsForVolunteer(loggedInUser.getUserID());
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
		model.addAttribute("clients", clients);
		return "volunteer/client";
	}
	
	@RequestMapping(value="/manage_users", method=RequestMethod.GET)
	public String manageUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
		ArrayList<User> userList = userDao.getAllUsers();
		model.addAttribute("users", userList);
		model.addAttribute("active", userDao.countActiveUsers());
		model.addAttribute("total", userDao.countAllUsers());
		if(failed != null) {
			model.addAttribute("failed", true);
		}
		return "admin/manage_users";
	}
	
	
	@RequestMapping(value="/manage_users", method=RequestMethod.POST)
	public String searchOnUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model, SecurityContextHolderAwareRequestWrapper request){
	
		String name = request.getParameter("searchName");		
		List<User> userList = userDao.getUsersByPartialName(name);		
		model.addAttribute("users", userList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		 
		
		model.addAttribute("searchName", name);
		
		return "admin/manage_users";
	}
	
	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
	//	ArrayList<User> volunteers = userDao.getAllActiveUsersWithRole("ROLE_USER");
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();
		
		model.addAttribute("volunteers", volunteers);
	    ArrayList<Patient> patientList = patientDao.getAllPatients();
        model.addAttribute("patients", patientList);
		return "admin/manage_patients";
	}

	@RequestMapping(value="/add_user", method=RequestMethod.POST)
	public String addUser(SecurityContextHolderAwareRequestWrapper request){
		//Add a new user
		User u = new User();
		
		//set name with firstname + lastname
		StringBuffer sb = new StringBuffer();
		sb.append(request.getParameter("firstname").trim());
		sb.append(" ");
		sb.append(request.getParameter("lastname").trim());
		u.setName(sb.toString());
//		u.setName(request.getParameter("name").trim());
		u.setUsername(request.getParameter("username").trim());
		u.setRole(request.getParameter("role"));
		
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(request.getParameter("password"), null); 
//		String hashedPassword = enc.encodePassword("password", null); //Default		
		u.setPassword(hashedPassword);
		u.setEmail(request.getParameter("email").trim());
		u.setPhoneNumber(request.getParameter("phonenumber"));
		u.setSite(request.getParameter("site"));		
		
		boolean success = userDao.createUser(u);
		if (mailAddress != null && success){
			try{
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailAddress));
				message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(u.getEmail()));
				message.setSubject("Welcome to Tapestry");
				String msg = "";
				msg += "Thank you for volunteering with Tapestry. Your account has been successfully created.\n";
				msg += "Your username and password are as follows:\n";
				msg += "Username: " + u.getUsername() + "\n";
				msg += "Password: password\n\n";
				msg += "We recommend that you change your password as soon as possible due to security reasons";
				message.setText(msg);
				System.out.println(msg);
				System.out.println("Sending...");
				Transport.send(message);
				System.out.println("Email sent containing credentials to " + u.getEmail());
			} catch (MessagingException e) {
				System.out.println("Error: Could not send email");
				System.out.println(e.toString());
			}
		}
		
		if(!success) {
			return "redirect:/manage_users?failed=true";
		}
		//Display page again
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/remove_user/{user_id}", method=RequestMethod.GET)
	public String removeUser(@PathVariable("user_id") int id){
		userDao.removeUserWithID(id);
		return "redirect:/manage_users";
	}
	
	@RequestMapping(value="/disable_user/{user_id}", method=RequestMethod.GET)
	public String disableUser(@PathVariable("user_id") int id){
		userDao.disableUserWithID(id);
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/enable_user/{user_id}", method=RequestMethod.GET)
	public String enableUser(@PathVariable("user_id") int id){
		userDao.enableUserWithID(id);
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
		//Add a new patient
		Patient p = new Patient();
		p.setFirstName(request.getParameter("firstname").trim());
		p.setLastName(request.getParameter("lastname").trim());
		if(request.getParameter("preferredname") != "") {
			p.setPreferredName(request.getParameter("preferredname").trim());
		}
		int v = Integer.parseInt(request.getParameter("volunteer"));
		p.setVolunteer(v);
		p.setGender(request.getParameter("gender"));
		p.setNotes(request.getParameter("notes"));
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
	
	@RequestMapping(value="/visit_complete/{appointment_id}", method=RequestMethod.GET)
	public String viewVisitComplete(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model) {
		Appointment appointment = appointmentDao.getAppointmentById(id);
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(appointment.getVolunteerID());
		model.addAttribute("unread", unreadMessages);
		Patient patient = patientDao.getPatientByID(appointment.getPatientID());
		model.addAttribute("appointment", appointment);
		model.addAttribute("patient", patient);
		return "/volunteer/visit_complete";
	}
	
	@RequestMapping(value="/complete_visit/{appointment_id}", method=RequestMethod.POST)
	public String completeVisit(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request) {
		boolean contactedAdmin = request.getParameter("contacted_admin") != null;
		appointmentDao.completeAppointment(id, request.getParameter("comments"), contactedAdmin);
		return "redirect:/";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String viewProfile(@RequestParam(value="error", required=false) String errorsPresent, @RequestParam(value="success", required=false) String success, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		model.addAttribute("vol", loggedInUser);
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
		if (errorsPresent != null)
			model.addAttribute("errors", errorsPresent);
		if(success != null)
			model.addAttribute("success", true);
		ArrayList<Picture> pics = pictureDao.getPicturesForUser(loggedInUser.getUserID());
		model.addAttribute("pictures", pics);
		return "/volunteer/profile";
	}
	
	@RequestMapping(value="/inbox", method=RequestMethod.GET)
	public String viewInbox(@RequestParam(value="success", required=false) Boolean messageSent,@RequestParam(value="failure", required=false) Boolean messageFailed, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		ArrayList<Message> messages = messageDao.getAllMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("messages", messages);
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
		if (messageSent != null)
			model.addAttribute("success", messageSent);
		if (messageFailed != null)
			model.addAttribute("failure", messageFailed);
		if (request.isUserInRole("ROLE_USER")) {
			ArrayList<User> administrators = userDao.getAllUsersWithRole("ROLE_ADMIN");
			model.addAttribute("administrators", administrators);
			return "/volunteer/inbox";
		} else {
//			ArrayList<User> volunteers = userDao.getAllUsersWithRole("ROLE_USER");
			List<Volunteer> volunteers = volunteerDao.getAllVolunteers();
			model.addAttribute("volunteers", volunteers);
			return "/admin/inbox";
		}
	}
	
	@RequestMapping(value="/view_message/{msgID}", method=RequestMethod.GET)
	public String viewMessage(@PathVariable("msgID") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		Message m = messageDao.getMessageByID(id);
		if (!(m.getRecipient() == loggedInUser.getUserID()))
			return "redirect:/403";
		if (!(m.isRead()))
			messageDao.markAsRead(id);
		int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
		model.addAttribute("unread", unreadMessages);
		model.addAttribute("message", m);
		if (request.isUserInRole("ROLE_USER"))
			return "/volunteer/view_message";
		else
			return "/admin/view_message";
	}
	
	@RequestMapping(value="/dismiss/{announcement}", method=RequestMethod.GET)
	public String dismissAnnouncement(@PathVariable("announcement") int id, SecurityContextHolderAwareRequestWrapper request){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		Message m = messageDao.getMessageByID(id);
		if (!(m.getRecipient() == loggedInUser.getUserID()))
			return "redirect:/403";
		messageDao.markAsRead(id);
		return "redirect:/";
	}

	@RequestMapping(value="/send_message", method=RequestMethod.POST)
	public String sendMessage(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		Message m = new Message();
		m.setSender(loggedInUser.getName());
		m.setSenderID(loggedInUser.getUserID());
		m.setText(request.getParameter("msgBody"));
		if (request.getParameter("isAnnouncement") != null && request.getParameter("isAnnouncement").equals("true")){ //Sound to all volunteers
			ArrayList<User> volunteers = userDao.getAllUsersWithRole("ROLE_USER");
			
			for (User u: volunteers){
				m.setSubject("ANNOUNCEMENT: " + request.getParameter("msgSubject"));
				m.setRecipient(u.getUserID());
				
				User recipient = userDao.getUserByID(u.getUserID());
				
				if (mailAddress != null){
					try{
						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(mailAddress));
						message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient.getEmail()));
						message.setSubject("Tapestry: New message notification");
						String msg = "You have received an announcement. To view it, log in to Tapestry.";
						message.setText(msg);
						System.out.println(msg);
						System.out.println("Sending...");
						Transport.send(message);
						System.out.println("Email sent notifying " + recipient.getEmail());
					} catch (MessagingException e) {
						System.out.println("Error: Could not send email");
						System.out.println(e.toString());
					}
				}
				messageDao.sendMessage(m);
			}
		}
		else{ //Send to one person
			
			String[] recipients = request.getParameterValues("recipient");
			if(recipients != null) {
				for (String recipientIDAsString: recipients){ //Annoyingly, the request comes as strings
					int recipientID = Integer.parseInt(recipientIDAsString);
					m.setRecipient(recipientID);
					m.setSubject(request.getParameter("msgSubject"));
					messageDao.sendMessage(m);
					
					User recipient = userDao.getUserByID(recipientID);
					
					if (mailAddress != null){
						try{
							MimeMessage message = new MimeMessage(session);
							message.setFrom(new InternetAddress(mailAddress));
							message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient.getEmail()));
							message.setSubject("Tapestry: New message notification");
							String msg = "You have received a message. To review it, log into Tapestry and open your inbox.";
							message.setText(msg);
							System.out.println(msg);
							System.out.println("Sending...");
							Transport.send(message);
							System.out.println("Email sent notifying " + recipient.getEmail());
						} catch (MessagingException e) {
							System.out.println("Error: Could not send email");
							System.out.println(e.toString());
						}
					}
				}
			} else {
				return "redirect:/inbox?failure=true";
			}
		}

		return "redirect:/inbox?success=true";
	}
	
	@RequestMapping(value="/delete_message/{msgID}", method=RequestMethod.GET)
	public String deleteMessage(@PathVariable("msgID") int id, ModelMap model){
		messageDao.deleteMessage(id);
		return "redirect:/inbox";
	}
	
	@RequestMapping(value="/reply_to/{msgID}", method=RequestMethod.POST)
	public String replyToMessage(@PathVariable("msgID") int id, ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		Message oldMsg = messageDao.getMessageByID(id);
		Message newMsg = new Message();
		//Reverse sender and recipient
		User recipient = userDao.getUserByID(oldMsg.getSenderID());
		int newRecipient = userDao.getUserByID(oldMsg.getRecipient()).getUserID();
		newMsg.setSenderID(newRecipient);
		newMsg.setRecipient(oldMsg.getSenderID());
		newMsg.setText(request.getParameter("msgBody"));
		newMsg.setSubject("RE: " + oldMsg.getSubject());
		messageDao.sendMessage(newMsg);
		
		if (mailAddress != null){
			try{
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailAddress));
				message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient.getEmail()));
				message.setSubject("Tapestry: New message notification");
				String msg = "You have received a message. To review it, log into Tapestry and open your inbox.";
				message.setText(msg);
				System.out.println(msg);
				System.out.println("Sending...");
				Transport.send(message);
				System.out.println("Email sent notifying " + recipient.getEmail());
			} catch (MessagingException e) {
				System.out.println("Error: Could not send email");
				System.out.println(e.toString());
			}
		}
		return "redirect:/inbox";
	}

	//Error pages
	@RequestMapping(value="/403", method=RequestMethod.GET)
	public String forbiddenError(){
		return "error-forbidden";
	}
	
	@RequestMapping(value="/update_user", method=RequestMethod.POST)
	public String updateUser(SecurityContextHolderAwareRequestWrapper request){
		String currentUsername = request.getUserPrincipal().getName();
		User loggedInUser = userDao.getUserByUsername(currentUsername);
		User u = new User();
		u.setUserID(loggedInUser.getUserID());
		u.setUsername(request.getParameter("volUsername"));
		u.setName(request.getParameter("volName"));
		u.setEmail(request.getParameter("volEmail"));
		userDao.modifyUser(u);
		if (!(currentUsername.equals(u.getUsername())))
			return "redirect:/login?usernameChanged=true";
		else
			return "redirect:/profile";
	}
	
	@RequestMapping(value="/change_password/{id}", method=RequestMethod.POST)
	public String changePassword(@PathVariable("id") int userID, SecurityContextHolderAwareRequestWrapper request){
		String newPassword;
		String target;
		if (request.isUserInRole("ROLE_USER")){
			String currentPassword = request.getParameter("currentPassword");
			newPassword = request.getParameter("newPassword");
			String confirmPassword = request.getParameter("confirmPassword");
			if (!newPassword.equals(confirmPassword)){
				return "redirect:/profile?error=confirm";
			}
			if (!userDao.userHasPassword(userID, currentPassword)){
				return "redirect:/profile?error=current";
			}
			target = "redirect:/profile?success=true";
		} else {
			System.out.println("Admin is changing password");
			newPassword = request.getParameter("newPassword");
			target = "redirect:/manage_users";
		}
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(newPassword, null);
		userDao.setPasswordForUser(userID, hashedPassword);
		activityDao.logActivity("Changed password", userID);
		return target;
	}
	
	@RequestMapping(value="/remove_picture/{id}", method=RequestMethod.GET)
	public String removePicture(@PathVariable("id") int pictureID){
		pictureDao.removePicture(pictureID);
		return "redirect:/profile";
	}
	
	@RequestMapping(value="/edit_patient/{id}", method=RequestMethod.GET)
	public String editPatientForm(@PathVariable("id") int patientID, ModelMap model){
		Patient p = patientDao.getPatientByID(patientID);
		model.addAttribute("patient", p);		
		
//		ArrayList<User> volunteers = userDao.getAllActiveUsersWithRole("ROLE_USER");
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();	
		
		model.addAttribute("volunteers", volunteers);
		return "/admin/edit_patient"; //Why this one requires a slash when none of the others do, I do not know.
	}
	
	@RequestMapping(value="/submit_edit_patient/{id}", method=RequestMethod.POST)
	public String modifyPatient(@PathVariable("id") int patientID, SecurityContextHolderAwareRequestWrapper request){
		Patient p = new Patient();
		p.setPatientID(patientID);
		p.setFirstName(request.getParameter("firstname"));
		p.setLastName(request.getParameter("lastname"));
		p.setPreferredName(request.getParameter("preferredname"));
		int v = Integer.parseInt(request.getParameter("volunteer"));
		p.setVolunteer(v);
		p.setGender(request.getParameter("gender"));
		p.setNotes(request.getParameter("notes"));
		patientDao.updatePatient(p);
		return "redirect:/manage_patients";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.GET)
	public String viewUserLogs(@PathVariable("page") int page, SecurityContextHolderAwareRequestWrapper request, ModelMap m){
		ArrayList<Activity> activityLog = activityDao.getPage((page - 1) * 20, 20);
		int count = activityDao.countEntries();
		m.addAttribute("numPages", count / 20);
		m.addAttribute("activities", activityLog);
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.POST)
	public String viewFilteredUserLogs(SecurityContextHolderAwareRequestWrapper request, ModelMap m){
		List<Activity> activityLog = new ArrayList<Activity>();
		String name = request.getParameter("name");
		if(name != null) {
			ArrayList<User> u = userDao.getUsersByPartialName(name);
			ArrayList<Integer> userIds = new ArrayList<Integer>();
			for(User user : u) {
				userIds.add(user.getUserID());
			}
			activityLog = activityDao.getAllActivitiesForVolunteers(userIds);
		} else {
			activityLog = activityDao.getAllActivities();
		}
		m.addAttribute("activities", activityLog);
		return "/admin/user_logs";
	}
}
