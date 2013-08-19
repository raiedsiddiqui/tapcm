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
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306/survey_app")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    	public PatientDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			e.printStackTrace();
		}
    	}

	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public Patient getPatientByID(int id){
		try{
			statement = con.prepareStatement("SELECT * FROM patients WHERE patient_ID=?");
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();
			results.first();
			return createFromSearch(results);
		} catch (SQLException e){
			System.out.println("Error: could not retrieve patient");
			e.printStackTrace();
			return null;
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
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
            		p.setPatientID(result.getInt("patient_ID"));
            		p.setFirstName(result.getString("firstname"));
            		p.setLastName(result.getString("lastname"));
            		p.setPreferredName(result.getString("preferredname"));
            		p.setGender(result.getString("gender"));
            		p.setVolunteer(result.getInt("volunteer"));
            		p.setNotes(result.getString("notes"));
            		//Set volunteer name
            		statement = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
           			statement.setInt(1, p.getVolunteer());
           			ResultSet r = statement.executeQuery();
           			r.first();
           			String name = r.getString("name");
           			p.setVolunteerName(name);
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			e.printStackTrace();
		}
		return p;
	}

	/**
	* List all the patients in the database
	* @return An ArrayList of Patient objects
	*/
    	public ArrayList<Patient> getAllPatients(){
        	try{
           		statement = con.prepareStatement("SELECT * FROM patients");
           		ResultSet result = statement.executeQuery();
           		ArrayList<Patient> allPatients = new ArrayList<Patient>();
           		while(result.next()){
           			Patient p = createFromSearch(result);
           			allPatients.add(p);
            	}
            	return allPatients;
        	} catch (SQLException e){
           		System.out.println("Error: Could not retrieve patients");
           		e.printStackTrace();
           		return null;
        	} finally {
        		try{
        			statement.close();
        		} catch (Exception e) {
        			//Ignore
        		}
        	}
    	}

	/**
	* Returns a list of patients assigned to the specified volunteer
	* @param volunteer The ID of the volunteer
	* @return An ArrayList of Patient objects
	*/
    	public ArrayList<Patient> getPatientsForVolunteer(int volunteer){
        	try{
            		statement = con.prepareStatement("SELECT * FROM patients WHERE volunteer=?");
            		statement.setInt(1, volunteer);
            		ResultSet result = statement.executeQuery();
            		ArrayList<Patient> patients = new ArrayList<Patient>();
            		while(result.next()){
            				Patient p = createFromSearch(result);
            				patients.add(p);
            		}
           	 	return patients;
        	} catch (SQLException e) {
            		System.out.println("Error: Could not retrieve patients");
            		e.printStackTrace();
			return null;
        	} finally {
        		try{
        			statement.close();
        		} catch (Exception e) {
        			//Ignore
        		}
        	}
    	}

	/**
	* Saves a patient in the database
	* @param p The Patient object to save
	*/
    public void createPatient(Patient p){
		try{
			statement = con.prepareStatement("INSERT INTO patients (firstname, lastname, preferredname, volunteer, gender, notes) VALUES (?, ?, ?, ?, ?, ?)");
			statement.setString(1, p.getFirstName());
			statement.setString(2, p.getLastName());
			statement.setString(3, p.getPreferredName());
			statement.setInt(4, p.getVolunteer());
			statement.setString(5, p.getGender());
			statement.setString(6, p.getNotes());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not create patient");
			e.printStackTrace();
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    /**
     * Changes a patient in the database
     * @param p The Patient object containing the new data (should have the same ID as the patient to replace)
     */
    public void updatePatient(Patient p){
    	try{
    		statement = con.prepareStatement("UPDATE patients SET firstname=?, lastname=?, preferredname=?, volunteer=?, gender=?, notes=? WHERE patient_ID=?");
    		statement.setString(1, p.getFirstName());
    		statement.setString(2, p.getLastName());
    		statement.setString(3, p.getPreferredName());
    		statement.setInt(4, p.getVolunteer());
    		statement.setString(5, p.getGender());
    		statement.setString(6, p.getNotes());
    		statement.setInt(7, p.getPatientID());
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not update patient");
    		e.printStackTrace();
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }

	/**
	* Removes the specified patient from the database
	* @param id The ID of the patient to remove
	*/
	public void removePatientWithId(int id){
		try{
			statement = con.prepareStatement("DELETE FROM patients WHERE patient_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not remove patient");
			e.printStackTrace();
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}

}
