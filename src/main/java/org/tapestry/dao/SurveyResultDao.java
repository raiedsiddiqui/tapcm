package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

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
			statement = con.prepareStatement("SELECT * FROM survey_app.SurveyResult WHERE patient_id=?");
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
	public ArrayList<SurveyResult> findAll(int startIndex, int itemsToReturn)
	{
		try {
			statement = con.prepareStatement("SELECT * FROM survey_app.SurveyResult");
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
	* Creates a Survey Template object from a database query
	* @param result The ResultSet from the database query
	* @return The Patient object
	*/
	private SurveyResult createFromSearch(ResultSet result){
		SurveyResult sr = new SurveyResult();
		try{
            sr.setResultID(result.getInt("result_ID"));
            sr.setSurveyID(result.getInt("survey_ID"));
            sr.setUserID(result.getInt("user_ID"));
            sr.setPatientID(result.getInt("patient_ID"));
            sr.setResults(result.getBytes("results"));
            Timestamp ts = result.getTimestamp("completed_date");
            Calendar cal = Calendar.getInstance();
            cal.setTime(ts);
            sr.setCompletedDate(cal);
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			System.out.println(e.toString());
		}
		return sr;
	}
}
