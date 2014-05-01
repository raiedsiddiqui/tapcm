package org.tapestry.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.util.StringUtils;
import org.tapestry.dao.MessageDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.VolunteerDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.ActivityDao;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Message;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Activity;
import org.tapestry.controller.Utils;

import java.util.Properties;

@Controller
public class VolunteerController {
	
protected static Logger logger = Logger.getLogger(VolunteerController.class);
	
	private VolunteerDao volunteerDao = null;
	private AppointmentDao appointmentDao = null;
	private UserDao userDao = null;
	private ActivityDao activityDao = null;
	private MessageDao messageDao = null;
	
//	private Properties props;
   	private String mailAddress = "";
   	private Session session;
	
	@PostConstruct
	public void readDatabaseConfig(){
		Utils.setDatabaseConfig();
		
		Properties props = System.getProperties();
		String DB = props.getProperty("db");
		String UN = props.getProperty("un");
		String PW = props.getProperty("pwd");
		String mailHost = "";
   		String mailUser = "";
   		String mailPassword = "";
   		String mailPort = "";
   		String useTLS = "";
   		String useAuth = "";
				
		
		volunteerDao = new VolunteerDao(DB, UN, PW);		
		appointmentDao = new AppointmentDao(DB, UN, PW);
		userDao = new UserDao(DB, UN, PW);
		activityDao = new ActivityDao(DB, UN, PW);
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
	//display all volunteers
	@RequestMapping(value="/view_volunteers", method=RequestMethod.GET)
	public String getAllVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
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
		//save exist user names for validating on UI
		List<String> existUserNames = new ArrayList<String>();
		existUserNames = volunteerDao.getAllExistUsernames();
		
		model.addAttribute("existUserNames", existUserNames);
		return "/admin/add_volunteer";
	}
		
	//display detail of a volunteer
	@RequestMapping(value="/display_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String displayVolunteer(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("volunteerId") int id, ModelMap model){
		
		//get volunteer by id
		Volunteer volunteer = new Volunteer();
		volunteer = volunteerDao.getVolunteerById(id);
		
		//set address
		StringBuffer sb = new StringBuffer();
		if (!Utils.isNullOrEmpty(volunteer.getAptNumber()))
		{
			sb.append("# ");
			sb.append(volunteer.getAptNumber());
			sb.append(" ,");
		}
			
		if (!Utils.isNullOrEmpty(volunteer.getStreetNumber()))
			sb.append(volunteer.getStreetNumber());
		sb.append(" ");
		
		if (!Utils.isNullOrEmpty(volunteer.getStreet()))
			sb.append(volunteer.getStreet());
		sb.append(",");
		
		volunteer.setAddress(sb.toString());		
		
		model.addAttribute("volunteer", volunteer);
		
		//set availability
		if (!Utils.isNullOrEmpty(volunteer.getAvailability()))
			Utils.saveAvailability(volunteer.getAvailability(),model);		
		
		//get all completed appointments
		List<Appointment> appointments = new ArrayList<Appointment>();		
		appointments = appointmentDao.getAllCompletedAppointmentsForVolunteer(id);	
		model.addAttribute("completedVisits", appointments);
		
		//get all upcoming appointments
		appointments = new ArrayList<Appointment>();		
		appointments = appointmentDao.getAllUpcomingAppointmentsForVolunteer(id);
		model.addAttribute("upcomingVisits", appointments);
		
		//get all activities
		List<Activity> activities = activityDao.getAllActivitiesForVolunteer(id);		
		model.addAttribute("activityLogs", activities);
		
		//get all messages		
		ArrayList<Message> messages = messageDao.getAllMessagesForRecipient(id);
		model.addAttribute("messages", messages);		
		
		return "/admin/display_volunteer";
	}	
	
	//create a new volunteer and save in the table of volunteers and users in the DB
	@RequestMapping(value="/add_volunteer", method=RequestMethod.POST)
	public String addVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		
		String username = request.getParameter("username").trim();
		List<String> uList = volunteerDao.getAllExistUsernames();
		
