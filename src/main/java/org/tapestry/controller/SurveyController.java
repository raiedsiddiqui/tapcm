package org.tapestry.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyQuestion;
import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.dao.SurveyTemplateDao;
import org.tapestry.dao.UserDao;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.objects.Patient;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.tapestry.objects.User;
import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.ResultParser;
import org.tapestry.surveys.SurveyFactory;
import org.tapestry.surveys.TapestryPHRSurvey;
import org.tapestry.surveys.TapestrySurveyMap;
import org.tapestry.controller.utils.MisUtils;
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
	
	@RequestMapping(value="/manage_survey", method=RequestMethod.GET)
	public String manageSurvey(@RequestParam(value="failed", required=false) String failed, Boolean deleteFailed,
			ModelMap model, HttpServletRequest request){

		List<SurveyTemplate>  surveyTemplateList = getSurveyTemplates(request);		
		model.addAttribute("survey_templates", surveyTemplateList);
		
		if (deleteFailed != null)
			model.addAttribute("failed", deleteFailed);

		return "admin/manage_survey";
	}	

	@RequestMapping(value="/search_survey", method=RequestMethod.POST)
	public String searchSurvey(@RequestParam(value="failed", required=false) Boolean failed, ModelMap model, SecurityContextHolderAwareRequestWrapper request){
		
		String title = request.getParameter("searchTitle");		
		List<SurveyTemplate>  surveyTemplateList = surveyTemplateDao.getSurveyTemplatesByPartialTitle(title);
		
		model.addAttribute("survey_templates", surveyTemplateList);
	
		if(failed != null) {
			model.addAttribute("failed", true);
		}		 
		
		model.addAttribute("searchTitle", title);
		
		return "admin/manage_survey";
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
   	
   	@RequestMapping(value="/go_assign_survey/{patientId}", method=RequestMethod.GET)
	public String goAssignSurvey(@PathVariable("patientId") int id, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model){
   		HttpSession session = request.getSession();
   		List<SurveyTemplate> surveyTemplates = getSurveyTemplates(request);
   		//Assign Survey in Survey Mangement, it will load all patients in the table with checkbox for later selection
   		if (id == 0)
   		{
   			List<Patient> patients  = MisUtils.getAllPatientsWithFullInfos(patientDao, request);
   			
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
	   			
	   			patients = patientDao.getPatientssByPartialName(name);			
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
		return "admin/assign_survey";
	}
   

	@RequestMapping(value="/assign_surveys", method=RequestMethod.POST)
	public String assignSurveys(SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
		String[] patients = request.getParameterValues("patients[]");
		if(patients == null) {
			return "redirect:/manage_surveys?failed=true";
		}
		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
   		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
   		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
   		int surveyId = Integer.parseInt(request.getParameter("surveyID"));
		SurveyTemplate st = surveyTemplateDao.getSurveyTemplateByID(surveyId);
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
		String name = u.getName();
		int userId = u.getUserID();
		
		SurveyResult surveyResult = surveyResultDao.getSurveyResultByID(id);
		Patient p = patientDao.getPatientByID(surveyResult.getPatientID());
		
		if(surveyResult.getStartDate() == null) {
			surveyResultDao.updateStartDate(id);
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
	
		activityDao.addUserLog(sb.toString(), u);
		
		return "redirect:/show_survey/" + id;
	}
   	
   	@RequestMapping(value="/show_survey/{resultID}", method=RequestMethod.GET)
   	public ModelAndView showSurvey(@PathVariable("resultID") int id, HttpServletRequest request) {   		   		
   		ModelAndView redirectAction = null;
   		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
		SurveyResult surveyResult = surveyResultDao.getSurveyResultByID(id);
		SurveyTemplate surveyTemplate = surveyTemplateDao.getSurveyTemplateByID(surveyResult.getSurveyID());
		
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
   		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
		ArrayList<SurveyTemplate> surveyTemplates = surveyTemplateDao.getAllSurveyTemplates();
		
		TapestrySurveyMap surveys = DoSurveyAction.getSurveyMapAndStoreInSession(request, surveyResults, surveyTemplates);
		PHRSurvey currentSurvey = surveys.getSurvey(Integer.toString(id));
		
		//For activity logging purposes
		User currentUser = userDao.getUserByUsername(request.getRemoteUser());
		SurveyResult surveyResult = surveyResultDao.getSurveyResultByID(id);
		Patient currentPatient = patientDao.getPatientByID(surveyResult.getPatientID());
		Appointment appointment = appointmentDao.getAppointmentByMostRecentIncomplete(currentPatient.getPatientID());
		
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
			surveyResultDao.updateSurveyResults(id, data);
			surveyResultDao.markAsComplete(id);
			
			//user logs
			sb  = new StringBuffer();
			sb.append("Completed survey ");
			sb.append(surveyResult.getSurveyTitle());
			sb.append(" for patient ");
			
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") 
				sb.append( currentPatient.getPreferredName());
			else
				sb.append( currentPatient.getDisplayName());
			
			activityDao.addUserLog(sb.toString(), currentUser);
		}
		
		if (!currentSurvey.isComplete())
		{
			byte[] data = SurveyAction.updateSurveyResult(currentSurvey);
			surveyResultDao.updateSurveyResults(id, data);
			
			//user logs
			sb  = new StringBuffer();
			sb.append("Saved incomplete survey ");
			sb.append(surveyResult.getSurveyTitle());
			sb.append(" for patient ");
			
			if(currentPatient.getPreferredName() != null && currentPatient.getPreferredName() != "") 
				sb.append( currentPatient.getPreferredName());
			else 
				sb.append( currentPatient.getDisplayName());
		
			activityDao.addUserLog(sb.toString(), currentUser);
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
   		List<DisplayedSurveyResult> displayedResults = ResultParser.getDisplayedSurveyResults(res);

   		model.addAttribute("results", displayedResults);
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
   	
   	@RequestMapping(value="/download_survey_template/{surveyID}", method=RequestMethod.GET)
   	@ResponseBody
   	public String downloadSurveyTemplate(@PathVariable("surveyID") int id, HttpServletResponse response){
   		   		
   		SurveyTemplate sTemplate = surveyTemplateDao.getSurveyTemplateByID(id);
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
			surveyTemplateList = surveyTemplateDao.getAllSurveyTemplates();
			
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
			patients = patientDao.getAllPatients();
			
			//save in the session
			if (patients != null && patients.size()>0)
				session.setAttribute("patient_list", patients);
		}
		else
			patients = (List<Patient>)session.getAttribute("patient_list");
		
		return MisUtils.getAllPatientsWithFullInfos(patientDao, request);
   	}
   	
   	//void duplicating survey in result sheet
   	private boolean isExistInSurveyResultList(ArrayList<SurveyResult> surveyResults, int surveyTemplateId, int patientId){
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
   	
   	private void assignSurveysToClient(ArrayList<SurveyTemplate> surveyTemplates, int[] patientIds, 
   			SecurityContextHolderAwareRequestWrapper request) throws JAXBException, DatatypeConfigurationException, Exception{
		
		ArrayList<SurveyResult> surveyResults = surveyResultDao.getAllSurveyResults();
		
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
					String documentId = surveyResultDao.assignSurvey(sr);
					blankSurvey.setDocumentId(documentId);
					surveys.addSurvey(blankSurvey);
					specificSurveys = surveys.getSurveyListById(Integer.toString(st.getSurveyID())); //reload
		    	}
			}   			
		}
   	}
   	
  	private void addSurveyTemplate(String[] surveyId,List<SurveyTemplate> allSurveyTemplates, 
   			ArrayList<SurveyTemplate> selectedSurveyTemplates){
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
   	
   	private void assignSurveysToClient(ArrayList<SurveyTemplate> selectSurveyTemplats, int[] patientIds,
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
