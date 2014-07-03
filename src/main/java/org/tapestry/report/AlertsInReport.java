package org.tapestry.report;

import java.util.Map;

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
