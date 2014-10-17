package org.tapestry.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.utils.MisUtils;
import org.tapestry.utils.Utils;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.objects.Message;
import org.tapestry.objects.Organization;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Picture;
import org.tapestry.objects.Report;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;
import org.tapestry.objects.Volunteer;
import org.tapestry.report.AlertManager;
import org.tapestry.report.AlertsInReport;
import org.tapestry.report.CalculationManager;
import org.tapestry.report.ScoresInReport;
import org.tapestry.service.AppointmentManager;
import org.tapestry.service.MessageManager;
import org.tapestry.service.PatientManager;
import org.tapestry.service.PictureManager;
import org.tapestry.service.SurveyManager;
import org.tapestry.service.UserManager;
import org.tapestry.service.VolunteerManager;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.ResultParser;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.surveys.TapestryPHRSurvey;
import org.tapestry.surveys.TapestrySurveyMap;
import org.yaml.snakeyaml.Yaml;

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
   	@Autowired
   	private UserManager userManager;
   	@Autowired
	private MessageManager messageManager;
   	@Autowired
   	private PictureManager pictureManager;
   	@Autowired
   	private PatientManager patientManager;
   	@Autowired
	private VolunteerManager volunteerManager;
   	@Autowired
   	private SurveyManager surveyManager;
   	@Autowired 
   	private AppointmentManager appointmentManager;
