package org.tapestry.objects;

import java.util.List;
import java.util.Map;

import org.tapestry.report.ScoresInReport;

public class Report {
	private List<String> alerts;
	private Map<String, String> healthGoals;
	private Map<String, String> dailyActivities;
	private Map<String, String> volunteerInformations;
	private Map<String, String> additionalInfos;
	private Patient patient;
	private Appointment appointment;
	private ScoresInReport scores;

	public Report(){}

	public List<String> getAlerts() {
		return alerts;
	}

	public ScoresInReport getScores() {
		return scores;
	}

	public void setScores(ScoresInReport scores) {
		this.scores = scores;
	}

	public void setAlerts(List<String> alerts) {
		this.alerts = alerts;
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
	
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	
}
