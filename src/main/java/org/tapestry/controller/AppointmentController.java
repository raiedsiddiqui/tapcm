package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

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
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.dao.NarrativeDao;
import org.tapestry.dao.MessageDao;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Message;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Availability;
import org.tapestry.objects.Narrative;
import org.yaml.snakeyaml.Yaml;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
   	public String manageAppointments(@RequestParam(value="success", required=false) Boolean appointmentBooked, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		ArrayList<Appointment> allAppointments = appointmentDao.getAllAppointments();  	   		
   		ArrayList<Patient> allPatients = patientDao.getAllPatients();
   		ArrayList<Activity> allAppointmentActivities = activityDao.getAllActivitiesWithAppointments();
   		List<Appointment> allPastAppointments = appointmentDao.getAllPastAppointments();
   		List<Appointment> allPendingAppointments = appointmentDao.getAllPendingAppointments();
   		
   		model.addAttribute("appointments", allAppointments);
   		model.addAttribute("pastAppointments", allPastAppointments);   		
   		model.addAttribute("pendingAppointments", allPendingAppointments);   		
   		model.addAttribute("patients", allPatients);
   		model.addAttribute("activities", allAppointmentActivities);
   		if(appointmentBooked != null)
   			model.addAttribute("success", appointmentBooked);
   		return "admin/manage_appointments";
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
		Patient p = patientDao.getPatientByID(patientId);			
		
		Appointment a = new Appointment();
		a.setVolunteerID(p.getVolunteer());
		a.setPartnerId(p.getPartner());
		a.setPatientID(p.getPatientID());
		a.setDate(request.getParameter("appointmentDate"));
		a.setTime(request.getParameter("appointmentTime"));
		appointmentDao.createAppointment(a);
		
		if(request.isUserInRole("ROLE_USER")) {
			//Email all the administrators with a notification
			System.out.println("hi...");
			
			if (mailAddress != null){
				try{
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(mailAddress));
					for (User admin : userDao.getAllUsersWithRole("ROLE_ADMIN")){
						message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(admin.getEmail()));
					}
					message.setSubject("Tapestry: New appointment booked");
					String msg = p.getVolunteerName() + " has booked an appointment with " + p.getFirstName() + " " + p.getLastName();
					msg += " for " + a.getTime() + " on " + a.getDate() + ".\n";
					msg += "This appointment is awaiting confirmation.";
					message.setText(msg);
					System.out.println(msg);
					System.out.println("Sending...");
					Transport.send(message);
					System.out.println("Email sent to administrators");
				} catch (MessagingException e) {
					System.out.println("Error: Could not send email");
					System.out.println(e.toString());
				}
			} else {
				System.out.println("Email address not set");
			}
			
			return "redirect:/?booked=true";
		} else {
			return "redirect:/manage_appointments?success=true";
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
			@RequestParam(value="patientId", required=false) int patientId, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		
		List<Patient> patients = getPatients();		
		List<Volunteer> allVolunteers = getAllVolunteers();
		
		//format time , remove day of week from string		
		int pos = time.indexOf("--");	
		time = time.substring(pos + 2);
		
		model.addAttribute("patients",patients);
		model.addAttribute("allvolunteers",allVolunteers);			
		model.addAttribute("selectedVolunteer",volunteerId);
		model.addAttribute("selectedPartner",partnerId);
		model.addAttribute("selectedTime", time);
		model.addAttribute("selectedPatient", patientId);
		

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
							availability.setMatchedTime(formateMatchTime(a1));
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
		
		return "/admin/view_scheduler";		
	}
	
	@RequestMapping(value="/schedule_appointment", method=RequestMethod.POST)
	public String createAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		//set up appointment
		Appointment appointment = new Appointment();
		
		int patientId = Integer.parseInt(request.getParameter("patient"));
		int volunteerId = Integer.parseInt(request.getParameter("volunteer"));
		int partnerId = Integer.parseInt(request.getParameter("partner"));
		appointment.setVolunteerID(volunteerId);
		appointment.setPatientID(patientId);
		appointment.setPartner(String.valueOf(partnerId));
		
		//get Date and time for appointment
		
		String day = request.getParameter("appointmentDate");
		String time = request.getParameter("appointmentTime");
		//format time, remove AM/PM
		time = time.substring(0,8);
		
		appointment.setDate(day);
		appointment.setTime(time);		
				
		//save new appointment in DB and send message 
		if (appointmentDao.createAppointment(appointment))
		{
			Patient patient = patientDao.getPatientByID(patientId);
			String vEmail = volunteerDao.getEmailByVolunteerId(volunteerId);
			String pEmail = volunteerDao.getEmailByVolunteerId(partnerId);
//			String vName = volunteerDao.getVolunteerNameById(volunteerId);
//			String pName = volunteerDao.getVolunteerNameById(partnerId);
			String logginUser = request.getUserPrincipal().getName();
			
			//send message to both volunteers
			if (mailAddress != null){			
				System.out.println("Email is not null...");
				//content of message
				StringBuffer sb = new StringBuffer();
				sb.append(logginUser);
				sb.append(" has booked an appointment with ");
				sb.append(patient.getFirstName());
				sb.append(" ");
				sb.append(patient.getLastName());
				sb.append( " for ");
				sb.append(time);
				sb.append(" on ");
				sb.append(day);
				sb.append(".\n");
				sb.append("This appointment is awaiting confirmation.");
				
				String msg = sb.toString();
				
				sendMessage(vEmail, msg, request);
				sendMessage(pEmail, msg, request);
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
		
//		List<Patient> patients = patientDao.getAllPatients();
//		List<Volunteer> volunteers = volunteerDao.getVolunteersWithAvailability();
//		model.addAttribute("patients",patients);
//		model.addAttribute("allvolunteers",volunteers);
		
//		return "/admin/view_scheduler";
		return "redirect:/view_scheduler";
	}
	
	
	private boolean isMatched(String level1, String level2){
		boolean matched = false;
		if (level1.equals("E") || level2.equals("E"))		
			matched = true;				
		else if (level1.equals("I") && level2.equals("I"))
			matched = true;
		
		return matched;
	}
	
	
	private boolean sendMessage(String email,String msg, SecurityContextHolderAwareRequestWrapper request){
		boolean success = false;
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		Message m = new Message();
		m.setSender(loggedInUser.getName());
		m.setSenderID(loggedInUser.getUserID());
		m.setText(msg);
		
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
				
				messageDao.sendMessage(m);
				success = true;
			} catch (MessagingException e) {
				System.out.println("Error: Could not send email");
				System.out.println(e.toString());
			}
		}
	
		return success;
	}
	
	private List<Patient> getPatients()
	{
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientDao.getAllPatientsWithAvailability();		
		
		return patients;
	}
	
	private List<Volunteer> getAllVolunteers(){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		volunteers = volunteerDao.getVolunteersWithAvailability();		
		
		return volunteers;
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
									availability.setMatchedTime(formateMatchTime(aSet1[i].toString()));
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
		time = formateMatchTime(time);		
		String v1Name = v1.getDisplayName();
		String v2Name = v2.getDisplayName();
		
		for (int i =0; i<list.size(); i++){
			a = list.get(i);
			
			if ((time.equals(a.getMatchedTime())) && ((v1Name.equals(a.getpDisplayName())) && (v2Name.equals(a.getvDisplayName())) ))
				return true;
		}
		return exist;
	}
	
	private String formateMatchTime(String time){
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
	
}