//   	@Autowired 
//   	private Report report;
//   	@Autowired
//   	private ScoresInReport scores;
   	//Mail-related settings;
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
  
    
   	//Everything below this point is a RequestMapping
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(@RequestParam(value="usernameChanged", required=false) Boolean usernameChanged, ModelMap model){		
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
		
		userManager.addUserLog(sb.toString(), u);
		
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
		userManager.addUserLog(sb.toString(), u);

		return "confirm-logout";
	}
	
	@RequestMapping(value="/client", method=RequestMethod.GET)
	public String getClients(SecurityContextHolderAwareRequestWrapper request, ModelMap model){	
		User loggedInUser = getLoggedInUser(request);
		//get volunteer Id from login user		
		int volunteerId= volunteerManager.getVolunteerIdByUsername(loggedInUser.getUsername());
		List<Patient> clients = patientManager.getPatientsForVolunteer(volunteerId);		
		model.addAttribute("clients", clients);
		
		setUnreadMessage(request, model);
		
		return "volunteer/client";
	}
	
	@RequestMapping(value="/manage_users", method=RequestMethod.GET)
	public String manageUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model,
			SecurityContextHolderAwareRequestWrapper request){
		setUnreadMessage(request, model);
		HttpSession session = request.getSession();
		
		List<User> userList = userManager.getAllUsers();
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
			organizations = volunteerManager.getAllOrganizations();
			session.setAttribute("organizations", organizations);
		}
		return "admin/manage_users";
	}
	
	
	@RequestMapping(value="/manage_users", method=RequestMethod.POST)
	public String searchOnUsers(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model,
			SecurityContextHolderAwareRequestWrapper request)
	{			
		String name = request.getParameter("searchName");		
		List<User> userList = userManager.getUsersByPartialName(name);		
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
		
		boolean success = userManager.createUser(u);
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
		userManager.removeUserWithID(id);
		return "redirect:/manage_users";
	}
	
	@RequestMapping(value="/disable_user/{user_id}", method=RequestMethod.GET)
	public String disableUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request){
		userManager.disableUserWithID(id);
		
		User u = userManager.getUserByID(id);
		User loggedInUser = getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" disable ");
		sb.append(u.getName());
		userManager.addUserLog(sb.toString(), loggedInUser);		
		
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/enable_user/{user_id}", method=RequestMethod.GET)
	public String enableUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request){
		userManager.enableUserWithID(id);
		
		User u = userManager.getUserByID(id);
		User loggedInUser = getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" enable ");
		sb.append(u.getName());
		userManager.addUserLog(sb.toString(), loggedInUser);		
		
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
		List<Picture> pics = pictureManager.getPicturesForUser(loggedInUser.getUserID());
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
		
		messages = messageManager.getAllMessagesForRecipient(userId);
		model.addAttribute("messages", messages);		
		setUnreadMessage(request, model);
			
		if (request.isUserInRole("ROLE_USER"))
		{
			List<User> administrators = userManager.getAllUsersWithRole("ROLE_ADMIN");
			model.addAttribute("administrators", administrators);
			
			return "/volunteer/inbox";
		} 
		else
		{			
			List<User> volunteers = userManager.getAllUsersWithRole("ROLE_USER");			
			model.addAttribute("volunteers", volunteers);
			
			return "/admin/inbox";
		}
	}
	
	@RequestMapping(value="/view_message/{msgID}", method=RequestMethod.GET)
	public String viewMessage(@PathVariable("msgID") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = getLoggedInUser(request);
		int userId = loggedInUser.getUserID();
		
		Message m;		
		m = messageManager.getMessageByID(id);		
		
		if (!(m.getRecipient() == userId))
			return "redirect:/403";
		
		if (!(m.isRead()))
			messageManager.markAsRead(id);
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
		
		m = messageManager.getMessageByID(id);		

		if (!(m.getRecipient() == userId))
			return "redirect:/403";
		
		messageManager.markAsRead(id);
		
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
			List<User> volunteers = userManager.getAllUsersWithRole("ROLE_USER");
			
			for (User u: volunteers){
				m.setSubject("ANNOUNCEMENT: " + request.getParameter("msgSubject"));
				m.setRecipient(u.getUserID());
				
				User recipient = userManager.getUserByID(u.getUserID());
				
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
				messageManager.sendMessage(m);
			}
		}
		else{ //Send to one person			
			String[] recipients = request.getParameterValues("recipient");
			if(recipients != null) {
				for (String recipientIDAsString: recipients){ //Annoyingly, the request comes as strings
					int recipientID = Integer.parseInt(recipientIDAsString);
					m.setRecipient(recipientID);
					m.setSubject(request.getParameter("msgSubject"));
					messageManager.sendMessage(m);
					
					User recipient = userManager.getUserByID(recipientID);
					
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
			
		messageManager.deleteMessage(id);
		return "redirect:/inbox";
	}
	
	@RequestMapping(value="/reply_to/{msgID}", method=RequestMethod.POST)
	public String replyToMessage(@PathVariable("msgID") int id, ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		Message oldMsg = messageManager.getMessageByID(id);
		Message newMsg = new Message();
		//Reverse sender and recipient
		
		User recipient = userManager.getUserByID(oldMsg.getSenderID());
		int newRecipient = userManager.getUserByID(oldMsg.getRecipient()).getUserID();		
		
		newMsg.setSenderID(newRecipient);
		newMsg.setRecipient(oldMsg.getSenderID());
		newMsg.setText(request.getParameter("msgBody"));
		newMsg.setSubject("RE: " + oldMsg.getSubject());
		
		messageManager.sendMessage(newMsg);
		
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
		userManager.modifyUser(u);
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
			if (!userManager.userHasPassword(userID, currentPassword)){
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
		userManager.setPasswordForUser(userID, hashedPassword);
		
		User whosePwdChanged = userManager.getUserByID(userID);
		User loggedInUser = getLoggedInUser(request);
		StringBuffer sb = new StringBuffer();
		sb.append(loggedInUser.getName());
		sb.append(" Changed password for ");
		sb.append(whosePwdChanged.getName());
		userManager.addUserLog(sb.toString(), loggedInUser);		
	
		return target;
	}
	
	@RequestMapping(value="/remove_picture/{id}", method=RequestMethod.GET)
	public String removePicture(@PathVariable("id") int pictureID){
		pictureManager.removePicture(pictureID);
		return "redirect:/profile";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.GET)
	public String viewUserLogs(@PathVariable("page") int page, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<UserLog> logs = userManager.getUserLogs((page - 1) * 20, 20);
		int count = userManager.count();
		
		model.addAttribute("numPages", count / 20 + 1);
		model.addAttribute("logs", logs);
		
		setUnreadMessage(request, model);
		
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/user_logs/{page}", method=RequestMethod.POST)
	public String viewFilteredUserLogs(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<UserLog> logs = new ArrayList<UserLog>();
		String name = request.getParameter("name");
		
		logs = userManager.getUserLogsByPartialName(name);

		model.addAttribute("logs", logs);
		
		setUnreadMessage(request, model);
		
		return "/admin/user_logs";
	}
	
	@RequestMapping(value="/upload_picture_to_profile", method=RequestMethod.POST)
	public String uploadPicture(MultipartHttpServletRequest request){

		User loggedInUser = Utils.getLoggedInUser(request);
		MultipartFile pic = request.getFile("pic");
		
		pictureManager.uploadPicture(pic, loggedInUser.getUserID(), true);				

		userManager.addUserLog(loggedInUser.getName() +" uploaded picture for profile", loggedInUser);
		
		return "redirect:/profile";
	}

	@RequestMapping(value="/upload_picture_for_patient/{patientID}", method=RequestMethod.POST)
	public String uploadPicture(@PathVariable("patientID") int id, MultipartHttpServletRequest request){
		User loggedInUser = Utils.getLoggedInUser(request);

		MultipartFile pic = request.getFile("pic");
		
		Patient p = patientManager.getPatientByID(id);
		pictureManager.uploadPicture(pic, id, false);

		userManager.addUserLog(loggedInUser.getName() + " uploaded picture for " + p.getDisplayName(), loggedInUser);
		
		return "redirect:/patient/" + id;
	}
		
	private void setUnreadMessage(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession session = request.getSession();
		User loggedInUser = Utils.getLoggedInUser(request);
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
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
				loggedInUser = userManager.getUserByUsername(name);								
				session.setAttribute("loggedInUser", loggedInUser);	
			}
		}
		
		return loggedInUser;
	}
	
	//===================== Client(patient)  =============================//
   	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(ModelMap model, SecurityContextHolderAwareRequestWrapper request){
   		User loggedInUser = Utils.getLoggedInUser(request);
   		HttpSession session = request.getSession();
   		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			int unreadMessages = messageManager.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}	
		loadPatientsAndVolunteers(model);

		return "admin/manage_patients";
	}
   	
   	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception{
		//Add a new patient
		Patient p = new Patient();
		
		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		Volunteer v1 = volunteerManager.getVolunteerById(vId1);
		Volunteer v2 = volunteerManager.getVolunteerById(vId2);
		
		if (Utils.isMatchVolunteer(v1, v2))
		{
			p.setFirstName(request.getParameter("firstname").trim());
			p.setLastName(request.getParameter("lastname").trim());
			if(request.getParameter("preferredname") != "") {
				p.setPreferredName(request.getParameter("preferredname").trim());
			}		
			p.setVolunteer(vId1);
			p.setPartner(vId2);		
			p.setMyoscarVerified(request.getParameter("myoscar_verified"));		
			p.setGender(request.getParameter("gender"));
			p.setNotes(request.getParameter("notes"));
			p.setAlerts(request.getParameter("alerts"));
			p.setClinic(request.getParameter("clinic"));
			
			patientManager.createPatient(p);
			
			Patient newPatient = patientManager.getNewestPatient();
			
			//Auto assign all existing surveys
			List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
	   		List<SurveyTemplate> surveyTemplates = surveyManager.getAllSurveyTemplates();
	   		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
	   		
	   		for(SurveyTemplate st: surveyTemplates) 
	   		{
				List<TapestryPHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID()));
				
				SurveyFactory surveyFactory = new SurveyFactory();
				TapestryPHRSurvey template = surveyFactory.getSurveyTemplate(st);
					SurveyResult sr = new SurveyResult();
		            sr.setSurveyID(st.getSurveyID());
		            sr.setPatientID(newPatient.getPatientID());
		            
		            //set today as startDate
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		        	String startDate = sdf.format(new Date());            
		            
		            sr.setStartDate(startDate);
		          //if requested survey that's already done
		    		if (specificSurveys.size() < template.getMaxInstances())
		    		{
		    			TapestryPHRSurvey blankSurvey = template;
		    			blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
		    			sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
		    			String documentId = surveyManager.assignSurvey(sr);
		    			blankSurvey.setDocumentId(documentId);
		    			surveys.addSurvey(blankSurvey);
		    			specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
		    		}
		    		else
		    		{
		    			return "redirect:/manage_patients";
		    		}
			}
	   		model.addAttribute("createPatientSuccessfully",true);
	   		loadPatientsAndVolunteers(model);
	   		
	        return "admin/manage_patients";
		}
		else
		{			
			model.addAttribute("misMatchedVolunteer",true);
			loadPatientsAndVolunteers(model);
			
			return "admin/manage_patients";
		}
	}
   	
   	@RequestMapping(value="/edit_patient/{id}", method=RequestMethod.GET)
	public String editPatientForm(@PathVariable("id") int patientID,SecurityContextHolderAwareRequestWrapper request, ModelMap model){   		
		Patient p = patientManager.getPatientByID(patientID);		
		if("Male".equalsIgnoreCase(p.getGender()))
			p.setGender("M");
		else if ("Female".equalsIgnoreCase(p.getGender()))
			p.setGender("F");
		else 
			p.setGender("O");
		
		model.addAttribute("patient", p);
		
		List<Volunteer> volunteers = volunteerManager.getAllVolunteers();			
		model.addAttribute("volunteers", volunteers);	
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/edit_patient"; //Why this one requires a slash when none of the others do, I do not know.
	}
   	@RequestMapping(value="/submit_edit_patient/{id}", method=RequestMethod.POST)
	public String modifyPatient(@PathVariable("id") int patientID, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
   		int vId1 = Integer.parseInt(request.getParameter("volunteer1"));
		int vId2 = Integer.parseInt(request.getParameter("volunteer2"));
		Volunteer v1 = volunteerManager.getVolunteerById(vId1);
		Volunteer v2 = volunteerManager.getVolunteerById(vId2);
		
		if (Utils.isMatchVolunteer(v1, v2)){
			Patient p = new Patient();
			p.setPatientID(patientID);
			p.setFirstName(request.getParameter("firstname"));
			p.setLastName(request.getParameter("lastname"));
			p.setPreferredName(request.getParameter("preferredname"));		
			p.setVolunteer(vId1);
			p.setPartner(vId2);
			p.setGender(request.getParameter("gender"));
			p.setNotes(request.getParameter("notes"));
			p.setClinic(request.getParameter("clinic"));
			p.setAlerts(request.getParameter("alerts"));
			p.setMyoscarVerified(request.getParameter("myoscar_verified"));
			
			patientManager.updatePatient(p);
			model.addAttribute("updatePatientSuccessfully",true);
		}
		else
			model.addAttribute("misMatchedVolunteer",true);		
		
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
        loadPatientsAndVolunteers(model);
        
		return "/admin/manage_patients";
	}
   	
   	@RequestMapping(value="/remove_patient/{patient_id}", method=RequestMethod.GET)
	public String removePatient(@PathVariable("patient_id") int id){
   		patientManager.deletePatientWithId(id);
		return "redirect:/manage_patients";
	}

	@RequestMapping(value="/patient/{patient_id}", method=RequestMethod.GET)
	public String viewPatient(@PathVariable("patient_id") int id, @RequestParam(value="complete", required=false)
					String completedSurvey, @RequestParam(value="aborted", required=false) String inProgressSurvey, 
					@RequestParam(value="appointmentId", required=false) Integer appointmentId, 
					SecurityContextHolderAwareRequestWrapper request, ModelMap model){
				
		Patient patient = patientManager.getPatientByID(id);
		//Find the name of the current user
		User u = Utils.getLoggedInUser(request);
		HttpSession session = request.getSession();
		
		int volunteerId =0;
		if (session.getAttribute("logged_in_volunteer") != null)
			volunteerId = Integer.parseInt(session.getAttribute("logged_in_volunteer").toString());
		
		//Make sure that the user is actually responsible for the patient in question
		int volunteerForPatient = patient.getVolunteer();
		if (!(volunteerId == patient.getVolunteer()) && !(volunteerId == patient.getPartner()))
		{
			String loggedInUser = u.getName();
			model.addAttribute("loggedIn", loggedInUser);
			model.addAttribute("patientOwner", volunteerForPatient);
			return "redirect:/403";
		}
		model.addAttribute("patient", patient);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));		

		List<SurveyResult> completedSurveyResultList = surveyManager.getCompletedSurveysByPatientID(id);
		List<SurveyResult> incompleteSurveyResultList = surveyManager.getIncompleteSurveysByPatientID(id);
		Collections.sort(completedSurveyResultList);
		Collections.sort(incompleteSurveyResultList);
		List<SurveyTemplate> surveyList = surveyManager.getAllSurveyTemplates();		
	
		List<Patient> patientsForUser = patientManager.getPatientsForVolunteer(volunteerId);						
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		
		model.addAttribute("appointment", appointment);
		model.addAttribute("patients", patientsForUser);
		model.addAttribute("completedSurveys", completedSurveyResultList);
		model.addAttribute("inProgressSurveys", incompleteSurveyResultList);
		model.addAttribute("surveys", surveyList);
