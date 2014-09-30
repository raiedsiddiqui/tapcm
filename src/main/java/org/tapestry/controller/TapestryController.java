package org.tapestry.controller;

import java.io.IOException;
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.tapestry.dao.ActivityDAO;
import org.tapestry.dao.ActivityDAOImpl;
import org.tapestry.dao.MessageDAO;
import org.tapestry.dao.MessageDAOImpl;
import org.tapestry.dao.PatientDAO;
import org.tapestry.dao.PatientDAOImpl;
import org.tapestry.dao.PictureDAO;
import org.tapestry.dao.PictureDAOImpl;
import org.tapestry.dao.UserDAO;
import org.tapestry.dao.UserDAOImpl;
import org.tapestry.dao.VolunteerDAO;
import org.tapestry.dao.VolunteerDAOImpl;
import org.tapestry.objects.Message;
import org.tapestry.objects.Organization;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Picture;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;
import org.tapestry.service.ActivityService;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;

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
//	@Autowired 
//	private ActivityService activityService;
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
		
	private UserDAO userDao =  getUserDAO();
   	private PatientDAO patientDao = getPatientDAO();   	
   	private MessageDAO messageDao = getMessageDAO();
   	private PictureDAO pictureDao = getPictureDAO();   
   	private VolunteerDAO volunteerDao = getVolunteerDAO();
   	private ActivityDAO activityDao = getActivityDAO();
 
   	//Mail-related settings;
   	private Properties props;
   	private String mailAddress = "";
   	private Session session;
   	
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
    
    public PictureDAO getPictureDAO()
    {
    	return new PictureDAOImpl(getDataSource());
    }
    public VolunteerDAO getVolunteerDAO(){
    	return new VolunteerDAOImpl(getDataSource());
    }
    
    public PatientDAO getPatientDAO(){
    	return new PatientDAOImpl(getDataSource());
    }
   	
   	//Everything below this point is a RequestMapping
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam(value="usernameChanged", required=false) Boolean usernameChanged, ModelMap model){
		System.out.println("hello Tapestry");
		if (usernameChanged != null)
			model.addAttribute("usernameChanged", usernameChanged);
		return "login";
	}
	

	@RequestMapping(value="/loginsuccess", method=RequestMethod.GET)
	public String loginSuccess(SecurityContextHolderAwareRequestWrapper request){				
		User u = getLoggedInUser(request);

		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" logged in");
