package org.tapestry.objects;

public class Narrative {
	
	private int narrativeId;
	private int patientId;	
	private String patientName;
	private int appointmentId;
	private String title;
	private String contents;
	private String editDate;
	private int volunteerId;
	

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
	
	public int getVolunteerId() {
		return volunteerId;
	}

	public void setVolunteerId(int volunteerId) {
		this.volunteerId = volunteerId;
	}
	

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

}
