package org.tapestry.objects;

/**
 * Appointment
 * Represents an appointment between the volunteer and a patient
 */
public class Appointment{

	private int volunteer;
	private int patientID;
	private String patient;
	private String time;
	private String date;
	private String desc;
	
	/**
	 * Empty constructor
	 */
	public Appointment(){
	}
	
	//Accessors
	/**
	 * @return The volunteer for whom the appointment is booked
	 */
	public int getVolunteer(){
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
	public String getDescription(){
		return desc;
	}
	
	//Mutators
	/**
	 * @param volunteer The volunteer for whom the appointment is booked
	 */
	public void setVolunteer(int volunteer){
		this.volunteer = volunteer;
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
	public void setDescription(String desc){
		this.desc = desc;
	}
}