package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.tapestry.objects.SurveyResult;

/**
 * SurveyTemplateDAO
 * Allow searching for appointments on the current date for a user,
 * all appointments for a user; adding new appointments.
 */
public class SurveyResultDao
{
	private PreparedStatement statement;
	private Connection con;
	
	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    public SurveyResultDao(String url, String username, String password){
    	try{
    		con = DriverManager.getConnection(url, username, password);
    	} catch (SQLException e){
    		System.out.println("Error: Could not connect to database");
    		System.out.println(e.toString());
    	}
    }
    
    public ArrayList<SurveyResult> getAllPatientsSurveys(int patientId){
    	try {
			statement = con.prepareStatement("SELECT * FROM survey_results WHERE patient_id=?");
			statement.setInt(1, patientId);
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey templates");
			System.out.println(e.toString());
			return null;
		}
    }
    
	/**
	 * Ordered by title ascending
	 * @param itemsToReturn 
	 * @param startIndex 
	 */
	public ArrayList<SurveyResult> getAllSurveyResults()
	{
		try {
			statement = con.prepareStatement("SELECT * FROM survey_results");
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				
				//Look up the name of the survey
       			statement = con.prepareStatement("SELECT title FROM surveys WHERE survey_ID=?");
       			statement.setInt(1, sr.getSurveyID());
       			ResultSet r = statement.executeQuery();
       			r.first();
       			String title = r.getString("title");
       			sr.setSurveyTitle(title);
       			
				//Look up the name of the patient
       			statement = con.prepareStatement("SELECT firstname, lastname FROM patients WHERE patient_ID=?");
       			statement.setInt(1, sr.getPatientID());
       			r = statement.executeQuery();
       			r.first();
       			String name = r.getString("firstname") + " " + r.getString("lastname");
       			sr.setPatientName(name);
       			
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey templates");
			System.out.println(e.toString());
			return null;
		}
	}

	/**
	* Creates a Survey Template object from a database query
	* @param result The ResultSet from the database query
	* @return The Patient object
	*/
	private SurveyResult createFromSearch(ResultSet result){
		SurveyResult sr = new SurveyResult();
		try{
            sr.setResultID(result.getInt("result_ID"));
            sr.setSurveyID(result.getInt("survey_ID"));
            sr.setPatientID(result.getInt("patient_ID"));
            sr.setResults(result.getBytes("data"));
            sr.setStartDate(result.getString("startDate"));
            sr.setStatus(result.getString("status"));
            sr.setEndDate(result.getString("endDate"));
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			System.out.println(e.toString());
		}
		return sr;
	}
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public void assignSurvey(SurveyResult sr) {
		try {
			statement = con.prepareStatement("INSERT INTO survey_results (patient_ID, survey_ID, data, status) values (?,?,?,?)");
			statement.setInt(1, sr.getPatientID());
			statement.setInt(2, sr.getSurveyID());
			statement.setBytes(3, sr.getResults());
			statement.setString(4, sr.getStatus());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not assign survey # " + sr.getSurveyID() + " to patient # " + sr.getPatientID());
			System.out.println(e.toString());
		}
	}
	
	/**
	 * Delete a survey from the database
	 * @param id
	 */
	public void deleteSurvey(int id) {
		try {
			statement = con.prepareStatement("DELETE FROM survey_results WHERE result_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not delete survey");
			System.out.println(e.toString());
		}
	}
}
