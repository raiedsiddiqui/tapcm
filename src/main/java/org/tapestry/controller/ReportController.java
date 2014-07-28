package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.tapestry.dao.AppointmentDao;
import org.tapestry.dao.PatientDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.SurveyResult;
import org.tapestry.report.AlertManager;
import org.tapestry.report.AlertsInReport;
import org.tapestry.surveys.ResultParser;
import org.tapestry.report.CalculationManager;
import org.yaml.snakeyaml.Yaml;
import org.tapestry.myoscar.utils.*;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;


@Controller
public class ReportController {
protected static Logger logger = Logger.getLogger(AppointmentController.class);
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;

	private PatientDao patientDao;
	private AppointmentDao appointmentDao;	
	private SurveyResultDao surveyResultDao;
	
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

		patientDao = new PatientDao(DB, UN, PW);
		appointmentDao = new AppointmentDao(DB, UN, PW);
		surveyResultDao = new SurveyResultDao(DB, UN, PW);
   	}
 	
	@RequestMapping(value="/view_report/{patientID}", method=RequestMethod.GET)
	public String viewReport(@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletRequest request){
		//should get patient id from session
		
		
		Patient patient = patientDao.getPatientByID(id);
		//call web service to get patient info from myoscar
		String userName = "carolchou.test";
		try{
			PersonTransfer3 personInMyoscar = ClientManager.getClientByUsername(userName);
			StringBuffer sb = new StringBuffer();
			if (personInMyoscar.getStreetAddress1() != null)
				sb.append(personInMyoscar.getStreetAddress1());
			String city = personInMyoscar.getCity();
			if (city != null)
			{
				sb.append(", ");
				sb.append(city);
				patient.setCity(city);
			}
			
			if (personInMyoscar.getProvince() != null)
			{
				sb.append(", ");
				sb.append(personInMyoscar.getProvince());
			}
			
			patient.setAddress(sb.toString());
			
			if (personInMyoscar.getBirthDate() != null)
			{
				Calendar birthDay = personInMyoscar.getBirthDate();
				patient.setBod(Utils.getDateByCalendar(birthDay));
				
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");					
//				patient.setBod(sdf.format(birthday.getTime()));
			}
			
		} catch (Exception e){
			System.err.println("Have some problems when calling myoscar web service");
			
		}
		
		Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
		Report report = new Report();		
		
		//Plan and Key Observations
		String keyObservation = appointmentDao.getKeyObservationByAppointmentId(appointmentId);
		String plan = appointmentDao.getPlanByAppointmentId(appointmentId);
		appointment.setKeyObservation(keyObservation);
		appointment.setPlans(plan);
		
		List<String> pList = new ArrayList<String>();
		if (!Utils.isNullOrEmpty(plan))
			pList = Arrays.asList(plan.split(","));		
		
		Map<String, String> pMap = new TreeMap<String, String>();
		
		for (int i = 1; i<= pList.size(); i++){
			pMap.put(String.valueOf(i), pList.get(i-1));
		}
		
		model.addAttribute("patient", patient);
		model.addAttribute("appointment", appointment);
		model.addAttribute("plans", pMap);
		
		
		//Survey---  goals setting
		List<SurveyResult> surveyResultList = surveyResultDao.getCompletedSurveysByPatientID(id);
		SurveyResult healthGoalsSurvey = new SurveyResult();
		SurveyResult dailyLifeActivitySurvey = new SurveyResult();	
		SurveyResult nutritionSurvey = new SurveyResult();
		SurveyResult rAPASurvey = new SurveyResult();
		SurveyResult mobilitySurvey = new SurveyResult();
		SurveyResult socialLifeSurvey = new SurveyResult();
		SurveyResult generalHealthySurvey = new SurveyResult();
		SurveyResult memorySurvey = new SurveyResult();
		SurveyResult carePlanSurvey = new SurveyResult();
		
		
		for(SurveyResult survey: surveyResultList){
			int surveyId = survey.getSurveyID();
			
			if (surveyId == 10)//Goal Setting survey
				healthGoalsSurvey = survey;
			
			if (surveyId == 11)//Daily life activity survey
				dailyLifeActivitySurvey = survey;
			
			if (surveyId == 7)//Nutrition
				nutritionSurvey = survey;
			
			if (surveyId == 12)//RAPA survey
				rAPASurvey = survey;
			
			if (surveyId == 16)//Mobility survey
				mobilitySurvey = survey;
			
			if (surveyId == 8) //Social Life(Duke Index of Social Support)
				socialLifeSurvey = survey;
			
			if (surveyId == 9) //General Health(Edmonton Frail Scale)
				generalHealthySurvey = survey;
			
			if (surveyId == 14) //Memory Survey
				memorySurvey = survey;
			
			if (surveyId == 15) //Care Plan/Advanced_Directive survey
				carePlanSurvey = survey;
		}
		
		String xml;
		//Healthy Goals
   		try{
   			xml = new String(healthGoalsSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mHealthGoalsSurvey = ResultParser.getResults(xml);
   		List<String> qList = new ArrayList<String>();
   		List<String> questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		//get answer list
		qList = getQuestionList(mHealthGoalsSurvey);   
		
   		Map<String, String> sMap = new TreeMap<String, String>();
   		sMap = getSurveyContentMap(questionTextList, qList);
   		
  		report.setHealthGoals(sMap);
   		
   		//Additional Information
  		//Memory
   		try{
   			xml = new String(memorySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mMemorySurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		
   		//only keep the second and forth question text in the list
   		List<String> displayQuestionTextList = new ArrayList<String>();
   		displayQuestionTextList.add(questionTextList.get(1));
   		displayQuestionTextList.add(questionTextList.get(3));
   		
   		displayQuestionTextList = removeRedundantFromQuestionText(displayQuestionTextList, "of 2");
   	
   		//get answer list
		qList = getQuestionListForMemorySurvey(mMemorySurvey);   					
   		sMap = new TreeMap<String, String>(); 	
   		
   		//Care Plan/Advanced_Directive
   		try{
   			xml = new String(carePlanSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mCarePlanSurvey = ResultParser.getResults(xml);

   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		
   		//take 3 question text from the list
   		for (int i = 1; i <= 3; i++)
   			displayQuestionTextList.add(questionTextList.get(i));
   		
   		displayQuestionTextList = removeRedundantFromQuestionText(displayQuestionTextList, "of 3");
   		
   		//get answer list   		
   		qList.addAll(getQuestionList(mCarePlanSurvey));   	
   		
   		sMap = getSurveyContentMapForMemorySurvey(displayQuestionTextList, qList);
   		report.setAdditionalInfos(sMap);	  			
   			
   			
   		//Daily Life Activities
   		try{
   			xml = new String(dailyLifeActivitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mDailyLifeActivitySurvey = ResultParser.getResults(xml);
   		questionTextList = new ArrayList<String>();
   		questionTextList = ResultParser.getSurveyQuestions(xml);   		
   		
   		qList = new ArrayList<String>();
   		qList = getQuestionList(mDailyLifeActivitySurvey);
   		
   		//last question in Daily life activity survey is about falling stuff
   		List<String> lAlert = new ArrayList<String>();
   		String fallingQA = qList.get(qList.size() -1);
   		if (fallingQA.contains("yes")||fallingQA.contains("fall"))
   			lAlert.add(AlertsInReport.DAILY_ACTIVITY_ALERT);   		
   		   		
   		sMap = new TreeMap<String, String>();
   		sMap = getSurveyContentMap(questionTextList, qList);
   		
   		report.setDailyActivities(sMap);   		
   		
 		//General Healthy Alert
   		try{
   			xml = new String(generalHealthySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mGeneralHealthySurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = getQuestionList(mGeneralHealthySurvey);
		
		int generalHealthyScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getGeneralHealthyAlerts(generalHealthyScore, lAlert);
		
		//Social Life Alert
		try{
   			xml = new String(socialLifeSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mSocialLifeSurvey = ResultParser.getResults(xml);
		qList = new ArrayList<String>();   		
   		//get answer list
		qList = getQuestionList(mSocialLifeSurvey);
		
		int socialLifeScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getSocialLifeAlerts(socialLifeScore, lAlert);
   		
   		//Nutrition Alerts   		
   		try{
   			xml = new String(nutritionSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mNutritionSurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = getQuestionList(mNutritionSurvey);  

		//get scores for nutrition survey based on answer list
		int nutritionScore = CalculationManager.getScoreByQuestionsList(qList);
		
		//high nutrition risk alert
		Map<String, String> nAlert = new TreeMap<String, String>();
		lAlert = AlertManager.getNutritionAlerts(nutritionScore, lAlert, qList);
		
		//set alerts in report bean
		if (nAlert != null && nAlert.size()>0)	
			report.setAlerts(lAlert);
		else
			report.setAlerts(null);
		
		//RAPA Alert
		try{
   			xml = new String(rAPASurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mRAPASurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = getQuestionList(mRAPASurvey);  		

		int rAPAScore = CalculationManager.getScoreForRAPA(qList);
		if (rAPAScore < 6)
			lAlert.add(AlertsInReport.PHYSICAL_ACTIVITY_ALERT);
		
		//Mobility Alerts
		try{
   			xml = new String(mobilitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mMobilitySurvey = ResultParser.getResults(xml);
   		Map<String, String> qMap = getQuestionMap(mMobilitySurvey);  
   		   		
   		lAlert = AlertManager.getMobilityAlerts(qMap, lAlert);   
		
		//send message to MyOscar test
//		try{
//			Long lll = ClientManager.sentMessageToPatientInMyOscar(new Long(15231), "Message From Tapestry", "Hello");
//			System.out.println("lll is === "+ lll);
//			
//		} catch (Exception e){
//			System.out.println("something wrong with myoscar server");
//			e.printStackTrace();
//		}
		
		
		report.setAlerts(lAlert);
		//end of alert
		
		//get volunteer information
		String volunteer = appointment.getVolunteer();
		String partner = appointment.getPartner();
		String comments = appointment.getComments();
				
		Map<String, String> vMap = new TreeMap<String, String>();
		
		if (!Utils.isNullOrEmpty(volunteer))
			vMap.put(" 1", volunteer);
		else
			vMap.put(" 1", "");
		
		if (!Utils.isNullOrEmpty(partner))
			vMap.put(" 2", partner);
		else
			vMap.put(" 2", "");
		
		if (!Utils.isNullOrEmpty(comments))
			vMap.put(" C", comments);					
		else
			vMap.put(" C", " ");
		
		report.setVolunteerInformations(vMap);
		
		model.addAttribute("report", report);
		return "/admin/view_report";
	}	
	

	private Map<String, String> getSurveyContentMap(List<String> questionTextList, List<String> questionAnswerList){
		Map<String, String> content;
		
		if (questionTextList != null && questionTextList.size() > 0)
   		{//remove the first element which is description about whole survey
   			questionTextList.remove(0);
   			
   	   		if (questionAnswerList != null && questionAnswerList.size() > 0)
   	   		{//remove the first element which is empty or "-"
	   	   		questionAnswerList.remove(0);
	   	   			
	   	   		content = new TreeMap<String, String>(); 
		   	   	StringBuffer sb;
	   	   		
	   	   		for (int i = 0; i < questionAnswerList.size(); i++){
	   	   			sb = new StringBuffer();
	   	   			sb.append(questionTextList.get(i));
	   	   			sb.append("<br/><br/>");
	   	   			sb.append("\"");
	   	   			sb.append(questionAnswerList.get(i));
	   	   			sb.append("\"");
	   	   			content.put(String.valueOf(i + 1), sb.toString());
	   	   		}	   	   		
	   	   		return content;
   	   		}
   	   		else
   	   			System.out.println("All answers in Goal Setting survey are empty!");   	   			
   		}   			
   		else
   			System.out.println("Bad thing happens, no question text found for this Goal Setting survey!");
		
		return null;   	
	}
	
	private Map<String, String> getSurveyContentMapForMemorySurvey(List<String> questionTextList, List<String> questionAnswerList){
		Map<String, String> displayContent = new TreeMap<String, String>();
		int size = questionTextList.size();
		Object answer;
		String questionText;
		
		if (questionAnswerList.size() == size)
		{
			for (int i = 0; i < size; i++)
			{
				questionText = questionTextList.get(i).toString();
				
				answer = questionAnswerList.get(i);
				if ((answer != null) && (answer.toString().equals("1")))
					displayContent.put(questionText, "YES");					
				else
					displayContent.put(questionText, "NO");			
			}
			
			return displayContent;
		}
		else
			System.out.println("Bad thing happens");
		
		return null;   	
	}
	
	private List<String> removeRedundantFromQuestionText(List<String> list, String redundantStr){
		String str;
		int index;		
		
		for (int i = 0 ; i < list.size(); i ++)
		{
			str = list.get(i).toString();
			index = str.indexOf(redundantStr);
			if (index > 0)
			{
				str = str.substring(index + 4);
				list.set(i, str);
			}
		}
		
		return list;
	}
	
	//remove observer notes from answer
	private List<String> getQuestionList(LinkedHashMap<String, String> questionMap) {
		List<String> qList = new ArrayList<String>();
		String question;
		int index;
		
		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
   		    String key = entry.getKey();
   		    
   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	
   		    	index = question.indexOf("/observernote/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(0, index);
   		    	
   		    	if (!question.equals("-"))
   		    		qList.add(question);   		    	
   		    }
   		}		
		return qList;
	}
	
	private List<String> getQuestionListForMemorySurvey(LinkedHashMap<String, String> questionMap){
		List<String> qList = new ArrayList<String>();
		String question;
		int index;
		
		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
   		    String key = entry.getKey();
   		
   		    if ((key.equalsIgnoreCase("YM1"))||(key.equalsIgnoreCase("YM2")))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	
   		    	//remove observer notes
   		    	index = question.indexOf("/observernote/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(0, index);   		    	
   		    	qList.add(question); 
   		    }   		   
   		}		
		return qList;
	}
	//remove observer notes and other not related to question/answer
	private Map<String, String> getQuestionMap(LinkedHashMap<String, String> questions){
		Map<String, String> qMap = new LinkedHashMap<String, String>();		
		String question;
		int index;
		
		for (Map.Entry<String, String> entry : questions.entrySet()) {
   		    String key = entry.getKey();
   		    
   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	
   		    	index = question.indexOf("/observernote/");
   		    	
   		    	if (index > 0)
   		    		question = question.substring(0, index);
   		    	
   		    	if (!question.equals("-"))
   		    		qMap.put(key, question);    	
   		    }
   		}
		return qMap;
	}

}
