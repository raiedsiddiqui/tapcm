package org.tapestry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.objects.User;
import org.tapestry.objects.Patient;
import java.util.ArrayList;

@Controller
public class TapestryController{

	private UserDao userDao = new UserDao("jdbc:mysql://localhost:3306", "root", "root");
    private PatientDao patientDao = new PatientDao("jdbc:mysql://localhost:3306", "root", "root");

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String loginView(ModelMap model){
		return "login";
	}

	@RequestMapping(value={"/", "/loginsuccess"}, method=RequestMethod.GET)
	public String welcome(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		if (request.isUserInRole("ROLE_USER")){
			String username = request.getUserPrincipal().getName();
			User u = userDao.getUserByUsername(username);
			ArrayList<Patient> patientsForUser = patientDao.getPatientsForVolunteer(u.getName());
			model.addAttribute("name", u.getName());
			model.addAttribute("patients", patientsForUser);
			return "volunteer/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN")){
			String name = request.getUserPrincipal().getName();
			model.addAttribute("name", name);
			return "admin/index";
		}
		else{
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/loginfailed", method=RequestMethod.GET)
	public String failed(ModelMap model){
		model.addAttribute("error", "true");
		return "login";
	}

	@RequestMapping(value="/manage_users", method=RequestMethod.GET)
	public String manageUsers(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		if (request.isUserInRole("ROLE_ADMIN")){
			ArrayList<User> userList = userDao.getAllUsers();
			model.addAttribute("users", userList);
			return "admin/manage_users";
		} else {
			return "redirect:/login";
		}
	}
	@RequestMapping(value="/manage_patients", method=RequestMethod.GET)
	public String managePatients(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		if (request.isUserInRole("ROLE_ADMIN")){
			ArrayList<User> volunteers = userDao.getAllUsersWithRole("ROLE_USER");
			model.addAttribute("volunteers", volunteers);
	                ArrayList<Patient> patientList = patientDao.getAllPatients();
        	        model.addAttribute("patients", patientList);
			return "admin/manage_patients";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/add_user", method=RequestMethod.POST)
	public String addUser(SecurityContextHolderAwareRequestWrapper request){
		if (request.isUserInRole("ROLE_ADMIN")){
			//Add a new user
			User u = new User();
			u.setName(request.getParameter("name"));
			u.setUsername(request.getParameter("username"));
			u.setRole(request.getParameter("role"));
			u.setEmail(request.getParameter("email"));
			userDao.createUser(u);
			//Display page again
			return "redirect:/manage_users";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/remove_user/{user_id}", method=RequestMethod.GET)
	public String removeUser(@PathVariable("user_id") int id, SecurityContextHolderAwareRequestWrapper request){
		if (request.isUserInRole("ROLE_ADMIN")){
			userDao.removeUserWithId(id);
			return "redirect:/manage_users";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/add_patient", method=RequestMethod.POST)
	public String addPatient(SecurityContextHolderAwareRequestWrapper request){
		if (request.isUserInRole("ROLE_ADMIN")){
			//Add a new patient
			Patient p = new Patient();
			p.setFirstName(request.getParameter("firstname"));
			p.setLastName(request.getParameter("lastname"));
			p.setVolunteer(request.getParameter("volunteer"));
			patientDao.createPatient(p);
			return "redirect:/manage_patients";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(value="/remove_patient/{patient_id}", method=RequestMethod.GET)
	public String removePatient(@PathVariable("patient_id") int id, SecurityContextHolderAwareRequestWrapper request){
		if (request.isUserInRole("ROLE_ADMIN")){
			patientDao.removePatientWithId(id);
			return "redirect:/manage_patients";
		} else {
			return "redirect:/login";
		}
	}

}
