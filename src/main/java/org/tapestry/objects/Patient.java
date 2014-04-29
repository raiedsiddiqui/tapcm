package org.tapestry.objects;

public class Patient{
	private int patientID;
	private String firstName;
	private String lastName;
	private String preferredName;
	private String gender;
	private String email;
	private int volunteer;
	private String volunteerName;
	private String notes;	
	private String bod;// birth of date
	private int age;
	private String clinic;
	private String mrp; // family doctor
	private String city;
	private String homePhone;
	private String availability;
	private String alerts;
	private String myoscarVerified;
	private int partner; // another volunteer(two volunteers should be assigned per visit)
	private String partnerName;

	/**
	* Empty constructor
	*/
	public Patient(){
	}
	
	public String getMyoscarVerified() {
		return myoscarVerified;
	}

	public void setMyoscarVerified(String myoscarVerified) {
		this.myoscarVerified = myoscarVerified;
	}
	
	public String getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts) {
		this.alerts = alerts;
	}

	public int getPartner() {
		return partner;
	}

	public void setPartner(int partner) {
		this.partner = partner;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getBod() {
		return bod;
	}

	public void setBod(String bod) {
		this.bod = bod;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getClinic() {
		return clinic;
	}

	public void setClinic(String clinic) {
		this.clinic = clinic;
	}

	public String getMrp() {
		return mrp;
	}

	public void setMrp(String mrp) {
		this.mrp = mrp;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	

	//Accessors
	/**
	*@return The ID of the patient
	*/
	public int getPatientID(){
		return patientID;
	}

	/**
	*@return The patient's first name
	*/
    public String getFirstName(){
        return firstName;
    }

	/**
	*@return The patient's last name
	*/
    public String getLastName(){
        return lastName;
    }
    
    /**
     * @return The patient's preferred name, defaulting to "First L."
     */
    public String getPreferredName(){
    	return preferredName;
    }

	/**
	*@return The first letter of the patient's last name
	*/
    public String getDisplayName(){
		return firstName + " " + lastName.substring(0, 1) + ".";
    }

	/**
	*@return The patient's gender
	*/
    public String getGender(){
        return gender;
    }

	/**
	*@return The patient's email address
	*/
    public String getEmail(){
        return email;
    }
    	
    /**
     * This should really only be used for displaying info in the interface,
     * use the volunteer (integer) version for referencing the user assigned
     * to the patient (since names are mutable and IDs are not)
     * @return The name of the volunteer responsible for the patient
     */
    public String getVolunteerName(){
    	return volunteerName;
    }

	/**
	*@return The name of the volunteer assigned to the patient
	*/
    public int getVolunteer(){
        return volunteer;
    }
    
    /**
     * @return The notes for the patient
     */
    public String getNotes(){
    	return notes;
    }

	//Mutators
	/**
	*@param id The new ID of the patient
	*/
    public void setPatientID(int id){
        this.patientID = id;
    }

	/**
	*@param firstName The new first name of the patient
	*/
    public void setFirstName(String firstName){
        this.firstName = firstName;
   	}

	/**
	*@param lastName The new last name of the patient
	*/
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    
    /**
     * @param preferredName The new preferred name of the patient
     */
    public void setPreferredName(String preferredName){
    	this.preferredName = preferredName;
    }

	/**
	*@param gender The new gender of the patient
	*/
    public void setGender(String gender){
        this.gender = gender;
    }

	/**
	*@param email The new email address of the patient
	*/
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * @param name The name of the volunteer assigned to the patient
     */
    public void setVolunteerName(String name){
    	this.volunteerName = name;
    }
    	
	/**
	*@param volunteer The id of the volunteer assigned to the patient
	*/
    public void setVolunteer(int volunteer){
        this.volunteer = volunteer;
    }
    
    /**	
     * @param warnings The notes for the patient
     */
    public void setNotes(String notes){
    	this.notes = notes;
    }
    
    public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
    
}
