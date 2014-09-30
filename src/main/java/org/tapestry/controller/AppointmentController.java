package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tapestry.controller.utils.MisUtils;
import org.tapestry.dao.ActivityDAO;
import org.tapestry.dao.ActivityDAOImpl;
import org.tapestry.dao.AppointmentDAO;
import org.tapestry.dao.AppointmentDAOImpl;
import org.tapestry.dao.MessageDAO;
import org.tapestry.dao.MessageDAOImpl;
import org.tapestry.dao.NarrativeDAO;
import org.tapestry.dao.NarrativeDAOImpl;
import org.tapestry.dao.PatientDAO;
import org.tapestry.dao.PatientDAOImpl;
import org.tapestry.dao.SurveyResultDAOImpl;
import org.tapestry.dao.SurveyTemplateDAOImpl;
import org.tapestry.dao.UserDAO;
import org.tapestry.dao.SurveyResultDAO;
import org.tapestry.dao.SurveyTemplateDAO;
import org.tapestry.dao.UserDAOImpl;
import org.tapestry.dao.VolunteerDAO;
import org.tapestry.dao.VolunteerDAOImpl;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.Message;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.report.AlertManager;
import org.tapestry.report.AlertsInReport;
import org.tapestry.report.CalculationManager;
import org.tapestry.surveys.ResultParser;
import org.yaml.snakeyaml.Yaml;

@Controller
public class AppointmentController{
	protected static Logger logger = Logger.getLogger(AppointmentController.class);
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private UserDAO userDao = getUserDAO();
	private PatientDAO patientDao = getPatientDAO();
	private AppointmentDAO appointmentDao = getAppointmentDAO();
	private ActivityDAO activityDao = getActivityDAO();
	private VolunteerDAO volunteerDao = getVolunteerDAO();
	private MessageDAO messageDao = getMessageDAO();
	private NarrativeDAO narrativeDao = getNarrativeDAO();
	private SurveyResultDAO surveyResultDao = getSurveyResultDAO();
	private SurveyTemplateDAO surveyTemplateDao = getSurveyTemplateDAO();
	
