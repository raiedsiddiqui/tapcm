package org.tapestry.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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
	//
	@RequestMapping(value="/book_appointment/{volunteerId}", method=RequestMethod.GET)
	public String addAppointment(@PathVariable("volunteerId") int volunteerId, @RequestParam(value="vId", required=false) int partnerId, 
			@RequestParam(value="pId", required=false) int patientId, @RequestParam(value="time", required=false) String time,
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		//set up appointment
		Appointment appointment = new Appointment();
		appointment.setVolunteerID(volunteerId);
		appointment.setPatientID(patientId);
		appointment.setPartner(String.valueOf(partnerId));
		
		//get Date and time for appointment
		String day = "";
		day = time.substring(0,3);
		String strDate = Utils.getDateOfWeek(day);		 
		time = time.substring(4,9);
		
		appointment.setDate(strDate.substring(0,10));
		appointment.setTime(time);		
				
		if (appointmentDao.createAppointment(appointment))
		{
			String vEmail = volunteerDao.getEmailByVolunteerId(volunteerId);
			String pEmail = volunteerDao.getEmailByVolunteerId(partnerId);
			
			//send message to both volunteers
			if (mailAddress != null){
				try{
					Patient patient = patientDao.getPatientByID(patientId);
					
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(mailAddress));
					message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(vEmail));
					message.setSubject("Tapestry: New appointment booked");
					
					StringBuffer sb = new StringBuffer();
					sb.append(patient.getVolunteerName());
					sb.append(" has booked an appointment with ");
					sb.append(patient.getFirstName());
					sb.append(" ");
					sb.append(patient.getLastName());
					sb.append( " for ");
					sb.append(time);
					sb.append(" on ");
					sb.append(strDate);
					sb.append(".\n");
					sb.append("This appointment is awaiting confirmation.");
					
					String msg = sb.toString();				
					message.setText(msg);
					
					System.out.println(msg);
					System.out.println("Sending...");
					Transport.send(message);
					System.out.println("Email sent to administrators");
				} catch (MessagingException e) {
					System.out.println("Error: Could not send email");
					System.out.println(e.toString());
				}
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
		
		List<Patient> patients = new ArrayList<Patient>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		model.addAttribute("patients",patients);
		model.addAttribute("allvolunteers",volunteers);
		
		return "/admin/view_scheduler";
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
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientDao.getAllPatients();
		
		List<Volunteer> allVolunteers = new ArrayList<Volunteer>();
		List<Volunteer> experiencedVolunteers = new ArrayList<Volunteer>();
		List<Volunteer> noBeginnerVolunteers = new ArrayList<Volunteer>();
		
		allVolunteers = volunteerDao.getAllVolunteers();
		experiencedVolunteers = volunteerDao.getMatchedVolunteersByLevel("B");
		noBeginnerVolunteers = volunteerDao.getMatchedVolunteersByLevel("I");
		
		model.addAttribute("patients",patients);
		model.addAttribute("allvolunteers",allVolunteers);
		model.addAttribute("experiencedvolunteers",experiencedVolunteers);
		model.addAttribute("nobeginnervolunteers",noBeginnerVolunteers);
		return "/admin/view_scheduler";
	}
	
	//load match time for both selected volunteers  /view_matchTime
	@RequestMapping(value="/view_matchTime", method=RequestMethod.POST)
	public String viewMatchAvailablities( SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		List<Patient> patients = new ArrayList<Patient>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		String patientId = request.getParameter("patient");
		patients = patientDao.getAllPatients();
		volunteers = volunteerDao.getAllVolunteers();
		
		String volunteer1 = request.getParameter("volunteer1");		
		String volunteer2 = request.getParameter("volunteer2");		
		
		if (volunteer1.equals(volunteer2))
		{
			model.addAttribute("sameVolunteer",true);
			model.addAttribute("allvolunteers", volunteers);
			return "/admin/view_scheduler";
		}

		Volunteer v1 = volunteerDao.getVolunteerById(Integer.parseInt(volunteer1));
		Volunteer v2 = volunteerDao.getVolunteerById(Integer.parseInt(volunteer2));
		
		String v1Level = v1.getExperienceLevel();
		String v2Level = v2.getExperienceLevel();
		
		if(!isMatched(v1Level, v2Level)){
			model.addAttribute("misMatchedVolunteer",true);
			model.addAttribute("allvolunteers", volunteers);
			return "/admin/view_scheduler";
		}		
					
		String[] aVolunteer1, aVolunteer2;
		if (!Utils.isNullOrEmpty(v1.getAvailability()) )
		{
			aVolunteer1= v1.getAvailability().split(";");		
				
			if (!Utils.isNullOrEmpty(v2.getAvailability()))
			{
				aVolunteer2 = v2.getAvailability().split(";");				
				List<String> matchList = new ArrayList<String>();
					
				for( String a1: aVolunteer1 )
				{
					for(String a2 : aVolunteer2)
					{
						if (a1.equals(a2))
							matchList.add(a1);
					}
				}
					
				if (matchList.size() == 0)
				{
					model.addAttribute("noMatchTime",true);
					model.addAttribute("allvolunteers", volunteers);
					return "/admin/view_scheduler";
				}
				else
				{						
					model.addAttribute("matchedAvailability",matchList);
					model.addAttribute("volunteerOne",v1);
					model.addAttribute("volunteerTwo",v2);
					model.addAttribute("volunteers", volunteers);
					model.addAttribute("patients", patients);		
					model.addAttribute("selectedPatient", patientId);
					return "/admin/make_scheduler";
				}					 
			}
			else
			{
					model.addAttribute("noAvailableTime", true);
					model.addAttribute("allvolunteers", volunteers);
					return "/admin/view_scheduler";
			}
		}
		else
		{
			model.addAttribute("allvolunteers", volunteers);
			model.addAttribute("noAvailableTime", true);
		
			return "/admin/view_scheduler";
		}
		
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
}
