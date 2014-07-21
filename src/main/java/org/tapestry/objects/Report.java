package org.tapestry.objects;

import java.util.List;
import java.util.Map;

public class Report {
	private List<String> alerts;
	private String keyObservations;
	private List<String> plans;
	private Map<String, String> healthGoals;
	private Map<String, String> dailyActivities;
	private Map<String, String> volunteerInformations;
	private Map<String, String> additionalInfos;
	
	public Report(){}

	public List<String> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<String> alerts) {
		this.alerts = alerts;
	}

	public String getKeyObservations() {
		return keyObservations;
	}

	public void setKeyObservations(String keyObservations) {
		this.keyObservations = keyObservations;
	}

	public List<String> getPlans() {
		return plans;
	}

	public void setPlans(List<String> plans) {
		this.plans = plans;
	}

	public Map<String, String> getHealthGoals() {
		return healthGoals;
	}

	public void setHealthGoals(Map<String, String> healthGoals) {
		this.healthGoals = healthGoals;
	}

	public Map<String, String> getDailyActivities() {
		return dailyActivities;
	}

	public void setDailyActivities(Map<String, String> dailyActivities) {
		this.dailyActivities = dailyActivities;
	}

	public Map<String, String> getVolunteerInformations() {
		return volunteerInformations;
	}

	public void setVolunteerInformations(Map<String, String> volunteerInformations) {
		this.volunteerInformations = volunteerInformations;
	}
	
	public Map<String, String> getAdditionalInfos() {
		return additionalInfos;
	}

	public void setAdditionalInfos(Map<String, String> additionalInfos) {
		this.additionalInfos = additionalInfos;
	}
	
}
