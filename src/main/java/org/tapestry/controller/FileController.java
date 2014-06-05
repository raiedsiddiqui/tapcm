package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.tapestry.dao.SurveyResultDao;

import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.PictureDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.UserDao;
import org.tapestry.dao.ActivityDao;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.User;
import org.tapestry.objects.Patient;
import org.tapestry.objects.SurveyTemplate;
import org.yaml.snakeyaml.Yaml;

@Controller
public class FileController extends MultiActionController{
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private SurveyTemplateDao surveyTemplateDao;
	private SurveyResultDao surveyResultDao;
	private PictureDao pictureDao;
	private UserDao userDao;
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
		surveyTemplateDao = new SurveyTemplateDao(DB, UN, PW);
		surveyResultDao = new SurveyResultDao(DB, UN, PW);
		pictureDao = new PictureDao(DB, UN, PW);
		userDao = new UserDao(DB, UN, PW);
		activityDao = new ActivityDao(DB, UN, PW);
		patientDao = new PatientDao(DB, UN, PW);
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
		surveyTemplateDao.uploadSurveyTemplate(st);
		
		return "redirect:/manage_survey_templates";
	}
   	
   	@RequestMapping(value="/delete_survey_template/{surveyID}", method=RequestMethod.GET)
   	public String deleteSurveyTemplate(@PathVariable("surveyID") int id, ModelMap model){
   		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResultsBySurveyId(id);
   		if(surveyResults.isEmpty()) {
   			surveyTemplateDao.deleteSurveyTemplate(id);
   			return "redirect:/manage_survey_templates";
   		} else {
   			return "redirect:/manage_survey_templates?failed=true";
   		}
   	}
   	
	@RequestMapping(value="/upload_picture_to_profile", method=RequestMethod.POST)
	public String uploadPicture(MultipartHttpServletRequest request){
		String currentUsername = request.getUserPrincipal().getName();
		User loggedInUser = userDao.getUserByUsername(currentUsername);
		MultipartFile pic = request.getFile("pic");
		System.out.println("Uploaded: " + pic);
		pictureDao.uploadPicture(pic, loggedInUser.getUserID(), true);
		activityDao.logActivity("Uploaded picture for profile", loggedInUser.getUserID());
		return "redirect:/profile";
	}

	@RequestMapping(value="/upload_picture_for_patient/{patientID}", method=RequestMethod.POST)
	public String uploadPicture(@PathVariable("patientID") int id, MultipartHttpServletRequest request){
		String currentUsername = request.getUserPrincipal().getName();
		User loggedInUser = userDao.getUserByUsername(currentUsername);
		MultipartFile pic = request.getFile("pic");
		System.out.println("Uploaded: " + pic);
		Patient p = patientDao.getPatientByID(id);
		pictureDao.uploadPicture(pic, id, false);
		activityDao.logActivity("Uploaded picture for " + p.getDisplayName(), loggedInUser.getUserID());
		return "redirect:/patient/" + id;
	}
}
