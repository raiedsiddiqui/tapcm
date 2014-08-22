package org.tapestry.controller.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.oscarehr.myoscar_server.ws.PersonTransfer3;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.tapestry.controller.Utils;
import org.tapestry.myoscar.utils.ClientManager;
import org.tapestry.objects.Patient;
import org.tapestry.dao.PatientDao;

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
	public static List<Patient> getAllPatientsWithFullInfos(PatientDao patientDao, SecurityContextHolderAwareRequestWrapper request){
				
		List<Patient> patients = patientDao.getAllPatients();
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
	
	
}