    public DataSource getDataSource() {
    	try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			String url = config.get("url");
			String username = config.get("username");
			String password = config.get("password");
			
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
	        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	        dataSource.setUrl(url);
	        dataSource.setUsername(username);
	        dataSource.setPassword(password);
	         
	        return dataSource;		
	        
		} catch (IOException e) {
			logger.error("Error reading from config file");
			e.printStackTrace();
			
			return null;
		}
    }
   
    public ActivityDAO getActivityDAO(){
    	return new ActivityDAOImpl(getDataSource());
    }
    
    public UserDAO getUserDAO(){
    	return new UserDAOImpl(getDataSource());
    }
    
    public MessageDAO getMessageDAO(){
    	return new MessageDAOImpl(getDataSource());
    }
    
    public NarrativeDAO getNarrativeDAO(){
    	return new NarrativeDAOImpl(getDataSource());
    }
    
    public SurveyTemplateDAO getSurveyTemplateDAO(){
    	return new SurveyTemplateDAOImpl(getDataSource());
    }
    
    public SurveyResultDAO getSurveyResultDAO(){
    	return new SurveyResultDAOImpl(getDataSource());
    }
    public AppointmentDAO getAppointmentDAO(){    	
    	return new AppointmentDAOImpl(getDataSource());
    	//    	return new AppointmentDAOImpl();
    }
   	
    public VolunteerDAO getVolunteerDAO(){
    	return new VolunteerDAOImpl(getDataSource());
    }    
    
    public PatientDAO getPatientDAO(){
    	return new PatientDAOImpl(getDataSource());
    }
    
	@RequestMapping(value="/", method=RequestMethod.GET)
	//Note that messageSent is Boolean, not boolean, to allow it to be null
	public String welcome(@RequestParam(value="booked", required=false) Boolean booked, 
			@RequestParam(value="noMachedTime", required=false) String noMachedTime,
			@RequestParam(value="patientId", required=false) Integer patientId, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		int unreadMessages;			
		List<Appointment> remindingAppointments = new ArrayList<Appointment>();
		HttpSession session = request.getSession();
			
		if (request.isUserInRole("ROLE_USER"))
		{
			User loggedInUser = Utils.getLoggedInUser(request);
			String username = loggedInUser.getUsername();	
			
			int userId = loggedInUser.getUserID();
			//get volunteer Id from login user
			int volunteerId = volunteerDao.getVolunteerIdByUsername(username);		
		
			session.setAttribute("logged_in_volunteer", volunteerId);
			List<Patient> patientsForUser = patientDao.getPatientsForVolunteer(volunteerId);
			List<Message> announcements = messageDao.getAnnouncementsForUser(userId);
			
			List<Appointment> approvedAppointments = new ArrayList<Appointment>();
			List<Appointment> pendingAppointments = new ArrayList<Appointment>();
			List<Appointment> declinedAppointments = new ArrayList<Appointment>();
			if(patientId != null) {				
				approvedAppointments = appointmentDao.getAllApprovedAppointmentsForPatient(patientId, volunteerId);
				pendingAppointments = appointmentDao.getAllPendingAppointmentsForPatient(patientId, volunteerId);
				declinedAppointments = appointmentDao.getAllDeclinedAppointmentsForPatient(patientId, volunteerId);
				
				Patient patient = patientDao.getPatientByID(patientId);
				model.addAttribute("patient", patient);
				
				//set patientId in the session for other screen, like narratives 
				session.setAttribute("patientId", patientId);				
			} 
			else 
			{
				approvedAppointments = appointmentDao.getAllApprovedAppointmentsForVolunteer(volunteerId);				
				pendingAppointments = appointmentDao.getAllPendingAppointmentsForVolunteer(volunteerId);
				declinedAppointments = appointmentDao.getAllDeclinedAppointmentsForVolunteer(volunteerId);						
			}
			
			model.addAttribute("approved_appointments", approvedAppointments);
			model.addAttribute("pending_appointments", pendingAppointments);
			model.addAttribute("declined_appointments", declinedAppointments);
			model.addAttribute("name", loggedInUser.getName());
			model.addAttribute("patients", patientsForUser);
			
			unreadMessages = messageDao.countUnreadMessagesForRecipient(userId);
			model.addAttribute("unread", unreadMessages);	
			//save unreadMessages in sesion
			session.setAttribute("unread_messages", unreadMessages);
			
			model.addAttribute("announcements", announcements);
			if (booked != null)
				model.addAttribute("booked", booked);
			
			if (noMachedTime != null)
				model.addAttribute("noMachedTime", noMachedTime);
			
			remindingAppointments = appointmentDao.getRemindingAppointmentList(volunteerId, -2);			
			model.addAttribute("reminding_appointments", remindingAppointments);

				
			return "volunteer/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{			
			User loggedInUser = Utils.getLoggedInUser(request);			
			
			unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
			//save unreadMessages in sesion
			session.setAttribute("unread_messages", unreadMessages);
			
			model.addAttribute("name", loggedInUser.getName());
			
			remindingAppointments = appointmentDao.getRemindingAppointmentList(0, -2);			
			model.addAttribute("appointments", remindingAppointments);

			return "admin/index";
		}
		else{ //This should not happen, but catch any unforseen behavior and logout			
			return "redirect:/login";
		}
	}
   	
   	@RequestMapping(value="/manage_appointments", method=RequestMethod.GET)
   	public String manageAppointments(@RequestParam(value="success", required=false) Boolean appointmentBooked,
   			@RequestParam(value="noMachedTime", required=false) String noMatchedMsg,
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		
   		List<Appointment> allAppointments = appointmentDao.getAllAppointments();  	   		
   		List<Patient> allPatients = patientDao.getAllPatients();  		
   		List<Appointment> allPastAppointments = appointmentDao.getAllPastAppointments();
   		List<Appointment> allPendingAppointments = appointmentDao.getAllPendingAppointments();
   
   		model.addAttribute("appointments", allAppointments);
   		model.addAttribute("pastAppointments", allPastAppointments);   		
   		model.addAttribute("pendingAppointments", allPendingAppointments);   		
   		model.addAttribute("patients", allPatients);
   		
   		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
   		if(appointmentBooked != null)
   			model.addAttribute("success", appointmentBooked);
   		
   		if (noMatchedMsg != null)
   			model.addAttribute("noMachedTime", noMatchedMsg); 
   		
   		return "admin/manage_appointments";
   	}
     
   	@RequestMapping(value="/authenticate_myoscar/{volunteerId}", method=RequestMethod.POST)
   	public String authenticateMyOscar(@PathVariable("volunteerId") int id, @RequestParam(value="patientId", required=true) int patientId,
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		int centralAdminId = 1;
 
   		String clientFirstName = request.getParameter("client_first_name");
   		String clientLastName = request.getParameter("client_last_name");
   		String volunteerFirstName = request.getParameter("volunteer_first_name");
   		String volunteerLastName = request.getParameter("volunteer_last_name");
   		int volunteerUserId = volunteerDao.getUserIdByVolunteerId(id);
   		
   		StringBuffer sb = new StringBuffer();
   		String subject = "MyOscar Authentication";   		
   		
   		sb.append("Please authenticate ");
   		sb.append(clientFirstName);
   		sb.append(" ");
   		sb.append(clientLastName);
   		sb.append(" to level 3 -- PHR.");
   		sb.append("/n");
   		sb.append("Send by Volunteer: ");
   		sb.append(volunteerFirstName);
   		sb.append(" ");
   		sb.append(volunteerLastName);
   		String message = sb.toString();
   		
   		//send message to Central Admin
   		sendMessageToInbox(subject, message, volunteerUserId, centralAdminId);
   		
   		HttpSession session = request.getSession();
   		if (session.getAttribute("appointmentId") != null)
   		{
   			String appointmentId = session.getAttribute("appointmentId").toString();
   			
   			return "redirect:/patient/" + patientId + "?appointmentId=" + appointmentId;
   		}
   		else
   			return "redirect:/?patientId=" + patientId;   		
   	}
   	
   	@RequestMapping(value="/display_appointment/{appointmentID}", method=RequestMethod.GET)
   	public String displayAppointment(@PathVariable("appointmentID") int id, 
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model){   		
   		Appointment appointment = appointmentDao.getAppointmentById(id);
   		model.addAttribute("appointment", appointment);
   		
   		int patientId = appointment.getPatientID();
   		Patient p = patientDao.getPatientByID(patientId);
   		String alerts = p.getAlerts();
   		
   		if (!Utils.isNullOrEmpty(alerts))
   			model.addAttribute("alerts", alerts);
   		
   		int volunteer1Id = appointment.getVolunteerID();
   		int volunteer2Id = appointment.getPartnerId();
   		int appointmentId = appointment.getAppointmentID();
   		
   		List<Narrative> v1Narratives = narrativeDao.getNarrativesByVolunteer(volunteer1Id, patientId, appointmentId);
   		if (v1Narratives.size() > 0)
   			model.addAttribute("narratives1", v1Narratives);
   		
   		List<Narrative> v2Narratives = narrativeDao.getNarrativesByVolunteer(volunteer2Id, patientId, appointmentId); 
   		if (v2Narratives.size() > 0)
   			model.addAttribute("narratives2", v2Narratives);   	
   		   		
   		List<Activity> activities = activityDao.getDetailedLog(patientId, appointmentId);   		   		   		   		
   		model.addAttribute("activities", activities);
   		   		
		if (request.isUserInRole("ROLE_ADMIN"))
			model.addAttribute("isCentralAdmin", true);   	
		
   		return "/admin/display_appointment";
   	}
   	
   	@RequestMapping(value="/book_appointment", method=RequestMethod.GET)
	public String goAddAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		List<Patient> patients = new ArrayList<Patient>();
   		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
   		if (request.isUserInRole("ROLE_USER"))
   		{
   			int loggedInVolunteer = MisUtils.getLoggedInVolunteerId(request);
   			patients = patientDao.getPatientsForVolunteer(loggedInVolunteer);   			
   			
   			model.addAttribute("patients", patients);
   			return "/volunteer/volunteer_book_appointment";
   		}
   		else
   		{
   			patients = getPatients();
   	   		model.addAttribute("patients", patients);
   	   		
   			return "/admin/admin_book_appointment";	 
   		}
   	}
   	
   	@RequestMapping(value="/out_book_appointment", method=RequestMethod.GET)
	public String outAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){   		
   		if (request.isUserInRole("ROLE_USER"))
   			return "redirect:/";
   		else
   			return "redirect:/manage_appointments";	   		
   	}
   	
	@RequestMapping(value="/book_appointment", method=RequestMethod.POST)
	public String addAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		
		int patientId = Integer.parseInt(request.getParameter("patient"));	
		String noMatchedMsg="";			
		Patient p = patientDao.getPatientByID(patientId);
		Appointment a = new Appointment();
				
		//check if selected time matches both volunteer's availability
		Volunteer volunteer1 = volunteerDao.getVolunteerById(p.getVolunteer());
		Volunteer volunteer2 = volunteerDao.getVolunteerById(p.getPartner());
		String vAvailability = volunteer1.getAvailability();
		String pAvailability = volunteer2.getAvailability();
		
		String date = request.getParameter("appointmentDate");	
		//format the date from yyyy/MM/dd to yyyy-MM-dd
		if (date.contains("/"))
			date = date.replace("/", "-");
		
		int dayOfWeek = Utils.getDayOfWeekByDate(date);
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(dayOfWeek - 1));
		String time = request.getParameter("appointmentTime");
				
		Map<String, String> m = Utils.getAvailabilityMap();
		
		Iterator iterator = m.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			
			if (mapEntry.getValue().toString().contains(time))
				sb.append(mapEntry.getKey());
		}
		String availability = sb.toString();		
		
		if ((isAvailableForVolunteer(availability, vAvailability)) && (isAvailableForVolunteer(availability, pAvailability)))
		{	//both volunteers are available, go to create an appointment for patient and send message to admin and volunteers
			a.setVolunteerID(p.getVolunteer());
			a.setPartnerId(p.getPartner());
			a.setPatientID(p.getPatientID());
			a.setDate(date);
			a.setTime(time);
			
			if (isFirstVisit(patientId))
				a.setType(0);//first visit
			else
				a.setType(1);//follow up
			
			//save new appointment in DB and send message 
			if (appointmentDao.createAppointment(a))
			{			
				String logginUser = request.getUserPrincipal().getName();	
				User user = Utils.getLoggedInUser(request);
				int userId = user.getUserID();	
				int volunteer1UserId = volunteerDao.getUserIdByVolunteerId(p.getVolunteer());	
				int volunteer2UserId = volunteerDao.getUserIdByVolunteerId(p.getPartner());							
										
				//send message to both volunteers
				//content of message
				sb = new StringBuffer();
				sb.append(logginUser);
				sb.append(" has booked an appointment for ");
				sb.append(p.getFirstName());
				sb.append(" ");
				sb.append(p.getLastName());
				sb.append( " at ");
				sb.append(time);
				sb.append(" on ");
				sb.append(date);
				sb.append(".\n");
				sb.append("This appointment is awaiting confirmation.");
				
				String msg = sb.toString();
				
				if (request.isUserInRole("ROLE_ADMIN")) //send message for user login as admin/volunteer coordinator
				{				
					sendMessageToInbox(msg, userId, volunteer1UserId);//send message to volunteer1 
					sendMessageToInbox(msg, userId, volunteer2UserId);//send message to volunteer2				
				}
				else //send message for user login as volunteer
				{						
					int organizationId = volunteer1.getOrganizationId();
					List<Integer> coordinators = userDao.getVolunteerCoordinatorByOrganizationId(organizationId);
					
					if (coordinators != null)
					{	//send message to all coordinators in the organization						
						for (int i = 0; i<coordinators.size(); i++)		
							sendMessageToInbox(msg, volunteer1UserId, coordinators.get(i).intValue());			
					}
					else{
						System.out.println("Can't find any coordinator in organization id# " + organizationId);
						logger.error("Can't find any coordinator in organization id# " + organizationId);
					}
				}						
				
				//after saving appointment in DB  
				if (request.isUserInRole("ROLE_ADMIN"))
					return "redirect:/manage_appointments?success=true";
				else
					return "redirect:/?booked=true";				
			}
			else //fail to save appointment in DB
			{
				if (request.isUserInRole("ROLE_ADMIN"))
					return "redirect:/manage_appointments?success=false";
				else
					return "redirect:/?booked=false";	
			}
		}		
		else if (!(isAvailableForVolunteer(availability, vAvailability)))
		{
			sb = new StringBuffer();
			
			if (!Utils.isNullOrEmpty(volunteer1.getDisplayName()))
				sb.append(volunteer1.getDisplayName());
			else
			{
				sb.append(volunteer1.getFirstName());
				sb.append(" ");
				sb.append(volunteer1.getLastName());
			}
			
			sb.append(" is not available at that time, please check volunteer's availability.");
			noMatchedMsg = sb.toString();
			
			if (request.isUserInRole("ROLE_ADMIN"))
				return "redirect:/manage_appointments?noMachedTime=" + noMatchedMsg;
			else
				return "redirect:/?noMachedTime=" + noMatchedMsg;	
			
		}
		else
		{
			sb = new StringBuffer();
			
			if (!Utils.isNullOrEmpty(volunteer2.getDisplayName()))
				sb.append(volunteer2.getDisplayName());
			else
			{
				sb.append(volunteer2.getFirstName());
				sb.append(" ");
				sb.append(volunteer2.getLastName());
			}
			
			sb.append(" is not available at that time, please check volunteer's availability.");
			noMatchedMsg = sb.toString();
			
			if (request.isUserInRole("ROLE_ADMIN"))
				return "redirect:/manage_appointments?noMachedTime=" + noMatchedMsg;
			else
				return "redirect:/?noMachedTime=" + noMatchedMsg;				
		}	
	}	
	
	@RequestMapping(value="/delete_appointment/{appointmentID}", method=RequestMethod.GET)
	public String deleteAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request){
		appointmentDao.deleteAppointment(id);
		if(request.isUserInRole("ROLE_USER")) {
			return "redirect:/";
		} else {
			return "redirect:/manage_appointments";
		}
	}
	
	@RequestMapping(value="/approve_appointment/{appointmentID}", method=RequestMethod.GET)
	public String approveAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request){
		appointmentDao.approveAppointment(id);
		return "redirect:/manage_appointments";
	}
	
	@RequestMapping(value="/decline_appointment/{appointmentID}", method=RequestMethod.GET)
	public String unapproveAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request){
		appointmentDao.declineAppointment(id);
		return "redirect:/manage_appointments";
	}
	
	//view_appointments
	@RequestMapping(value="/view_appointments", method=RequestMethod.GET)
	public String viewAppointmentByAdmin( SecurityContextHolderAwareRequestWrapper request){
		
		return "/admin/view_appointments";
	}
	
	@RequestMapping(value="/go_scheduler", method=RequestMethod.GET)
	public String goScheduler( SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/go_scheduler";
	}
	
	//display scheduler page
	@RequestMapping(value="/view_scheduler", method=RequestMethod.GET)
	public String viewScheduler( SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<Patient> patients = getPatients();		
		List<Volunteer> allVolunteers = getAllVolunteers();
		
		List<Availability> matchList = getAllMatchedPairs(allVolunteers, allVolunteers);
		
		model.addAttribute("patients",patients);
		model.addAttribute("allvolunteers",allVolunteers);		
		model.addAttribute("matcheList", matchList);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
	
		return "/admin/view_scheduler";
	}
	
	//Open add new appointment from scheduler
	@RequestMapping(value="/add_appointment/{volunteerId}", method=RequestMethod.GET)
	public String addAppointmentFromSecheduler(@PathVariable("volunteerId") int volunteerId, 
			@RequestParam(value="vId", required=false) int partnerId, 			
			@RequestParam(value="time", required=false) String time, 			
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		
		List<Patient> patients = getPatients();		
	
		//get volunteers's name		
		String vName = volunteerDao.getVolunteerNameById(volunteerId);
		String pName = volunteerDao.getVolunteerNameById(partnerId);
			
		String date = time.substring(0,10);		
		time = time.substring(11);
				
		model.addAttribute("patients",patients);		
		model.addAttribute("selectedVolunteer",vName);
		model.addAttribute("selectedPartner",pName);
		model.addAttribute("selectedTime", time);
		model.addAttribute("selectedDate", date);
		
		//save volunteers id in the session
		HttpSession  session = request.getSession();	
		session.setAttribute("volunteerId", volunteerId);
		session.setAttribute("partnerId", partnerId);				
	
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));

		return "/admin/schedule_appointment";
	}
	
	//load match time for both selected volunteers  /view_matchTime
	@RequestMapping(value="/view_matchTime", method=RequestMethod.POST)
	public String viewMatchAvailablities( SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		List<Patient> patients = getPatients();		
		List<Volunteer> volunteers = getAllVolunteers();
		
		String patientId = request.getParameter("patient");
		String volunteerId1 = request.getParameter("volunteer1");		
		String volunteerId2 = request.getParameter("volunteer2");		

		Volunteer v1 = volunteerDao.getVolunteerById(Integer.parseInt(volunteerId1));
		Volunteer v2 = volunteerDao.getVolunteerById(Integer.parseInt(volunteerId2));
		
		Patient p = patientDao.getPatientByID(Integer.parseInt(patientId));
		
		//check if two volunteers are same persons
		if (volunteerId1.equals(volunteerId2))
			model.addAttribute("sameVolunteer",true);
		else 
		{
			String v1Level = v1.getExperienceLevel();
			String v2Level = v2.getExperienceLevel();
				
			// matching rule is Beginner can only be paired with Experienced
			if(!isMatched(v1Level, v2Level))
				model.addAttribute("misMatchedVolunteer",true);
			else
			{
				String[] aVolunteer1, aVolunteer2;
				Availability availability;
					
				aVolunteer1= v1.getAvailability().split(",");							
				aVolunteer2 = v2.getAvailability().split(",");				
				List<Availability> matchList = new ArrayList<Availability>();
								
				for( String a1: aVolunteer1 )
				{
					for(String a2 : aVolunteer2)
					{
						if ( (!a1.contains("non")) && (a1.equals(a2)) )
						{//find matched available time for both volunteers
							availability = new Availability();								
							availability.setvDisplayName(v1.getDisplayName());
							availability.setvPhone(v1.getHomePhone());
							availability.setvEmail(v1.getEmail());
							availability.setpDisplayName(v2.getDisplayName());
							availability.setpPhone(v2.getHomePhone());
							availability.setpEmail(v2.getEmail());				
							availability.setMatchedTime(formatMatchTime(a1));
							availability.setvId(Integer.parseInt(volunteerId1));
							availability.setpId(Integer.parseInt(volunteerId2));
							availability.setPatientId(Integer.parseInt(patientId));
							availability.setPatientName(p.getDisplayName());
							matchList.add(availability);
						}
					}
				}					
				if (matchList.size() == 0)
					model.addAttribute("noMatchTime",true);
				else
					model.addAttribute("matcheList",matchList);		
			}			
		}		
		model.addAttribute("allvolunteers", volunteers);
		model.addAttribute("volunteerOne",v1);
		model.addAttribute("volunteerTwo",v2);
		model.addAttribute("patients", patients);
		model.addAttribute("selectedPatient", patientId);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/manage_appointments";		
	}
	
	@RequestMapping(value="/find_volunteers", method=RequestMethod.POST)
	public String getPairedVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		//get Date and time for appointment		
		String day = request.getParameter("appointmentDate");
		//when date pick up from calendar, format is different, need to change from yyyy/mm/dd to yyyy-MM-dd
		day = day.replace("/", "-");
		
		int dayOfWeek = Utils.getDayOfWeekByDate(day);
		String time = request.getParameter("appointmentTime");				
		
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(dayOfWeek -1));
		sb.append(time);
		String date_time = sb.toString();
		
		List<Volunteer> volunteers = getAllVolunteers();	
		if (volunteers.size() == 0)
			model.addAttribute("noAvailableTime",true);	
		else
		{
			List<Volunteer> availableVolunteers = getAllMatchedVolunteers(volunteers, date_time);
			if (availableVolunteers.size() == 0)
				model.addAttribute("noAvailableVolunteers",true);	
			else
			{
				List<Availability> matchList = getAllAvailablilities(availableVolunteers, date_time, day);
				if (matchList.size() == 0)
					model.addAttribute("noFound",true);	
				else
					model.addAttribute("matcheList",matchList);	
			}
		}		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/view_scheduler";
	}
	
	@RequestMapping(value="/schedule_appointment", method=RequestMethod.POST)
	public String createAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		//set up appointment
		Appointment appointment = new Appointment();
		
		int patientId = Integer.parseInt(request.getParameter("patient"));
		
		//retrieve volunteers id from session
		HttpSession  session = request.getSession();			
		Integer vId = (Integer)session.getAttribute("volunteerId");
		int volunteerId = vId.intValue();
		
		Integer pId = (Integer)session.getAttribute("partnerId");
		int partnerId = pId.intValue();
		
		appointment.setVolunteerID(volunteerId);
		appointment.setPatientID(patientId);
