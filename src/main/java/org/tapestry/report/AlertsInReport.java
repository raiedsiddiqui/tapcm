package org.tapestry.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;

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
import org.survey_component.data.SurveyMap;
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
import org.yaml.snakeyaml.Yaml;

public class AlertsInReport {	
	private boolean hasSocialAlert;
	private Map<String, Boolean> nutritionAlerts;
	private boolean hasDailyActivityAlert;
	private boolean hasPhysicalActivityAlert;
	private static final String socialAlertMsg = "Cognitive concerns9abnormal clock test); poor functional perfomance";
	private static final String nutritionAlertMsg1 = "High Nutritional Risk";
	private static final String nutritionAlertMsg2a= "Don't know if weight changed";	
	private static final String nutritionAlertMsg2b = "Gained > 10 pounds";
	private static final String nutritionAlertMsg2c = "Lost > 10 pounds";
	private static final String nutritionAlertMsg3 = "Skip meals";
	private static final String nutritionAlertMsg4 = "Poor Appetitite";
	private static final String nutritionAlertMsg5 = "Patient cough, choke or have pain when swallowing";
	private static final String dailyActiveityAlertMsg = "High risk for fall";
	private static final String physicalActivityAlertMsg = "Activity level is suboptimal";
	
	
	public static String getSocialAlertMsg() {
		return socialAlertMsg;
	}


	public static String getNutritionAlertMsg1() {
		return nutritionAlertMsg1;
	}


	public static String getNutritionAlertMsg2a() {
		return nutritionAlertMsg2a;
	}


	public static String getNutritionAlertMsg3() {
		return nutritionAlertMsg3;
	}


	public static String getNutritionAlertMsg4() {
		return nutritionAlertMsg4;
	}


	public static String getNutritionAlertMsg5() {
		return nutritionAlertMsg5;
	}


	public static String getNutritionAlertMsg2b() {
		return nutritionAlertMsg2b;
	}


	public static String getNutritionAlertMsg2c() {
		return nutritionAlertMsg2c;
	}


	public static String getDailyActiveityAlertMsg() {
		return dailyActiveityAlertMsg;
	}


	public static String getPhysicalActivityAlertMsg() {
		return physicalActivityAlertMsg;
	}

	public AlertsInReport() {
		
	}	
	
	
	public boolean isHasSocialAlert() {
		return hasSocialAlert;
	}

	public void setHasSocialAlert(boolean hasSocialAlert) {
		this.hasSocialAlert = hasSocialAlert;
	}
	
	public boolean isHasDailyActivityAlert() {
		return hasDailyActivityAlert;
	}

	public void setHasDailyActivityAlert(boolean hasDailyActivityAlert) {
		this.hasDailyActivityAlert = hasDailyActivityAlert;
	}

	public boolean isHasPhysicalActivityAlert() {
		return hasPhysicalActivityAlert;
	}

	public void setHasPhysicalActivityAlert(boolean hasPhysicalActivityAlert) {
		this.hasPhysicalActivityAlert = hasPhysicalActivityAlert;
	}

	public String getSocialAlertMessage(){
		return socialAlertMsg;
	}
	
	
	public String getDailyActivityAlertMessage(){
		return dailyActiveityAlertMsg;
	}
	
	public String getPhysicalActivityAlertMessage(){
		return physicalActivityAlertMsg;
	}

	public Map<String, Boolean> getNutritionAlerts() {
		return nutritionAlerts;
	}


	public void setNutritionAlerts(Map<String, Boolean> nutritionAlerts) {
		this.nutritionAlerts = nutritionAlerts;
	}

}
