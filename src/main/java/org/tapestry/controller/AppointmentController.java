package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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
import org.tapestry.dao.MessageDao;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Message;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Availability;
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
   		
   		model.addAttribute("appointments", allAppointments);
   		model.addAttribute("patients", allPatients);
   		model.addAttribute("activities", allAppointmentActivities);
   		if(appointmentBooked != null)
   			model.addAttribute("success", appointmentBooked);
   		return "admin/manage_appointments";
   	}
   	
	@RequestMapping(value="/book_appointment", method=RequestMethod.POST)
	public String addAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		int patientId = Integer.parseInt(request.getParameter("patient"));
		Patient p = patientDao.getPatientByID(patientId);			
		
		Appointment a = new Appointment();
		a.setVolunteerID(p.getVolunteer());
		a.setPatientID(p.getPatientID());
		a.setDate(request.getParameter("appointmentDate"));
		a.setTime(request.getParameter("appointmentTime"));
		appointmentDao.createAppointment(a);
		
		if(request.isUserInRole("ROLE_USER")) {
			//Email all the administrators with a notification
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
		model.addAttribute("showPatients", false);

		return "/admin/view_scheduler";
	}
	
	//Open add new appointment from scheduler
	@RequestMapping(value="/add_appointment/{volunteerId}", method=RequestMethod.GET)
	public String addAppointmentFromSecheduler(@PathVariable("volunteerId") int volunteerId, 
			@RequestParam(value="vId", required=false) int partnerId, 			
			@RequestParam(value="time", required=false) String time, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		
		List<Patient> patients = getPatients();		
		List<Volunteer> allVolunteers = getAllVolunteers();
		
		//format time 
		StringBuffer sb = new StringBuffer();
		sb.append(time.substring(4,9));
		sb.append(":00");
		
		time = sb.toString();
		//selectedPatient
		model.addAttribute("patients",patients);
		model.addAttribute("allvolunteers",allVolunteers);	
		
		model.addAttribute("selectedVolunteer",volunteerId);
		model.addAttribute("selectedPartner",partnerId);
		model.addAttribute("selectedTime", time);
		

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
				if (!Utils.isNullOrEmpty(v1.getAvailability()) )
				{
					aVolunteer1= v1.getAvailability().split(";");						
					if (!Utils.isNullOrEmpty(v2.getAvailability()))
					{
						aVolunteer2 = v2.getAvailability().split(";");				
						List<Availability> matchList = new ArrayList<Availability>();
							
						for( String a1: aVolunteer1 )
						{
							for(String a2 : aVolunteer2)
							{
								if (a1.equals(a2))
								{//find matched available time for both volunteers
									availability = new Availability();								
									availability.setvDisplayName(v1.getDisplayName());
						        	availability.setvPhone(v1.getHomePhone());
						        	availability.setvEmail(v1.getEmail());
						        	availability.setpDisplayName(v2.getDisplayName());
						        	availability.setpPhone(v2.getHomePhone());
						        	availability.setpEmail(v2.getEmail());
						        	availability.setMatchedTime(a1);
						        	availability.setvId(Integer.parseInt(volunteerId1));
						        	availability.setpId(Integer.parseInt(volunteerId2));
									matchList.add(availability);
								}
							}
						}					
						if (matchList.size() == 0)
							model.addAttribute("noMatchTime",true);
						else
							model.addAttribute("matcheList",matchList);				 
					}
					else
						model.addAttribute("noAvailableTime", true);
				}
				else							
					model.addAttribute("noAvailableTime", true);
			}			
		}
		model.addAttribute("allvolunteers", volunteers);
		model.addAttribute("volunteerOne",v1);
		model.addAttribute("volunteerTwo",v2);
		model.addAttribute("patients", patients);
		model.addAttribute("selectedPatient", patientId);
		model.addAttribute("showPatients", true);
		
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
		patients = patientDao.getAllPatients();
		
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
			if (!Utils.isNullOrEmpty(availability1))
			{				
				aSet1 = availability1.split(";");		
				
				for (Volunteer v2: list2)
				{
					if (v1.getVolunteerId()!= v2.getVolunteerId())
					{		
						availability2 = v2.getAvailability();						
						if ((!Utils.isNullOrEmpty(availability2)) && (isMatchVolunteer(v1, v2)))						
						{
							aSet2  = availability2.split(";");							
							//find match availability					
							for(int i = 0; i < aSet1.length; i++)
							{								
							    for(int j = 0; j <aSet2.length; j++)
							    {
							    	
							        if(aSet1[i].toString().equals(aSet2[j].toString()) )		
							        {								        	
							        	availability = new Availability();
							        	availability.setvDisplayName(v1.getDisplayName());
							        	availability.setvPhone(v1.getHomePhone());
							        	availability.setvEmail(v1.getEmail());
							        	availability.setpDisplayName(v2.getDisplayName());
							        	availability.setpPhone(v2.getHomePhone());
							        	availability.setpEmail(v2.getEmail());
							        	availability.setMatchedTime(aSet1[i].toString());
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
		}		
		
		return aList;
	}
	
	private boolean isMatchVolunteer(Volunteer vol1, Volunteer vol2){
		String v1Type = vol1.getExperienceLevel();
		String v2Type = vol2.getExperienceLevel();		
		
		boolean matched = false;		
		
		if ("Experienced".equals(v1Type) || "Experienced".equals(v2Type)){
			matched = true;
		}
		else if ("Intermediate".equals(v1Type) && "Intermediate".equals(v2Type))
		{
			matched = true;
		}
		
		return matched;
	}
	
	
}
