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
    		e.printStackTrace();
    	}
    }
    
    public ArrayList<SurveyResult> getSurveysByPatientID(int patientId){
    	try {
			statement = con.prepareStatement("SELECT * FROM survey_results WHERE patient_ID=?");
			statement.setInt(1, patientId);
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey results for patient ID #" + patientId);
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
    
    public ArrayList<SurveyResult> getIncompleteSurveysByPatientID(int patientId){
    	try {
			statement = con.prepareStatement("SELECT * FROM survey_results WHERE patient_ID=? AND completed=0");
			PreparedStatement priorityLookup = con.prepareStatement("SELECT priority FROM surveys WHERE survey_ID=?");
			statement.setInt(1, patientId);
			ResultSet result = statement.executeQuery();
			ResultSet r;
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				priorityLookup.setInt(1, sr.getSurveyID());
				r = priorityLookup.executeQuery();
				r.first();
				sr.setPriority(r.getInt("priority"));
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey results for patient ID #" + patientId);
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
    
    public SurveyResult getSurveyResultByID(int id){
    	try{
    		statement = con.prepareStatement("SELECT * FROM survey_results WHERE result_ID=?");
    		statement.setInt(1, id);
    		ResultSet result = statement.executeQuery();
    		result.first();
    		return createFromSearch(result);
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve survey result for result ID #" + id);
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
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve all survey results");
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
	 * Retrieves all survey results by survey Id
	 * @param itemsToReturn 
	 * @param id survey ID
	 */
	public ArrayList<SurveyResult> getAllSurveyResultsBySurveyId(int id)
	{
		try {
			statement = con.prepareStatement("SELECT * FROM survey_results WHERE survey_ID=?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve all survey results by survey Id #" + id);
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
	* Creates a Survey Template object from a database query
	* @param result The ResultSet from the database query
	* @return The SurveyResult object
	*/
	private SurveyResult createFromSearch(ResultSet result){
		SurveyResult sr = new SurveyResult();
		try{
            sr.setResultID(result.getInt("result_ID"));
            int surveyID = result.getInt("survey_ID");
            sr.setSurveyID(surveyID);
            //Look up the name of the survey
   			statement = con.prepareStatement("SELECT title, description FROM surveys WHERE survey_ID=?");
   			statement.setInt(1, surveyID);
   			ResultSet r = statement.executeQuery();
   			r.first();
   			sr.setSurveyTitle(r.getString("title"));
   			sr.setDescription(r.getString("description"));
   			
   			int patientID = result.getInt("patient_ID");
   			sr.setPatientID(patientID);
			//Look up the name of the patient
   			statement = con.prepareStatement("SELECT firstname, lastname FROM patients WHERE patient_ID=?");
   			statement.setInt(1, patientID);
   			r = statement.executeQuery();
   			r.first();
   			sr.setPatientName(r.getString("firstname") + " " + r.getString("lastname"));
   			sr.setCompleted(result.getBoolean("completed"));
            sr.setStartDate(result.getString("startDate"));
            sr.setEditDate(result.getString("editDate"));
            sr.setResults(result.getBytes("data"));
            
            sr = sr.processMumpsResults(sr);
		} catch (Exception e) {
			System.out.println("Error: Failed to create Survey Result object");
			e.printStackTrace();
		}
		return sr;
	}
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public String assignSurvey(SurveyResult sr) {
		String resultId = null;
		try {
			statement = con.prepareStatement("INSERT INTO survey_results (patient_ID, survey_ID, data, startDate) values (?,?,?, now())");
			statement.setInt(1, sr.getPatientID());
			statement.setInt(2, sr.getSurveyID());
			statement.setBytes(3, sr.getResults());
			statement.execute();
			
			 //Look up the id of the new survey result
   			statement = con.prepareStatement("SELECT MAX(result_ID) AS result_ID FROM survey_results");
   			ResultSet r = statement.executeQuery();
   			r.first();
   			resultId = r.getString("result_ID");
		} catch (SQLException e){
			System.out.println("Error: Could not assign survey # " + sr.getSurveyID() + " to patient # " + sr.getPatientID());
			e.printStackTrace();
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		return resultId;
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
	 * Mark a survey as completed
	 * @param id
	 */
	public void markAsComplete(int id){
		try{
			statement = con.prepareStatement("UPDATE survey_results SET completed=1 WHERE result_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not mark survey as completed");
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
	 * Upload survey results
	 * @param id The ID of the survey result
	 * @param data The survey results
	 */
	public void updateSurveyResults(int id, byte[] data){
		try{
			statement = con.prepareStatement("UPDATE survey_results SET data=?, editDate=now() WHERE result_ID=?");
			statement.setBytes(1, data);
			statement.setInt(2, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not save survey results");
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
