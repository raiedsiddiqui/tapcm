package org.tapestry.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.PictureDao;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.yaml.snakeyaml.Yaml;

@Controller
public class FileController extends MultiActionController{
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private PictureDao pictureDao;	
	private PatientDao patientDao;
	private ActivityDao activityDao;
	
	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 */
   	@PostConstruct
   	public void readDatabaseConfig(){
   		String DB = "";
   		String UN = "";
   		String PW = "";
		try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			DB = config.get("url");
			UN = config.get("username");
			PW = config.get("password");
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());
		}
		
		pictureDao = new PictureDao(DB, UN, PW);		
		activityDao = new ActivityDao(DB, UN, PW);
		patientDao = new PatientDao(DB, UN, PW);
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
}