		if (uList.contains(username))
		{
			model.addAttribute("userNameExist", true);			
			return "/admin/add_volunteer";						
		}
		else
		{
			Volunteer volunteer = new Volunteer();
			
			volunteer.setFirstName(request.getParameter("firstname").trim());
			volunteer.setLastName(request.getParameter("lastname").trim());
			volunteer.setEmail(request.getParameter("email").trim());	
			
			
			ShaPasswordEncoder enc = new ShaPasswordEncoder();
			String hashedPassword = enc.encodePassword(request.getParameter("password"), null);
			
			volunteer.setPassword(hashedPassword);
			volunteer.setExperienceLevel(request.getParameter("level"));	
			
			if (!Utils.isNullOrEmpty(request.getParameter("province")))
				volunteer.setStreet(request.getParameter("province"));
			if (!Utils.isNullOrEmpty(request.getParameter("conuntry")))
				volunteer.setStreet(request.getParameter("conuntry"));	
			if (!Utils.isNullOrEmpty(request.getParameter("street")))
				volunteer.setStreet(request.getParameter("street"));
			if (!Utils.isNullOrEmpty(request.getParameter("streetnum")))
				volunteer.setStreetNumber(request.getParameter("streetnum"));
			if (!Utils.isNullOrEmpty(request.getParameter("username")))
				volunteer.setUserName(request.getParameter("username").trim());				
			if(!Utils.isNullOrEmpty(request.getParameter("city")))
				volunteer.setCity(request.getParameter("city").trim());		
			if(!Utils.isNullOrEmpty(request.getParameter("homephone")))
				volunteer.setHomePhone(request.getParameter("homephone").trim());
			if (!Utils.isNullOrEmpty(request.getParameter("cellphone")))
				volunteer.setCellPhone(request.getParameter("cellphone"));
			if (!Utils.isNullOrEmpty(request.getParameter("emergencycontact")))
				volunteer.setEmergencyContact(request.getParameter("emergencycontact").trim());
			if (!Utils.isNullOrEmpty(request.getParameter("postalcode")))
				volunteer.setPostalCode(request.getParameter("postalcode"));
			if (!Utils.isNullOrEmpty(request.getParameter("emergencyphone")))
				volunteer.setEmergencyPhone(request.getParameter("emergencyphone").trim());
			if (!Utils.isNullOrEmpty(request.getParameter("aptnum")))
				volunteer.setAptNumber(request.getParameter("aptnum"));
			if (!Utils.isNullOrEmpty(request.getParameter("notes")))
				volunteer.setNotes(request.getParameter("notes"));
		
			String strAvailableTime = getAvailableTime(request);
			volunteer.setAvailability(strAvailableTime);
			//save a volunteer in the table volunteers
			boolean success = volunteerDao.addVolunteer(volunteer);			
			//save in the table users
			if (success)
				addUser(volunteer);	
			else{
				model.addAttribute("volunteerExist", true);
				return "/admin/add_volunteer";	
			}
			
			//set displayed message information 
			HttpSession session = request.getSession();
			session.setAttribute("volunteerMessage", "C");
			
			return "redirect:/view_volunteers";
		}
	}	
	
	@RequestMapping(value="/modify_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String modifyVolunteer(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("volunteerId") int id, ModelMap model){
		Volunteer volunteer = new Volunteer();
		volunteer = volunteerDao.getVolunteerById(id);
		
		String strAvailibilities = volunteer.getAvailability();
		boolean mondayNull = false;
		boolean tuesdayNull = false;
		boolean wednesdayNull = false;
		boolean thursdayNull = false;
		boolean fridayNull = false;
		
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
		
		model.addAttribute("volunteer", volunteer);
		model.addAttribute("mondayNull", mondayNull);
		model.addAttribute("tuesdayNull", tuesdayNull);
		model.addAttribute("wednesdayNull", wednesdayNull);
		model.addAttribute("thursdayNull", thursdayNull);
		model.addAttribute("fridayNull", fridayNull);
				
		return "/admin/modify_volunteer";
	}
	
	//update 
	@RequestMapping(value="/update_volunteer/{volunteerId}", method=RequestMethod.POST)
	public String updateVolunteer(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("volunteerId") int id, ModelMap model){		
		
		HttpSession  session = request.getSession();
		Volunteer volunteer;
			
		volunteer = volunteerDao.getVolunteerById(id);		
		
		if (!Utils.isNullOrEmpty(request.getParameter("firstname")))
			volunteer.setFirstName(request.getParameter("firstname"));
		
		if (!Utils.isNullOrEmpty(request.getParameter("lastname")))
			volunteer.setLastName(request.getParameter("lastname"));	
		
		//set encoded password for security
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(request.getParameter("password"), null);
		
		volunteer.setPassword(hashedPassword);
		
		if (!Utils.isNullOrEmpty(request.getParameter("email")))
			volunteer.setEmail(request.getParameter("email"));			
			
		if (!Utils.isNullOrEmpty(request.getParameter("level")))		
			volunteer.setExperienceLevel((request.getParameter("level")));	
			
		if (!Utils.isNullOrEmpty(request.getParameter("street")))
			volunteer.setStreet(request.getParameter("street"));
		
		if (!Utils.isNullOrEmpty(request.getParameter("city")))
			volunteer.setCity(request.getParameter("city"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("province")))
			volunteer.setProvince(request.getParameter("province"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("country")))
			volunteer.setCountry(request.getParameter("country"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("streetnum")))
			volunteer.setStreetNumber(request.getParameter("streetnum"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("aptnum")))
			volunteer.setAptNumber(request.getParameter("aptnum"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("postalcode")))
			volunteer.setPostalCode(request.getParameter("postalcode"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("homephone")))
			volunteer.setHomePhone(request.getParameter("homephone"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("cellphone")))
			volunteer.setCellPhone(request.getParameter("cellphone"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("emergencycontact")))
			volunteer.setEmergencyContact(request.getParameter("emergencycontact"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("emergencyphone")))
			volunteer.setEmergencyPhone(request.getParameter("emergencyphone"));
			
		if (!Utils.isNullOrEmpty(request.getParameter("notes")))
			volunteer.setNotes(request.getParameter("notes"));
			
		String strAvailableTime = getAvailableTime(request);		
		
		volunteer.setAvailability(strAvailableTime);
			
		volunteerDao.updateVolunteer(volunteer);
			
		//update users table as well
		modifyUser(volunteer);
		
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
	
	private void saveAvailability(String availability, ModelMap model){
		List<String> aList = new ArrayList<String>();		
		List<String> lMonday = new ArrayList<String>();
		List<String> lTuesday = new ArrayList<String>();
		List<String> lWednesday = new ArrayList<String>();
		List<String> lThursday = new ArrayList<String>();
		List<String> lFriday = new ArrayList<String>();	
			
		Map<String, String> showAvailableTime = Utils.getAvailabilityMap();		
		aList = Arrays.asList(availability.split(","));		
	
		for (String l : aList){			
			if (l.startsWith("1"))
				lMonday = getFormatedTimeList(l, showAvailableTime, lMonday);									
			
			if (l.startsWith("2"))
				lTuesday = getFormatedTimeList(l, showAvailableTime, lTuesday);									

			if (l.startsWith("3"))
				lWednesday = getFormatedTimeList(l, showAvailableTime, lWednesday);					
			
			if (l.startsWith("4"))
				lThursday = getFormatedTimeList(l, showAvailableTime, lThursday);	
						
			if (l.startsWith("5"))
				lFriday = getFormatedTimeList(l, showAvailableTime, lFriday);							
		}	
		
		if ((lMonday == null)||(lMonday.size() == 0))
			lMonday.add("1non");	
		if ((lTuesday == null)||(lTuesday.size() == 0))
			lTuesday.add("2non");
		if ((lWednesday == null)||(lWednesday.size() == 0))
			lWednesday.add("3non");
		if ((lThursday == null)||(lThursday.size() == 0))
			lThursday.add("4non");
		if ((lFriday == null)||(lFriday.size() == 0))
			lFriday.add("5non");
		
		model.addAttribute("monAvailability", lMonday);		
		model.addAttribute("tueAvailability", lTuesday);
		model.addAttribute("wedAvailability", lWednesday);
		model.addAttribute("thuAvailability", lThursday);
		model.addAttribute("friAvailability", lFriday);		
	}
	
	private List<String> getFormatedTimeList(String str, Map<String, String> map, List<String> list){
		String key;
		key = str.substring(1);
		
		if (!key.equals("non"))
		{
			list.add(map.get(key));
			Utils.sortList(list);
		}	
		
		return list;
	}
	
	private void modifyUser(Volunteer volunteer){		
		User user = userDao.getUserByUsername(volunteer.getUserName());
		
		StringBuffer sb = new StringBuffer();
		sb.append(volunteer.getFirstName());
		sb.append(" ");
		sb.append(volunteer.getLastName());
		user.setName(sb.toString());
			
		user.setEmail(volunteer.getEmail());
		userDao.modifyUser(user);
		
		//update password
		userDao.setPasswordForUser(user.getUserID(), volunteer.getPassword());		
	}
	
	private void addUser(Volunteer volunteer){
	
		User user = new User();
		StringBuffer sb = new StringBuffer();
		sb.append(volunteer.getFirstName());
		sb.append(" ");
		sb.append(volunteer.getLastName());
		user.setName(sb.toString());
		user.setUsername(volunteer.getUserName());
		user.setRole("ROLE_USER");
				
		user.setPassword(volunteer.getPassword());
		user.setEmail(volunteer.getEmail());
				
		boolean success = userDao.createUser(user);
				
		if (mailAddress != null && success){
			try{
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailAddress));
				message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmail()));
				message.setSubject("Welcome to Tapestry");
				
				sb = new StringBuffer();
				sb.append("Thank you for volunteering with Tapestry. Your account has been successfully created.\n");
				sb.append("Your username and password are as follows:\n");
				sb.append("Username: ");
				sb.append(user.getUsername());
				sb.append("\n");
				sb.append("Password: password\n\n");

				message.setText(sb.toString());
				System.out.println(sb.toString());
				System.out.println("Sending...");
				Transport.send(message);
				System.out.println("Email sent containing credentials to " + user.getEmail());
			} catch (MessagingException e) {
				System.out.println("Error: Could not send email");
				System.out.println(e.toString());
			}
		}
					
	}
	
	private String getAvailableTime(SecurityContextHolderAwareRequestWrapper request)
	{			
		String strAvailableTime = "";
		List<String> availability = new ArrayList<String>();
		String mondayNull = request.getParameter("mondayNull");
		String tuesdayNull = request.getParameter("tuesdayNull");
		String wednesdayNull = request.getParameter("wednesdayNull");
		String thursdayNull = request.getParameter("thursdayNull");
		String fridayNull = request.getParameter("fridayNull");		
		String from1, from2, to1, to2;		
		
		//get availability for Monday
		if (!"non".equals(mondayNull))
		{
			from1 = request.getParameter("monFrom1");		
			from2 = request.getParameter("monFrom2");
			to1 = request.getParameter("monTo1");
			to2 = request.getParameter("monTo2");
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("1non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("1non");		
		
		//get availability for Tuesday
		if(!"non".equals(tuesdayNull))
		{
			from1 = request.getParameter("tueFrom1");
			from2 = request.getParameter("tueFrom2");
			to1 = request.getParameter("tueTo1");
			to2 = request.getParameter("tueTo2");		
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("2non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);				
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("2non");
		
		//get availability for Wednesday
		if (!"non".equals(wednesdayNull))
		{
			from1 = request.getParameter("wedFrom1");
			from2 = request.getParameter("wedFrom2");
			to1 = request.getParameter("wedTo1");
			to2 = request.getParameter("wedTo2");
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("3non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);					
			}
		}
		else
			availability.add("3non");
		
		//get availability for Thursday
		if (!"non".equals(thursdayNull))
		{
			from1 = request.getParameter("thuFrom1");
			from2 = request.getParameter("thuFrom2");
			to1 = request.getParameter("thuTo1");
			to2 = request.getParameter("thuTo2");
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("4non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);	
			}
		}
		else
			availability.add("4non");
		
		//get availability for Friday
		if(!"non".equals(fridayNull))
		{			
			from1 = request.getParameter("friFrom1");
			from2 = request.getParameter("friFrom2");
			to1 = request.getParameter("friTo1");
			to2 = request.getParameter("friTo2");	
			
			if ((from1.equals(from2))&&(from1.equals("0")))
				availability.add("5non");	
			else
			{
				availability = Utils.getAvailablePeriod(from1, to1, availability);
				availability = Utils.getAvailablePeriod(from2, to2, availability);
			}
		}
		else
			availability.add("5non");
	
		//convert arrayList to string for matching data type in DB
		if (availability != null)
			strAvailableTime=StringUtils.collectionToCommaDelimitedString(availability);	
		
		return strAvailableTime;
	}

}
