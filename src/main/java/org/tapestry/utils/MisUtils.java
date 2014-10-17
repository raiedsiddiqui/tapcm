package org.tapestry.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.ui.ModelMap;
import org.tapestry.utils.Utils;
//import org.tapestry.controller.PatientController.ReportHeader;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Patient;
import org.tapestry.objects.Report;
import org.tapestry.objects.User;
import org.tapestry.service.MessageManager;
import org.tapestry.service.PatientManager;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class MisUtils {

	public static String getMyOscarAuthenticationInfo(){
		String info = "MyOscar offers you the opportunity to communicate electronically with your healthy care team, as well as store personal"
				+ "health information online. Thansmitting private health information poses risk of which you should be aware. You should not"
				+ " agree to use MyOscar to communicate with your health care team without understanding and accepting these risk. The terms "
				+ "and conditions for using MyOscar include, but are not limited to the following: /n"
				+ "1. MyOscar makes every reasonable attempt to protect your privacy and security Messages sent through MyOscar are far more"
				+ "secure than regular email. ";
		
		return info;
	}
	
	//all patient's info are from tapestry DB + myoscar DB
	public static List<Patient> getAllPatientsWithFullInfos(PatientManager patientManager, SecurityContextHolderAwareRequestWrapper request){
				
		List<Patient> patients = patientManager.getAllPatients();
		HttpSession session = request.getSession();
		
		if (session.getAttribute("allPatientWithFullInfos") != null)
			patients = (List<Patient>)session.getAttribute("allPatientWithFullInfos");
		else
		{
			int age;				
			
			try {			
				List<PersonTransfer3> patientsInMyOscar = ClientManager.getClients();
				
				for(PersonTransfer3 person: patientsInMyOscar)
				{	
					age = Utils.getAgeByBirthDate(person.getBirthDate());
					
					for(Patient p: patients)
					{
						if (person.getUserName().equals(p.getUserName()))
						{
							Calendar birthDate = person.getBirthDate();						
							if (birthDate != null)
								p.setBod(Utils.getDateByCalendar(birthDate));
							
							p.setAge(age);
							p.setCity(person.getCity());					
							p.setHomePhone(person.getPhone1());		
							if (person.getStreetAddress1() != null)
								p.setAddress(person.getStreetAddress1());
							else if(person.getStreetAddress2() != null)
								p.setAddress(person.getStreetAddress2());
							
							break;
						}
					}
				}
				
				
			} catch (Exception e) {
				System.out.println("something wrong when calling myoscar server...");			
				e.printStackTrace();
			}
			
			session.setAttribute("allPatientWithFullInfos", patients);
		}
		
		
		return patients;
	}
	
	public static int getLoggedInVolunteerId(SecurityContextHolderAwareRequestWrapper request){
		int volunteerId = 0;
		HttpSession session = request.getSession();
		
		if (session.getAttribute("logged_in_volunteer") != null){			
			volunteerId = Integer.parseInt(session.getAttribute("logged_in_volunteer").toString());
		}
		
		return volunteerId;
	}
	
	//report generator --- 
	public static Map<String, String> getSurveyContentMap(List<String> questionTextList, List<String> questionAnswerList){
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
	
	//report generator --- 
	public static Map<String, String> getSurveyContentMapForMemorySurvey(List<String> questionTextList, List<String> questionAnswerList){
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
	
	//report generator --- 
	public static List<String> removeRedundantFromQuestionText(List<String> list, String redundantStr){
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
	
	//report generator --- remove observer notes from answer
	public static List<String> getQuestionList(LinkedHashMap<String, String> questionMap) {
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
	//report generator --- 
	public static List<String> getQuestionListForMemorySurvey(LinkedHashMap<String, String> questionMap){
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
	//report generator --- remove observer notes and other not related to question/answer 
	public static Map<String, String> getQuestionMap(LinkedHashMap<String, String> questions){
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
	
	public static void sendMessageToMyOscar()
	{
		//send message to MyOscar test
		try{
			Long lll = ClientManager.sentMessageToPatientInMyOscar(new Long(15231), "Message From Tapestry", "Hello");
			
			
		} catch (Exception e){
			System.out.println("something wrong with myoscar server");
			e.printStackTrace();
		}
	}
	
	public static void setUnreadMsg(HttpSession session, SecurityContextHolderAwareRequestWrapper request, 
			ModelMap model, MessageManager messageDao){		
		if (session == null)
			session = request.getSession();
		
		if (session.getAttribute("unread_messages") != null)
			model.addAttribute("unread", session.getAttribute("unread_messages"));
		else
		{
			User loggedInUser = Utils.getLoggedInUser(request);
			int unreadMessages = messageDao.countUnreadMessagesForRecipient(loggedInUser.getUserID());
			model.addAttribute("unread", unreadMessages);
		}
	}
	
	public static void buildPDF(Report report, HttpServletResponse response){		
		String patientName = report.getPatient().getFirstName() + " " + report.getPatient().getLastName();
		String orignalFileName= patientName +"_report.pdf";
		try {
			Document document = new Document();
			document.setPageSize(PageSize.A4);
			document.setMargins(36, 36, 60, 36);
			document.setMarginMirroring(true);
			response.setHeader("Content-Disposition", "outline;filename=\"" +orignalFileName+ "\"");
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
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
			//set multiple images as header
			List<Image> imageHeader = new ArrayList<Image>();      	
	            
			Image imageLogo = Image.getInstance("webapps/tapestry/resources/images/logo.png"); 
			imageLogo.scalePercent(25f);
			imageHeader.add(imageLogo);			
				            
			Image imageDegroote = Image.getInstance("webapps/tapestry/resources/images/degroote.png");
			imageDegroote.scalePercent(25f);
			imageHeader.add(imageDegroote);	
			
			Image imageFhs = Image.getInstance("webapps/tapestry/resources/images/fhs.png");
			imageFhs.scalePercent(25f);	
			imageHeader.add(imageFhs);
						
			ReportHeader event = new ReportHeader();
			event.setHeader(imageHeader);
			writer.setPageEvent(event);			
			
			document.open(); 
			//Patient info
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{1f, 2f});
			
			PdfPCell cell = new PdfPCell(new Phrase(patientName, sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);		
			cell.setPadding(5);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("Address: 11 hunter Street S, Hamilton, On" + report.getPatient().getAddress(), sbFont));
			cell.setBorderWidthTop(1f);
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);	 
			cell.setPadding(5);
			table.addCell(cell);
		     
			cell = new PdfPCell(new Phrase("MRP: David Chan", sbFont));
			cell.setBorderWidthLeft(1f);		        
			cell.setBorderWidthTop(0);	          
			cell.setBorderWidthBottom(0);
			cell.setBorderWidthRight(0);
			cell.setPadding(5);
			table.addCell(cell);
		        
			cell = new PdfPCell( new Phrase("Date of visit: " + report.getAppointment().getDate(), sbFont));
			cell.setBorderWidthRight(1f);		        
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);
			cell.setBorderWidthBottom(0);
			cell.setPadding(5);
			table.addCell(cell);
		        
			cell = new PdfPCell(new Phrase("Time: " + report.getAppointment().getTime(), sbFont));
			cell.setBorderWidthLeft(1f);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthRight(0);	
			cell.setPadding(5);
			table.addCell(cell);
		        
			cell = new PdfPCell(new Phrase("Visit: " + report.getAppointment().getStrType(), sbFont));
			cell.setBorderWidthRight(1f);
			cell.setBorderWidthBottom(1f);
			cell.setBorderWidthTop(0);
			cell.setBorderWidthLeft(0);	  
			cell.setPadding(5);
			table.addCell(cell);
	        
			document.add(table);		   	        
			//Patient Info	
			table = new PdfPTable(1);
			table.setWidthPercentage(100);
//			cell = new PdfPCell(new Phrase("TAPESTRY REPORT: --- " + report.getPatient().getBod(), blFont));
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
			float[] cWidths = {1f, 18f};
		            
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
				cell.setPadding(3);
				table.addCell(cell);
			}
			document.add(table);
			document.add(new Phrase("    "));   
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
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);	            	
	            	
				cell = new PdfPCell(new Phrase(entry.getValue()));
				table.addCell(cell); 
			}			
			table.setWidths(new float[]{1f, 18f});
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
			document.newPage();
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
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getVolunteer(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("2", bmFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase(report.getAppointment().getPartner(), ibMediumFont));
			table.addCell(cell);
	            
			cell = new PdfPCell(new Phrase("V", gbMediumFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
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
	
	static class ReportHeader extends PdfPageEventHelper {
		List<Image> header;
		PdfTemplate total;
		
		public void setHeader(List<Image> header){
			this.header = header;
		}
		
		public void onOpenDocument(PdfWriter writer, Document document){
			total = writer.getDirectContent().createAppearance(10, 16);
		}
		
		public void onEndPage(PdfWriter writer, Document document){
			PdfPTable table = new PdfPTable(3);
            try
            { 
            	table.setTotalWidth(527);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(header.get(2).getScaledHeight());
                table.getDefaultCell().setBorder(0);
                
                table.addCell(header.get(0));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);                
                table.addCell(header.get(1));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(header.get(2));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                //set page number
//                table.addCell(String.format("Page %d of", writer.getPageNumber()));
//                PdfPCell cell = new PdfPCell(Image.getInstance(total));
//                cell.setBorder(0);    
//                table.addCell(cell);
                table.writeSelectedRows(0, -1, 34, 823, writer.getDirectContent());  
            }
            catch (Exception e){

            }
		}
		
		public void onCloseDocument(PdfWriter writer, Document document){
			 ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
	                    new Phrase(String.valueOf(writer.getPageNumber() - 1)),
	                    2, 2, 0);
		}
	}
	
//	public static Map<String, String> getSurveyContentMap(List<String> questionTextList, List<String> questionAnswerList){
//		Map<String, String> content;
//		String questionText;
//		
//		if (questionTextList != null && questionTextList.size() > 0)
//   		{//remove the first element which is description about whole survey
//   			questionTextList.remove(0);
//   			
//   	   		if (questionAnswerList != null && questionAnswerList.size() > 0)
//   	   		{//remove the first element which is empty or "-"
//   	   			if ("-".equals(questionAnswerList.get(0)))
//   	   				questionAnswerList.remove(0);
//	   	   			
//	   	   		content = new TreeMap<String, String>(); 
//		   	   	StringBuffer sb;
//	   	   		
//	   	   		for (int i = 0; i < questionAnswerList.size(); i++){	   	   			
//	   	   			sb = new StringBuffer();	
//	   	   			questionText = questionTextList.get(i).toString();
//	   	   			//remove "question m of n"
//	   	   			if (questionText.startsWith("Question"))	   	   			
//	   	   				questionText = questionText.substring(15);
//	   	   			
//	   	   			sb.append(removeObserverNotes(questionText));
//	   	  // 			sb.append("<br/><br/>"); //html view
//	   	   			sb.append("\n\n");// for PDF format	   	   			
//	   	   			sb.append(questionAnswerList.get(i));
//	   	   			
//	   	   			content.put(String.valueOf(i + 1), sb.toString());
//	   	   		}	   	   		
//	   	   		return content;
//   	   		}
//   	   		else
//   	   		{
//   	   			System.out.println("All answers in Goal Setting survey are empty!");   	
//   	   			return null;  
//   	   		}
//   		}   			
//   		else
//   		{
//   			System.out.println("Bad thing happens, no question text found for this Goal Setting survey!");
//			return null;   	
//   		}
//	}
	
	public static String removeObserverNotes(String questionText)
	{		
		//remove /observernotes/ from question text
		int index = questionText.indexOf("/observernote/");
	    	
	    if (index > 0)
	    	questionText = questionText.substring(0, index);
	    
	    return questionText;
	}
	
//	private Map<String, String> getSurveyContentMapForMemorySurvey(List<String> questionTextList, List<String> questionAnswerList){
//		Map<String, String> displayContent = new TreeMap<String, String>();
//		int size = questionTextList.size();
//		Object answer;
//		String questionText;
//			
//		if (questionAnswerList.size() == size)
//		{
//			for (int i = 0; i < size; i++)
//			{
//				questionText = questionTextList.get(i).toString();
//				
//				//remove /observernotes/ from question text
//				removeObserverNotes(questionText);
//
//				answer = questionAnswerList.get(i);
//				if ((answer != null) && (answer.toString().equals("1")))
//					displayContent.put(questionText, "YES");					
//				else
//					displayContent.put(questionText, "NO");			
//			}
//			
//			return displayContent;
//		}
//		else
//			System.out.println("Bad thing happens");
//		
//		return null;   	
//	}
//	
//	private List<String> removeRedundantFromQuestionText(List<String> list, String redundantStr){
//		String str;
//		int index;		
//		
//		for (int i = 0 ; i < list.size(); i ++)
//		{
//			str = list.get(i).toString();
//			index = str.indexOf(redundantStr);
//			if (index > 0)
//			{
//				str = str.substring(index + 4);
//				list.set(i, str);
//			}
//		}
//		
//		return list;
//	}
//	
//	//remove observer notes from answer
//	private List<String> getQuestionList(LinkedHashMap<String, String> questionMap) {
//		List<String> qList = new ArrayList<String>();
//		String question;
//		
//		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
//   		    String key = entry.getKey();
//   		    
//   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
//   		    {
//   		    	Object value = entry.getValue();
//   		    	question = value.toString();
//   		    	
//   		    	question = removeObserverNotes(question);
//   		    	
//   		    	if (!question.equals("-"))
//   		    		qList.add(question);   		    	
//   		    }
//   		}		
//		return qList;
//	}
//	
//	private List<String> getQuestionListForMemorySurvey(LinkedHashMap<String, String> questionMap){
//		List<String> qList = new ArrayList<String>();
//		String question;	
//		
//		for (Map.Entry<String, String> entry : questionMap.entrySet()) {
//   		    String key = entry.getKey();
//   		
//   		    if ((key.equalsIgnoreCase("YM1"))||(key.equalsIgnoreCase("YM2")))
//   		    {
//   		    	Object value = entry.getValue();
//   		    	question = value.toString();
//   		    	//remove observer notes
//   		    	question = removeObserverNotes(question);   		    			    	
//   		    	qList.add(question); 
//   		    }   		   
//   		}		
//		return qList;
//	}
//	//remove observer notes and other not related to question/answer
//	private Map<String, String> getQuestionMap(LinkedHashMap<String, String> questions){
//		Map<String, String> qMap = new LinkedHashMap<String, String>();		
//		String question;
//		
//		for (Map.Entry<String, String> entry : questions.entrySet()) {
//   		    String key = entry.getKey();
//   		    
//   		    if (!key.equalsIgnoreCase("title") && !key.equalsIgnoreCase("date") && !key.equalsIgnoreCase("surveyId"))
//   		    {
//   		    	Object value = entry.getValue();
//   		    	question = value.toString();
//   		    	
//   		    	question = removeObserverNotes(question);  		
//   		    	
//   		    	if (!question.equals("-"))
//   		    		qMap.put(key, question);    	
//   		    }
//   		}
//		return qMap;
//	}
	
	
	
}
