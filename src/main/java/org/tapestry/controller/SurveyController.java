package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.objects.Patient;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.SurveyResult;
import org.yaml.snakeyaml.Yaml;

@Controller
public class SurveyController{
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private SurveyResultDao surveyResultDao;
	private SurveyTemplateDao surveyTemplateDao;
	private PatientDao patientDao;
	
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
		surveyResultDao = new SurveyResultDao(DB, UN, PW);
		surveyTemplateDao = new SurveyTemplateDao(DB, UN, PW);
		patientDao = new PatientDao(DB, UN, PW);
   	}
   	
   	@RequestMapping(value="/manage_surveys", method=RequestMethod.GET)
	public String manageSurveys(ModelMap model){
   		ArrayList<SurveyResult> surveyResultList = surveyResultDao.getAllSurveyResults();
		model.addAttribute("surveys", surveyResultList);
		ArrayList<SurveyTemplate> surveyTemplateList = surveyTemplateDao.getAllSurveyTemplates();
		model.addAttribute("survey_templates", surveyTemplateList);
	    ArrayList<Patient> patientList = patientDao.getAllPatients();
        model.addAttribute("patients", patientList);
		return "admin/manage_surveys";
	}
   	
   	@RequestMapping(value="/assign_surveys", method=RequestMethod.POST)
	public String assignSurveys(SecurityContextHolderAwareRequestWrapper request){
		String[] patients = request.getParameterValues("patients[]");
		SurveyTemplate st = surveyTemplateDao.getSurveyTemplateByID(Integer.parseInt(request.getParameter("surveyID")));
		for(int i=0; i < patients.length; i++) {
			SurveyResult sr = new SurveyResult();
            sr.setSurveyID(Integer.parseInt(request.getParameter("surveyID")));
            sr.setPatientID(Integer.parseInt(patients[i]));
            sr.setResults(st.getContents());
            sr.setStatus("Incomplete");
            surveyResultDao.assignSurvey(sr);
		}
		return "redirect:/manage_surveys";
	}
   	
   	@RequestMapping(value="/delete_survey/{resultID}", method=RequestMethod.GET)
   	public String deleteSurvey(@PathVariable("resultID") int id){
   		surveyResultDao.deleteSurvey(id);
   		return "redirect:/manage_surveys";
   	}
}