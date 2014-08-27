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
import javax.servlet.http.HttpServletResponse;

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
import org.tapestry.report.ScoresInReport;
import org.tapestry.surveys.ResultParser;
import org.tapestry.report.CalculationManager;
import org.yaml.snakeyaml.Yaml;
import org.tapestry.myoscar.utils.*;
import org.oscarehr.myoscar_server.ws.PersonTransfer3;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.List;


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
 	
	@RequestMapping(value="/downlad_report/{patientID}", method=RequestMethod.GET)
	public String downladReport(@PathVariable("patientID") int id,
			@RequestParam(value="appointmentId", required=true) int appointmentId, 	
			ModelMap model, HttpServletResponse response){
	
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
			}
			
		} catch (Exception e){
			System.err.println("Have some problems when calling myoscar web service");
			
		}
				
		Appointment appointment = appointmentDao.getAppointmentById(appointmentId);
		Report report = new Report();		
		ScoresInReport scores = new ScoresInReport();
		
		report.setPatient(patient);
		
		//Plan and Key Observations
		String keyObservation = appointmentDao.getKeyObservationByAppointmentId(appointmentId);
		String plan = appointmentDao.getPlanByAppointmentId(appointmentId);
		appointment.setKeyObservation(keyObservation);
		appointment.setPlans(plan);
		
		report.setAppointment(appointment);

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
		SurveyResult goals = new SurveyResult();		
		
		for(SurveyResult survey: surveyResultList){			
			String title = survey.getSurveyTitle();
			
			if (title.equalsIgnoreCase("Goal Setting"))//Goal Setting survey
				healthGoalsSurvey = survey;
			
			if (title.equalsIgnoreCase("Daily Life Activities"))//Daily life activity survey
				dailyLifeActivitySurvey = survey;
			
			if (title.equalsIgnoreCase("Screen II"))//Nutrition
				nutritionSurvey = survey;
			
			if (title.equalsIgnoreCase("Rapid Assessment of Physical Activity"))//RAPA survey
				rAPASurvey = survey;
			
			if (title.equalsIgnoreCase("Mobility Survey"))//Mobility survey
				mobilitySurvey = survey;
			
			if (title.equalsIgnoreCase("Social Life")) //Social Life(Duke Index of Social Support)
				socialLifeSurvey = survey;
			
			if (title.equalsIgnoreCase("General Health")) //General Health(Edmonton Frail Scale)
				generalHealthySurvey = survey;
			
			if (title.equalsIgnoreCase("Memory")) //Memory Survey
				memorySurvey = survey;
			
			if (title.equalsIgnoreCase("Advance_Directives")) //Care Plan/Advanced_Directive survey
				carePlanSurvey = survey;
			
			if (title.equalsIgnoreCase("GAS"))
				goals = survey;				
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
   		if ((questionTextList != null) && (questionTextList.size() > 0))
   		{
   			List<String> displayQuestionTextList = new ArrayList<String>();
   	   		displayQuestionTextList.add(removeObserverNotes(questionTextList.get(1)));
   	   		displayQuestionTextList.add(removeObserverNotes(questionTextList.get(3)));
   	   		
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
   	   			displayQuestionTextList.add(removeObserverNotes(questionTextList.get(i)));
   	   		
   	   		displayQuestionTextList = removeRedundantFromQuestionText(displayQuestionTextList, "of 3");
   	   		
   	   		//get answer list   		
   	   		qList.addAll(getQuestionList(mCarePlanSurvey));   	
   	   		
   	   		sMap = getSurveyContentMapForMemorySurvey(displayQuestionTextList, qList);
   	   		report.setAdditionalInfos(sMap);
   		}
   			
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
   		if (fallingQA.startsWith("yes")||fallingQA.startsWith("Yes"))
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
		
		//get score info for Summary of tapestry tools
		if ((qList != null)&&(qList.size()>10))
		{
			if ("1".equals(qList.get(0))) 
				scores.setClockDrawingTest("No errors");
			else if ("2".equals(qList.get(0))) 
				scores.setClockDrawingTest("Minor spacing errors");
			else if ("3".equals(qList.get(0))) 
				scores.setClockDrawingTest("Other errors");
			else 
				scores.setClockDrawingTest("Not done");
			
			if ("1".equals(qList.get(10))) 
				scores.setTimeUpGoTest("1 (0-10s)");
			else if ("2".equals(qList.get(10))) 
				scores.setTimeUpGoTest("2 (11-20s)");
			else if ("3".equals(qList.get(10))) 
				scores.setTimeUpGoTest("3 (More than 20s)");
			else if ("4".equals(qList.get(10))) 
				scores.setTimeUpGoTest("4 (Patient required assistance)");
			else 
				scores.setTimeUpGoTest("5 (Patient is unwilling)");
		}
		
		int generalHealthyScore = CalculationManager.getScoreByQuestionsList(qList);
		lAlert = AlertManager.getGeneralHealthyAlerts(generalHealthyScore, lAlert);
		
		if (generalHealthyScore < 5)
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Robust)");
		else if (generalHealthyScore < 7)
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Apparently Vulnerable)");
		else
			scores.setEdmontonFrailScale(String.valueOf(generalHealthyScore) + " (Frail)");		
	
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
		
		//summary tools for social supports
		if ((qList != null)&&(qList.size()>0))
		{
			int satisfactionScore = CalculationManager.getScoreByQuestionsList(qList.subList(0, 6));
			scores.setSocialSatisfication(satisfactionScore);
			int networkScore = CalculationManager.getScoreByQuestionsList(qList.subList(6, 10));
	   		scores.setSocialNetwork(networkScore);
		}
   		   		
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
		if ((qList != null)&&(qList.size()>0))
		{
			int nutritionScore = CalculationManager.getScoreByQuestionsList(qList);
			scores.setNutritionScreen(nutritionScore);
			
			//high nutrition risk alert
			Map<String, String> nAlert = new TreeMap<String, String>();
			lAlert = AlertManager.getNutritionAlerts(nutritionScore, lAlert, qList);
			
			//set alerts in report bean
			if (nAlert != null && nAlert.size()>0)	
				report.setAlerts(lAlert);
			else
				report.setAlerts(null);
		}
		
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
		
		scores.setPhysicalActivity(rAPAScore);
				
		//Mobility Alerts
		try{
   			xml = new String(mobilitySurvey.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
		
		LinkedHashMap<String, String> mMobilitySurvey = ResultParser.getResults(xml);
   		Map<String, String> qMap = getQuestionMap(mMobilitySurvey);  
   		   		
   		lAlert = AlertManager.getMobilityAlerts(qMap, lAlert);    		
   		
   		//summary tools for Mobility
   		for (int i = 0; i < lAlert.size(); i++)
   		{
   			if (lAlert.get(i).contains("2.0"))
   				scores.setMobilityWalking2(lAlert.get(i));
   			
   			if (lAlert.get(i).contains("0.5"))
   				scores.setMobilityWalkingHalf(lAlert.get(i));
   			
   			if (lAlert.get(i).contains("climbing"))
   				scores.setMobilityClimbing(lAlert.get(i));   			
   		}
   		
   		String noLimitation = "No Limitation";   		
   		if (Utils.isNullOrEmpty(scores.getMobilityWalking2()))
   			scores.setMobilityWalking2(noLimitation);
   		
   		if (Utils.isNullOrEmpty(scores.getMobilityWalkingHalf()))
   			scores.setMobilityWalkingHalf(noLimitation);
   		
   		if (Utils.isNullOrEmpty(scores.getMobilityClimbing()))
   			scores.setMobilityClimbing(noLimitation);
   		
   		report.setScores(scores);
   		model.addAttribute("scores", scores);
		
		report.setAlerts(lAlert);
		//end of alert
		try{
   			xml = new String(goals.getResults(), "UTF-8");
   		} catch (Exception e) {
   			xml = "";
   		}
   		
   		LinkedHashMap<String, String> mGoals = ResultParser.getResults(xml);
   	
   		questionTextList = ResultParser.getSurveyQuestions(xml);
   		//get answer list
		qList = getQuestionList(mGoals);   
		
		List<String> gAS = new ArrayList<String>();
		if ((qList != null) && (qList.size()>0))
		{
			String lifeGoals = "Life Goals: " + qList.get(1);
			gAS.add(lifeGoals);
			String healthyGoals = "Health Goals : " + qList.get(4);
			gAS.add(healthyGoals);
			
			report.setPatientGoals(gAS);
		}
		
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
			vMap.put(" V", comments);					
		else
			vMap.put(" V", " ");
		
		report.setVolunteerInformations(vMap);
		
		model.addAttribute("report", report);
	//	return "/admin/view_report";
		buildPDF(report, response);
	
		return null;
	}	
	
	private void buildPDF(Report report, HttpServletResponse response){		
		String patientName = report.getPatient().getFirstName() + " " + report.getPatient().getLastName();
		String orignalFileName= patientName +"_report.pdf";
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 36, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter.getInstance(document, response.getOutputStream());

			document.open();
	            
			Image imageFhs = Image.getInstance("webapps/tapestry/resources/images/fhs.png");
			imageFhs.scalePercent(25f);
			imageFhs.setAbsolutePosition(450, PageSize.A4.getHeight() - imageFhs.getScaledHeight());	
			document.add(imageFhs);	            
	            
			Image imageLogo = Image.getInstance("webapps/tapestry/resources/images/logo.png"); 
			imageLogo.scalePercent(25f);
			imageLogo.setAbsolutePosition(50, PageSize.A4.getHeight() - imageFhs.getScaledHeight());	            

			document.add(imageLogo);
	            
			Image imageDegroote = Image.getInstance("webapps/tapestry/resources/images/degroote.png");
			imageDegroote.scalePercent(25f);
			imageDegroote.setAbsolutePosition(250, PageSize.A4.getHeight() - imageFhs.getScaledHeight());	
			document.add(imageDegroote);
	            
			document.add(new Phrase("    "));
			document.add(new Phrase("    "));
			document.add(new Phrase("    "));
	            
			//tapestry report		        
			//Font setup
			//white font			
			Font wbLargeFont = new Font(Font.FontFamily.HELVETICA  , 20, Font.BOLD);
			wbLargeFont.setColor(BaseColor.WHITE);
			Font wMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);
			wMediumFont.setColor(BaseColor.WHITE);
			//red font
			Font rbFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
			rbFont.setColor(BaseColor.RED);
			
			Font rmFont = new Font(Font.FontFamily.HELVETICA, 16);
			rmFont.setColor(BaseColor.RED);
			
			Font rFont = new Font(Font.FontFamily.HELVETICA, 20);
			rFont.setColor(BaseColor.RED);
		        
			Font rMediumFont = new Font(Font.FontFamily.HELVETICA, 12);
			rMediumFont.setColor(BaseColor.RED);
		        
			Font rSmallFont = new Font(Font.FontFamily.HELVETICA, 8);
			rSmallFont.setColor(BaseColor.RED);
			//green font
			Font gbMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			gbMediumFont.setColor(BaseColor.GREEN);
			Font gbSmallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
			gbSmallFont.setColor(BaseColor.GREEN);
			//black font
			Font sFont = new Font(Font.FontFamily.HELVETICA, 9);	
			Font sbFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);	
			Font mFont = new Font(Font.FontFamily.HELVETICA, 12);		        
			Font bMediumFont = new Font(Font.FontFamily.HELVETICA , 16, Font.BOLD);		        
			Font iSmallFont = new Font(Font.FontFamily.HELVETICA , 9, Font.ITALIC );
			Font ibMediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC);
			Font bmFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Font blFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
		
			//Patient info
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
	        
			float[] columWidths = {1f, 2f};
			table.setWidths(columWidths);
			
			PdfPCell cell;
			
			cell = new PdfPCell(new Phrase(patientName, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);		
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Address: 11 hunter Street S, Hamilton, On" + report.getPatient().getAddress(), sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	            
			table.addCell(cell);
		     
			cell = new PdfPCell(new Phrase("MRP: David Chan", sbFont));
			cell.setBorderWidthLeft(1f);		        
			cell.setBorderWidthTop(0);	          
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);
		        
			table.addCell(cell);
		        
			cell = new PdfPCell( new Phrase("Date of visit: " + report.getAppointment().getDate(), sbFont));
			cell.setBorderWidthRight(1f);		        
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);
			table.addCell(cell);
		        
			cell = new PdfPCell(new Phrase("Time: " + report.getAppointment().getTime(), sbFont));
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);		        
			table.addCell(cell);
		        
			cell = new PdfPCell(new Phrase("Visit: " + report.getAppointment().getStrType(), sbFont));
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);	          
			table.addCell(cell);
		        
			document.add(table);		   	        
			//Patient Info	
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("TAPESTRY REPORT: --- (0000-00-00)", blFont));
			cell.setBorder(0);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("PATIENT GOAL(S)", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	            
	            
			table.addCell(cell);
	            
			for (int i = 0; i < report.getPatientGoals().size(); i++){
				cell = new PdfPCell(new Phrase(report.getPatientGoals().get(i).toString()));
				table.addCell(cell);
			}	            
			document.add(table);	           
			//alert
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
	            
			Phrase comb = new Phrase(); 
			comb.add(new Phrase("     ALERT :", rbFont));
			comb.add(new Phrase(" Consider Case Review wirh IP-TEAM", wbLargeFont));	    
			cell.addElement(comb);
			cell.setBackgroundColor(BaseColor.BLACK);	           
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			List<String> alerts = report.getAlerts(); 
	         	            
			for (int i =0; i<alerts.size(); i++){
				cell = new PdfPCell(new Phrase(" . " + alerts.get(i).toString(), rmFont));
				table.addCell(cell);
			}
			document.add(table);
	            
			//Key observation
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
	            
			cell = new PdfPCell(new Phrase("KEY OBSERVATIONS by Volunteer", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	 
	            
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getKeyObservation()));
			table.addCell(cell);
			document.add(table);	           
	            
			//Plan
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("PLAN", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			List<String> pList = new ArrayList<String>();
			if (!Utils.isNullOrEmpty(report.getAppointment().getPlans()))
				pList = Arrays.asList(report.getAppointment().getPlans().split(","));		
	    		
			Map<String, String> pMap = new TreeMap<String, String>();
	    		
			for (int i = 1; i<= pList.size(); i++){
				pMap.put(String.valueOf(i), pList.get(i-1));
			}
	    		
			for (Map.Entry<String, String> entry : pMap.entrySet()) {
				cell = new PdfPCell(new Phrase(entry.getKey()));
				table.addCell(cell);	            	
	            	
				cell = new PdfPCell(new Phrase(entry.getValue()));
				table.addCell(cell); 
			}	                        
	            
			float[] cWidths = {1f, 18f};
			table.setWidths(cWidths);
			document.add(table);
			document.add(new Phrase("    "));
	           
			//Additional Information
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("ADDITIONAL INFORMATION", wbLargeFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setColspan(2);
			table.addCell(cell);
	            
			for (Map.Entry<String, String> entry : report.getAdditionalInfos().entrySet()) {
				if ("YES".equalsIgnoreCase(entry.getValue())){	            		
					cell = new PdfPCell(new Phrase(entry.getKey(), rMediumFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), rMediumFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}
				else{
					cell = new PdfPCell(new Phrase(entry.getKey(), mFont));		            	
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell);	            	
		            	
					cell = new PdfPCell(new Phrase(entry.getValue(), mFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setPaddingBottom(5);
					table.addCell(cell); 
				}           	
	            	
			}
			float[] aWidths = {24f, 3f};
			table.setWidths(aWidths);
			document.add(table);
			document.add(new Phrase("    "));
	            
			//Summary of Tapestry tools
			table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{1.2f, 2f, 2f});
			cell = new PdfPCell(new Phrase("Summary of TAPESTRY Tools", wbLargeFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			cell.setColspan(3);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DOMAIN", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("SCORE", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("DESCRIPTION", wMediumFont));
			cell.setBackgroundColor(BaseColor.BLACK);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setFixedHeight(28f);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Functional Status", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	           
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(45f);
			table.addCell(cell);	            
	           
			StringBuffer sb = new StringBuffer();
			sb.append("Clock drawing test: ");
			sb.append(report.getScores().getClockDrawingTest());
			sb.append("\n");
			sb.append("Timed up-and-go test score = ");
			sb.append(report.getScores().getTimeUpGoTest());
			sb.append("\n");
			sb.append("Edmonton Frail Scale sore = ");
			sb.append(report.getScores().getEdmontonFrailScale());	
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			Phrase p = new Phrase();
			Chunk underline = new Chunk("Edmonton Frail Scale (Score Key):", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	 
			p.add(underline);
	            
			sb = new StringBuffer();	           
			sb.append(" ");
			sb.append("\n");
			sb.append("Robust: 0-4");
			sb.append("\n");
			sb.append("Apparently Vulnerable: 5-6");
			sb.append("\n");
			sb.append("Frail: 7-17");
			sb.append("\n");

			p.add(new Chunk(sb.toString(), sFont));

			cell = new PdfPCell(p);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	    
			cell.setNoWrap(false);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Nutritional Status", mFont));
	            	           
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(35f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Screen II score : ");
			sb.append(report.getScores().getNutritionScreen());
			sb.append("\n");	           
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();
			underline = new Chunk("Screen II Nutrition Screening Tool:", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline);
	           
			sb = new StringBuffer();
			sb.append(" ");
			sb.append("\n");
			sb.append("Max Score = 64");
			sb.append("\n");
			sb.append("High Risk < 50");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Social Support", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(55f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Satisfaction score =  ");
			sb.append(report.getScores().getSocialSatisfication());
			sb.append("\n");	
			sb.append("Network score = ");
			sb.append(report.getScores().getSocialNetwork());
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			p = new Phrase();	   
			underline = new Chunk("Screen II Nutrition Screening Tool:", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location	  
			p.add(underline); 	     
			p.add(new Chunk("\n"));
			p.add(new Chunk("(Score < 10 risk cut off)", iSmallFont));
	            
			sb = new StringBuffer();
			sb.append(" ");	            	            
			sb.append("\n");
			sb.append("Perceived satisfaction with behavioural or");
			sb.append("\n");
			sb.append("emotional support obtained from this network");
			sb.append("\n");
			p.add(new Chunk(sb.toString(), sFont));
	            
			underline = new Chunk("Network score range : 4-12", sFont);
			underline.setUnderline(0.1f, -1f); //0.1 thick, -1 y-location
			p.add(underline);
	            	            
			sb = new StringBuffer();
			sb.append("\n");
			sb.append("Size and structure of social network");
			sb.append("\n");	
			p.add(new Chunk(sb.toString(), sFont));
	            
			cell = new PdfPCell(p);
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			////Mobility
			PdfPTable nest_table1 = new PdfPTable(1);
			
			cell = new PdfPCell(new Phrase("Mobility ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);		         
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Walking 2.0 km ", sFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	   
			cell.setBorder(0);
	            
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Walking 0.5 km ", sFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	           
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Climbing Stairs ", sFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	 
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	            
			cell.setBorderWidthRight(0);	
			nest_table1.addCell(cell);
	            
			PdfPTable nest_table2 = new PdfPTable(1);
	            
			cell = new PdfPCell(new Phrase(" ", mFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			nest_table2.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getScores().getMobilityWalking2(), iSmallFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	
			cell.setBorder(0);	            	
			nest_table2.addCell(cell);
			
			cell = new PdfPCell(new Phrase(report.getScores().getMobilityWalkingHalf(), iSmallFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	           
			cell.setBorderWidthRight(0);
			nest_table2.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getScores().getMobilityClimbing(), iSmallFont));	               
			cell.setVerticalAlignment(Element.ALIGN_TOP);	   
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	            
			cell.setBorderWidthRight(0);	
			nest_table2.addCell(cell);
	            
			table.addCell(nest_table1);
			table.addCell(nest_table2);
	            	            	            
			sb = new StringBuffer();
			sb.append("MANTY:");
			sb.append("\n");
			sb.append("No Limitation");
			sb.append("\n");
			sb.append("Preclinical Limitation");
			sb.append("\n");
			sb.append("Minor Manifest Limitation");
			sb.append("\n");
			sb.append("Major Manifest Limitation");
			sb.append("\n");	          
			sb.append("\n");	            
	            
			cell = new PdfPCell(new Phrase(sb.toString(), sFont));
			cell.setNoWrap(false);	         
			table.addCell(cell);   
	            
			//RAPA	            
			cell = new PdfPCell(new Phrase("Physical Activity", mFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	      
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setMinimumHeight(45f);
			table.addCell(cell);            
	           
			sb = new StringBuffer();
			sb.append("Score =  ");
			sb.append(report.getScores().getPhysicalActivity());
			sb.append("\n");	
			sb.append("\n");	            
			sb.append("\n");
	            
			cell = new PdfPCell(new Phrase(sb.toString(), iSmallFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			cell.setNoWrap(false);
			table.addCell(cell);
	            
			sb = new StringBuffer();
			sb.append("Rapid Assessment of Physical Activity(RAPA)");
			sb.append("\n");
			sb.append("Score range : 1-7");
			sb.append("\n");
			sb.append("Score < 6 Suboptimal Activity(Aerobic)");
			sb.append("\n");	            
			sb.append("\n");	            
	            
			cell = new PdfPCell(new Phrase(sb.toString(), sFont));
			cell.setNoWrap(false);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);	 
			table.addCell(cell);
	            
			document.add(table);
			document.add(new Phrase("    "));
	            
			//Tapestry Questions
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("TAPESTRY QUESTIONS", bMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			for (Map.Entry<String, String> entry : report.getDailyActivities().entrySet()) {
				cell = new PdfPCell(new Phrase(entry.getKey(), sFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);	            	
	            
				cell = new PdfPCell(new Phrase(entry.getValue(), sFont));
				table.addCell(cell); 
			}
	           
			table.setWidths(cWidths);
			document.add(table);

			//Volunteer Information
			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			cell = new PdfPCell(new Phrase("VOLUNTEER INFORMATION & NOTES", gbMediumFont));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);	
			cell.setColspan(2);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("1", bmFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getVolunteer(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("2", bmFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getPartner(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("V", gbMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getComments(), gbMediumFont));
			cell.setPaddingBottom(10);
			table.addCell(cell);
	            
			table.setWidths(cWidths);
			document.add(table);
	            
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	

	private Map<String, String> getSurveyContentMap(List<String> questionTextList, List<String> questionAnswerList){
		Map<String, String> content;
		String questionText;
		
		if (questionTextList != null && questionTextList.size() > 0)
   		{//remove the first element which is description about whole survey
   			questionTextList.remove(0);
   			
   	   		if (questionAnswerList != null && questionAnswerList.size() > 0)
   	   		{//remove the first element which is empty or "-"
   	   			if ("-".equals(questionAnswerList.get(0)))
   	   				questionAnswerList.remove(0);
	   	   			
	   	   		content = new TreeMap<String, String>(); 
		   	   	StringBuffer sb;
	   	   		
	   	   		for (int i = 0; i < questionAnswerList.size(); i++){	   	   			
	   	   			sb = new StringBuffer();	
	   	   			questionText = questionTextList.get(i).toString();
	   	   			//remove "question m of n"
	   	   			if (questionText.startsWith("Question"))	   	   			
	   	   				questionText = questionText.substring(15);
	   	   			
	   	   			sb.append(removeObserverNotes(questionText));
	   	  // 			sb.append("<br/><br/>"); //html view
	   	   			sb.append("\n\n");// for PDF format	   	   			
	   	   			sb.append(questionAnswerList.get(i));
	   	   			
	   	   			content.put(String.valueOf(i + 1), sb.toString());
	   	   		}	   	   		
	   	   		return content;
   	   		}
   	   		else
   	   		{
   	   			System.out.println("All answers in Goal Setting survey are empty!");   	
   	   			return null;  
   	   		}
   		}   			
   		else
   		{
   			System.out.println("Bad thing happens, no question text found for this Goal Setting survey!");
			return null;   	
   		}
	}
	
	private String removeObserverNotes(String questionText)
	{		
		//remove /observernotes/ from question text
		int index = questionText.indexOf("/observernote/");
	    	
	    if (index > 0)
	    	questionText = questionText.substring(0, index);
	    
	    return questionText;
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
				
				//remove /observernotes/ from question text
				removeObserverNotes(questionText);

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
		
		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
   		    String key = entry.getKey();
   		    
   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	
   		    	question = removeObserverNotes(question);
   		    	
   		    	if (!question.equals("-"))
   		    		qList.add(question);   		    	
   		    }
   		}		
		return qList;
	}
	
	private List<String> getQuestionListForMemorySurvey(LinkedHashMap<String, String> questionMap){
		List<String> qList = new ArrayList<String>();
		String question;	
		
		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
   		    String key = entry.getKey();
   		
   		    if ((key.equalsIgnoreCase("YM1"))||(key.equalsIgnoreCase("YM2")))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	//remove observer notes
   		    	question = removeObserverNotes(question);   		    			    	
   		    	qList.add(question); 
   		    }   		   
   		}		
		return qList;
	}
	//remove observer notes and other not related to question/answer
	private Map<String, String> getQuestionMap(LinkedHashMap<String, String> questions){
		Map<String, String> qMap = new LinkedHashMap<String, String>();		
		String question;
		
		for (Map.Entry<String, String> entry : questions.entrySet()) {
   		    String key = entry.getKey();
   		    
   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
   		    {
   		    	Object value = entry.getValue();
   		    	question = value.toString();
   		    	
   		    	question = removeObserverNotes(question);  		
   		    	
   		    	if (!question.equals("-"))
   		    		qMap.put(key, question);    	
   		    }
   		}
		return qMap;
	}

}
