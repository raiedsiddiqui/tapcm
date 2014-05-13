package org.tapestry.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyMap;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.UserDao;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.SurveyResult;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.surveys.ResultParser;
import org.yaml.snakeyaml.Yaml;

@Controller
public class SurveyController{
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;
	
	private SurveyResultDao surveyResultDao;
	private SurveyTemplateDao surveyTemplateDao;
	private PatientDao patientDao;
	private AppointmentDao appointmentDao;
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
		appointmentDao = new AppointmentDao(DB, UN, PW);
		userDao = new UserDao(DB, UN, PW);
   	}
   	
	@RequestMapping(value="/manage_survey_templates", method=RequestMethod.GET)
	public String manageSurveyTemplates(@RequestParam(value="failed", required=false) Boolean deleteFailed, ModelMap model){
		ArrayList<SurveyTemplate> surveyTemplateList = surveyTemplateDao.getAllSurveyTemplates();
		model.addAttribute("survey_templates", surveyTemplateList);
		if (deleteFailed != null)
			model.addAttribute("failed", deleteFailed);
		return "admin/manage_survey_templates";
	}
   	
   	@RequestMapping(value="/manage_surveys", method=RequestMethod.GET)
	public String manageSurveys(@RequestParam(value="failed", required=false) String failed, ModelMap model, HttpServletRequest request){
   		ArrayList<SurveyResult> surveyResultList = surveyResultDao.getAllSurveyResults();
		model.addAttribute("surveys", surveyResultList);
		ArrayList<SurveyTemplate> surveyTemplateList = surveyTemplateDao.getAllSurveyTemplates();
		model.addAttribute("survey_templates", surveyTemplateList);
		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResultList, surveyTemplateList);
	    ArrayList<Patient> patientList = patientDao.getAllPatients();
        model.addAttribute("patients", patientList);
        if(failed != null) {
        	model.addAttribute("failed", true);
        }
		return "admin/manage_surveys";
	}
   	
	@RequestMapping(value="/assign_surveys", method=RequestMethod.POST)
	public String assignSurveys(SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
		String[] patients = request.getParameterValues("patients[]");
		if(patients == null) {
			return "redirect:/manage_surveys?failed=true";
		}
		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
   		SurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		int surveyId = Integer.parseInt(request.getParameter("surveyID"));
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
	
	@RequestMapping(value="open_survey/{resultID}", method=RequestMethod.GET)
	public String openSurvey(@PathVariable("resultID") int id, HttpServletRequest request) {
		String username = request.getUserPrincipal().getName();
		User u = userDao.getUserByUsername(username);
		SurveyResult surveyResult = surveyResultDao.getSurveyResultByID(id);
		Patient p = patientDao.getPatientByID(surveyResult.getPatientID());
		
		if(surveyResult.getStartDate() == null) {
			surveyResultDao.updateStartDate(id);
		}
		System.out.print("Volunteer or Admin?");
		//user logs
		if(p.getPreferredName() != null && p.getPreferredName() != "") {
			activityDao.logActivity(u.getName() + " opened survey " + surveyResult.getSurveyTitle() + " for patient " + p.getPreferredName(), u.getUserID());
		} else {
			activityDao.logActivity(u.getName() + " opened survey " + surveyResult.getSurveyTitle() + " for patient " + p.getDisplayName(), u.getUserID());
		}
		return "redirect:/show_survey/" + id;
	}
   	
   	@RequestMapping(value="/show_survey/{resultID}", method=RequestMethod.GET)
   	public ModelAndView showSurvey(@PathVariable("resultID") int id, HttpServletRequest request) {
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
   		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
		
		SurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
		PHRSurvey currentSurvey = surveys.getSurvey(Integer.toString(id));
		
		//For activity logging purposes
		User currentUser = userDao.getUserByUsername(request.getRemoteUser());
		SurveyResult surveyResult = surveyResultDao.getSurveyResultByID(id);
		Patient currentPatient = patientDao.getPatientByID(surveyResult.getPatientID());
		Appointment appointment = appointmentDao.getAppointmentByMostRecentIncomplete(currentPatient.getPatientID());
		
		if (isComplete) {
			byte[] data = null;
				try {
				data = SurveyAction.updateSurveyResult(currentSurvey);
				currentSurvey.setComplete(true);
			} catch (Exception e) {
				System.out.println("Failed to convert PHRSurvey into a byte array");
				e.printStackTrace();
			}
			surveyResultDao.updateSurveyResults(id, data);
			surveyResultDao.markAsComplete(id);
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") {
				activityDao.logActivity("Completed survey " + surveyResult.getSurveyTitle() + " for patient: " + currentPatient.getPreferredName(), currentUser.getUserID(), currentPatient.getPatientID(), appointment.getAppointmentID());
			} else {
				activityDao.logActivity("Completed survey " + surveyResult.getSurveyTitle() + " for patient: " + currentPatient.getDisplayName(), currentUser.getUserID(), currentPatient.getPatientID(), appointment.getAppointmentID());
			}
		}
		
		if (!currentSurvey.isComplete())
		{
			byte[] data = SurveyAction.updateSurveyResult(currentSurvey);
			surveyResultDao.updateSurveyResults(id, data);
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") {
				activityDao.logActivity("Saved incomplete survey " + surveyResult.getSurveyTitle() + " for patient: " + currentPatient.getPreferredName(), currentUser.getUserID(), currentPatient.getPatientID(), appointment.getAppointmentID());
			} else {
				activityDao.logActivity("Saved incomplete survey " + surveyResult.getSurveyTitle() + " for patient: " + currentPatient.getDisplayName(), currentUser.getUserID(), currentPatient.getPatientID(), appointment.getAppointmentID());
			}
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
   		surveyResultDao.deleteSurvey(id);
		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
   		DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		return "redirect:/manage_surveys";
   	}
   	
   	@RequestMapping(value="/view_survey_results/{resultID}", method=RequestMethod.GET)
   	public String viewSurveyResults(@PathVariable("resultID") int id, HttpServletRequest request, ModelMap model){
   		SurveyResult r = surveyResultDao.getSurveyResultByID(id);
   		String xml;
   		try{
   			xml = new String(r.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		LinkedHashMap<String, String> res = ResultParser.getResults(xml);
   		model.addAttribute("results", res);
   		model.addAttribute("id", id);
   		return "/admin/view_survey_results";
   	}
   	
   	@RequestMapping(value="/export_csv/{resultID}", method=RequestMethod.GET)
   	@ResponseBody
   	public String downloadCSV(@PathVariable("resultID") int id, HttpServletResponse response){
   		SurveyResult r = surveyResultDao.getSurveyResultByID(id);
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
}
