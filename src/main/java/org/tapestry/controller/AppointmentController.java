package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tapestry.controller.utils.MisUtils;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.MessageDao;
import org.tapestry.dao.NarrativeDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.Message;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.yaml.snakeyaml.Yaml;

@Controller
public class AppointmentController{
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
	private NarrativeDao narrativeDao;
	
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
		narrativeDao = new NarrativeDao(DB, UN, PW);
		
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
   	
   	@RequestMapping(value="/manage_appointments", method=RequestMethod.GET)
   	public String manageAppointments(@RequestParam(value="success", required=false) Boolean appointmentBooked,
   			@RequestParam(value="noMachedTime", required=false) String noMatchedMsg,
   			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		ArrayList<Appointment> allAppointments = appointmentDao.getAllAppointments();  	   		
   		ArrayList<Patient> allPatients = patientDao.getAllPatients();
 //  		ArrayList<Activity> allAppointmentActivities = activityDao.getAllActivitiesWithAppointments();
   		List<Appointment> allPastAppointments = appointmentDao.getAllPastAppointments();
   		List<Appointment> allPendingAppointments = appointmentDao.getAllPendingAppointments();
   
   		model.addAttribute("appointments", allAppointments);
   		model.addAttribute("pastAppointments", allPastAppointments);   		
   		model.addAttribute("pendingAppointments", allPendingAppointments);   		
   		model.addAttribute("patients", allPatients);
//   		model.addAttribute("activities", allAppointmentActivities); 
   		
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
   		sendMessageToInbox(subject, message, id, centralAdminId);
   		
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
   		
   		List<Narrative> v1Narratives = narrativeDao.getAllNarrativesByVolunteer(volunteer1Id, patientId, appointmentId);
   		if (v1Narratives.size() > 0)
   			model.addAttribute("narratives1", v1Narratives);
   		
   		List<Narrative> v2Narratives = narrativeDao.getAllNarrativesByVolunteer(volunteer2Id, patientId, appointmentId); 
   		if (v2Narratives.size() > 0)
   			model.addAttribute("narratives2", v2Narratives);   	
   		   		
   		List<Activity> activities = activityDao.getDetailedLog(patientId, appointmentId);   		   		   		   		
   		model.addAttribute("activities", activities);
   		   		
		if (request.isUserInRole("ROLE_ADMIN"))
			model.addAttribute("isCentralAdmin", true);   	
		
   		return "/admin/display_appointment";
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
				if (mailAddress != null){					
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
					
					if (request.isUserInRole("ROLE_ADMIN"))//login as admin/volunteer coordinator
					{				
						sendMessageToInbox(msg, userId, volunteer1UserId);//send message to volunteer1 
						sendMessageToInbox(msg, userId, volunteer2UserId);//send message to volunteer2
						sendMessageToInbox(msg, userId, userId);//send message to admin self	
					}
					else //login as volunteer
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
						
						sendMessageToInbox(msg, volunteer1UserId, volunteer1UserId);//send message to volunteer his/her self
					}						
				}
				else {
					System.out.println("Email address not set");
					logger.error("Email address not set");
				}
				if (request.isUserInRole("ROLE_ADMIN"))
					return "redirect:/manage_appointments?success=true";
				else
					return "redirect:/";				
			}
			else
			{
				if (request.isUserInRole("ROLE_ADMIN"))
					return "redirect:/manage_appointments?success=false";
				else
					return "redirect:/";	
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
				return "redirect:/";	
			
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
				return "redirect:/";	
			
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
		
		String pAvailability = p.getAvailability();
		List<String> pList = new ArrayList<String>(Arrays.asList(pAvailability.split(",")));		
		
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
						if ( (!a1.contains("non")) && (a1.equals(a2)) && (pList.contains(a1)))
						{//find matched available time for both volunteers	and selected patient
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
		return "/admin/view_scheduler";
	}
	
	private boolean isFirstVisit(int patientId){
		boolean isFirst = true;
		List<Appointment> appointments = appointmentDao.getAllApprovedAppointmentsForPatient(patientId);	
		
		if ((appointments != null)&& (appointments.size()>0))
			isFirst = false;
		return isFirst;
		
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
		appointment.setPartner(String.valueOf(partnerId));
		
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
			if (mailAddress != null){		
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
			}
			else {
				System.out.println("Email address not set");
				logger.error("Email address not set");
			}
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
	@RequestMapping(value="/open_alerts_keyObservations_plan/{appointmentId}", method=RequestMethod.GET)
	public String goAlertsAndKeyObservationsAndPlan(@PathVariable("appointmentId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){			
		HttpSession session = request.getSession();
		session.setAttribute("appointmentId", id);
		
		Appointment appt = appointmentDao.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		
		return "/volunteer/alerts_keyObservations_plan";
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
		
		HttpSession session = request.getSession();
		session.setAttribute("appointmentId", id);
		
		Appointment appt = appointmentDao.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		
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
		
		HttpSession session = request.getSession();
		Integer appointmentId = (Integer)session.getAttribute("appointmentId");
		int iAppointmentId = appointmentId.intValue();
		
		String alerts = request.getParameter("alerts");
		String keyObservations = request.getParameter("keyObservations");
		
		appointmentDao.addAlertsAndKeyObservations(iAppointmentId, alerts, keyObservations);
		
		return "/volunteer/alerts_keyObservations_plan";
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
		
		HttpSession session = request.getSession();
		session.setAttribute("appointmentId", id);
		
		Appointment appt = appointmentDao.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		model.addAttribute("plans", planDef);
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
		
		HttpSession session = request.getSession();
		Integer appointmentId = (Integer)session.getAttribute("appointmentId");
		int iAppointmentId = appointmentId.intValue();
		
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
				
		String plans = sb.toString();
		
		appointmentDao.addPlans(iAppointmentId, plans);
		
		Appointment appt = appointmentDao.getAppointmentById(iAppointmentId);
		
		int patientId = appt.getPatientID();
		Patient patient = patientDao.getPatientByID(patientId);
		
		model.addAttribute("appointment", appt);
		model.addAttribute("patient", patient);
		
		return "/volunteer/alerts_keyObservations_plan";
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
		
		String termsInfo = MisUtils.getMyOscarAuthenticationInfo();
		
		model.addAttribute("patient", patient);
		model.addAttribute("vFirstName", firstName);
		model.addAttribute("vLastName", lastName);
		model.addAttribute("termsInfo", termsInfo);			
		
		return "/volunteer/client_myoscar_authentication";
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
		
		System.out.println("sending message ...from "+ sender + "  To: " + recipient );
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
	
	//send message into Inbox and email account too
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
	
}