//		appointment.setPartner(String.valueOf(partnerId));
		appointment.setPartnerId(partnerId);
		
		//get Date and time for appointment		
		String date = request.getParameter("appointmentDate");
		String time = request.getParameter("appointmentTime");
		//format time, remove AM/PM
		time = time.substring(0,8);
		
		appointment.setDate(date);
		appointment.setTime(time);		
		
		if (isFirstVisit(patientId))
			appointment.setType(0);//first visit
		else
			appointment.setType(1);//follow up
				
		//save new appointment in DB and send message 
		if (appointmentDao.createAppointment(appointment))
		{
			Patient patient = patientDao.getPatientByID(patientId);	
			String logginUser = request.getUserPrincipal().getName();				
			User user = Utils.getLoggedInUser(request);			
			int userId = user.getUserID();
						
			//send message to both volunteers
			StringBuffer sb = new StringBuffer();
			
			sb.append(logginUser);
			sb.append(" has booked an appointment for ");
			sb.append(patient.getFirstName());
			sb.append(" ");
			sb.append(patient.getLastName());
			sb.append( " at ");
			sb.append(time);
			sb.append(" on ");
			sb.append(date);
			sb.append(".\n");
			sb.append("This appointment is awaiting confirmation.");
			
			String msg = sb.toString();
			
			sendMessageToInbox(msg, userId, volunteerId); //send message to volunteer
			sendMessageToInbox(msg, userId, partnerId); //send message to volunteer
			sendMessageToInbox(msg, userId, userId); //send message to admin her/his self	
			model.addAttribute("successToCreateAppointment",true);
		}
		else
		{
			model.addAttribute("failedToCreateAppointment",true);
		}		
		return "redirect:/manage_appointments";		
	}
	
	/**
	 *  
	 * @param id appointment Id
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/open_alerts_keyObservations/{appointmentId}", method=RequestMethod.GET)
	public String openAlertsAndKeyObservations(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){			
		Appointment appt = appointmentDao.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		//session.setAttribute("newNarrative", true);
		HttpSession session = request.getSession();
		if (session.getAttribute("newNarrative") != null)
		{
			model.addAttribute("newNarrative", true);
			session.removeAttribute("newNarrative");
		}
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);		
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/volunteer/add_alerts_keyObservation";
	}
	
	/**
	 *  
	 * @param id appointment Id
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/saveAlertsAndKeyObservations", method=RequestMethod.POST)
	public String saveAlertsAndKeyObservations(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		int appointmentId = getAppointmentId(request);		
		String alerts = request.getParameter("alerts");
		String keyObservations = request.getParameter("keyObservations");
		
		appointmentDao.addAlertsAndKeyObservations(appointmentId, alerts, keyObservations);
		
		int patientId = getPatientId(request);		
		Appointment appointment = appointmentDao.getAppointmentById(appointmentId);		
		
		if (completedAllSurveys(patientId))
			return "redirect:/open_plan/" + appointmentId ;	
		else 
		{
			if (!Utils.isNullOrEmpty(appointment.getComments()))
			{//send alert to MRP	
				
			}
				
			return "redirect:/";	
		}
				
//		if (!Utils.isNullOrEmpty(appointment.getComments()))
//		{
//			if (completedAllSurveys(patientId))
//				return "redirect:/open_plan/" + appointmentId ;	
//			else //send alert to MRP				
//				return "redirect:/";
//		}
//		else//no alert, no complete surveys
//		{
//			if (completedAllSurveys(patientId))
//				return "redirect:/open_plan/" + appointmentId ;	
//			else
//				return "redirect:/";
//		}
	}
	
	/**
	 *  
	 * @param id appointment Id
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/open_plan/{appointmentId}", method=RequestMethod.GET)
	public String openPlan(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		List<String> planDef = Utils.getPlanDefinition();				
		Appointment appt = appointmentDao.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		model.addAttribute("plans", planDef);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/volunteer/add_plan";
	}
	
	/**
	 *  
	 * @param id appointment Id
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/savePlans", method=RequestMethod.POST)
	public String savePlans(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		
//		int patientId = getPatientId(request);
		int appointmentId = getAppointmentId(request);
		
		StringBuffer sb = new StringBuffer();		
		sb.append(request.getParameter("plan1"));
		sb.append(",");
		sb.append(request.getParameter("plan2"));
		sb.append(",");
		sb.append(request.getParameter("plan3"));
		sb.append(",");
		sb.append(request.getParameter("plan4"));
		sb.append(",");
		sb.append(request.getParameter("plan5"));
		if (!Utils.isNullOrEmpty(request.getParameter("planSpecify")))
		{
			sb.append(",");
			sb.append(request.getParameter("planSpecify"));
		}
				
		appointmentDao.addPlans(appointmentId, sb.toString());
		//generate report for patient 
	//	generateReport(patientId, appointmentId);	
		
		//Todo: save report as PDF in harddrive
		
		//Todo:send copy of report to MRP
		
		//send message to patient in MyOscar
//		Long patientIdInMyOscar = new Long(15231);
//		try{
//			Long lll = ClientManager.sentMessageToPatientInMyOscar(patientIdInMyOscar, "Message From Tapestry", "Hello");
//			System.out.println("lll is === "+ lll);
//			
//		} catch (Exception e){
//			System.out.println("something wrong with myoscar server");
//			e.printStackTrace();
//		}
//		
		
		return "redirect:/";
	}
	
	/**
	 *  
	 * @param id appointment Id
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/goMyOscarAuthenticate/{appointmentId}", method=RequestMethod.GET)
	public String openMyOscarAuthenticate(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		
		HttpSession session = request.getSession();
		session.setAttribute("appointmentId", id);
		
		Appointment appt = appointmentDao.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		String vName = patient.getVolunteerName();
		
		int index = vName.indexOf(" ");
		String firstName = vName.substring(0, index);
		String lastName = vName.substring(index);
		
//		String termsInfo = MisUtils.getMyOscarAuthenticationInfo();
		
		model.addAttribute("patient", patient);
		model.addAttribute("vFirstName", firstName);
		model.addAttribute("vLastName", lastName);
//		model.addAttribute("termsInfo", termsInfo);	
	
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/volunteer/client_myoscar_authentication";
	}
	
	@RequestMapping(value="/visit_complete/{appointment_id}", method=RequestMethod.GET)
	public String viewVisitComplete(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model) {
		
		Appointment appointment = appointmentDao.getAppointmentById(id);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		Patient patient = patientDao.getPatientByID(appointment.getPatientID());
		model.addAttribute("appointment", appointment);
		model.addAttribute("patient", patient);
		return "/volunteer/visit_complete";
	}
	
	@RequestMapping(value="/complete_visit/{appointment_id}", method=RequestMethod.POST)
	public String completeVisit(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model) {
//		boolean contactedAdmin = request.getParameter("contacted_admin") != null;
		//set visit alert as comments in DB
		appointmentDao.completeAppointment(id, request.getParameter("visitAlerts"));
		
		Appointment appt = appointmentDao.getAppointmentById(id);
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		model.addAttribute("patient", patient);
		model.addAttribute("appointment", appt);	
		
		if (appt.getType() == 0)
		{//first visit						
			System.out.println("in complete visit, for first visit and and go to alert and keyObservation");
			HttpSession  session = request.getSession();
			if (session.getAttribute("unread_messages") != null)
				model.addAttribute("unread", session.getAttribute("unread_messages"));
			
			return "/volunteer/add_alerts_keyObservation";			
		}
		else
		{//follow up visit			
			boolean completedAllSurveys = completedAllSurveys(patientId);
			if (!Utils.isNullOrEmpty(appt.getComments()))
			{//has alert
				if (completedAllSurveys)
				{System.out.println("in complete visit, for fowllowup visit, there is alert, finish all surveys and and go to Plans");
					//alert attached to report
					return "redirect:/open_plan/" + id ;	
				}
				else
				{System.out.println("in complete visit, for fowllowup visit,there is alert, not finish all surveys and and go to Home");
					//todo: send Alert to MRP
					String alerts = appt.getComments();
					System.out.println("Alert send to MRP in Oscar  === " + alerts);	
					return "redirect:/";
				}				
			}
			else
			{//does not have alert
				if (completedAllSurveys)
				{System.out.println("in complete visit, for fowllowup visit,there is no alert, finish all surveys and and go to Plan");
					return "redirect:/open_plan/" + id ;	
				}
				else
				{System.out.println("in complete visit, for fowllowup visit,there is no alert, not finish all surveys and and go to Home");
					return "redirect:/";
				}
			}
		}
	}
	
	public boolean completedAllSurveys(int patientId){
   		boolean completed = false;
   		
   		int count = surveyTemplateDao.countSurveyTemplate();
   		List<SurveyResult> completedSurveys = surveyResultDao.getCompletedSurveysByPatientID(patientId);
   		
   		if (count == completedSurveys.size())
   			completed = true;
   		
   		return completed;
   	}
	
	private boolean isFirstVisit(int patientId){
		boolean isFirst = true;
		List<Appointment> appointments = appointmentDao.getAllApprovedAppointmentsForPatient(patientId);	
		
		if ((appointments != null)&& (appointments.size()>0))
			isFirst = false;
		
		return isFirst;
		
	}
	
	private boolean isMatched(String level1, String level2){
		boolean matched = false;
		if (level1.equals("E") || level2.equals("E"))		
			matched = true;				
		else if (level1.equals("I") && level2.equals("I"))
			matched = true;
		
		return matched;
	}
	
	//this is a temporary method for sending message only into Inbox
	private void sendMessageToInbox(String msg, int sender, int recipient){	
			Message m = new Message();
			m.setRecipient(recipient);
			m.setSenderID(sender);
			m.setText(msg);
			m.setSubject("New Appointment");
			messageDao.sendMessage(m);
	}
	
	//this is a temporary method for sending message only into Inbox
	private void sendMessageToInbox(String subject, String msg, int sender, int recipient){	
			Message m = new Message();
			m.setRecipient(recipient);
			m.setSenderID(sender);
			m.setText(msg);
			m.setSubject(subject);
			messageDao.sendMessage(m);
	}

	/**	 send message into Inbox and email account too	
	private void sendMessage(String email,String msg, int sender, int recipient){
		Message m = new Message();
		m.setRecipient(recipient);
		m.setSenderID(sender);
		m.setText(msg);
		m.setSubject("New Appointment");
		messageDao.sendMessage(m);
		
		if (mailAddress != null){
			try{
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailAddress));
				message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
				message.setSubject("Tapestry: New message notification");
				
				message.setText(msg);
				System.out.println(msg);
				System.out.println("Sending...");
				Transport.send(message);
				System.out.println("Email sent notifying " + email);
				
			} catch (MessagingException e) {
				System.out.println("Error: Could not send email");
				System.out.println(e.toString());
			}
		}
	}
	*/
	
	private List<Patient> getPatients()
	{
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientDao.getAllPatients();		
		
		return patients;
	}
	
	private List<Volunteer> getAllVolunteers(){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		volunteers = volunteerDao.getVolunteersWithAvailability();		
		
		return volunteers;
	}
	
	private List<Availability> getAllAvailablilities(List<Volunteer> list, String time, String day)
	{		
		Availability availability;
		List<Availability> aList = new ArrayList<Availability>();
		
		for (Volunteer v: list)
		{
			for (Volunteer p: list)
			{
				if( (!(v.getVolunteerId()==p.getVolunteerId())) && 
						(Utils.isMatchVolunteer(v, p)) && ((!isExist(aList,v, p))))
				{
					availability = new Availability();
					availability.setvDisplayName(v.getDisplayName());
					availability.setvPhone(v.getHomePhone());
					availability.setvEmail(v.getEmail());
					availability.setpDisplayName(p.getDisplayName());
					availability.setpPhone(p.getHomePhone());
					availability.setpEmail(p.getEmail());		
					availability.setMatchedTime(formatdateTime(time, day));
					availability.setvId(v.getVolunteerId());
					availability.setpId(p.getVolunteerId());
				      	
					aList.add(availability);	
				}
			}
		}
		return aList;
	}
	
	private List<Volunteer> getAllMatchedVolunteers(List<Volunteer> list, String time){		
		List<Volunteer> vList = new ArrayList<Volunteer>();		
		String availableTime;
				
		for (Volunteer v: list)
		{	//get volunteer's available time
			availableTime = v.getAvailability();
			
			if (availableTime.contains(time))
				vList.add(v);
		}
		return vList;
	}
	
	private List<Availability> getAllMatchedPairs(List<Volunteer> list1, List<Volunteer> list2){
		String availability1, availability2;
		String[] aSet1, aSet2;
		List<Availability> aList = new ArrayList<Availability>();
		Availability availability;
		
		for (Volunteer v1: list1)
		{
			availability1 = v1.getAvailability();				
			aSet1 = availability1.split(",");	
			
			for (Volunteer v2: list2)
			{
				if (v1.getVolunteerId()!= v2.getVolunteerId())
				{		
					availability2 = v2.getAvailability();		
					
					if (Utils.isMatchVolunteer(v1, v2))						
					{					
						aSet2  = availability2.split(",");		
						
						//find match availability					
						for(int i = 0; i < aSet1.length; i++)
						{								
							for(int j = 0; j <aSet2.length; j++)//
							{  	//same time, no duplicated
								if ((!aSet1[i].toString().contains("non")) 
										&&  (aSet1[i].toString().equals(aSet2[j].toString()))
										&&	(!isExist(aList, aSet1[i].toString(), v1, v2)))								
								{	
									availability = new Availability();
									availability.setvDisplayName(v1.getDisplayName());
									availability.setvPhone(v1.getHomePhone());
									availability.setvEmail(v1.getEmail());
									availability.setpDisplayName(v2.getDisplayName());
									availability.setpPhone(v2.getHomePhone());
									availability.setpEmail(v2.getEmail());								  
									availability.setMatchedTime(formatMatchTime(aSet1[i].toString()));
									availability.setvId(v1.getVolunteerId());
									availability.setpId(v2.getVolunteerId());
								      	
									aList.add(availability);	
								}
							}
						}
					}
				}		
			}			
		}	
		
		return aList;
	}
	
	//avoid duplicated element
	private boolean isExist(List<Availability> list, String time, Volunteer v1, Volunteer v2){
		Availability a = new Availability();
		boolean exist = false;
		time = formatMatchTime(time);		
		String v1Name = v1.getDisplayName();
		String v2Name = v2.getDisplayName();
		
		for (int i =0; i<list.size(); i++){
			a = list.get(i);
			
			if ((time.equals(a.getMatchedTime())) && ((v1Name.equals(a.getpDisplayName())) && (v2Name.equals(a.getvDisplayName())) ))
				return true;
		}
		return exist;
	}
	
	//avoid duplicate element
	private boolean isExist(List<Availability> list, Volunteer v1, Volunteer v2){
		Availability a = new Availability();
		boolean exist = false;
		
		String v1Name = v1.getDisplayName();
		String v2Name = v2.getDisplayName();
		
		for (int i =0; i<list.size(); i++){
			a = list.get(i);
			
			if ((v1Name.equals(a.getpDisplayName())) && (v2Name.equals(a.getvDisplayName())))			
				return true;
		}
		return exist;
	}
	
	private String formatMatchTime(String time){
		StringBuffer sb = new StringBuffer();
		
		Map<String, String> dayMap = new HashMap<String, String>();
		dayMap.put("1", "Monday");
		dayMap.put("2", "Tuesday");
		dayMap.put("3", "Wednesday");
		dayMap.put("4", "Thursday");
		dayMap.put("5", "Friday");
		
		Map<String, String> timePeriodMap = Utils.getAvailabilityMap();
		
		sb.append(dayMap.get(time.substring(0,1)));
		sb.append("--");
		sb.append(timePeriodMap.get(time.substring(1)));
		
		return sb.toString();
	}
	
	private String formatdateTime(String time, String day){			
		Map<String, String> timePeriodMap = Utils.getAvailabilityMap();
		
		StringBuffer sb = new StringBuffer();
		sb.append(day);
		sb.append(" ");
		sb.append(timePeriodMap.get(time.substring(1)));
		
		return sb.toString();
	}
	
	private boolean isAvailableForVolunteer(String appointmentTime, String volunteerAvailability){
		boolean available = false;
		String[] availabilityArray = volunteerAvailability.split(",");
		
		for(String s: availabilityArray){
			if (appointmentTime.equals(s))
				return true;
		}
		return available;
	}
	
	//narrative 
	@RequestMapping(value="/view_narratives", method=RequestMethod.GET)
	public String getNarrativesByUser(SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{	
		List<Narrative> narratives = new ArrayList<Narrative>();
		HttpSession  session = request.getSession();					
		int loggedInVolunteerId = MisUtils.getLoggedInVolunteerId(request);
		
		narratives = narrativeDao.getAllNarrativesByUser(loggedInVolunteerId);		

		//check if there is message should be displayed
		if (session.getAttribute("narrativeMessage") != null)
		{
			String message = session.getAttribute("narrativeMessage").toString();
					
			if ("D".equals(message)){
				model.addAttribute("narrativeDeleted", true);
				session.removeAttribute("narrativeMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("narrativeUpdate", true);
				session.removeAttribute("narrativeMessage");
			}			
		}
		
		MisUtils.setUnreadMsg(session, request, model, messageDao);
		
		model.addAttribute("narratives", narratives);	
		return "/volunteer/view_narrative";
	}
	
	//loading a existing narrative to view detail or make a change
	@RequestMapping(value="/modify_narrative/{narrativeId}", method=RequestMethod.GET)
	public String modifyNarrative(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("narrativeId") int id, ModelMap model){		
		Narrative narrative = narrativeDao.getNarrativeById(id);			
		
		//set Date format for editDate
		String editDate = narrative.getEditDate();
		
		if(!Utils.isNullOrEmpty(editDate))
			editDate = editDate.substring(0,10);
		
		narrative.setEditDate(editDate);
		
		model.addAttribute("narrative", narrative);	
		
		MisUtils.setUnreadMsg(null, request, model, messageDao);
		
		return "/volunteer/modify_narrative";
	}
	
	@RequestMapping(value="/new_narrative", method=RequestMethod.GET)
	public String newNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		int appointmentId = getAppointmentId(request);
		
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
		int patientId = getPatientId(request);
		int appointmentId = getAppointmentId(request);
		int loggedInVolunteerId  = MisUtils.getLoggedInVolunteerId(request);		
		
		String title = request.getParameter("narrativeTitle");
		String content = request.getParameter("narrativeContent");	
		
		Narrative narrative = new Narrative();
		//convert current date to the format matched in DB
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sdf.format(new Date()); 
		
		String editDate = date.toString();		
		
		narrative.setVolunteerId(loggedInVolunteerId);
		narrative.setContents(content);
		narrative.setTitle(title);
		narrative.setEditDate(editDate);
		narrative.setPatientId(patientId);
		narrative.setAppointmentId(appointmentId);
		
		//add new narrative in narrative table in DB
		narrativeDao.addNarrative(narrative);
		//set complete narrative in Appointment table in DB
		appointmentDao.completeNarrative(appointmentId);		
		
		HttpSession session = request.getSession();
		session.setAttribute("newNarrative", true);

		return "redirect:/open_alerts_keyObservations/" + appointmentId;
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
	private int getPatientId(SecurityContextHolderAwareRequestWrapper request){
		int patientId = 0;	
		HttpSession session = request.getSession();
		
		
		if (session.getAttribute("patientId") != null){			
			patientId = Integer.parseInt(session.getAttribute("patientId").toString());
		}
		
		return patientId;
	}
	
	/*
	 * get selected appointment id which is stored in the session
	 *  when an appointment is selected in the main page
	 */
	private int getAppointmentId(SecurityContextHolderAwareRequestWrapper request){
		int appointmentId = 0;		
		HttpSession session = request.getSession();
		
		if (session.getAttribute("appointmentId") != null){			
			appointmentId = Integer.parseInt(session.getAttribute("appointmentId").toString());
		}
		
		return appointmentId;
	}
	
//	private void generateReport(int patientId, int appointmentId){
//		
//		Patient patient = patientDao.getPatientByID(patientId);
//		//call web service to get patient info from myoscar
//		String userName = "carolchou.test";
//		try{
//			PersonTransfer3 personInMyoscar = ClientManager.getClientByUsername(userName);
//			StringBuffer sb = new StringBuffer();
//			if (personInMyoscar.getStreetAddress1() != null)
//				sb.append(personInMyoscar.getStreetAddress1());
//			String city = personInMyoscar.getCity();
//			if (city != null)
//			{
//				sb.append(", ");
//				sb.append(city);
//				patient.setCity(city);
//			}
//			
//			if (personInMyoscar.getProvince() != null)
//			{
//				sb.append(", ");
//				sb.append(personInMyoscar.getProvince());
//			}
//			
//			patient.setAddress(sb.toString());
//			
//			if (personInMyoscar.getBirthDate() != null)
//			{
//				Calendar birthDay = personInMyoscar.getBirthDate();
//				patient.setBod(Utils.getDateByCalendar(birthDay));
//			}
//			
//		} catch (Exception e){
//			System.err.println("Have some problems when calling myoscar web service");
//			
//		}
//		
//		Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
//		Report report = new Report();		
//		
//		//Plan and Key Observations
//		String keyObservation = appointmentDao.getKeyObservationByAppointmentId(appointmentId);
//		String plan = appointmentDao.getPlanByAppointmentId(appointmentId);
//		appointment.setKeyObservation(keyObservation);
//		appointment.setPlans(plan);
//		
//		List<String> pList = new ArrayList<String>();
//		if (!Utils.isNullOrEmpty(plan))
//			pList = Arrays.asList(plan.split(","));		
//		
//		Map<String, String> pMap = new TreeMap<String, String>();
//		
//		for (int i = 1; i<= pList.size(); i++){
//			pMap.put(String.valueOf(i), pList.get(i-1));
//		}
//		
////		model.addAttribute("patient", patient);
////		model.addAttribute("appointment", appointment);
////		model.addAttribute("plans", pMap);
//				
//		//Survey---  goals setting
//		List<SurveyResult> surveyResultList = surveyResultDao.getCompletedSurveysByPatientID(patientId);
//		SurveyResult healthGoalsSurvey = new SurveyResult();
//		SurveyResult dailyLifeActivitySurvey = new SurveyResult();	
//		SurveyResult nutritionSurvey = new SurveyResult();
//		SurveyResult rAPASurvey = new SurveyResult();
//		SurveyResult mobilitySurvey = new SurveyResult();
//		SurveyResult socialLifeSurvey = new SurveyResult();
//		SurveyResult generalHealthySurvey = new SurveyResult();
//		SurveyResult memorySurvey = new SurveyResult();
//		SurveyResult carePlanSurvey = new SurveyResult();
//		
//		
//		for(SurveyResult survey: surveyResultList){
//			int surveyId = survey.getSurveyID();
//			
//			if (surveyId == 10)//Goal Setting survey
//				healthGoalsSurvey = survey;
//			
//			if (surveyId == 11)//Daily life activity survey
//				dailyLifeActivitySurvey = survey;
//			
//			if (surveyId == 7)//Nutrition
//				nutritionSurvey = survey;
//			
//			if (surveyId == 12)//RAPA survey
//				rAPASurvey = survey;
//			
//			if (surveyId == 16)//Mobility survey
//				mobilitySurvey = survey;
//			
//			if (surveyId == 8) //Social Life(Duke Index of Social Support)
//				socialLifeSurvey = survey;
//			
//			if (surveyId == 9) //General Health(Edmonton Frail Scale)
//				generalHealthySurvey = survey;
//			
//			if (surveyId == 14) //Memory Survey
//				memorySurvey = survey;
//			
//			if (surveyId == 15) //Care Plan/Advanced_Directive survey
//				carePlanSurvey = survey;
//		}
//		
//		String xml;
//		//Healthy Goals
//   		try{
//   			xml = new String(healthGoalsSurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//   		
//   		LinkedHashMap<String, String> mHealthGoalsSurvey = ResultParser.getResults(xml);
//   		List<String> qList = new ArrayList<String>();
//   		List<String> questionTextList = new ArrayList<String>();
//   		questionTextList = ResultParser.getSurveyQuestions(xml);
//   		//get answer list
//		qList = MisUtils.getQuestionList(mHealthGoalsSurvey);   
//		
//   		Map<String, String> sMap = new TreeMap<String, String>();
//   		sMap = MisUtils.getSurveyContentMap(questionTextList, qList);
//   		
//  		report.setHealthGoals(sMap);
//   		
//   		//Additional Information
//  		//Memory
//   		try{
//   			xml = new String(memorySurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//   		
//   		LinkedHashMap<String, String> mMemorySurvey = ResultParser.getResults(xml);
//   		qList = new ArrayList<String>();
//   		questionTextList = new ArrayList<String>();
//   		questionTextList = ResultParser.getSurveyQuestions(xml);
//   		
//   		//only keep the second and forth question text in the list
//   		List<String> displayQuestionTextList = new ArrayList<String>();
//   		displayQuestionTextList.add(questionTextList.get(1));
//   		displayQuestionTextList.add(questionTextList.get(3));
//   		
//   		displayQuestionTextList = MisUtils.removeRedundantFromQuestionText(displayQuestionTextList, "of 2");
//   	
//   		//get answer list
//		qList = MisUtils.getQuestionListForMemorySurvey(mMemorySurvey);   					
//   		sMap = new TreeMap<String, String>(); 	
//   		
//   		//Care Plan/Advanced_Directive
//   		try{
//   			xml = new String(carePlanSurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//   		
//   		LinkedHashMap<String, String> mCarePlanSurvey = ResultParser.getResults(xml);
//
//   		questionTextList = new ArrayList<String>();
//   		questionTextList = ResultParser.getSurveyQuestions(xml);
//   		
//   		//take 3 question text from the list
//   		for (int i = 1; i <= 3; i++)
//   			displayQuestionTextList.add(questionTextList.get(i));
//   		
//   		displayQuestionTextList = MisUtils.removeRedundantFromQuestionText(displayQuestionTextList, "of 3");
//   		
//   		//get answer list   		
//   		qList.addAll(MisUtils.getQuestionList(mCarePlanSurvey));   	
//   		
//   		sMap = MisUtils.getSurveyContentMapForMemorySurvey(displayQuestionTextList, qList);
//   		report.setAdditionalInfos(sMap);	  			
//   			
//   			
//   		//Daily Life Activities
//   		try{
//   			xml = new String(dailyLifeActivitySurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//   		
//   		LinkedHashMap<String, String> mDailyLifeActivitySurvey = ResultParser.getResults(xml);
//   		questionTextList = new ArrayList<String>();
//   		questionTextList = ResultParser.getSurveyQuestions(xml);   		
//   		
//   		qList = new ArrayList<String>();
//   		qList = MisUtils.getQuestionList(mDailyLifeActivitySurvey);
//   		
//   		//last question in Daily life activity survey is about falling stuff
//   		List<String> lAlert = new ArrayList<String>();
//   		String fallingQA = qList.get(qList.size() -1);
//   		if (fallingQA.contains("yes")||fallingQA.contains("fall"))
//   			lAlert.add(AlertsInReport.DAILY_ACTIVITY_ALERT);   		
//   		   		
//   		sMap = new TreeMap<String, String>();
//   		sMap = MisUtils.getSurveyContentMap(questionTextList, qList);
//   		
//   		report.setDailyActivities(sMap);   		
//   		
// 		//General Healthy Alert
//   		try{
//   			xml = new String(generalHealthySurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//		
//		LinkedHashMap<String, String> mGeneralHealthySurvey = ResultParser.getResults(xml);
//		qList = new ArrayList<String>();   		
//   		//get answer list
//		qList = MisUtils.getQuestionList(mGeneralHealthySurvey);
//		
//		int generalHealthyScore = CalculationManager.getScoreByQuestionsList(qList);
//		lAlert = AlertManager.getGeneralHealthyAlerts(generalHealthyScore, lAlert);
//		
//		//Social Life Alert
//		try{
//   			xml = new String(socialLifeSurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//		
//		LinkedHashMap<String, String> mSocialLifeSurvey = ResultParser.getResults(xml);
//		qList = new ArrayList<String>();   		
//   		//get answer list
//		qList = MisUtils.getQuestionList(mSocialLifeSurvey);
//		
//		int socialLifeScore = CalculationManager.getScoreByQuestionsList(qList);
//		lAlert = AlertManager.getSocialLifeAlerts(socialLifeScore, lAlert);
//   		
//   		//Nutrition Alerts   		
//   		try{
//   			xml = new String(nutritionSurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//   		
//   		LinkedHashMap<String, String> mNutritionSurvey = ResultParser.getResults(xml);
//   		qList = new ArrayList<String>();   		
//   		//get answer list
//		qList = MisUtils.getQuestionList(mNutritionSurvey);  
//
//		//get scores for nutrition survey based on answer list
//		int nutritionScore = CalculationManager.getScoreByQuestionsList(qList);
//		
//		//high nutrition risk alert
//		Map<String, String> nAlert = new TreeMap<String, String>();
//		lAlert = AlertManager.getNutritionAlerts(nutritionScore, lAlert, qList);
//		
//		//set alerts in report bean
//		if (nAlert != null && nAlert.size()>0)	
//			report.setAlerts(lAlert);
//		else
//			report.setAlerts(null);
//		
//		//RAPA Alert
//		try{
//   			xml = new String(rAPASurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//   		
//   		LinkedHashMap<String, String> mRAPASurvey = ResultParser.getResults(xml);
//   		qList = new ArrayList<String>();   		
//   		//get answer list
//		qList = MisUtils.getQuestionList(mRAPASurvey);  		
//
//		int rAPAScore = CalculationManager.getScoreForRAPA(qList);
//		if (rAPAScore < 6)
//			lAlert.add(AlertsInReport.PHYSICAL_ACTIVITY_ALERT);
//		
//		//Mobility Alerts
//		try{
//   			xml = new String(mobilitySurvey.getResults(), "UTF-8");
//   		} catch (Exception e) {
//   			xml = "";
//   		}
//		
//		LinkedHashMap<String, String> mMobilitySurvey = ResultParser.getResults(xml);
//   		Map<String, String> qMap = MisUtils.getQuestionMap(mMobilitySurvey);  
//   		   		
//   		lAlert = AlertManager.getMobilityAlerts(qMap, lAlert);   
//		
//		//send message to MyOscar test
////		try{
////			Long lll = ClientManager.sentMessageToPatientInMyOscar(new Long(15231), "Message From Tapestry", "Hello");
////			System.out.println("lll is === "+ lll);
////			
////		} catch (Exception e){
////			System.out.println("something wrong with myoscar server");
////			e.printStackTrace();
////		}
//		
//		
//		report.setAlerts(lAlert);
//		//end of alert
//		
//		//get volunteer information
//		String volunteer = appointment.getVolunteer();
//		String partner = appointment.getPartner();
//		String comments = appointment.getComments();
//				
//		Map<String, String> vMap = new TreeMap<String, String>();
//		
//		if (!Utils.isNullOrEmpty(volunteer))
//			vMap.put(" 1", volunteer);
//		else
//			vMap.put(" 1", "");
//		
//		if (!Utils.isNullOrEmpty(partner))
//			vMap.put(" 2", partner);
//		else
//			vMap.put(" 2", "");
//		
//		if (!Utils.isNullOrEmpty(comments))
//			vMap.put(" C", comments);					
//		else
//			vMap.put(" C", " ");
//		
//		report.setVolunteerInformations(vMap);
//	}
	
}
