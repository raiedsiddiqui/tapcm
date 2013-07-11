package org.tapestry.objects;

public class Patient{
	private int patientId;
	private String firstName;
	private String lastName;
	private String gender;
	private int age;
	private String email;
	private String volunteer;

	/**
	* Default constructor
	*/
	public Patient(){
	}

	//Accessors
	public int getPatientId(){
		return patientId;
	}

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getLastInitial(){
	return lastName.substring(0, 1);
    }

    public String getGender(){
        return gender;
    }

    public int getAge(){
        return age;
    }

    public String getEmail(){
        return email;
    }

    public String getVolunteer(){
        return volunteer;
    }

	//Mutators
    public void setPatientId(int id){
        this.patientId = id;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setVolunteer(String volunteer){
        this.volunteer = volunteer;
    }
}
