package org.tapestry.objects;

public class Activity{
	private String description;
	private String date;
	private String volunteer;
	private int appointment;
	
	public Activity(){
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public void setDate(String date){
		this.date = date;
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
	
	public String getVolunteer(){
		return volunteer;
	}
	
	public int getAppointment(){
		return appointment;
	}
}