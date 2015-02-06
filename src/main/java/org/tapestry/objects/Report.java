package org.tapestry.objects;

import java.util.List;
import java.util.Map;

import org.tapestry.report.ScoresInReport;

public class Report {
	private List<String> alerts; //alert:consider case review with IP-team
//	private Map<String, String> healthGoals;
	private Map<String, String> dailyActivities;//tapestry questions
	private Map<String, String> volunteerInformations;//volunteer information and notes
	private Map<String, String> additionalInfos;//additional information
	private Patient patient;//patient information,name...
	private Appointment appointment;//visit,plan, key observation
	private ScoresInReport scores;//summary of tapestry tools
	private List<String> patientGoals; // patient goal(s)
	private Map<String, String> caringPlan;
	private Map<String, String> memory;
	private Map<String, String> goals;

	public Report(){}
	
	public List<String> getPatientGoals() {
		return patientGoals;
	}


	public void setPatientGoals(List<String> patientGoals) {
		this.patientGoals = patientGoals;
	}


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
//	public Map<String, String> getHealthGoals() {
//		return healthGoals;
//	}
//
//	public void setHealthGoals(Map<String, String> healthGoals) {
//		this.healthGoals = healthGoals;
//	}

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

	public Map<String, String> getCaringPlan() {
		return caringPlan;
	}

	public void setCaringPlan(Map<String, String> caringPlan) {
		this.caringPlan = caringPlan;
	}

	public Map<String, String> getMemory() {
		return memory;
	}

	public void setMemory(Map<String, String> memory) {
		this.memory = memory;
	}

	public Map<String, String> getGoals() {
		return goals;
	}

	public void setGoals(Map<String, String> goals) {
		this.goals = goals;
	}
	
	
	
}
