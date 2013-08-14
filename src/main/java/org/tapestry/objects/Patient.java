package org.tapestry.objects;

public class Patient{
	private int patientID;
	private String firstName;
	private String lastName;
	private String gender;
	private String email;
	private int volunteer;
	private String volunteerName;
	private String warnings;

	/**
	* Empty constructor
	*/
	public Patient(){
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
     * @return The warnings for the patient
     */
    public String getWarnings(){
    	return warnings;
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
     * @param warnings The warnings for the patient
     */
    public void setWarnings(String warnings){
    	this.warnings = warnings;
    }
}
