package org.tapestry.objects;

public class Narrative {
	
	private int narrativeId;
	private int userId;
	private int patientId;	
	private int appointmentId;
	private String title;
	private String contents;
	private String editDate;

	/**
	* Empty constructor
	*/
	public Narrative(){
		
	}
	
	/**
	* Accessors and Mutators
	*/
	public int getNarrativeId() {
		return narrativeId;
	}
	
	public void setNarrativeId(int narrativeId) {
		this.narrativeId = narrativeId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	
	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public String getEditDate() {
		return editDate;
	}

	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
}
