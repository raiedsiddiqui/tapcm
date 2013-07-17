package org.tapestry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.objects.User;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Appointment;
import java.util.ArrayList;

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

	private final String DB = "jdbc:mysql://localhost:3306";
	private final String UN = "root";
	private final String PW = "root";
	
	private UserDao userDao = new UserDao(DB, UN, PW);
   	private PatientDao patientDao = new PatientDao(DB, UN, PW);
   	private AppointmentDao appointmentDao = new AppointmentDao(DB, UN, PW);

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(ModelMap model){
		return "login";
	}

	@RequestMapping(value={"/", "/loginsuccess"}, method=RequestMethod.GET)
	public String welcome(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		if (request.isUserInRole("ROLE_USER")){
			String username = request.getUserPrincipal().getName();
			User u = userDao.getUserByUsername(username);
			ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(u.getName());
			ArrayList<Appointment> appointmentsForToday = appointmentDao.getAllAppointmentsForVolunteerForToday(u.getName());
			model.addAttribute("name", u.getName());
			model.addAttribute("patients", patientsForUser);
			model.addAttribute("appointments", appointmentsForToday);
			
			return "volunteer/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN")){
			String name = request.getUserPrincipal().getName();
			model.addAttribute("name", name);
			return "admin/index";
		}
		else{ //This should not happen, but catch any unforseen behavior
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/loginfailed", method=RequestMethod.GET)
	public String failed(ModelMap model){
		model.addAttribute("error", "true");
		return "login";
	}

	@RequestMapping(value="/manage_users", method=RequestMethod.GET)
	public String manageUsers(ModelMap model){
		ArrayList<User> userList = userDao.getAllUsers();
		model.addAttribute("users", userList);
		return "admin/manage_users";
	}
	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(ModelMap model){
		ArrayList<User> volunteers = userDao.getAllUsersWithRole("ROLE_USER");
		model.addAttribute("volunteers", volunteers);
	        ArrayList<Patient> patientList = patientDao.getAllPatients();
                model.addAttribute("patients", patientList);
		return "admin/manage_patients";
	}

	@RequestMapping(value="/add_user", method=RequestMethod.POST)
	public String addUser(SecurityContextHolderAwareRequestWrapper request){
		//Add a new user
		User u = new User();
		u.setName(request.getParameter("name"));
		u.setUsername(request.getParameter("username"));
		u.setRole(request.getParameter("role"));
		
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword("password", null); //Default
		
		u.setPassword(hashedPassword);
		u.setEmail(request.getParameter("email"));
		userDao.createUser(u);
		//Display page again
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/remove_user/{user_id}", method=RequestMethod.GET)
	public String removeUser(@PathVariable("user_id") int id){
		userDao.removeUserWithId(id);
		return "redirect:/manage_users";
	}

	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request){
		//Add a new patient
		Patient p = new Patient();
		p.setFirstName(request.getParameter("firstname"));
		p.setLastName(request.getParameter("lastname"));
		p.setVolunteer(request.getParameter("volunteer"));
		p.setColor(request.getParameter("backgroundColor"));
		patientDao.createPatient(p);
		return "redirect:/manage_patients";
	}

	@RequestMapping(value="/remove_patient/{patient_id}", method=RequestMethod.GET)
	public String removePatient(@PathVariable("patient_id") int id){
		patientDao.removePatientWithId(id);
		return "redirect:/manage_patients";
	}

	@RequestMapping(value="/patient/{patient_id}", method=RequestMethod.GET)
	public String viewPatient(@PathVariable("patient_id") int id, SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		Patient patient = patientDao.getPatientById(id);
		//Find the name of the current user
		User u = userDao.getUserByUsername(request.getUserPrincipal().getName());
		String loggedInUser = u.getName();
		//Make sure that the user is actually responsible for the patient in question
		String volunteerForPatient = patient.getVolunteer();
		if (!(loggedInUser.equals(volunteerForPatient))){
			model.addAttribute("loggedIn", loggedInUser);
			model.addAttribute("patientOwner", volunteerForPatient);
			return "/error-forbidden";
		}
		model.addAttribute("patient", patient);
		return "/patient";
	}
	
	@RequestMapping(value="/book_appointment", method=RequestMethod.POST)
	public String addAppointment(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User u = userDao.getUserByUsername(request.getUserPrincipal().getName());
		String loggedInUser = u.getName();
		
		Appointment a = new Appointment();
		a.setVolunteer(loggedInUser);
		a.setPatient(request.getParameter("patient"));
		a.setDate(request.getParameter("appointmentDate"));
		a.setTime(request.getParameter("appointmentTime"));
		appointmentDao.createAppointment(a);
		return "redirect:/";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String viewProfile(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		User loggedInUser = userDao.getUserByUsername(request.getUserPrincipal().getName());
		model.addAttribute("vol", loggedInUser);
		return "/volunteer/profile";
	}

	//Error pages
	@RequestMapping(value="/403", method=RequestMethod.GET)
	public String forbiddenError(){
		return "error-forbidden";
	}

}
