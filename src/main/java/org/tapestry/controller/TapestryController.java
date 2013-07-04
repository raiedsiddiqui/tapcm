package org.tapestry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

@Controller
public class TapestryController{

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String loginView(ModelMap model){
		return "login";
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String welcome(SecurityContextHolderAwareRequestWrapper request, ModelMap model){
		if (request.isUserInRole("ROLE_USER")){
			String name = request.getUserPrincipal().getName();
			model.addAttribute("name", name);
			return "caretaker/index";
		}
		else if (request.isUserInRole("ROLE_ADMIN")){
			String name = request.getUserPrincipal().getName();
			model.addAttribute("name", name);
			return "admin/index";
		}
		else{
			model.addAttribute("error", "true");
			return "login";
		}
	}

	@RequestMapping(value="/loginfailed", method=RequestMethod.GET)
	public String failed(ModelMap model){
		model.addAttribute("error", "true");
		return "login";
	}

}
