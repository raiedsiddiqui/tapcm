package org.tapestry.objects;

/**
 * Appointment
 * Represents an appointment between the volunteer and a patient
 */
public class Appointment{
	private int appointmentID;
	private int volunteerID;
	private String volunteer;
	private int patientID;
	private String patient;
	private String time;
	private String date;
	private String comments;
	private String status;
	private boolean completed;
	
	/**
	 * Empty constructor
	 */
	public Appointment(){
	}
	
	//Accessors
	
	/**
	 * @return the appointment ID
	 */
	public int getAppointmentID(){
		return appointmentID;
	}
	
	/**
	 * @return The volunteer for whom the appointment is booked
	 */
	public int getVolunteerID(){
		return volunteerID;
	}
	
	/**
	 * @return The volunteers name for the appointment booking
	 */
	public String getVolunteer(){
		return volunteer;
	}
	
	/**
	 * @return The patient for whom the appointment is booked
	 */
	public int getPatientID(){
		return patientID;
	}
	
	public String getPatient(){
		return patient;
	}
	
	/**
	 * @return The time for which the appointment is booked
	 */
	public String getTime(){
		return time;
	}
	
	/**
	 * @return The date for which the appointment is booked
	 */
	public String getDate(){
		return date;
	}

	/**
	 * @return The text description of the appointment
	 */
	public String getComments(){
		return comments;
	}
	
	/**
	 * @return The approval status of the appointment
	 */
	public String getStatus(){
		return status;
	}
	
	/**
	 * @return The completion status of the appointment
	 */
	public boolean isCompleted(){
		return completed;
	}
	
	//Mutators
	
	/**
	 * @param appointmentID the id of the appointment
	 */
	public void setAppointmentID(int appointmentID){
		this.appointmentID = appointmentID;
	}
	
	/**
	 * @param volunteer The volunteer's name for the appointment booking
	 */
	public void setVolunteer(String volunteer){
		this.volunteer = volunteer;
	}
	
	/**
	 * @param volunteerID the volunteer's ID for the appointment booking
	 */
	public void setVolunteerID(int volunteerID){
		this.volunteerID = volunteerID;
	}
	
	/**
	 * @param patient The patient for whom the appointment is booked
	 */
	public void setPatient(String patient){
		this.patient = patient;
	}
	
	public void setPatientID(int id){
		this.patientID = id;
	}
	
	/**
	 * @param date The date for which the appointment is booked, in YYYY-MM-DD format
	 */
	public void setDate(String date){
		this.date = date;
	}
	
	/**
	 * @param time The time for which the appointment is booked, in HH:MM:SS format
	 */
	public void setTime(String time){
		this.time = time;
	}
	
	/**
	 * @param desc The text description for the appointment
	 */
	public void setComments(String comments){
		this.comments = comments;
	}
	
	/**
	 * @param approved The approval status of the appointment
	 */
	public void setStatus(String status){
		this.status = status;
	}
	
	/**
	 * @param approved The completion status of the appointment
	 */
	public void setCompleted(boolean completed){
		this.completed = completed;
	}
}