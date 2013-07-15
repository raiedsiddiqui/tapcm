package org.tapestry.dao;

import org.tapestry.objects.Patient;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
* PatientDAO class
* Allows the application to interface with the 'survey_app.patients' table.
*/
public class PatientDao {

    	private PreparedStatement statement;
    	private Connection con;

	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    	public PatientDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			System.out.println(e.toString());
		}
    	}

	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public Patient getPatientById(int id){
		try{
			statement = con.prepareStatement("SELECT * FROM survey_app.patients WHERE patient_ID=?");
			statement.setString(1, "" + id);
			ResultSet results = statement.executeQuery();
			results.first();
			return createFromSearch(results);
		} catch (SQLException e){
			System.out.println("Error: could not retrieve patient");
			System.out.println(e.toString());
			return null;
		}
	}

	/**
	* Creates a Patient object from a database query
	* @param result The ResultSet from the database query
	* @return The Patient object
	*/
	private Patient createFromSearch(ResultSet result){
		Patient p = new Patient();
		try{
            		p.setPatientId(Integer.parseInt(result.getString("patient_ID")));
            		p.setFirstName(result.getString("firstname"));
            		p.setLastName(result.getString("lastname"));
            		p.setGender(result.getString("gender"));
			//String age = result.getString("age");
			//if (age != null);
	            	//p.setAge(Integer.parseInt(result.getString("age")));
            		p.setVolunteer(result.getString("volunteer"));
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			System.out.println(e.toString());
		}
		return p;
	}

	/**
	* List all the patients in the database
	* @return An ArrayList of Patient objects
	*/
    	public ArrayList<Patient> getAllPatients(){
        	try{
            		statement = con.prepareStatement("SELECT * FROM survey_app.patients");
            		ResultSet result = statement.executeQuery();
            		ArrayList<Patient> allPatients = new ArrayList<Patient>();
            		while(result.next()){
				Patient p = createFromSearch(result);
				allPatients.add(p);
			}
            		return allPatients;
        	} catch (SQLException e){
            		System.out.println("Error: Could not retrieve patients");
            		System.out.println(e.toString());
            		return null;
        	}
    	}

	/**
	* Returns a list of patients assigned to the specified volunteer
	* @param volunteer The name of the volunteer (must be exact)
	* @return An ArrayList of Patient objects
	*/
    	public ArrayList<Patient> getPatientsForVolunteer(String volunteer){
        	try{
            		statement = con.prepareStatement("SELECT * FROM survey_app.patients WHERE volunteer=?");
            		statement.setString(1, volunteer);
            		ResultSet result = statement.executeQuery();
            		ArrayList<Patient> patients = new ArrayList<Patient>();
            		while(result.next()){
				Patient p = createFromSearch(result);
				patients.add(p);
			}
           	 	return patients;
        	} catch (SQLException e) {
            		System.out.println("Error: Could not retrieve patients");
            		System.out.println(e.toString());
			return null;
        	}
    	}

	/**
	* Saves a patient in the database
	* @param p The Patient object to save
	*/
    	public void createPatient(Patient p){
		try{
			statement = con.prepareStatement("INSERT INTO survey_app.patients (firstname, lastname, volunteer) VALUES (?, ?, ?)");
			statement.setString(1, p.getFirstName());
			statement.setString(2, p.getLastName());
			statement.setString(3, p.getVolunteer());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not create patient");
			System.out.println(e.toString());
		}
    	}

	/**
	* Removes the specified patient from the database
	* @param id The ID of the patient to remove
	*/
	public void removePatientWithId(int id){
		try{
			statement = con.prepareStatement("DELETE FROM survey_app.patients WHERE patient_ID=?");
			statement.setString(1, "" + id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not remove patient");
			System.out.println(e.toString());
		}
	}

}
