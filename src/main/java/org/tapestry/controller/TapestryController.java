package org.tapestry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

@Controller
public class TapestryController{

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String welcome(SecurityContextHolderAwareRequestWrapper request){
		if (request.isUserInRole("ROLE_USER"))
			return "caretaker/index";
		else if (request.isUserInRole("ROLE_ADMIN"))
			return "admin/index";
		else
			return "error";
	}

}
