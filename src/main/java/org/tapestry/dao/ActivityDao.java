package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.tapestry.objects.Activity;

/**
 * ActivityDAO
 * Allow logging and displaying activities for a user
 */
public class ActivityDao {
	
	private PreparedStatement statement;
	private Connection con;
	
	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306/survey_app")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    public ActivityDao(String url, String username, String password){
    	try{
    		con = DriverManager.getConnection(url, username, password);
    	} catch (SQLException e){
    		System.out.println("Error: Could not connect to database");
    		e.printStackTrace();
    	}
    }
    
    public void logActivity(String description, int volunteer){
    	try{
    		statement = con.prepareStatement("INSERT INTO activities (description,volunteer) VALUES (?, ?)");
    		statement.setString(1, description);
    		statement.setInt(2, volunteer);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not record event");
    		e.printStackTrace();
    	}
    }
    
    public void logActivity(String description, int volunteer, int patient){
    	try{
    		statement = con.prepareStatement("INSERT INTO activities (description,volunteer,patient) VALUES (?, ?, ?)");
    		statement.setString(1, description);
    		statement.setInt(2, volunteer);
    		statement.setInt(3, patient);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not record event");
    		e.printStackTrace();
    	}
    }
    
    public ArrayList<Activity> getAllActivitiesForVolunteer(int user){
    	try{
    		statement = con.prepareStatement("SELECT DATE(event_timestamp) as event_date, description FROM activities WHERE volunteer=? ORDER BY event_timestamp DESC");
    		statement.setInt(1, user);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_date"));
    			a.setDescription(result.getString("description"));
    			log.add(a);
    		}
    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public ArrayList<Activity> getLastNActivitiesForVolunteer(int user, int n){
    	try{
    		statement = con.prepareStatement("SELECT DATE(event_timestamp) as event_date, description FROM activities WHERE volunteer=? ORDER BY event_timestamp DESC");
    		statement.setInt(1, user);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		int count = 0;
    		while (result.next() && count < n){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_date"));
    			a.setDescription(result.getString("description"));
    			log.add(a);
    			count++;
    		}
    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log");
    		e.printStackTrace();
    		return null;
    	}
    }
    
}