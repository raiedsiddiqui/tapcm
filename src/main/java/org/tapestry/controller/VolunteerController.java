package org.tapestry.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tapestry.controller.utils.MisUtils;
import org.tapestry.dao.ActivityDAO;
import org.tapestry.dao.ActivityDAOImpl;
import org.tapestry.dao.AppointmentDAO;
import org.tapestry.dao.AppointmentDAOImpl;
import org.tapestry.dao.MessageDAO;
import org.tapestry.dao.MessageDAOImpl;
import org.tapestry.dao.UserDAO;
import org.tapestry.dao.UserDAOImpl;
import org.tapestry.dao.VolunteerDAO;
import org.tapestry.dao.VolunteerDAOImpl;
import org.tapestry.objects.Activity;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Message;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Organization;
import org.yaml.snakeyaml.Yaml;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;

@Controller
public class VolunteerController {
	
protected static Logger logger = Logger.getLogger(VolunteerController.class);
	
	private VolunteerDAO volunteerDao = getVolunteerDAO();
	private AppointmentDAO appointmentDao = getAppointmentDAO();
	private UserDAO userDao = getUserDAO();
	private ActivityDAO activityDao = getActivityDAO();
	private MessageDAO messageDao = getMessageDAO();
	
	private Properties props;
   	private String mailAddress = "";
   	private Session session;
   	
   	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	@PostConstruct
	public void readDatabaseConfig(){
		Utils.setDatabaseConfig();
		
		Properties props = System.getProperties();
	
		String mailHost = "";
   		String mailUser = "";
   		String mailPassword = "";
   		String mailPort = "";
   		String useTLS = "";
   		String useAuth = "";				
		
	
			
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
    
    public AppointmentDAO getAppointmentDAO(){    	
    	return new AppointmentDAOImpl(getDataSource());
    }
    public VolunteerDAO getVolunteerDAO(){
    	return new VolunteerDAOImpl(getDataSource());
    }
	
	//display all volunteers
	@RequestMapping(value="/view_volunteers", method=RequestMethod.GET)
	public String getAllVolunteers(SecurityContextHolderAwareRequestWrapper request, ModelMap model){		
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		HttpSession  session = request.getSession();	
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
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
		HttpSession  session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_volunteers";
	}
	
	@RequestMapping(value="/new_volunteer", method=RequestMethod.GET)
	public String newVolunteer(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		//save exist user names for validating on UI
		List<String> existUserNames = new ArrayList<String>();
		existUserNames = volunteerDao.getAllExistUsernames();
		model.addAttribute("existUserNames", existUserNames);
		
		List<Organization> organizations;
		HttpSession session = request.getSession();
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			organizations = volunteerDao.getAllOrganizations();
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
		List<Message> messages = messageDao.getAllMessagesForRecipient(Utils.getLoggedInUserId(request));
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
			if (!Utils.isNullOrEmpty(request.getParameter("organization")))
				volunteer.setOrganizationId(Integer.valueOf(request.getParameter("organization")));
								
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
		
		List<Organization> organizations;
		HttpSession session = request.getSession();
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			organizations = volunteerDao.getAllOrganizations();
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
		
		if (!Utils.isNullOrEmpty(request.getParameter("organization")))
			volunteer.setOrganizationId(Integer.valueOf(request.getParameter("organization")));
			
		String strAvailableTime = getAvailableTime(request);		
		
		volunteer.setAvailability(strAvailableTime);
			
		volunteerDao.updateVolunteer(volunteer);
			
		//update users table as well
		modifyUser(volunteer);
		
		session.setAttribute("volunteerMessage","U");
		return "redirect:/view_volunteers";
	
	}
	
	@RequestMapping(value="/delete_volunteer/{volunteerId}", method=RequestMethod.GET)
	public String deleteVolunteerById(@PathVariable("volunteerId") int id, 
				SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		
		volunteerDao.deleteVolunteerById(id);
				
		HttpSession  session = request.getSession();		
		session.setAttribute("volunteerMessage", "D");		
	
		return "redirect:/view_volunteers";
		
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
		user.setOrganization(volunteer.getOrganizationId());		

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
	
	//Activity in Volunteer
	//display all activity input by volunteers
	@RequestMapping(value="/view_activity_admin", method=RequestMethod.GET)
	public String viewActivityByAdmin( SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		List<Activity> activities = new ArrayList<Activity>();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();		
		User loggedInUser = Utils.getLoggedInUser(request);
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			activities = activityDao.getAllActivitiesForAdmin();
			volunteers = volunteerDao.getAllVolunteers();
		}
		else if (request.isUserInRole("ROLE_LOCAL_ADMIN"))
		{
			int organizationId = loggedInUser.getOrganization();
			activities = activityDao.getAllActivitiesForLocalAdmin(organizationId);
			volunteers = volunteerDao.getAllVolunteersByOrganization(organizationId);
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
			activities = activityDao.getAllActivitiesForVolunteer(Integer.parseInt(strVId));		
				
			if (activities.size() == 0 )  
				model.addAttribute("emptyActivityLogs", true);				
		}		
		List<Volunteer> volunteers = volunteerDao.getAllVolunteers();
			
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
		activities = activityDao.getAllActivitiesForVolunteer(volunteerId);
					
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
			
		Volunteer volunteer = volunteerDao.getVolunteerById(volunteerId);
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
			
		activityDao.logActivity(activity);
			
		//update view activity page with new record		
		session.setAttribute("ActivityMessage", "C");		
		
		return "redirect:/view_activity";
	}	
		
	@RequestMapping(value="/delete_activity/{activityId}", method=RequestMethod.GET)
	public String deleteActivityById(@PathVariable("activityId") int id, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
				
		activityDao.deleteActivityById(id);	
					
		HttpSession  session = request.getSession();		
		session.setAttribute("ActivityMessage", "D");		
		
		return "redirect:/view_activity";		
	}
		
	@RequestMapping(value="/modify_activity/{activityId}", method=RequestMethod.GET)
	public String modifyActivityLog(SecurityContextHolderAwareRequestWrapper request, 
			@PathVariable("activityId") int id, ModelMap model){
		Activity activity = new Activity();
		activity = activityDao.getActivityLogById(id);			
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
				
			activity = activityDao.getActivityLogById(iActivityId);
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
			
		activityDao.updateActivity(activity);
			
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
		
		organizations = volunteerDao.getAllOrganizations();		
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
			organizations = volunteerDao.getOrganizationsByName(name);			
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
			
		if (volunteerDao.addOrganization(organization))
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
		messageDao.sendMessage(m);
	}

}
