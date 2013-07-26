package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.tapestry.objects.SurveyTemplate;

/**
 * SurveyTemplateDAO
 * Allow searching for appointments on the current date for a user,
 * all appointments for a user; adding new appointments.
 */
public class SurveyTemplateDao
{
	private PreparedStatement statement;
	private Connection con;
	
	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    public SurveyTemplateDao(String url, String username, String password){
    	try{
    		con = DriverManager.getConnection(url, username, password);
    	} catch (SQLException e){
    		System.out.println("Error: Could not connect to database");
    		System.out.println(e.toString());
    	}
    }
    
    /**
	* Creates a Survey Template object from a database query
	* @param result The ResultSet from the database query
	* @return The Patient object
	*/
	private SurveyTemplate createFromSearch(ResultSet result){
		SurveyTemplate st = new SurveyTemplate();
		try{
            st.setSurveyID(result.getInt("survey_ID"));
            st.setTitle(result.getString("title"));
            st.setType(result.getString("type"));
            st.setContents(result.getBytes("contents"));
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			System.out.println(e.toString());
		}
		return st;
	}
    
	/**
	 * Ordered by title ascending
	 * @param itemsToReturn 
	 * @param startIndex 
	 */
	public ArrayList<SurveyTemplate> getAllSurveyTemplates()
	{
		try {
			statement = con.prepareStatement("SELECT * FROM survey_app.surveys");
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyTemplate> allSurveyTemplates = new ArrayList<SurveyTemplate>();
			while(result.next()){
				SurveyTemplate st = createFromSearch(result);
				allSurveyTemplates.add(st);
			}
			return allSurveyTemplates;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey templates");
			System.out.println(e.toString());
			return null;
		}
	}
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public void uploadSurveyTemplate(SurveyTemplate st) {
		try {
			statement = con.prepareStatement("INSERT INTO survey_app.surveys (title, type, contents) values (?,?,?)");
			statement.setString(1, st.getTitle());
			statement.setString(2, st.getType());
			statement.setBytes(3, st.getContents());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not upload survey template");
			System.out.println(e.toString());
		}
	}
	
	/**
	 * Delete a survey template from the database
	 * @param id
	 */
	public void deleteSurveyTemplate(int id) {
		try {
			statement = con.prepareStatement("DELETE FROM survey_app.surveys WHERE survey_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not delete survey template");
			System.out.println(e.toString());
		}
	}
}
