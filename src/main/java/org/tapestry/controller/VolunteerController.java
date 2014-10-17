package org.tapestry.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tapestry.utils.MisUtils;
import org.tapestry.utils.Utils;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Availability;
import org.tapestry.objects.Message;
import org.tapestry.objects.Narrative;
import org.tapestry.objects.Patient;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Organization;
import org.tapestry.service.AppointmentManager;
import org.tapestry.service.MessageManager;
import org.tapestry.service.PatientManager;
import org.tapestry.service.SurveyManager;
import org.tapestry.service.UserManager;
import org.tapestry.service.VolunteerManager;
import org.yaml.snakeyaml.Yaml;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;

@Controller
public class VolunteerController {
	protected static Logger logger = Logger.getLogger(VolunteerController.class);	
	
	@Autowired
   	private UserManager userManager;
	@Autowired
	private MessageManager messageManager;
	@Autowired
	private AppointmentManager appointmentManager;
	@Autowired
	private VolunteerManager volunteerManager;
	@Autowired
	private PatientManager patientManager;
	@Autowired
	private SurveyManager surveyManager;
	
	private Properties props;
   	private String mailAddress = "";
   	private Session session;
   	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
   	
   	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 */
   	@PostConstruct
   	public void readConfig(){
   		
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
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		volunteers = volunteerManager.getAllVolunteers();		
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
			volunteers = volunteerManager.getVolunteersByName(name);			
		} 		
		model.addAttribute("searchName", name);
		model.addAttribute("volunteers", volunteers);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_volunteers";
	}
	
	@RequestMapping(value="/new_volunteer", method=RequestMethod.GET)
	public String newVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		//save exist user names for validating on UI
		List<String> existUserNames = new ArrayList<String>();
		existUserNames = volunteerManager.getAllExistUsernames();
		model.addAttribute("existUserNames", existUserNames);
		
		List<Organization> organizations;
		HttpSession session = request.getSession();
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			organizations = volunteerManager.getAllOrganizations();
			session.setAttribute("organizations", organizations);
		}

		model.addAttribute("organizations", organizations);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/add_volunteer";
	}
		
	//display detail of a volunteer
	@RequestMapping(value="/display_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String displayVolunteer(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("volunteerId") int id, ModelMap model){
		
		//get volunteer by id
		Volunteer volunteer = new Volunteer();
		volunteer = volunteerManager.getVolunteerById(id);
		
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
		appointments = appointmentManager.getAllCompletedAppointmentsForVolunteer(id);	
		model.addAttribute("completedVisits", appointments);
		
		//get all upcoming appointments
		appointments = new ArrayList<Appointment>();		
		appointments = appointmentManager.getAllUpcomingAppointmentsForVolunteer(id);
		model.addAttribute("upcomingVisits", appointments);
		
		//get all activities
		List<Activity> activities = volunteerManager.getActivitiesForVolunteer(id);		
		model.addAttribute("activityLogs", activities);
		
		//get all messages		
		List<Message> messages = messageManager.getAllMessagesForRecipient(Utils.getLoggedInUserId(request));
		model.addAttribute("messages", messages);		
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/display_volunteer";
	}	
	
	//create a new volunteer and save in the table of volunteers and users in the DB
	@RequestMapping(value="/add_volunteer", method=RequestMethod.POST)
	public String addVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		
		String username = request.getParameter("username").trim();
		List<String> uList = volunteerManager.getAllExistUsernames();
		
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
			if (!Utils.isNullOrEmpty(request.getParameter("organization")))
				volunteer.setOrganizationId(Integer.valueOf(request.getParameter("organization")));		
								
			String strAvailableTime = getAvailableTime(request);
			volunteer.setAvailability(strAvailableTime);
			//save a volunteer in the table volunteers
			boolean success = volunteerManager.addVolunteer(volunteer);			
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
		volunteer = volunteerManager.getVolunteerById(id);
		
		List<Organization> organizations;
		HttpSession session = request.getSession();
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			organizations = volunteerManager.getAllOrganizations();
			session.setAttribute("organizations", organizations);
		}
		
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
		model.addAttribute("organizations", organizations);		
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
				
		return "/admin/modify_volunteer";
	}
	
	//update 
	@RequestMapping(value="/update_volunteer/{volunteerId}", method=RequestMethod.POST)
	public String updateVolunteer(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("volunteerId") int id, ModelMap model){		
		
		HttpSession  session = request.getSession();
		Volunteer volunteer;
			
		volunteer = volunteerManager.getVolunteerById(id);		
		
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
		
		if (!Utils.isNullOrEmpty(request.getParameter("organization")))
			volunteer.setOrganizationId(Integer.valueOf(request.getParameter("organization")));
			
		String strAvailableTime = getAvailableTime(request);		
		
		volunteer.setAvailability(strAvailableTime);
			
		volunteerManager.updateVolunteer(volunteer);
			
		//update users table as well
		modifyUser(volunteer);
		
		session.setAttribute("volunteerMessage","U");
		return "redirect:/view_volunteers";
	
	}
	
	@RequestMapping(value="/delete_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String deleteVolunteerById(@PathVariable("volunteerId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		volunteerManager.deleteVolunteerById(id);
				
		HttpSession  session = request.getSession();		
		session.setAttribute("volunteerMessage", "D");		
	
		return "redirect:/view_volunteers";
		
	}
	

	private void modifyUser(Volunteer volunteer){				
		User user = userManager.getUserByUsername(volunteer.getUserName());
		
		StringBuffer sb = new StringBuffer();
		sb.append(volunteer.getFirstName());
		sb.append(" ");
		sb.append(volunteer.getLastName());
		user.setName(sb.toString());
			
		user.setEmail(volunteer.getEmail());
		userManager.modifyUser(user);
		
		//update password
		userManager.setPasswordForUser(user.getUserID(), volunteer.getPassword());		
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
		user.setOrganization(volunteer.getOrganizationId());		

		boolean success = userManager.createUser(user);				


		
		
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
	
	//Activity in Volunteer
	//display all activity input by volunteers
	@RequestMapping(value="/view_activity_admin", method=RequestMethod.GET)
	public String viewActivityByAdmin( SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		List<Activity> activities = new ArrayList<Activity>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();		
		User loggedInUser = Utils.getLoggedInUser(request);
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			activities = volunteerManager.getActivitiesForAdmin();
			volunteers = volunteerManager.getAllVolunteers();
		}
		else if (request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{
			int organizationId = loggedInUser.getOrganization();
			activities = volunteerManager.getActivitiesForLocalAdmin(organizationId);
			volunteers = volunteerManager.getAllVolunteersByOrganization(organizationId);
		}
			
		if (activities.size() == 0 )  
			model.addAttribute("emptyActivityLogs", true);			
		
		model.addAttribute("activityLogs", activities);	
		model.addAttribute("volunteers", volunteers);	
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		
			
		return "/admin/view_activityLogs";
	}
		
	//display all activity logs for selected volunteer
	@RequestMapping(value="/view_activity_admin", method=RequestMethod.POST)
	public String viewActivityBySelectedVolunteerByAdmin( SecurityContextHolderAwareRequestWrapper request, ModelMap model)
	{		
		String strVId = request.getParameter("search_volunteer");		
		List<Activity> activities = new ArrayList<Activity>();
		
		if (!Utils.isNullOrEmpty(strVId))
		{
			activities = volunteerManager.getActivitiesForVolunteer(Integer.parseInt(strVId));		
				
			if (activities.size() == 0 )  
				model.addAttribute("emptyActivityLogs", true);				
		}		
		List<Volunteer> volunteers = volunteerManager.getAllVolunteers();
			
		model.addAttribute("activityLogs", activities);	
		model.addAttribute("volunteers", volunteers);	
		model.addAttribute("selectedVolunteer", strVId);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/admin/view_activityLogs";
	}
		
	@RequestMapping(value="/view_activity", method=RequestMethod.GET)
	public String viewActivityByVolunteer( SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession  session = request.getSession();		
		int volunteerId = MisUtils.getLoggedInVolunteerId(request);	
		List<Activity> activities = new ArrayList<Activity>();
		activities = volunteerManager.getActivitiesForVolunteer(volunteerId);
					
		//check if there is message should be displayed
		if (session.getAttribute("ActivityMessage") != null)
		{
			String message = session.getAttribute("ActivityMessage").toString();
				
			if ("C".equals(message)){
				model.addAttribute("activityCreated", true);
				session.removeAttribute("ActivityMessage");
			}
			else if ("D".equals(message)){
				model.addAttribute("activityDeleted", true);
				session.removeAttribute("ActivityMessage");
			}
			else if ("U".equals(message)){
				model.addAttribute("activityUpdate", true);
				session.removeAttribute("ActivityMessage");
			}			
		}
		model.addAttribute("activityLogs", activities);	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/volunteer/view_activityLogs";
	}
		
	@RequestMapping(value="/new_activity", method=RequestMethod.GET)
	public String newActivityByVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/volunteer/new_activityLogs";
	}
		
	@RequestMapping(value="/add_activity", method=RequestMethod.POST)
	public String addActivityByVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		HttpSession  session = request.getSession();		
		int volunteerId = MisUtils.getLoggedInVolunteerId(request);	
			
		Volunteer volunteer = volunteerManager.getVolunteerById(volunteerId);
		int organizationId = volunteer.getOrganizationId();
			
		String date = request.getParameter("activityDate");
		String startTime = request.getParameter("activityStartTime");	
		String endTime = request.getParameter("activityEndTime");	
		String description = request.getParameter("activityDesc");
			
		Activity activity = new Activity();
		activity.setDescription(description);
		activity.setVolunteer(String.valueOf(volunteerId));				
		activity.setDate(date);		
		activity.setOrganizationId(organizationId);
			
			//format start_Time and end_Time to match data type in DB
		StringBuffer sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(startTime);
		startTime = sb.toString();
		activity.setStartTime(startTime);
			
		sb = new StringBuffer();
		sb.append(date);
		sb.append(" ");
		sb.append(endTime);
		endTime = sb.toString();
			
		activity.setEndTime(endTime);
			
		volunteerManager.logActivity(activity);
			
		//update view activity page with new record		
		session.setAttribute("ActivityMessage", "C");		
		
		return "redirect:/view_activity";
	}	
		
	@RequestMapping(value="/delete_activity/{activityId}", method=RequestMethod.GET)
	public String deleteActivityById(@PathVariable("activityId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
				
		volunteerManager.deleteActivity(id);	
					
		HttpSession  session = request.getSession();		
		session.setAttribute("ActivityMessage", "D");		
		
		return "redirect:/view_activity";		
	}
		
	@RequestMapping(value="/modify_activity/{activityId}", method=RequestMethod.GET)
	public String modifyActivityLog(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("activityId") int id, ModelMap model){
		Activity activity = new Activity();
		activity = volunteerManager.getActivity(id);			
		model.addAttribute("activityLog", activity);
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
			
		return "/volunteer/modify_activityLog";
	}
		
	@RequestMapping(value="/update_activity", method=RequestMethod.POST)
	public String updateActivityById(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		String activityId = null;
		int iActivityId = 0;		
		Activity activity = new Activity();
			
		HttpSession  session = request.getSession();		
		int volunteerId = MisUtils.getLoggedInVolunteerId(request);	
			
		if (!Utils.isNullOrEmpty(request.getParameter("activityId")))
		{
			activityId = request.getParameter("activityId");
			iActivityId = Integer.parseInt(activityId);
				
			activity = volunteerManager.getActivity(iActivityId);
			activity.setVolunteer(String.valueOf(volunteerId));
				
			String date = null;
			if(!Utils.isNullOrEmpty(request.getParameter("activityDate")))
				date = request.getParameter("activityDate");
			activity.setDate(date + " 00:00:00");
				
			String startTime = null;
			if (!Utils.isNullOrEmpty(request.getParameter("activityStartTime")))
				startTime = request.getParameter("activityStartTime");
			
			System.out.println("start time === " + startTime);
			
			if (startTime.length() < 6)//format 
				activity.setStartTime(date +" " + startTime + ":00"); 
			else
				activity.setStartTime(date +" " + startTime); 
			
			System.out.println("after start time === " + activity.getStartTime());
				
			String endTime = null;
			if (!Utils.isNullOrEmpty(request.getParameter("activityEndTime")))
				endTime = request.getParameter("activityEndTime");
				
			System.out.println("end time === " + endTime);
			if (endTime.length() < 6)//format
				activity.setEndTime(date + " " + endTime + ":00");
			else
				activity.setEndTime(date + " " + endTime);
			System.out.println("after end time === " + activity.getEndTime());
			String desc = null;
			desc = request.getParameter("activityDesc");
			if (!Utils.isNullOrEmpty(desc))
				activity.setDescription(desc);						
		}
			
		volunteerManager.updateActivity(activity);
			
		session.setAttribute("ActivityMessage","U");
		return "redirect:/view_activity";	
	}
	
	//Organizations		
	@RequestMapping(value="/view_organizations", method=RequestMethod.GET)
	public String getOganizations(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		List<Organization> organizations = new ArrayList<Organization>();
		HttpSession  session = request.getSession();	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		organizations = volunteerManager.getAllOrganizations();		
		model.addAttribute("organizations", organizations);	
		
		if (session.getAttribute("organizatioMessage") != null)
		{
			String message = session.getAttribute("organizatioMessage").toString();
			
			if ("C".equals(message)){
				model.addAttribute("organizationCreated", true);
				session.removeAttribute("organizatioMessage");
			}
	
		}		
		return "/admin/view_organizations";
	}
	
	//display all volunteers with search criteria
	@RequestMapping(value="/view_organizations", method=RequestMethod.POST)
	public String viewFilteredOrganizations(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<Organization> organizations = new ArrayList<Organization>();
		
		String name = request.getParameter("searchName");
		if(!Utils.isNullOrEmpty(name)) {
			organizations = volunteerManager.getOrganizationsByName(name);			
		} 		
		model.addAttribute("searchName", name);
		model.addAttribute("organizations", organizations);	
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_organizations";
	}
	
	@RequestMapping(value="/new_organization", method=RequestMethod.GET)
	public String newOrganization(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/add_organization";
	}
	
	//create a new volunteer and save in the table of volunteers and users in the DB
	@RequestMapping(value="/add_organization", method=RequestMethod.POST)
	public String addOrganization(SecurityContextHolderAwareRequestWrapper request, ModelMap model){					
		
		Organization organization = new Organization();
			
		organization.setName(request.getParameter("name").trim());
		organization.setCity(request.getParameter("city"));
		organization.setPrimaryContact(request.getParameter("primaryContact"));
		organization.setPrimaryPhone(request.getParameter("primaryPhone"));
		organization.setSecondaryContact(request.getParameter("secondaryContact"));
		organization.setSecondaryPhone(request.getParameter("secondaryPhone"));
		organization.setStreetNumbet(request.getParameter("streetNumber"));
		organization.setStreetName(request.getParameter("streetName"));
		organization.setPostCode(request.getParameter("postCode"));
		organization.setProvince(request.getParameter("province"));		
		organization.setCountry(request.getParameter("country"));
			
		if (volunteerManager.addOrganization(organization))
		{
			HttpSession session = request.getSession();
			session.setAttribute("organizatioMessage", "C");							
		}		
		
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "redirect:/view_organizations";			
	}	
	
	
	@RequestMapping(value="/view_pdf", method=RequestMethod.GET)
	public String viewPDF(HttpServletResponse response) throws DocumentException, IOException{
		
		return "/admin/pdf_generator";
	}
	
	@RequestMapping(value="/go_pdf", method=RequestMethod.GET)
	@ResponseBody
	public String goPDF(HttpServletResponse response) throws DocumentException, IOException{	

	       String orignalFileName="reportTest.pdf";

	        try {
	        	   // step 1
	            Document document = new Document(PageSize.A5.rotate());
	            response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
	            // step 2
	            PdfWriter.getInstance(document, response.getOutputStream());

	            // step 3
	            document.open();
	            // step 4
	            PdfPTable table = new PdfPTable(2);
	            // a long phrase
	            Phrase p = new Phrase(
	                "Dr. iText or: How I Learned to Stop Worrying and Love PDF.");
	            PdfPCell cell = new PdfPCell(p);
	            // the prhase is wrapped
	            table.addCell("wrap");
	            cell.setNoWrap(false);
	            table.addCell(cell);
	            // the phrase isn't wrapped
	            table.addCell("no wrap");
	            cell.setNoWrap(true);
	            table.addCell(cell);
	            // a long phrase with newlines
	            p = new Phrase(
	                "Dr. iText or:\nHow I Learned to Stop Worrying\nand Love PDF.");
	            cell = new PdfPCell(p);
	            // the phrase fits the fixed height
	            table.addCell("fixed height (more than sufficient)");
	            cell.setFixedHeight(72f);
	            table.addCell(cell);
	            // the phrase doesn't fit the fixed height
	            table.addCell("fixed height (not sufficient)");
	            cell.setFixedHeight(36f);
	            table.addCell(cell);
	            // The minimum height is exceeded
	            table.addCell("minimum height");
	            cell = new PdfPCell(new Phrase("Dr. iText"));
	            cell.setMinimumHeight(36f);
	            table.addCell(cell);
	            // The last row is extended
	            table.setExtendLastRow(true);
	            table.addCell("extend last row");
	            table.addCell(cell);
	            document.add(table);
	            // step 5
	           
	            document.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
		return null;
	}	
	
	private void sendMessageToInbox(String subject, String msg, int sender, int recipient){	
		Message m = new Message();
		m.setRecipient(recipient);
		m.setSenderID(sender);
		m.setText(msg);
		m.setSubject(subject);
		messageManager.sendMessage(m);
	}
	
	//==================== Appointment ==============================//
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
			int volunteerId = volunteerManager.getVolunteerIdByUsername(username);		
		
			session.setAttribute("logged_in_volunteer", volunteerId);
			List<Patient> patientsForUser = patientManager.getPatientsForVolunteer(volunteerId);
			List<Message> announcements = messageManager.getAnnouncementsForUser(userId);
			
			List<Appointment> approvedAppointments = new ArrayList<Appointment>();
			List<Appointment> pendingAppointments = new ArrayList<Appointment>();
			List<Appointment> declinedAppointments = new ArrayList<Appointment>();
			if(patientId != null) {				
				approvedAppointments = appointmentManager.getAllApprovedAppointmentsForPatient(patientId, volunteerId);
				pendingAppointments = appointmentManager.getAllPendingAppointmentsForPatient(patientId, volunteerId);
				declinedAppointments = appointmentManager.getAllDeclinedAppointmentsForPatient(patientId, volunteerId);
				
				Patient patient = patientManager.getPatientByID(patientId);
				model.addAttribute("patient", patient);
				
				//set patientId in the session for other screen, like narratives 
				session.setAttribute("patientId", patientId);				
			} 
			else 
			{
				approvedAppointments = appointmentManager.getAllApprovedAppointmentsForVolunteer(volunteerId);				
				pendingAppointments = appointmentManager.getAllPendingAppointmentsForVolunteer(volunteerId);
				declinedAppointments = appointmentManager.getAllDeclinedAppointmentsForVolunteer(volunteerId);						
			}
			
			model.addAttribute("approved_appointments", approvedAppointments);
			model.addAttribute("pending_appointments", pendingAppointments);
			model.addAttribute("declined_appointments", declinedAppointments);
			model.addAttribute("name", loggedInUser.getName());
			model.addAttribute("patients", patientsForUser);
			
			unreadMessages = messageManager.countUnreadMessagesForRecipient(userId);
			model.addAttribute("unread", unreadMessages);	
			//save unreadMessages in sesion
			session.setAttribute("unread_messages", unreadMessages);
			
			model.addAttribute("announcements", announcements);
			if (booked != null)
				model.addAttribute("booked", booked);
			
			if (noMachedTime != null)
				model.addAttribute("noMachedTime", noMachedTime);
			
			remindingAppointments = appointmentManager.getRemindingAppointmentList(volunteerId, -2);			
			model.addAttribute("reminding_appointments", remindingAppointments);

				
			return "volunteer/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{			
			User loggedInUser = Utils.getLoggedInUser(request);			
			
			unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
			//save unreadMessages in sesion
			session.setAttribute("unread_messages", unreadMessages);
			
			model.addAttribute("name", loggedInUser.getName());
			
			remindingAppointments = appointmentManager.getRemindingAppointmentList(0, -2);			
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
   		
   		List<Appointment> allAppointments = appointmentManager.getAllAppointments();  	   		
   		List<Patient> allPatients = patientManager.getAllPatients();  		
   		List<Appointment> allPastAppointments = appointmentManager.getAllPastAppointments();
   		List<Appointment> allPendingAppointments = appointmentManager.getAllPendingAppointments();
   
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
   		int volunteerUserId = volunteerManager.getUserIdByVolunteerId(id);
   		
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
   		Appointment appointment = appointmentManager.getAppointmentById(id);
   		model.addAttribute("appointment", appointment);
   		
   		int patientId = appointment.getPatientID();
   		Patient p = patientManager.getPatientByID(patientId);
   		String alerts = p.getAlerts();
   		
   		if (!Utils.isNullOrEmpty(alerts))
   			model.addAttribute("alerts", alerts);
   		
   		int volunteer1Id = appointment.getVolunteerID();
   		int volunteer2Id = appointment.getPartnerId();
   		int appointmentId = appointment.getAppointmentID();
   		
   		List<Narrative> v1Narratives = volunteerManager.getNarrativesByVolunteer(volunteer1Id, patientId, appointmentId);
   		if (v1Narratives.size() > 0)
   			model.addAttribute("narratives1", v1Narratives);
   		
   		List<Narrative> v2Narratives = volunteerManager.getNarrativesByVolunteer(volunteer2Id, patientId, appointmentId); 
   		if (v2Narratives.size() > 0)
   			model.addAttribute("narratives2", v2Narratives);   	
   		   		
   		List<Activity> activities = volunteerManager.getActivities(patientId, appointmentId);   		   		   		   		
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
   			patients = patientManager.getPatientsForVolunteer(loggedInVolunteer);   			
   			
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
		Patient p = patientManager.getPatientByID(patientId);
		Appointment a = new Appointment();
				
		//check if selected time matches both volunteer's availability
		Volunteer volunteer1 = volunteerManager.getVolunteerById(p.getVolunteer());
		Volunteer volunteer2 = volunteerManager.getVolunteerById(p.getPartner());
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
			if (appointmentManager.createAppointment(a))
			{			
				String logginUser = request.getUserPrincipal().getName();	
				User user = Utils.getLoggedInUser(request);
				int userId = user.getUserID();	
				int volunteer1UserId = volunteerManager.getUserIdByVolunteerId(p.getVolunteer());	
				int volunteer2UserId = volunteerManager.getUserIdByVolunteerId(p.getPartner());							
										
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
					List<Integer> coordinators = userManager.getVolunteerCoordinatorByOrganizationId(organizationId);
					
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
		appointmentManager.deleteAppointment(id);
		if(request.isUserInRole("ROLE_USER")) {
			return "redirect:/";
		} else {
			return "redirect:/manage_appointments";
		}
	}
	
	@RequestMapping(value="/approve_appointment/{appointmentID}", method=RequestMethod.GET)
	public String approveAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request){
		appointmentManager.approveAppointment(id);
		return "redirect:/manage_appointments";
	}
	
	@RequestMapping(value="/decline_appointment/{appointmentID}", method=RequestMethod.GET)
	public String unapproveAppointment(@PathVariable("appointmentID") int id, SecurityContextHolderAwareRequestWrapper request){
		appointmentManager.declineAppointment(id);
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
		String vName = volunteerManager.getVolunteerNameById(volunteerId);
		String pName = volunteerManager.getVolunteerNameById(partnerId);
			
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

		Volunteer v1 = volunteerManager.getVolunteerById(Integer.parseInt(volunteerId1));
		Volunteer v2 = volunteerManager.getVolunteerById(Integer.parseInt(volunteerId2));
		
		Patient p = patientManager.getPatientByID(Integer.parseInt(patientId));
		
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
		if (appointmentManager.createAppointment(appointment))
		{
			Patient patient = patientManager.getPatientByID(patientId);	
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
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
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
		
		appointmentManager.addAlertsAndKeyObservations(appointmentId, alerts, keyObservations);
		
		int patientId = getPatientId(request);		
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);		
		
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
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		
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
				
		appointmentManager.addPlans(appointmentId, sb.toString());
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
		
		Appointment appt = appointmentManager.getAppointmentById(id);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		
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
		
		Appointment appointment = appointmentManager.getAppointmentById(id);
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		Patient patient = patientManager.getPatientByID(appointment.getPatientID());
		model.addAttribute("appointment", appointment);
		model.addAttribute("patient", patient);
		return "/volunteer/visit_complete";
	}
	
	@RequestMapping(value="/complete_visit/{appointment_id}", method=RequestMethod.POST)
	public String completeVisit(@PathVariable("appointment_id") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model) {
//		boolean contactedAdmin = request.getParameter("contacted_admin") != null;
		//set visit alert as comments in DB
		appointmentManager.completeAppointment(id, request.getParameter("visitAlerts"));
		
		Appointment appt = appointmentManager.getAppointmentById(id);
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
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
   		
   		int count = surveyManager.countSurveyTemplate();
   		List<SurveyResult> completedSurveys = surveyManager.getCompletedSurveysByPatientID(patientId);
   		
   		if (count == completedSurveys.size())
   			completed = true;
   		
   		return completed;
   	}
	
	private boolean isFirstVisit(int patientId){
		boolean isFirst = true;
		List<Appointment> appointments = appointmentManager.getAllApprovedAppointmentsForPatient(patientId);	
		
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
			messageManager.sendMessage(m);
	}
	
	private List<Patient> getPatients()
	{
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientManager.getAllPatients();		
		
		return patients;
	}
	
	private List<Volunteer> getAllVolunteers(){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		volunteers = volunteerManager.getVolunteersWithAvailability();		
		
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
		
		narratives = volunteerManager.getAllNarrativesByUser(loggedInVolunteerId);		

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
		
		MisUtils.setUnreadMsg(session, request, model, messageManager);
		
		model.addAttribute("narratives", narratives);	
		return "/volunteer/view_narrative";
	}
	
	//loading a existing narrative to view detail or make a change
	@RequestMapping(value="/modify_narrative/{narrativeId}", method=RequestMethod.GET)
	public String modifyNarrative(SecurityContextHolderAwareRequestWrapper request, 
				@PathVariable("narrativeId") int id, ModelMap model){		
		Narrative narrative = volunteerManager.getNarrativeById(id);			
		
		//set Date format for editDate
		String editDate = narrative.getEditDate();
		
		if(!Utils.isNullOrEmpty(editDate))
			editDate = editDate.substring(0,10);
		
		narrative.setEditDate(editDate);
		
		model.addAttribute("narrative", narrative);	
		
		MisUtils.setUnreadMsg(null, request, model, messageManager);
		
		return "/volunteer/modify_narrative";
	}
	
	@RequestMapping(value="/new_narrative", method=RequestMethod.GET)
	public String newNarrative(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		int appointmentId = getAppointmentId(request);
		
		Appointment appt = appointmentManager.getAppointmentById(appointmentId);
		
		int patientId = appt.getPatientID();
		Patient patient = patientManager.getPatientByID(patientId);
		
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
			
			narrative = volunteerManager.getNarrativeById(iNarrativeId);
						
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
			
			volunteerManager.updateNarrative(narrative);			
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
		volunteerManager.addNarrative(narrative);
		//set complete narrative in Appointment table in DB
		appointmentManager.completeNarrative(appointmentId);		
		
		HttpSession session = request.getSession();
		session.setAttribute("newNarrative", true);

		return "redirect:/open_alerts_keyObservations/" + appointmentId;
	}
	
	@RequestMapping(value="/delete_narrative/{narrativeId}", method=RequestMethod.GET)
	public String deleteNarrativeById(@PathVariable("narrativeId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		volunteerManager.deleteNarrativeById(id);
				
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
	

}
