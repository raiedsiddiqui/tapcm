package org.tapestry.report;

import java.util.Map;

public class AlertsInReport {	
	private boolean hasSocialAlert;
	private Map<String, Boolean> nutritionAlerts;
	private boolean hasDailyActivityAlert;
	private boolean hasPhysicalActivityAlert;
	
	public static final String SOCIAL_ALERT = "Cognitive concerns(abnormal clock test); poor functional performance";
	public static final String NUTRITION_ALERT1 =  "High Nutritional Risk";
	public static final String NUTRITION_ALERT2 =  "Not trying to change weight in the past 6 months but it changed anyway";
	public static final String NUTRITION_ALERT2A = "Don't know if weight changed";	
	public static final String NUTRITION_ALERT2B = "Gained > 10 pounds";
	public static final String NUTRITION_ALERT2C = "Lost > 10 pounds";
	public static final String NUTRITION_ALERT3 = "Skip meals";
	public static final String NUTRITION_ALERT4 = "Poor Appetitite";
	public static final String NUTRITION_ALERT5 = "Often or always coughs, chokes, or has pain when swallowing food or fluids";
	public static final String DAILY_ACTIVITY_ALERT = "High risk for fall";
	public static final String PHYSICAL_ACTIVITY_ALERT = "Activity level is suboptimal";
//	public static final String MOBILITY_WALKING_ALERT1 = "Preclinical Limitation in Walking 2.0 km";
//	public static final String MOBILITY_WALKING_ALERT2 = "Minor Manifest Limitation in Walking 2.0 km";
	public static final String MOBILITY_WALKING_ALERT1 = "Major Manifest Limitation in Walking 2.0 km";
//	public static final String MOBILITY_WALKING_ALERT4 = "Preclinical Limitation in Walking 0.5 km";
//	public static final String MOBILITY_WALKING_ALERT5 = "Minor Manifest Limitation in Walking 0.5 km";
	public static final String MOBILITY_WALKING_ALERT2 = "Major Manifest Limitation in Walking 0.5 km";
//	public static final String MOBILITY_CLIMBING_ALERT1 = "Preclinical Limitation in Climbing Stairs";
//	public static final String MOBILITY_CLIMBING_ALERT2 = "Minor Manifest Limitation in Climbing Stairs";
	public static final String MOBILITY_CLIMBING_ALERT = "Major Manifest Limitation in Climbing Stairs";
	public static final String EDMONTON_FRAIL_SCALE_ALERT1 = "Edmonton Frail Scale socores indicates high risk";
	public static final String EDMONTON_FRAIL_SCALE_ALERT2 = "Patient uses 5 or more prescription medications";
	public static final String EDMONTON_FRAIL_SCALE_ALERT3 = "At times, sometimes forgets to take prescription medication";
	public static final String EDMONTON_FRAIL_SCALE_ALERT4 = "Other errors in the clock";
	public static final String EDMONTON_FRAIL_SCALE_ALERT5 = "More that 20s";
	public static final String EDMONTON_FRAIL_SCALE_ALERT6 = "Requires assistance for timed up and go";
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
