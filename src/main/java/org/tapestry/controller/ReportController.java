package org.tapestry.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
//import org.tapestry.dao.ActivityDao;
import org.tapestry.dao.AppointmentDao;
//import org.tapestry.dao.MessageDao;
//import org.tapestry.dao.NarrativeDao;
import org.tapestry.dao.PatientDao;
//import org.tapestry.dao.UserDao;
import org.tapestry.dao.SurveyResultDao;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.SurveyResult;
import org.tapestry.report.AlertsInReport;
//import org.tapestry.surveys.DoSurveyAction;
import org.tapestry.surveys.ResultParser;
import org.yaml.snakeyaml.Yaml;


@Controller
public class ReportController {
protected static Logger logger = Logger.getLogger(AppointmentController.class);
	
	private ClassPathResource dbConfigFile;
	private Map<String, String> config;
	private Yaml yaml;

	private PatientDao patientDao;
	private AppointmentDao appointmentDao;
	
	private SurveyResultDao surveyResultDao;
	
	
   	//Mail-related settings;
   	private Properties props;
   	private String mailAddress = "";
   	private Session session;
	
	/**
   	 * Reads the file /WEB-INF/classes/db.yaml and gets the values contained therein
   	 */
   	@PostConstruct
   	public void readDatabaseConfig(){
   		String DB = "";
   		String UN = "";
   		String PW = "";
   		String mailHost = "";
   		String mailUser = "";
   		String mailPassword = "";
   		String mailPort = "";
   		String useTLS = "";
   		String useAuth = "";
		try{
			dbConfigFile = new ClassPathResource("tapestry.yaml");
			yaml = new Yaml();
			config = (Map<String, String>) yaml.load(dbConfigFile.getInputStream());
			DB = config.get("url");
			UN = config.get("username");
			PW = config.get("password");
			mailHost = config.get("mailHost");
			mailUser = config.get("mailUser");
			mailPassword = config.get("mailPassword");
			mailAddress = config.get("mailFrom");
			mailPort = config.get("mailPort");
			useTLS = config.get("mailUsesTLS");
			useAuth = config.get("mailRequiresAuth");
		} catch (IOException e) {
			System.out.println("Error reading from config file");
			System.out.println(e.toString());
		}

		patientDao = new PatientDao(DB, UN, PW);
		appointmentDao = new AppointmentDao(DB, UN, PW);
		surveyResultDao = new SurveyResultDao(DB, UN, PW);
		
		//Mail-related settings
		final String username = mailUser;
		final String password = mailPassword;
		props = System.getProperties();
		session = Session.getDefaultInstance(props, 
				 new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
		  		});
		props.setProperty("mail.smtp.host", mailHost);
		props.setProperty("mail.smtp.socketFactory.port", mailPort);
		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.auth", useAuth);
		props.setProperty("mail.smtp.starttls.enable", useTLS);
		props.setProperty("mail.user", mailUser);
		props.setProperty("mail.password", mailPassword);
   	}
 	
	@RequestMapping(value="/view_report/{patientID}", method=RequestMethod.GET)
	public String viewReport(@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletRequest request){
		Patient patient = patientDao.getPatientByID(id);
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
		
		//Survey---  goal setting
		List<SurveyResult> surveyResultList = surveyResultDao.getCompletedSurveysByPatientID(id);
		SurveyResult healthGoalsSurvey = new SurveyResult();
		SurveyResult dailyLifeActivitySurvey = new SurveyResult();	
		SurveyResult nutritionSurvey = new SurveyResult();
		SurveyResult rAPASurvey = new SurveyResult();
		SurveyResult mobilitySurvey = new SurveyResult();
		
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
   		
   		//Survey--- daily life activity 
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
   			lAlert.add(AlertsInReport.getDailyActiveityAlertMsg());
   		
   		   		
   		sMap = new TreeMap<String, String>();
   		sMap = getSurveyContentMap(questionTextList, qList);
   		
   		report.setDailyActivities(sMap);   		
   		
   		//Nutrition survey   		
   		try{
   			xml = new String(nutritionSurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mNutritionSurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = getQuestionList(mNutritionSurvey);  
		String ans ="";
		int scores = 0;
		int iAns = 0;
		
		
		for (int i=0; i< qList.size(); i++){
			ans = qList.get(i);
			//get score for high nutrition risk alert in nutrition
			if (!Utils.isNullOrEmpty(ans) && !ans.contains("-"))
			{
				iAns = Integer.parseInt(ans.trim());
				scores = scores + iAns;
			}			
		}
		//high nutrition risk alert
		Map<String, String> nAlert = new TreeMap<String, String>();
		if (scores < 50)
			lAlert.add(AlertsInReport.getNutritionAlertMsg1());
				
		//weight alert-- the first question in nutrition survey
		ans = qList.get(1);
		ans = ans.trim();		
		
		if (!Utils.isNullOrEmpty(ans)) {
			if (ans.equals("2"))				
				lAlert.add(AlertsInReport.getNutritionAlertMsg2a());			
			else if (ans.equals("3"))
				lAlert.add(AlertsInReport.getNutritionAlertMsg2b());
			else if (ans.equals("6"))
				lAlert.add(AlertsInReport.getNutritionAlertMsg2c());
		}
		
		//meal alert -- forth question in nutrition survey
		ans = qList.get(4);
		ans = ans.trim();
		if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
			lAlert.add(AlertsInReport.getNutritionAlertMsg3());
		
		//Appetitive alert --- sixth question in nutrition survey
		ans = qList.get(6);
		ans = ans.trim();
		if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
			lAlert.add(AlertsInReport.getNutritionAlertMsg4());
		
		//cough, choke and pain alert --- ninth question in nutrition survey
		ans = qList.get(11);
		ans = ans.trim();
		if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
			lAlert.add(AlertsInReport.getNutritionAlertMsg5());	
		//set alerts in report bean
		if (nAlert != null && nAlert.size()>0)	
			report.setAlerts(lAlert);
		else
			report.setAlerts(null);
		
		//RAPA survey
		try{
   			xml = new String(rAPASurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mRAPASurvey = ResultParser.getResults(xml);
   		qList = new ArrayList<String>();   		
   		//get answer list
		qList = getQuestionList(mRAPASurvey);  
		
		int rAPAScore = 0;
		Integer[] answerArray = new Integer[6];
		//set answer into an array
		for(int i = 0; i < qList.size() - 2; i++)
			answerArray[i] = Integer.valueOf(qList.get(i));	
		
		Integer[] otherAnswers = removeFirstElementInArray(answerArray);
		
		if ((answerArray[0].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			rAPAScore = 2;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[1].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			rAPAScore = 3;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[2].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			rAPAScore = 4;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[3].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			rAPAScore = 5;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[4].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			rAPAScore = 6;		
	
		if (answerArray[5].intValue() == 1) 
			rAPAScore = 7;
		
		if (rAPAScore < 6)
			lAlert.add(AlertsInReport.getPhysicalActivityAlertMsg());
		
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
	
	private boolean isAllOtherAnswersNo(Integer[] answers){
		boolean allNo = true;
		for(Integer a: answers){
			if (a.equals(1))
			{
				allNo = false;
				break;
			}
		}
		
		return allNo;
	}
	
	private Integer[] removeFirstElementInArray(Integer[] array){		
		final Integer[] EMPTY_Integer_ARRAY = new Integer[0];
		//convert array to ArrayList
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(array));
		//remove first element from list
		list.remove(0);	
		
		return list.toArray(EMPTY_Integer_ARRAY);
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

}
