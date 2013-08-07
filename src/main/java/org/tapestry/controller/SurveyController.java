package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyMap;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.UserDao;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.SurveyResult;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.SurveyFactory;
import org.yaml.snakeyaml.Yaml;

@Controller
public class SurveyController{
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private SurveyResultDao surveyResultDao;
	private SurveyTemplateDao surveyTemplateDao;
	private PatientDao patientDao;
	private ActivityDao activityDao;
	private UserDao userDao;
	
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
		activityDao = new ActivityDao(DB, UN, PW);
		userDao = new UserDao(DB, UN, PW);
   	}
   	
   	@RequestMapping(value="/manage_surveys", method=RequestMethod.GET)
	public String manageSurveys(ModelMap model, HttpServletRequest request){
   		ArrayList<SurveyResult> surveyResultList = surveyResultDao.getAllSurveyResults();
		model.addAttribute("surveys", surveyResultList);
		ArrayList<SurveyTemplate> surveyTemplateList = surveyTemplateDao.getAllSurveyTemplates();
		model.addAttribute("survey_templates", surveyTemplateList);
		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResultList, surveyTemplateList);
	    ArrayList<Patient> patientList = patientDao.getAllPatients();
        model.addAttribute("patients", patientList);
		return "admin/manage_surveys";
	}
   	
	@RequestMapping(value="/assign_surveys", method=RequestMethod.POST)
	public String assignSurveys(SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
   		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
   		SurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		int surveyId = Integer.parseInt(request.getParameter("surveyID"));
		String[] patients = request.getParameterValues("patients[]");
		SurveyTemplate st = surveyTemplateDao.getSurveyTemplateByID(surveyId);
		List<PHRSurvey> specificSurveys = surveys.getSurveyListById(Integer.toString(surveyId));
		
		SurveyFactory surveyFactory = new SurveyFactory();
		PHRSurvey template = surveyFactory.getSurveyTemplate(st);
		for(int i=0; i < patients.length; i++) {
			SurveyResult sr = new SurveyResult();
            sr.setSurveyID(surveyId);
            sr.setPatientID(Integer.parseInt(patients[i]));
          //if requested survey that's already done
    		if (specificSurveys.size() < template.getMaxInstances())
    		{
    			PHRSurvey blankSurvey = template;
    			blankSurvey.setQuestions(new ArrayList<SurveyQuestion>());// make blank survey
    			sr.setResults(SurveyAction.updateSurveyResult(blankSurvey));
    			String documentId = surveyResultDao.assignSurvey(sr);
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
   	
   	@RequestMapping(value="/show_survey/{resultID}", method=RequestMethod.GET)
   	public ModelAndView openSurvey(@PathVariable("resultID") int id, HttpServletRequest request) {
   		ModelAndView redirectAction = null;
   		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
		SurveyResult surveyResult = surveyResultDao.getSurveyResultByID(id);
		SurveyTemplate surveyTemplate = surveyTemplateDao.getSurveyTemplateByID(surveyResult.getSurveyID());
		SurveyMap userSurveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
		PHRSurvey currentSurvey = userSurveys.getSurvey(Integer.toString(id));
		
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
   			redirectAction.setViewName("redirect:/volunteer/index");
   			return redirectAction;
   		} else if (request.isUserInRole("ROLE_ADMIN") && redirectAction.getViewName() == "failed") {
   			redirectAction.setViewName("redirect:/manage_surveys");
   			return redirectAction;
   		} else {
   			if (redirectAction.getModelMap().containsKey("survey_completed")){
   				surveyResultDao.markAsComplete(id);
   				User currentUser = userDao.getUserByUsername(request.getRemoteUser());
   				SurveyResult s = surveyResultDao.getSurveyResultByID(id);
   				activityDao.logActivity("Completed survey " + s.getSurveyTitle(), currentUser.getUserID());
   			}
   			return redirectAction;
   		}
   	}
   	
   	@RequestMapping(value="/delete_survey/{resultID}", method=RequestMethod.GET)
   	public String deleteSurvey(@PathVariable("resultID") int id, HttpServletRequest request){
   		surveyResultDao.deleteSurvey(id);
		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
   		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		return "redirect:/manage_surveys";
   	}
}