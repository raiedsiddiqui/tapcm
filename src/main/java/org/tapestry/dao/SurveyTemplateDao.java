package org.tapestry.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    		e.printStackTrace();
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
            st.setPriority(result.getInt("priority"));
            st.setDescription(result.getString("description"));
            
            //format date, remove time
            String date = result.getString("last_modified");
            date = date.substring(0, 10);
            st.setCreatedDate(date);
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Survey template object");
			e.printStackTrace();
		}
		return st;
	}
	
	/**
	* Returns the patient with the given ID
	* @param id The ID of the patient to find
	* @return A Patient object representing the result
	*/
	public SurveyTemplate getSurveyTemplateByID(int id){
		try{
			statement = con.prepareStatement("SELECT * FROM surveys WHERE survey_ID=?");
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
	 * Ordered by title ascending
	 * @param itemsToReturn 
	 * @param startIndex 
	 */
	public ArrayList<SurveyTemplate> getAllSurveyTemplates()
	{
		try {
			statement = con.prepareStatement("SELECT * FROM surveys ORDER BY priority DESC" );
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyTemplate> allSurveyTemplates = new ArrayList<SurveyTemplate>();
			while(result.next()){
				SurveyTemplate st = createFromSearch(result);
				allSurveyTemplates.add(st);
			}
			return allSurveyTemplates;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey templates");
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
	//getUsersByPartialName(name);
	/**
	 * Ordered by title ascending
	 * @param itemsToReturn 
	 * @param startIndex 
	 */
	public ArrayList<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle)
	{
		try {
			statement = con.prepareStatement("SELECT * FROM surveys WHERE UPPER(title) LIKE UPPER('%" + partialTitle + "%')");
			
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyTemplate> allSurveyTemplates = new ArrayList<SurveyTemplate>();
			while(result.next()){
				SurveyTemplate st = createFromSearch(result);
				allSurveyTemplates.add(st);
			}
			return allSurveyTemplates;
		} catch (SQLException e) {
			System.out.println("Error: Could not retrieve survey template by partial title " + partialTitle);
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
	 * Uploads a survey template to the database
	 * @param st
	 */
	public void uploadSurveyTemplate(SurveyTemplate st) {
		try {
			statement = con.prepareStatement("INSERT INTO surveys (title, type, contents, priority, description) values (?,?,?,?,?)");
			statement.setString(1, st.getTitle());
			statement.setString(2, st.getType());
			statement.setBytes(3, st.getContents());
			statement.setInt(4, st.getPriority());
			statement.setString(5, st.getDescription());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not upload survey template");
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
	 * Delete a survey template from the database
	 * @param id
	 */
	public void deleteSurveyTemplate(int id) {
		try {
			statement = con.prepareStatement("DELETE FROM surveys WHERE survey_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not delete survey template");
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
