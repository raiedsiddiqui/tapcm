package org.tapestry.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.tapestry.controller.Utils;
import org.tapestry.objects.Patient;

/**
* PatientDAO class
* Allows the application to interface with the 'survey_app.patients' table.
*/
public class PatientDao {

    	private PreparedStatement statement;
    	private Connection con;    		
    	private ResultSet rs;

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
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public Patient getNewestPatient(){
		try{
			statement = con.prepareStatement("SELECT * FROM patients ORDER BY patient_ID DESC LIMIT 1");
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
    	 * 
    	 * @return a list of patients who have availability
    	 */
    	
    	public List<Patient> getAllPatientsWithAvailability()
    	{
    		List<Patient> patients = getAllPatients();
    		List<Patient> aList = new ArrayList<Patient>();
    		String pAvailability;
    		
    		for( Patient p: patients){
    			pAvailability = p.getAvailability();
    			
    			if( (!Utils.isNullOrEmpty(pAvailability))&& (!pAvailability.equals("1non,2non,3non,4non,5non")))
    				aList.add(p);
    		}
    		
    		return aList;    		
    	}

	/**
	* Returns a list of patients assigned to the specified volunteer
	* @param volunteer The ID of the volunteer
	* @return An ArrayList of Patient objects
	*/
    	public ArrayList<Patient> getPatientsForVolunteer(int volunteer){
        	try{
            		statement = con.prepareStatement("SELECT * FROM patients WHERE volunteer=? OR volunteer2=?");
            		statement.setInt(1, volunteer);
            		statement.setInt(2, volunteer);
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
    	 * 
    	 * get all patients with partialName in firstname or lastname
    	 * @param partialName
    	 * @return
    	 */
    	public List<Patient> getPatientssByPartialName(String partialName){
    		try{
    			statement = con.prepareStatement("SELECT * FROM patients WHERE UPPER(firstname) "
    					+ "LIKE UPPER('%" + partialName + "%') OR UPPER(lastname) LIKE UPPER('%" + partialName + "%')");
    			ResultSet result = statement.executeQuery();
    			List<Patient> patients = new ArrayList<Patient>();
    			
    			while(result.next()){
    				Patient p = createFromSearch(result);
    				patients.add(p);
    			}
    			
    			return patients;
    		} catch (SQLException e){
    			System.out.println("Error: Could not retrieve patients by partial name " + partialName);
    			e.printStackTrace();
    			return null;
    		}
    	}

	/**
	* Saves a patient in the database
	* @param p The Patient object to save
	*/
    public void createPatient(Patient p){
    	//check if it is new record in DB
    	if(!isExist(p)){
    		try{
//    			statement = con.prepareStatement("INSERT INTO patients (firstname, lastname, preferredname, volunteer,"
//    					+ " gender, notes, volunteer2, alerts, myoscar_verified, clinic, availability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
       			statement = con.prepareStatement("INSERT INTO patients (firstname, lastname, preferredname, volunteer,"
    					+ " gender, notes, volunteer2, alerts, myoscar_verified, clinic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    			statement.setString(1, p.getFirstName());
    			statement.setString(2, p.getLastName());
    			statement.setString(3, p.getPreferredName());
    			statement.setInt(4, p.getVolunteer());
    			statement.setString(5, p.getGender());
    			statement.setString(6, p.getNotes());
    			statement.setInt(7, p.getPartner());
    			statement.setString(8, p.getAlerts());
    			statement.setString(9, p.getMyoscarVerified());
    			statement.setString(10, p.getClinic());
 //   			statement.setString(11, p.getAvailability());
    			
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
		
    }
    
    /**
     * Changes a patient in the database
     * @param p The Patient object containing the new data (should have the same ID as the patient to replace)
     */
    public void updatePatient(Patient p){
    	try{
 //   		statement = con.prepareStatement("UPDATE patients SET firstname=?, lastname=?, preferredname=?, volunteer=?, "
 //   				+ "gender=?, notes=?, clinic=?, availability=?, myoscar_verified=?, alerts=?, volunteer2=? WHERE patient_ID=?");
    		statement = con.prepareStatement("UPDATE patients SET firstname=?, lastname=?, preferredname=?, volunteer=?, "
    				+ "gender=?, notes=?, clinic=?, myoscar_verified=?, alerts=?, volunteer2=? WHERE patient_ID=?");
    		statement.setString(1, p.getFirstName());
    		statement.setString(2, p.getLastName());
    		statement.setString(3, p.getPreferredName());
    		statement.setInt(4, p.getVolunteer());
    		statement.setString(5, p.getGender());
    		statement.setString(6, p.getNotes());
    		statement.setString(7, p.getClinic());
 //   		statement.setString(8, p.getAvailability());
    		statement.setString(8, p.getMyoscarVerified());
    		statement.setString(9, p.getAlerts());
    		statement.setInt(10,p.getPartner());
    		statement.setInt(11, p.getPatientID());   		
    		
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
	
	/**
	* Creates a Patient object from a database query
	* @param result The ResultSet from the database query
	* @return The Patient object
	*/
	private Patient createFromSearch(ResultSet result){
		Patient p = new Patient();
		try
		{
			p.setPatientID(result.getInt("patient_ID"));
			p.setFirstName(result.getString("firstname"));
			p.setLastName(result.getString("lastname"));
			p.setPreferredName(result.getString("preferredname"));
			//set gender
			String gender = result.getString("gender");
			if ("M".equals(gender))
				p.setGender("Male");
			else if ("F".equals(gender))
				p.setGender("Female");
			else
				p.setGender("Other");
			//set clinic
			String clinicCode = result.getString("clinic");
			p.setClinic(clinicCode);
			p.setClinicName(Utils.getClinicName(clinicCode));
			
			p.setVolunteer(result.getInt("volunteer"));
			p.setNotes(result.getString("notes"));
//			p.setAvailability(result.getString("availability"));
			p.setAlerts(result.getString("alerts"));	
			
			String myOscarVerfied = result.getString("myoscar_verified");
			p.setMyoscarVerified(myOscarVerfied);    
			//set myoscar authentication for display in client's detail page
			if ("1".equals(myOscarVerfied))
				p.setMyOscarAuthentication("Authenticated");
			else
				p.setMyOscarAuthentication("Not Authenticated");
			p.setPartner(result.getInt("volunteer2"));
			//Set volunteer name and partner name
			setVolunteerDisplayName(p, "volunteer1");
			setVolunteerDisplayName(p, "volunteer2");
			
			p.setUserName(result.getString("username"));
			
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			e.printStackTrace();
		}
		return p;
	}
	
	/**
	 * 
	 * Set up volunteer's displayName in patient object 
	 * @param p patient
	 * @param who volunteer1 or volunteer2 in patient
	 */

	private void setVolunteerDisplayName(Patient p, String who){
		try{
			statement = con.prepareStatement("SELECT firstname, lastname FROM volunteers WHERE volunteer_ID=?");
			if (who.equals("volunteer1"))
				statement.setInt(1, p.getVolunteer());
			else 
				statement.setInt(1, p.getPartner());
			ResultSet rs = statement.executeQuery();
			while(rs.next()){           				
				StringBuilder sb = new StringBuilder();
	   			if (!Utils.isNullOrEmpty(rs.getString("firstname"))){
	   				sb.append(rs.getString("firstname"));
	   				sb.append(" ");               				
	   			}
	   			
	   			if (rs.getString("lastname") != null){
	   				sb.append(rs.getString("lastname"));               				
	   			}
	   			
	   			if (who.equals("volunteer1")) 
	   			{
	   				if(!Utils.isNullOrEmpty(sb.toString()))
	   	   				p.setVolunteerName(sb.toString());
	   	   			else
	   	   				p.setVolunteerName("");
	   	   			
	   			}
				else
				{
					if(!Utils.isNullOrEmpty(sb.toString()))
	   	   				p.setPartnerName(sb.toString());
	   	   			else
	   	   				p.setPartnerName("");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			e.printStackTrace();
		}
	}
	
	/**	
	 * Avoid duplicated record in DB
	 * 
	 */
	
	private boolean isExist(Patient patient){		
		int count = 0;		
		try{
			statement = con.prepareStatement("SELECT COUNT(*) as c FROM patients WHERE UPPER(firstname) = UPPER(?) "
					+ "AND UPPER(lastname) = UPPER(?) AND UPPER(email) = UPPER(?)");
			statement.setString(1, patient.getFirstName());
			statement.setString(2, patient.getLastName());
			statement.setString(3, patient.getEmail());
			
			rs = statement.executeQuery();
			rs.first();
			
			count = rs.getInt("c");
			
			if (rs != null)
				rs.close();
			
		} catch (SQLException e){
			System.out.println("Error: Could not count volunteers");
			e.printStackTrace();
			
		} finally {
    		try{//close statement
    			if (statement != null)
    				statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}	
		
		if (count > 0)
			return true;
		else 
			return false;
		
	}

}