//		activityService.createUserLog(sb.toString(), u);
		activityDao.addUserLog(sb.toString(), u);
		
		return "redirect:/";
	}

	@RequestMapping(value="/loginfailed", method=RequestMethod.GET)
	public String failed(ModelMap model){
		model.addAttribute("error", "true");
		return "login";
	}

	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(SecurityContextHolderAwareRequestWrapper request){
		User u = getLoggedInUser(request);
		
		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" logged out");
		activityDao.addUserLog(sb.toString(), u);

		return "confirm-logout";
	}
	
	@RequestMapping(value="/client", method=RequestMethod.GET)
	public String getClients(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		User loggedInUser = getLoggedInUser(request);
		//get volunteer Id from login user		
		int volunteerId= volunteerDao.getVolunteerIdByUsername(loggedInUser.getUsername());
		List<Patient> clients = patientDao.getPatientsForVolunteer(volunteerId);		
		model.addAttribute("clients", clients);
		
		setUnreadMessage(request, model);
		
		return "volunteer/client";
	}
	
	@RequestMapping(value="/manage_users", method=RequestMethod.GET)
	public String manageUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model,
			SecurityContextHolderAwareRequestWrapper request){
		setUnreadMessage(request, model);
		HttpSession session = request.getSession();
		
		List<User> userList = userDao.getAllUsers();
		model.addAttribute("users", userList);
//		model.addAttribute("active", userDao.countActiveUsers());
//		model.addAttribute("total", userDao.countAllUsers());
		if(failed != null) {
			model.addAttribute("failed", true);
		}
		
		List<Organization> organizations;
		
		if (session.getAttribute("organizations") != null)
			organizations = (List<Organization>) session.getAttribute("organizations");
		else 
		{
			organizations = volunteerDao.getAllOrganizations();
			session.setAttribute("organizations", organizations);
		}
		return "admin/manage_users";
	}
	
	
	@RequestMapping(value="/manage_users", method=RequestMethod.POST)
	public String searchOnUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model,
			SecurityContextHolderAwareRequestWrapper request)
	{			
		String name = request.getParameter("searchName");		
		List<User> userList = userDao.getUsersByPartialName(name);		
		model.addAttribute("users", userList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		
		model.addAttribute("searchName", name);
		
		setUnreadMessage(request, model);
		
		return "admin/manage_users";
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

		u.setUsername(request.getParameter("username").trim());
		u.setRole(request.getParameter("role"));		
		u.setOrganization(Integer.valueOf(request.getParameter("organization")));
		
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(request.getParameter("password"), null); 
	
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
	public String disableUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request){
		userDao.disableUserWithID(id);
		
		User u = userDao.getUserByID(id);
		User loggedInUser = getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" disable ");
		sb.append(u.getName());
		activityDao.addUserLog(sb.toString(), loggedInUser);		
		
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/enable_user/{user_id}", method=RequestMethod.GET)
	public String enableUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request){
		userDao.enableUserWithID(id);
		
		User u = userDao.getUserByID(id);
		User loggedInUser = getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" enable ");
		sb.append(u.getName());
		activityDao.addUserLog(sb.toString(), loggedInUser);		
		
		return "redirect:/manage_users";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String viewProfile(@RequestParam(value="error", required=false) String errorsPresent, @RequestParam(value="success", required=false) String success, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
	
		User loggedInUser = getLoggedInUser(request);
		model.addAttribute("vol", loggedInUser);
		setUnreadMessage(request, model);
		
		if (errorsPresent != null)
			model.addAttribute("errors", errorsPresent);
		if(success != null)
			model.addAttribute("success", true);
		List<Picture> pics = pictureDao.getPicturesForUser(loggedInUser.getUserID());
		model.addAttribute("pictures", pics);
		return "/volunteer/profile";
	}
	
	@RequestMapping(value="/inbox", method=RequestMethod.GET)
	public String viewInbox(@RequestParam(value="success", required=false) Boolean messageSent,@RequestParam(value="failure",
			required=false) Boolean messageFailed, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = getLoggedInUser(request);
		int userId = loggedInUser.getUserID();
		List<Message> messages;		
		
		if (messageSent != null)
			model.addAttribute("success", messageSent);
		if (messageFailed != null)
			model.addAttribute("failure", messageFailed);
		
		messages = messageDao.getAllMessagesForRecipient(userId);
		model.addAttribute("messages", messages);		
		setUnreadMessage(request, model);
			
		if (request.isUserInRole("ROLE_USER"))
		{
			List<User> administrators = userDao.getAllUsersWithRole("ROLE_ADMIN");
			model.addAttribute("administrators", administrators);
			
			return "/volunteer/inbox";
		} 
		else
		{			
			List<User> volunteers = userDao.getAllUsersWithRole("ROLE_USER");			
			model.addAttribute("volunteers", volunteers);
			
			return "/admin/inbox";
		}
	}
	
	@RequestMapping(value="/view_message/{msgID}", method=RequestMethod.GET)
	public String viewMessage(@PathVariable("msgID") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = getLoggedInUser(request);
		int userId = loggedInUser.getUserID();
		
		Message m;		
		m = messageDao.getMessageByID(id);		
		
		if (!(m.getRecipient() == userId))
			return "redirect:/403";
		
		if (!(m.isRead()))
			messageDao.markAsRead(id);
		model.addAttribute("message", m);
		setUnreadMessage(request, model);
		
		if (request.isUserInRole("ROLE_USER"))
			return "/volunteer/view_message";
		else
			return "/admin/view_message";
	}
	
	@RequestMapping(value="/dismiss/{announcement}", method=RequestMethod.GET)
	public String dismissAnnouncement(@PathVariable("announcement") int id, SecurityContextHolderAwareRequestWrapper request){	
		User loggedInUser = getLoggedInUser(request);
		int userId = loggedInUser.getUserID();
		Message m;
		
		m = messageDao.getMessageByID(id);		

		if (!(m.getRecipient() == userId))
			return "redirect:/403";
		
		messageDao.markAsRead(id);
		
		return "redirect:/";
	}

	@RequestMapping(value="/send_message", method=RequestMethod.POST)
	public String sendMessage(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		User loggedInUser = getLoggedInUser(request);
		Message m = new Message();
		
		m.setSender(loggedInUser.getName());
		m.setSenderID(loggedInUser.getUserID());
		m.setText(request.getParameter("msgBody"));
		
		if (request.getParameter("isAnnouncement") != null && request.getParameter("isAnnouncement").equals("true")){ //Sound to all volunteers
			List<User> volunteers = userDao.getAllUsersWithRole("ROLE_USER");
			
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
	public String deleteMessage(SecurityContextHolderAwareRequestWrapper request, 
			@RequestParam(value="isRead", required=true) boolean isRead, @PathVariable("msgID") int id, ModelMap model)
	{//if an unread message is deleted, unread indicator should be modified				
		if (!isRead){
			HttpSession session = request.getSession();
			if (session.getAttribute("unread_messages") != null)
			{				
				int iUnRead = Integer.parseInt(session.getAttribute("unread_messages").toString());
				iUnRead = iUnRead - 1;
				
				session.setAttribute("unread_messages", iUnRead);
				model.addAttribute("unread", iUnRead);
			}
		}
			
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

		User loggedInUser = Utils.getLoggedInUser(request);
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
		
		User whosePwdChanged = userDao.getUserByID(userID);
		User loggedInUser = getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" Changed password for ");
		sb.append(whosePwdChanged.getName());
		activityDao.addUserLog(sb.toString(), loggedInUser);		
	
		return target;
	}
	
	@RequestMapping(value="/remove_picture/{id}", method=RequestMethod.GET)
	public String removePicture(@PathVariable("id") int pictureID){
		pictureDao.removePicture(pictureID);
		return "redirect:/profile";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.GET)
	public String viewUserLogs(@PathVariable("page") int page, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<UserLog> logs = activityDao.getUserLogsPage((page - 1) * 20, 20);
		int count = activityDao.countEntries();
		
		model.addAttribute("numPages", count / 20 + 1);
		model.addAttribute("logs", logs);
		
		setUnreadMessage(request, model);
		
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.POST)
	public String viewFilteredUserLogs(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<UserLog> logs = new ArrayList<UserLog>();
		String name = request.getParameter("name");
		
		logs = activityDao.getUserLogsByPartialName(name);

		model.addAttribute("logs", logs);
		
		setUnreadMessage(request, model);
		
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/upload_picture_to_profile", method=RequestMethod.POST)
	public String uploadPicture(MultipartHttpServletRequest request){

		User loggedInUser = Utils.getLoggedInUser(request);
		MultipartFile pic = request.getFile("pic");
		
		pictureDao.uploadPicture(pic, loggedInUser.getUserID(), true);				

		activityDao.addUserLog(loggedInUser.getName() +" uploaded picture for profile", loggedInUser);
		
		return "redirect:/profile";
	}

	@RequestMapping(value="/upload_picture_for_patient/{patientID}", method=RequestMethod.POST)
	public String uploadPicture(@PathVariable("patientID") int id, MultipartHttpServletRequest request){
		User loggedInUser = Utils.getLoggedInUser(request);

		MultipartFile pic = request.getFile("pic");
		
		Patient p = patientDao.getPatientByID(id);
		pictureDao.uploadPicture(pic, id, false);

		activityDao.addUserLog(loggedInUser.getName() + " uploaded picture for " + p.getDisplayName(), loggedInUser);
		
		return "redirect:/patient/" + id;
	}
		
	private void setUnreadMessage(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession session = request.getSession();
		User loggedInUser = Utils.getLoggedInUser(request);
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}
	}
	
	private User getLoggedInUser(SecurityContextHolderAwareRequestWrapper request){
		HttpSession session = request.getSession();
		String name = null;		

		User loggedInUser = null;
		//check if loggedInUserId is in the session
		if (session.getAttribute("loggedInUser") != null) //get loggedInUser from session			
			loggedInUser = (User)session.getAttribute("loggedInUser");		
		else if (request.getUserPrincipal() != null){		
			//get loggedInUser from request
			name = request.getUserPrincipal().getName();	
					
			if (name != null){			
				loggedInUser = userDao.getUserByUsername(name);								
				session.setAttribute("loggedInUser", loggedInUser);	
			}
		}
		
		return loggedInUser;
	}
	
}