//		ArrayList<Picture> pics = pictureDao.getPicturesForPatient(id);
//		model.addAttribute("pictures", pics);
		
		//user logs
		StringBuffer sb = new StringBuffer();
		sb.append(u.getName());
		sb.append(" viewing patient: ");
		
		if(patient.getPreferredName() != null && patient.getPreferredName() != "")
			sb.append(patient.getPreferredName());
		else 
			sb.append(patient.getDisplayName());
		
		userManager.addUserLog(sb.toString(), u);					
		//save selected appointmentId in the session for other screen, like narrative		
		session.setAttribute("appointmentId", appointmentId);
		session.setAttribute("patientId", id);
				
		return "/patient";
	}
	
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.GET)
	public String viewPatientsFromAdmin(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		HttpSession session = request.getSession();
		
		List<Patient> patients = MisUtils.getAllPatientsWithFullInfos(patientManager, request);
		model.addAttribute("patients", patients);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "/admin/view_clients";
	}
	
	//display all patients by search name
	@RequestMapping(value="/view_clients_admin", method=RequestMethod.POST)
	public String viewPatientsBySelectedName(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		String name = request.getParameter("searchName");
		HttpSession session = request.getSession();
		List<Patient> patients = new ArrayList<Patient>();
		
		patients = patientManager.getPatientsByPartialName(name);			
		model.addAttribute("searchName", name);	 
		model.addAttribute("patients", patients);
		
		if (session.getAttribute("unread_messages") != null)		
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "/admin/view_clients";		
	}
	
	@RequestMapping(value="/display_client/{patient_id}",method=RequestMethod.GET)
	public String displayPatientDetails(@PathVariable("patient_id") int id, SecurityContextHolderAwareRequestWrapper
			request, ModelMap model){
		Patient patient = new Patient();
		List<Patient> patients  = MisUtils.getAllPatientsWithFullInfos(patientManager, request);
		for (Patient p: patients)
		{
			if (id == p.getPatientID())
			{
				patient = p;
				break;
			}
		}				
		model.addAttribute("patient", patient);
		
		int totalSurveys = surveyManager.countSurveyTemplate();
		int totalCompletedSurveys = surveyManager.countCompletedSurveys(id);
		
		if (totalSurveys == totalCompletedSurveys)
			model.addAttribute("showReport", true);

		String volunteer1Name = volunteerManager.getVolunteerById(patient.getVolunteer()).getDisplayName();
		String volunteer2Name = volunteerManager.getVolunteerById(patient.getPartner()).getDisplayName();
		
		model.addAttribute("volunteer1", volunteer1Name);
		model.addAttribute("volunteer2", volunteer2Name);
				
		List<Appointment> appointments = new ArrayList<Appointment>();
		appointments = appointmentManager.getAllUpcommingAppointmentForPatient(id);
				
		model.addAttribute("upcomingVisits", appointments);
		
		appointments = new ArrayList<Appointment>();		
		appointments = appointmentManager.getAllCompletedAppointmentsForPatient(id);
		model.addAttribute("completedVisits", appointments);
		
		List<SurveyResult> surveys = surveyManager.getSurveysByPatientID(id);
		model.addAttribute("surveys", surveys);
		
		HttpSession session = request.getSession();				
 		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));				
		return "/admin/display_client";
	}
	
	private List<Patient> getAllPatients(){
		List<Patient> patients = new ArrayList<Patient>();
		patients = patientManager.getAllPatients();
		
		return patients;
	}
	
	private void loadPatientsAndVolunteers(ModelMap model){
		List<Volunteer> volunteers = volunteerManager.getAllVolunteers();		
		model.addAttribute("volunteers", volunteers);
		
	    List<Patient> patientList = getAllPatients();
        model.addAttribute("patients", patientList);
	}
	
	//============report======================
	@RequestMapping(value="/downlad_report/{patientID}", method=RequestMethod.GET)
	public String downladReport(@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletResponse response){
	
		Patient patient = patientManager.getPatientByID(id);
		//call web service to get patient info from myoscar
		String userName = "carolchou.test";
		try{
			PersonTransfer3 personInMyoscar = ClientManager.getClientByUsername(userName);
			StringBuffer sb = new StringBuffer();
			if (personInMyoscar.getStreetAddress1() != null)
				sb.append(personInMyoscar.getStreetAddress1());
			String city = personInMyoscar.getCity();
			if (city != null)
			{
				sb.append(", ");
				sb.append(city);
				patient.setCity(city);
			}
			
			if (personInMyoscar.getProvince() != null)
			{
				sb.append(", ");
				sb.append(personInMyoscar.getProvince());
			}
			
			patient.setAddress(sb.toString());
			
			if (personInMyoscar.getBirthDate() != null)
			{
				Calendar birthDay = personInMyoscar.getBirthDate();
				patient.setBod(Utils.getDateByCalendar(birthDay));
			}
			
		} catch (Exception e){
			System.err.println("Have some problems when calling myoscar web service");
			
		}
				
		Appointment appointment = appointmentManager.getAppointmentById(appointmentId);
		Report report = new Report();		
		ScoresInReport scores = new ScoresInReport();
		    	
		report.setPatient(patient);
		
		//Plan and Key Observations
		String keyObservation = appointmentManager.getKeyObservationByAppointmentId(appointmentId);
		String plan = appointmentManager.getPlanByAppointmentId(appointmentId);
		appointment.setKeyObservation(keyObservation);
		appointment.setPlans(plan);
		
		report.setAppointment(appointment);

		//Survey---  goals setting
		List<SurveyResult> surveyResultList = surveyManager.getCompletedSurveysByPatientID(id);
		SurveyResult healthGoalsSurvey = new SurveyResult();
		SurveyResult dailyLifeActivitySurvey = new SurveyResult();	
		SurveyResult nutritionSurvey = new SurveyResult();
		SurveyResult rAPASurvey = new SurveyResult();
		SurveyResult mobilitySurvey = new SurveyResult();
		SurveyResult socialLifeSurvey = new SurveyResult();
		SurveyResult generalHealthySurvey = new SurveyResult();
		SurveyResult memorySurvey = new SurveyResult();
		SurveyResult carePlanSurvey = new SurveyResult();
		SurveyResult goals = new SurveyResult();		
		
		for(SurveyResult survey: surveyResultList){			
			String title = survey.getSurveyTitle();
			
			if (title.equalsIgnoreCase("Goal Setting"))//Goal Setting survey
				healthGoalsSurvey = survey;
			
			if (title.equalsIgnoreCase("Daily Life Activities"))//Daily life activity survey
				dailyLifeActivitySurvey = survey;
			
			if (title.equalsIgnoreCase("Screen II"))//Nutrition
				nutritionSurvey = survey;
			
			if (title.equalsIgnoreCase("Rapid Assessment of Physical Activity"))//RAPA survey
				rAPASurvey = survey;
			
			if (title.equalsIgnoreCase("Mobility Survey"))//Mobility survey
				mobilitySurvey = survey;
			
			if (title.equalsIgnoreCase("Social Life")) //Social Life(Duke Index of Social Support)
				socialLifeSurvey = survey;
			
			if (title.equalsIgnoreCase("General Health")) //General Health(Edmonton Frail Scale)
				generalHealthySurvey = survey;
			
			if (title.equalsIgnoreCase("Memory")) //Memory Survey
				memorySurvey = survey;
			
			if (title.equalsIgnoreCase("Advance_Directives")) //Care Plan/Advanced_Directive survey
				carePlanSurvey = survey;
			
			if (title.equalsIgnoreCase("GAS"))
				goals = survey;				
		}
		
		String xml;
		//Healthy Goals
   		try{
   			xml = new String(healthGoalsSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mHealthGoalsSurvey = ResultParser.getResults(xml);
   		List<String> qList = new ArrayList<String>();
   		List<String> questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		//get answer list
		qList = MisUtils.getQuestionList(mHealthGoalsSurvey);   
		
   		Map<String, String> sMap = new TreeMap<String, String>();
   		sMap = MisUtils.getSurveyContentMap(questionTextList, qList);
   		
  		report.setHealthGoals(sMap);   		
   		//Additional Information
  		//Memory
   		try{
   			xml = new String(memorySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mMemorySurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		   		
   		//only keep the second and forth question text in the list
   		if ((questionTextList != null) && (questionTextList.size() > 0))
   		{
   			List<String> displayQuestionTextList = new ArrayList<String>();
   	   		displayQuestionTextList.add(MisUtils.removeObserverNotes(questionTextList.get(1)));
   	   		displayQuestionTextList.add(MisUtils.removeObserverNotes(questionTextList.get(3)));
   	   		
   	   		displayQuestionTextList = MisUtils.removeRedundantFromQuestionText(displayQuestionTextList, "of 2");
   	   	
   	   		//get answer list
   			qList = MisUtils.getQuestionListForMemorySurvey(mMemorySurvey);   
   			sMap = new TreeMap<String, String>(); 	
   	   		
   	   		//Care Plan/Advanced_Directive
   	   		try{
   	   			xml = new String(carePlanSurvey.getResults(), "UTF-8");
   	   		} catch (Exception e) {
   	   			xml = "";
   	   		}
   	   		
   	   		LinkedHashMap<String, String> mCarePlanSurvey = ResultParser.getResults(xml);

   	   		questionTextList = new ArrayList<String>();
   	   		questionTextList = ResultParser.getSurveyQuestions(xml);   	   		
   	   		
   	   		//take 3 question text from the list
   	   		if ((questionTextList != null)&&(questionTextList.size() > 0))
   	   		{
	   	   		for (int i = 1; i <= 3; i++)
	   	   			displayQuestionTextList.add(MisUtils.removeObserverNotes(questionTextList.get(i)));
	   	   		
	   	   		displayQuestionTextList = MisUtils.removeRedundantFromQuestionText(displayQuestionTextList, "of 3");
	   	   		
	   	   		//get answer list   		
	   	   		qList.addAll(MisUtils.getQuestionList(mCarePlanSurvey));   	
	   	   		
	   	   		sMap = MisUtils.getSurveyContentMapForMemorySurvey(displayQuestionTextList, qList);
	   	   		report.setAdditionalInfos(sMap);
   	   		}   
   		}
   			
   		//Daily Life Activities
   		try{
   			xml = new String(dailyLifeActivitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mDailyLifeActivitySurvey = ResultParser.getResults(xml);
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);   		
   		   		
   		qList = new ArrayList<String>();
   		qList = MisUtils.getQuestionList(mDailyLifeActivitySurvey);
   		
   		//last question in Daily life activity survey is about falling stuff
   		List<String> lAlert = new ArrayList<String>();
   		String fallingQA = qList.get(qList.size() -1);
   		if (fallingQA.startsWith("yes")||fallingQA.startsWith("Yes"))
   			lAlert.add(AlertsInReport.DAILY_ACTIVITY_ALERT);   		
   		   		
   		sMap = new TreeMap<String, String>();
   		sMap = MisUtils.getSurveyContentMap(questionTextList, qList);
   		
   		report.setDailyActivities(sMap);   		
   		
 		//General Healthy Alert
   		try{
   			xml = new String(generalHealthySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mGeneralHealthySurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = MisUtils.getQuestionList(mGeneralHealthySurvey);
		
		//get score info for Summary of tapestry tools
		if ((qList != null)&&(qList.size()>10))
		{
			if ("1".equals(qList.get(0))) 
				scores.setClockDrawingTest("No errors");
			else if ("2".equals(qList.get(0))) 
				scores.setClockDrawingTest("Minor spacing errors");
			else if ("3".equals(qList.get(0))) 
				scores.setClockDrawingTest("Other errors");
			else 
				scores.setClockDrawingTest("Not done");
			
			if ("1".equals(qList.get(10))) 
				scores.setTimeUpGoTest("1 (0-10s)");
			else if ("2".equals(qList.get(10))) 
				scores.setTimeUpGoTest("2 (11-20s)");
			else if ("3".equals(qList.get(10))) 
				scores.setTimeUpGoTest("3 (More than 20s)");
			else if ("4".equals(qList.get(10))) 
				scores.setTimeUpGoTest("4 (Patient required assistance)");
			else 
				scores.setTimeUpGoTest("5 (Patient is unwilling)");
		}
		
		int generalHealthyScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getGeneralHealthyAlerts(generalHealthyScore, lAlert);
		
		if (generalHealthyScore < 5)
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Robust)");
		else if (generalHealthyScore < 7)
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Apparently Vulnerable)");
		else
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Frail)");		
	
		//Social Life Alert
		try{
   			xml = new String(socialLifeSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mSocialLifeSurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = MisUtils.getQuestionList(mSocialLifeSurvey);
		
		int socialLifeScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getSocialLifeAlerts(socialLifeScore, lAlert);
		
		//summary tools for social supports
		if ((qList != null)&&(qList.size()>0))
		{
			int satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));
			scores.setSocialSatisfication(satisfactionScore);
			int networkScore = CalculationManager.getScoreByQuestionsList(qList.subList(6, 10));
	   		scores.setSocialNetwork(networkScore);
		}
   		   		
   		//Nutrition Alerts   		
   		try{
   			xml = new String(nutritionSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mNutritionSurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = MisUtils.getQuestionList(mNutritionSurvey);  

		//get scores for nutrition survey based on answer list
		if ((qList != null)&&(qList.size()>0))
		{
			int nutritionScore = CalculationManager.getScoreByQuestionsList(qList);
			scores.setNutritionScreen(nutritionScore);
			
			//high nutrition risk alert
			Map<String, String> nAlert = new TreeMap<String, String>();
			lAlert = AlertManager.getNutritionAlerts(nutritionScore, lAlert, qList);
			
			//set alerts in report bean
			if (nAlert != null && nAlert.size()>0)	
				report.setAlerts(lAlert);
			else
				report.setAlerts(null);
		}
		
		//RAPA Alert
		try{
   			xml = new String(rAPASurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mRAPASurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = MisUtils.getQuestionList(mRAPASurvey);  		

		int rAPAScore = CalculationManager.getScoreForRAPA(qList);
		if (rAPAScore < 6)
			lAlert.add(AlertsInReport.PHYSICAL_ACTIVITY_ALERT);
		
		scores.setPhysicalActivity(rAPAScore);
				
		//Mobility Alerts
		try{
   			xml = new String(mobilitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mMobilitySurvey = ResultParser.getResults(xml);
   		Map<String, String> qMap = MisUtils.getQuestionMap(mMobilitySurvey);  
   		   		
   		lAlert = AlertManager.getMobilityAlerts(qMap, lAlert);    		
   		
   		//summary tools for Mobility
   		for (int i = 0; i < lAlert.size(); i++)
   		{
   			if (lAlert.get(i).contains("2.0"))
   				scores.setMobilityWalking2(lAlert.get(i));
   			
   			if (lAlert.get(i).contains("0.5"))
   				scores.setMobilityWalkingHalf(lAlert.get(i));
   			
   			if (lAlert.get(i).contains("climbing"))
   				scores.setMobilityClimbing(lAlert.get(i));   			
   		}
   		
   		String noLimitation = "No Limitation";   		
   		if (Utils.isNullOrEmpty(scores.getMobilityWalking2()))
   			scores.setMobilityWalking2(noLimitation);
   		
   		if (Utils.isNullOrEmpty(scores.getMobilityWalkingHalf()))
   			scores.setMobilityWalkingHalf(noLimitation);
   		
   		if (Utils.isNullOrEmpty(scores.getMobilityClimbing()))
   			scores.setMobilityClimbing(noLimitation);
   		
   		report.setScores(scores);
   		model.addAttribute("scores", scores);
		
		report.setAlerts(lAlert);
		//end of alert
		try{
   			xml = new String(goals.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mGoals = ResultParser.getResults(xml);
   	
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		//get answer list
		qList = MisUtils.getQuestionList(mGoals);   
		
		List<String> gAS = new ArrayList<String>();
		if ((qList != null) && (qList.size()>0))
		{
			String lifeGoals = "Life Goals: " + qList.get(1);
			gAS.add(lifeGoals);
			String healthyGoals = "Health Goals : " + qList.get(4);
			gAS.add(healthyGoals);
			
			report.setPatientGoals(gAS);
		}
		
		//get volunteer information
		String volunteer = appointment.getVolunteer();
		String partner = appointment.getPartner();
		String comments = appointment.getComments();
				
		Map<String, String> vMap = new TreeMap<String, String>();
		
		if (!Utils.isNullOrEmpty(volunteer))
			vMap.put(" 1", volunteer);
		else
			vMap.put(" 1", "");
		
		if (!Utils.isNullOrEmpty(partner))
			vMap.put(" 2", partner);
		else
			vMap.put(" 2", "");
		
		if (!Utils.isNullOrEmpty(comments))
			vMap.put(" V", comments);					
		else
			vMap.put(" V", " ");
		
		report.setVolunteerInformations(vMap);
		
		model.addAttribute("report", report);
	
		MisUtils.buildPDF(report, response);
	
		return null;
	}
	
	//====================== Survey ===================================//
	@RequestMapping(value="/manage_survey_templates", method=RequestMethod.GET)
	public String manageSurveyTemplates(@RequestParam(value="failed", required=false) Boolean deleteFailed, 
			SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		List<SurveyTemplate> surveyTemplateList = surveyManager.getAllSurveyTemplates();
		model.addAttribute("survey_templates", surveyTemplateList);
		if (deleteFailed != null)
			model.addAttribute("failed", deleteFailed);
		
		HttpSession session = request.getSession();
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		
		return "admin/manage_survey_templates";
	}
	
	@RequestMapping(value="/manage_survey", method=RequestMethod.GET)
	public String manageSurvey(@RequestParam(value="failed", required=false) String failed, Boolean deleteFailed, 
			ModelMap model, HttpServletRequest request){
		HttpSession session = request.getSession();
		List<SurveyTemplate>  surveyTemplateList = getSurveyTemplates(request);		
		model.addAttribute("survey_templates", surveyTemplateList);
		
		if (deleteFailed != null)
			model.addAttribute("failed", deleteFailed);
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));

		return "admin/manage_survey";
	}	

	@RequestMapping(value="/search_survey", method=RequestMethod.POST)
	public String searchSurvey(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model, 
			SecurityContextHolderAwareRequestWrapper request){
		
		String title = request.getParameter("searchTitle");		
		List<SurveyTemplate>  surveyTemplateList = surveyManager.getSurveyTemplatesByPartialTitle(title);
		
		model.addAttribute("survey_templates", surveyTemplateList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		 
		
		model.addAttribute("searchTitle", title);
		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		
		return "admin/manage_survey";
	}
   	
   	@RequestMapping(value="/manage_surveys", method=RequestMethod.GET)
	public String manageSurveys(@RequestParam(value="failed", required=false) String failed, ModelMap model, HttpServletRequest request){
   		List<SurveyResult> surveyResultList = surveyManager.getAllSurveyResults();
		model.addAttribute("surveys", surveyResultList);
		List<SurveyTemplate> surveyTemplateList = surveyManager.getAllSurveyTemplates();
		model.addAttribute("survey_templates", surveyTemplateList);
		
		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResultList, surveyTemplateList);
	    List<Patient> patientList = patientManager.getAllPatients();
        model.addAttribute("patients", patientList);
        if(failed != null) {
        	model.addAttribute("failed", true);
        }
        HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
        
		return "admin/manage_surveys";
	}
   	
   	@RequestMapping(value="/go_assign_survey/{patientId}", method=RequestMethod.GET)
	public String goAssignSurvey(@PathVariable("patientId") int id, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model){
 
   		List<SurveyTemplate> surveyTemplates = getSurveyTemplates(request);
   		//Assign Survey in Survey Mangement, it will load all patients in the table with checkbox for later selection
   		if (id == 0)
   		{
   			List<Patient> patients  = MisUtils.getAllPatientsWithFullInfos(patientManager, request);
   			
   			if(patients == null || surveyTemplates == null)
   				return "redirect:/manage_surveys?failed=true";
   			else
   			{
   				model.addAttribute("patients", patients);
   				model.addAttribute("surveyTemplates", surveyTemplates);
   			}
   		}//Assign Survey in Client/details, assign surveys for selected patient
   		else
   		{
   			model.addAttribute("patient", id);
   			
   			if (surveyTemplates == null)
   				return "redirect:/manage_surveys?failed=true";
   			else
   			{
   				model.addAttribute("surveyTemplates", surveyTemplates);
   				model.addAttribute("hideClients", true);
   			}
   		}
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
		return "/admin/assign_survey";
	} 
   	
   	@RequestMapping(value="/assign_selectedsurvey", method=RequestMethod.POST)
	public String assignSurvey(SecurityContextHolderAwareRequestWrapper request, ModelMap model) 
			throws JAXBException, DatatypeConfigurationException, Exception{      		
   		List<SurveyTemplate> sTemplates = getSurveyTemplates(request);
   		List<Patient> patients = getPatients(request);   	
   		ArrayList<SurveyTemplate> selectSurveyTemplats = new ArrayList<SurveyTemplate>();
   		String[] surveyTemplateIds = request.getParameterValues("surveyTemplates"); 
   		int[] patientIds;
   		
   		//if user selects Client/Details/Assign Survey, patient id would store in hidden field called patient   		
   		String hPatient = request.getParameter("patient");
   		if (!Utils.isNullOrEmpty(hPatient))
   		{      			
   			if(request.getParameter("assignSurvey") != null)//assign selected surveys to selected patients
   	   		{   
   				if (surveyTemplateIds != null && surveyTemplateIds.length > 0){

   					addSurveyTemplate(surveyTemplateIds,sTemplates, selectSurveyTemplats);   
   					
   	   	   			patientIds = new int[] {Integer.valueOf(hPatient)};
   	   	   			
   	   	   			assignSurveysToClient(selectSurveyTemplats, patientIds, request, model);   		   	   		 
   	   	   		}
   	   	   		else//no survey template has been selected
   	   	   		{
   	   	   			model.addAttribute("no_survey_selected", true);   	
   	   	   			return "admin/assign_survey";
   	   	   		}
   				return "redirect:/display_client/" + hPatient;  
   	   		}
   		}
   		else//user select SurveyManagement/Assign Survey
   		{ 		
	   		if (request.getParameter("searchPatient") != null && 
	   				request.getParameter("searchPatientName") !=null )//search patient by name
	   		{
	   			String name = request.getParameter("searchPatientName");   			
	   			
	   			patients = patientManager.getPatientsByPartialName(name);			
	   			model.addAttribute("searchPatientName", name);	 
	   			
	   		}
	   		else if(request.getParameter("assignSurvey") != null)//assign selected surveys to selected patients
	   		{    	   		
	   	   		String[] selectedPatientIds = request.getParameterValues("patientId");
	   	   		String assignToAll = request.getParameter("assignAllClinets");	   	   		   	   		
	   	   		
	   	   		//get survey template list 
	   	   		if (surveyTemplateIds != null && surveyTemplateIds.length > 0)
	   	   		{
	   	   			addSurveyTemplate(surveyTemplateIds,sTemplates, selectSurveyTemplats);   
	   	   			
		   	   		if ("true".equalsIgnoreCase(assignToAll))
		   	   		{//for assign to all clients   			
		   	   			Patient patient;   			
		   	   			patientIds = new int[patients.size()];
		   	   			
		   	   			for(int i = 0; i < patients.size(); i++){
		   	   				patient = new Patient();
		   	   				patient = patients.get(i);
		   	   				patientIds[i] = patient.getPatientID();
		   	   			}		   	   			
		   	   			assignSurveysToClient(selectSurveyTemplats, patientIds, request, model);			
		   	   		}
		   	   		else
		   	   		{//for selected patients, convert String[] to int[]   			
		   	   			if (selectedPatientIds == null || selectedPatientIds.length == 0)
		   	   				model.addAttribute("no_patient_selected", true);
		   	   			else
		   	   			{
		   	   				int[] iSelectedPatientIds = new int[selectedPatientIds.length];
		   	   	   			for (int j = 0; j < selectedPatientIds.length; j++){
		   	   	   				iSelectedPatientIds[j] = Integer.parseInt(selectedPatientIds[j]);
		   	   				}
		   	   	   			assignSurveysToClient(selectSurveyTemplats, iSelectedPatientIds, request, model);
		   	   			}   			
		   	   		} 
	   	   		}
	   	   		else//no survey template has been selected
	   	   			model.addAttribute("no_survey_selected", true);
	   		}
	   		model.addAttribute("surveyTemplates", sTemplates);
	   		model.addAttribute("patients", patients);
   		}
   		
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		return "admin/assign_survey";
	}
   

	@RequestMapping(value="/assign_surveys", method=RequestMethod.POST)
	public String assignSurveys(SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
		String[] patients = request.getParameterValues("patients[]");
		if(patients == null) {
			return "redirect:/manage_surveys?failed=true";
		}
		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
   		List<SurveyTemplate> surveyTemplates = surveyManager.getAllSurveyTemplates();
   		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		int surveyId = Integer.parseInt(request.getParameter("surveyID"));
		SurveyTemplate st = surveyManager.getSurveyTemplateByID(surveyId);
		List<TapestryPHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(surveyId));
		
		SurveyFactory surveyFactory = new SurveyFactory();
		PHRSurvey template = (TapestryPHRSurvey)surveyFactory.getSurveyTemplate(st);
		for(int i=0; i < patients.length; i++) {
			SurveyResult sr = new SurveyResult();
            sr.setSurveyID(surveyId);
            sr.setPatientID(Integer.parseInt(patients[i]));
          //if requested survey that's already done
    		if (specificSurveys.size() < template.getMaxInstances())
    		    
    		    
    		   	
    		{
    			TapestryPHRSurvey blankSurvey = (TapestryPHRSurvey)template;
    			blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
    			sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
    			String documentId = surveyManager.assignSurvey(sr);
    			blankSurvey.setDocumentId(documentId);
    			surveys.addSurvey(blankSurvey);
    			specificSurveys = surveys.getSurveyListById(Integer.toString(surveyId)); //reload
    		}
    		else
    		{
    			return "redirect:/manage_surveys";
    		}
		}
		return "redirect:/manage_surveys";
	}
	
 	@RequestMapping(value = "/upload_survey_template", method=RequestMethod.POST)
	public String addSurveyTemplate(HttpServletRequest request) throws Exception{
   		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
   		MultipartFile multipartFile = multipartRequest.getFile("file");
   		
		//Add a new survey template
		SurveyTemplate st = new SurveyTemplate();
		st.setTitle(request.getParameter("title"));
		st.setType(request.getParameter("type"));
		st.setDescription(request.getParameter("desc"));
		int p = Integer.parseInt(request.getParameter("priority"));
		st.setPriority(p);
		st.setContents(multipartFile.getBytes());
		surveyManager.uploadSurveyTemplate(st);
		
		return "redirect:/manage_survey_templates";
	}
 	
   	@RequestMapping(value="/delete_survey_template/{surveyID}", method=RequestMethod.GET)
   	public String deleteSurveyTemplate(@PathVariable("surveyID") int id, ModelMap model){   		
   		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResultsBySurveyId(id);  		
   		
   		if(surveyResults.isEmpty()) {
   			surveyManager.deleteSurveyTemplate(id);
   			return "redirect:/manage_survey_templates";
   		} else {
   			return "redirect:/manage_survey_templates?failed=true";
   		}
   	}
	
	@RequestMapping(value="open_survey/{resultID}", method=RequestMethod.GET)
	public String openSurvey(@PathVariable("resultID") int id, HttpServletRequest request) {		
		HttpSession session = request.getSession();
		User u = (User)session.getAttribute("loggedInUser");	
		String name = u.getName();
		
		SurveyResult surveyResult = surveyManager.getSurveyResultByID(id);
		Patient p = patientManager.getPatientByID(surveyResult.getPatientID());
		
		if(surveyResult.getStartDate() == null) {
			surveyManager.updateStartDate(id);
		}
		
		//user logs
		StringBuffer sb  = new StringBuffer();
		sb.append(name);
		sb.append(" opened survey ");
		sb.append(surveyResult.getSurveyTitle());
		sb.append(" for patient ");
		if(p.getPreferredName() != null && p.getPreferredName() != "")
			sb.append(p.getPreferredName());
		else 
			sb.append(p.getDisplayName());
	
		userManager.addUserLog(sb.toString(), u);
		
		return "redirect:/show_survey/" + id;
	}
   	
   	@RequestMapping(value="/show_survey/{resultID}", method=RequestMethod.GET)
   	public ModelAndView showSurvey(@PathVariable("resultID") int id, HttpServletRequest request) {   		   		
   		ModelAndView redirectAction = null;
   		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
		List<SurveyTemplate> surveyTemplates = surveyManager.getAllSurveyTemplates();
		SurveyResult surveyResult = surveyManager.getSurveyResultByID(id);
		SurveyTemplate surveyTemplate = surveyManager.getSurveyTemplateByID(surveyResult.getSurveyID());
		
		//all survey results stored in map		
		TapestrySurveyMap userSurveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);			
		TapestryPHRSurvey currentSurvey = userSurveys.getSurvey(Integer.toString(id));
				
		try {
			SurveyFactory surveyFactory = new SurveyFactory();
			PHRSurvey templateSurvey = surveyFactory.getSurveyTemplate(surveyTemplate);	
			
			redirectAction = DoSurveyAction.execute(request, Integer.toString(id), currentSurvey, templateSurvey);
		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		}
		
		if (redirectAction == null){ //Assuming we've completed the survey
			System.out.println("Something bad happened");
		}
		
   		if (request.isUserInRole("ROLE_USER") && redirectAction.getViewName() == "failed"){
   			redirectAction.setViewName("redirect:/");
   		} else if (request.isUserInRole("ROLE_ADMIN") && redirectAction.getViewName() == "failed") {
   			redirectAction.setViewName("redirect:/manage_surveys");
   		}
   		return redirectAction;
   	}
   	  
   	@RequestMapping(value="/save_survey/{resultID}", method=RequestMethod.GET)
   	public String saveAndExit(@PathVariable("resultID") int id, HttpServletRequest request) throws Exception
	{
   		boolean isComplete = Boolean.parseBoolean(request.getParameter("survey_completed"));
   		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
		List<SurveyTemplate> surveyTemplates = surveyManager.getAllSurveyTemplates();
		
		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
		PHRSurvey currentSurvey = surveys.getSurvey(Integer.toString(id));
		
		//For activity logging purposes
		HttpSession session = request.getSession();
		User currentUser = (User)session.getAttribute("loggedInUser");	
		
		SurveyResult surveyResult = surveyManager.getSurveyResultByID(id);
		Patient currentPatient = patientManager.getPatientByID(surveyResult.getPatientID());
		Appointment appointment = appointmentManager.getAppointmentByMostRecentIncomplete(currentPatient.getPatientID());
		
		StringBuffer sb;
		if (isComplete) {
			byte[] data = null;
				try {
				data = SurveyAction.updateSurveyResult(currentSurvey);
				currentSurvey.setComplete(true);
			} catch (Exception e) {
				System.out.println("Failed to convert PHRSurvey into a byte array");
				e.printStackTrace();
			}
			surveyManager.updateSurveyResults(id, data);
			surveyManager.markAsComplete(id);
			
			//user logs
			sb  = new StringBuffer();
			sb.append("Completed survey ");
			sb.append(surveyResult.getSurveyTitle());
			sb.append(" for patient ");
			
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") 
				sb.append( currentPatient.getPreferredName());
			else
				sb.append( currentPatient.getDisplayName());
			
			userManager.addUserLog(sb.toString(), currentUser);
		}
		
		if (!currentSurvey.isComplete())
		{
			byte[] data = SurveyAction.updateSurveyResult(currentSurvey);
			surveyManager.updateSurveyResults(id, data);
			
			//user logs
			sb  = new StringBuffer();
			sb.append("Saved incomplete survey ");
			sb.append(surveyResult.getSurveyTitle());
			sb.append(" for patient ");
			
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") 
				sb.append( currentPatient.getPreferredName());
			else 
				sb.append( currentPatient.getDisplayName());
		
			userManager.addUserLog(sb.toString(), currentUser);
		}
		
		if (request.isUserInRole("ROLE_ADMIN")){
   			return "redirect:/manage_surveys";
   		} else {
   			if (isComplete) {
   				return "redirect:/patient/" + currentPatient.getPatientID() + "?complete=" + surveyResult.getSurveyTitle() + "&appointmentId=" + appointment.getAppointmentID();
   			} else {
   				return "redirect:/patient/" + currentPatient.getPatientID() + "?aborted=" + surveyResult.getSurveyTitle() + "&appointmentId=" + appointment.getAppointmentID();
   			}
   		}
	}
  
   	@RequestMapping(value="/delete_survey/{resultID}", method=RequestMethod.GET)
   	public String deleteSurvey(@PathVariable("resultID") int id, HttpServletRequest request){
   		surveyManager.deleteSurvey(id);
		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
   		List<SurveyTemplate> surveyTemplates = surveyManager.getAllSurveyTemplates();
   		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		return "redirect:/manage_surveys";
   	}
   	
   	@RequestMapping(value="/view_survey_results/{resultID}", method=RequestMethod.GET)
   	public String viewSurveyResults(@PathVariable("resultID") int id, HttpServletRequest request, ModelMap model){
   		SurveyResult r = surveyManager.getSurveyResultByID(id); 		
   		
   		String xml;
   		try{
   			xml = new String(r.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> res = ResultParser.getResults(xml);
   		List<DisplayedSurveyResult> displayedResults = ResultParser.getDisplayedSurveyResults(res);

   		model.addAttribute("results", displayedResults);
   		model.addAttribute("id", id);
   		HttpSession session = request.getSession();
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
   		
   		return "/admin/view_survey_results";
   	}
   	
   	@RequestMapping(value="/export_csv/{resultID}", method=RequestMethod.GET)
   	@ResponseBody
   	public String downloadCSV(@PathVariable("resultID") int id, HttpServletResponse response){
   		SurveyResult r = surveyManager.getSurveyResultByID(id);
   		String xml;
   		try{
   			xml = new String(r.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		String res = ResultParser.resultsAsCSV(ResultParser.getResults(xml));   		
   		
   		response.setContentType("text/csv");
   		response.setContentLength(res.length());
   		response.setHeader("Content-Disposition", "attachment; filename=\"result.csv\"");
   		try{
   			PrintWriter pw = new PrintWriter(response.getOutputStream());
   			pw.write(res);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		
   		return res;
   	}
   	
   	@RequestMapping(value="/download_survey_template/{surveyID}", method=RequestMethod.GET)
   	@ResponseBody
   	public String downloadSurveyTemplate(@PathVariable("surveyID") int id, HttpServletResponse response){
   		   		
   		SurveyTemplate sTemplate = surveyManager.getSurveyTemplateByID(id);
   		String fileName = sTemplate.getTitle();
   		byte[] bContent = sTemplate.getContents();
   		response.setContentType("text");
   		response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + ".text\"");
   	
   		try{
   			ServletOutputStream output = response.getOutputStream();
   			output.write(bContent);   			
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		   		
   		return "admin/manage_survey";
   	}
   	
	private List<SurveyTemplate> getSurveyTemplates(HttpServletRequest request){
		HttpSession session = request.getSession();		
		List<SurveyTemplate> surveyTemplateList;
		if (session.getAttribute("survey_template_list") == null)
		{
			surveyTemplateList = surveyManager.getAllSurveyTemplates();
			
			//save in the session
			if (surveyTemplateList != null && surveyTemplateList.size()>0)
				session.setAttribute("survey_template_list", surveyTemplateList);
		}
		else
			surveyTemplateList = (List<SurveyTemplate>)session.getAttribute("survey_template_list");
		
		return surveyTemplateList;
	}
	
   	private List<Patient> getPatients(SecurityContextHolderAwareRequestWrapper request ){
   		
   		HttpSession session = request.getSession();		
		List<Patient> patients;
		if (session.getAttribute("patient_list") == null)
		{
			patients = patientManager.getAllPatients();
			
			//save in the session
			if (patients != null && patients.size()>0)
				session.setAttribute("patient_list", patients);
		}
		else
			patients = (List<Patient>)session.getAttribute("patient_list");
		return patients;
	//	return MisUtils.getAllPatientsWithFullInfos(patientDao, request);
   	}
   	
   	//void duplicating survey in result sheet
   	private boolean isExistInSurveyResultList(List<SurveyResult> surveyResults, int surveyTemplateId, int patientId){
   		boolean exist = false;
   		int sId = 0;
   		int pId = 0;
   		for (SurveyResult sr : surveyResults){
   			sId = sr.getSurveyID();
   			pId = sr.getPatientID();
   			
   			if (surveyTemplateId == sId && patientId == pId)
   				exist = true;
   		}
   		return exist;
   	}
   	
   	private void assignSurveysToClient(List<SurveyTemplate> surveyTemplates, int[] patientIds, 
   			SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
		
		List<SurveyResult> surveyResults = surveyManager.getAllSurveyResults();
		
   		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		SurveyResult sr;
   		
   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   		String startDate = sdf.format(new Date());   
 	
   		for(SurveyTemplate st: surveyTemplates) 
   		{
			List<TapestryPHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID()));
			
			SurveyFactory surveyFactory = new SurveyFactory();
			TapestryPHRSurvey template = surveyFactory.getSurveyTemplate(st);
			sr = new SurveyResult();
				
			for (int i = 0; i < patientIds.length; i++){
				sr.setPatientID(patientIds[i]);
				sr.setSurveyID(st.getSurveyID());
	            	
				//set today as startDate
				sr.setStartDate(startDate);	            	
				//if requested survey that's already done
				if (specificSurveys.size() < template.getMaxInstances() && 
						!isExistInSurveyResultList(surveyResults,st.getSurveyID(), patientIds[i]))
				{		    		
					TapestryPHRSurvey blankSurvey = template;
					blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
					sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
					String documentId = surveyManager.assignSurvey(sr);
					
					blankSurvey.setDocumentId(documentId);
					surveys.addSurvey(blankSurvey);
					specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
		    	}
			}   			
		}
   	}
   	
  	private void addSurveyTemplate(String[] surveyId,List<SurveyTemplate> allSurveyTemplates, 
   			List<SurveyTemplate> selectedSurveyTemplates){
   		int surveyTemplateId;
   		for (int i = 0; i < surveyId.length; i ++)
   		{  						
   			surveyTemplateId = Integer.parseInt(surveyId[i]);
  				
  			for (SurveyTemplate st: allSurveyTemplates){
  				if (surveyTemplateId == st.getSurveyID())
  					selectedSurveyTemplates.add(st);
  	   		}
   		}
   	}
   	
   	private void assignSurveysToClient(List<SurveyTemplate> selectSurveyTemplats, int[] patientIds,
   			SecurityContextHolderAwareRequestWrapper request,ModelMap model) 
   					throws JAXBException, DatatypeConfigurationException, Exception{
   		try
   		{   				
   			assignSurveysToClient(selectSurveyTemplats, patientIds, request);
   			model.addAttribute("successful", true);
  		}catch (Exception e){
  			System.out.println("something wrong");
  		} 
   	}
	
	
}
