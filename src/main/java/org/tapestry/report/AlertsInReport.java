package org.tapestry.report;

import java.util.Map;

public class AlertsInReport {	
	private boolean hasSocialAlert;
	private Map<String, Boolean> nutritionAlerts;
	private boolean hasDailyActivityAlert;
	private boolean hasPhysicalActivityAlert;
	
	public static final String SOCIAL_ALERT = "Cognitive concerns(abnormal clock test); poor functional performance";
	public static final String NUTRITION_ALERT1 =  "High Nutritional Risk";
	public static final String NUTRITION_ALERT2A = "Don't know if weight changed";	
	public static final String NUTRITION_ALERT2B = "Gained > 10 pounds";
	public static final String NUTRITION_ALERT2C = "Lost > 10 pounds";
	public static final String NUTRITION_ALERT3 = "Skip meals";
	public static final String NUTRITION_ALERT4 = "Poor Appetitite";
	public static final String NUTRITION_ALERT5 = "Patient cough, choke or have pain when swallowing";
	public static final String DAILY_ACTIVITY_ALERT = "High risk for fall";
	public static final String PHYSICAL_ACTIVITY_ALERT = "Activity level is suboptimal";
	public static final String MOBILITY_WALKING_ALERT1 = "Preclinical Limitation in Walking 2.0 km";
	public static final String MOBILITY_WALKING_ALERT2 = "Minor Manifest Limitation in Walking 2.0 km";
	public static final String MOBILITY_WALKING_ALERT3 = "Major Manifest Limitation in Walking 2.0 km";
	public static final String MOBILITY_WALKING_ALERT4 = "Preclinical Limitation in Walking 0.5 km";
	public static final String MOBILITY_WALKING_ALERT5 = "Minor Manifest Limitation in Walking 0.5 km";
	public static final String MOBILITY_WALKING_ALERT6 = "Major Manifest Limitation in Walking 0.5 km";
	public static final String MOBILITY_CLIMBING_ALERT1 = "Preclinical Limitation in Climbing Stairs";
	public static final String MOBILITY_CLIMBING_ALERT2 = "Minor Manifest Limitation in Climbing Stairs";
	public static final String MOBILITY_CLIMBING_ALERT3 = "Major Manifest Limitation in Climbing Stairs";
	public static final String EDMONTON_FRAIL_SCALE_ALERT = "Edmonton Frail Scale socores indicates high risk";
	public static final String DUKE_INDEX_OF_SOCIAL_SUPPORT_ALERT = "Social Support Scale scores denotes hight risk";	
	
	private AlertsInReport() {
		//
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

	

}
