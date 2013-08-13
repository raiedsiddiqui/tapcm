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
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
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
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public ArrayList<Activity> getAllActivities(){
    	try{
    		statement = con.prepareStatement("SELECT event_timestamp, description, volunteer FROM activities ORDER BY event_timestamp DESC");
    		ResultSet result = statement.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			
    			statement = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
    			statement.setInt(1, result.getInt("volunteer"));
    			ResultSet r = statement.executeQuery();
    			if(r.isBeforeFirst()) {
    				r.first();
	    			a.setVolunteer(r.getString("name"));
    			} else {
    				a.setVolunteer("");
    			}
    			log.add(a);
    		}
    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log");
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
    
    public ArrayList<Activity> getAllActivitiesForVolunteer(int user){
    	try{
    		statement = con.prepareStatement("SELECT event_timestamp, description FROM activities WHERE volunteer=? ORDER BY event_timestamp DESC");
    		statement.setInt(1, user);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			log.add(a);
    		}
    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log");
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
    
    public ArrayList<Activity> getAllActivitiesForVolunteers(ArrayList<Integer> users){
    	try{
    		String sqlStatement = "SELECT event_timestamp, description, volunteer FROM activities WHERE ";
    		for(Integer id : users){
    			sqlStatement += "volunteer=" + id + " OR ";
    		}
    		if (sqlStatement.endsWith("OR ")) {
    			  sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 3);
    		}
    		sqlStatement += "ORDER BY event_timestamp DESC";
    		statement = con.prepareStatement(sqlStatement);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			
    			statement = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
    			statement.setInt(1, result.getInt("volunteer"));
    			ResultSet r = statement.executeQuery();
    			if(r.isBeforeFirst()) {
    				r.first();
	    			a.setVolunteer(r.getString("name"));
    			} else {
    				a.setVolunteer("");
    			}
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
    		statement = con.prepareStatement("SELECT event_timestamp, description FROM activities WHERE volunteer=? ORDER BY event_timestamp DESC");
    		statement.setInt(1, user);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		int count = 0;
    		while (result.next() && count < n){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			log.add(a);
    			count++;
    		}
    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log");
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
    
}