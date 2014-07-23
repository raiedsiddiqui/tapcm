package org.tapestry.objects;

public class Activity{
	private int activityId;	
	private String description;
	private String date;
	private String time;
	private String startTime;
	private String endTime;	
	private String volunteer;
	private int appointment;
	private String volunteerName;
	private String patient;
	private int organizationId;	
	
	public Activity(){
	}
	
	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	public void setVolunteer(String volunteer){
		this.volunteer = volunteer;
	}
	
	public void setAppointment(int appointment){
		this.appointment = appointment;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getTime(){
		return time;
	}
	
	public String getVolunteer(){
		return volunteer;
	}
	
	public int getAppointment(){
		return appointment;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getVolunteerName() {
		return volunteerName;
	}

	public void setVolunteerName(String volunteerName) {
		this.volunteerName = volunteerName;
	}
	
	public String getPatient() {
		return patient;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

}