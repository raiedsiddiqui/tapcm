package org.tapestry.objects;

public class Patient{
	private int patientId;
	private String firstName;
	private String lastName;
	private String gender;
	private int age;
	private String email;
	private String volunteer;
	private String color;

	/**
	* Empty constructor
	*/
	public Patient(){
	}

	//Accessors
	/**
	*@return The ID of the patient
	*/
	public int getPatientId(){
		return patientId;
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
	*@return The patient's age
	*/
    	public int getAge(){
        	return age;
    	}

	/**
	*@return The patient's email address
	*/
    	public String getEmail(){
        	return email;
    	}

	/**
	*@return The name of the volunteer assigned to the patient
	*/
    	public String getVolunteer(){
        	return volunteer;
    	}
    
    /**
     * @return The background color for this patient
     */
    	public String getColor(){
    		return color;
    	}

	//Mutators
	/**
	*@param id The new ID of the patient
	*/
    	public void setPatientId(int id){
        	this.patientId = id;
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
	*@param age The new age of the patient
	*/
    	public void setAge(int age){
        	this.age = age;
    	}

	/**
	*@param email The new email address of the patient
	*/
    	public void setEmail(String email){
        	this.email = email;
    	}

	/**
	*@param volunteer The name of the volunteer assigned to the patient
	*/
    	public void setVolunteer(String volunteer){
        	this.volunteer = volunteer;
    	}
    	
    /**
     * @param color The color to use as the background for the patient's page
     */
    	public void setColor(String color){
    		this.color = color;
    	}
}
