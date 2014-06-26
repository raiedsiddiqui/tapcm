package org.tapestry.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.tapestry.controller.Utils;
import org.tapestry.objects.Activity;

/**
 * ActivityDAO
 * Allow logging and displaying activities for a user
 */
public class ActivityDao {
	
	private PreparedStatement stmt;
	private Connection con;	
	
	private static Logger logger = Logger.getLogger(ActivityDao.class);
	
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
    
    public ArrayList<Activity> getAllActivities(){
    	try{
    		stmt = con.prepareStatement("SELECT event_timestamp, description, volunteer, appointment "
    				+ "FROM activities ORDER BY event_timestamp DESC");
    		ResultSet result = stmt.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			a.setAppointment(result.getInt("appointment"));
    			
    			stmt = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
    			stmt.setInt(1, result.getInt("volunteer"));
    			ResultSet r = stmt.executeQuery();
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
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public ArrayList<Activity> getAllActivitiesWithAppointments(){
    	try{
    		stmt = con.prepareStatement("SELECT event_timestamp, description, volunteer, appointment, "
    				+ "TIME(event_timestamp) AS time FROM activities WHERE appointment IS NOT NULL ORDER BY event_timestamp DESC");
    		ResultSet result = stmt.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			a.setAppointment(result.getInt("appointment"));
    			String time = result.getString("time");
    			a.setTime(time.substring(0, time.length() - 3));
    			
    			stmt = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
    			stmt.setInt(1, result.getInt("volunteer"));
    			ResultSet r = stmt.executeQuery();
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
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    //get all activities set by volunteers , not by Admin(logged in and logged out...)
    public List<Activity> getAllActivitiesForVolunteer(int user, boolean isVolunteer){
    	List<Activity> activities = new ArrayList<Activity>();
    	
    	if (isVolunteer)
    		activities = getAllActivitiesForVolunteer(user);
    	
    	return activities;
    }
    
    public List<Activity> getAllActivitiesForVolunteer(int user){
    	try{
    		//filter out all logged in/out, or password changing... by setting patient = 0
    		stmt = con.prepareStatement("SELECT * FROM activities WHERE volunteer=? AND patient = 0");
    		
    		stmt.setInt(1, user);
    		ResultSet result = stmt.executeQuery();
    		
    		List<Activity> log = new ArrayList<Activity>();
    		log = getActivitiesByResultSet(result);

    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log for volunteer");
    		e.printStackTrace();
    		return null;
    	} finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public List<Activity> getAllActivitiesForAdmin(){
    	try{
    		//filter out all logged in/out, or password changing... by setting patient = 0
    		stmt = con.prepareStatement("SELECT * FROM activities WHERE patient = 0 ORDER BY event_timestamp"  );    		
    		ResultSet result = stmt.executeQuery();
    		
    		List<Activity> log = new ArrayList<Activity>();
    		log = getActivitiesByResultSet(result);

    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activities log for Admin");
    		e.printStackTrace();
    		return null;
    	} finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
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
    		stmt = con.prepareStatement(sqlStatement);
    		ResultSet result = stmt.executeQuery();
    		ArrayList<Activity> log = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			
    			stmt = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
    			stmt.setInt(1, result.getInt("volunteer"));
    			ResultSet r = stmt.executeQuery();
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
    	}finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e){
    			//Ignore
    		}
    	}
    }
    
    public List<Activity> getDetailedLog(int patientId, int appointmentId){
    	try{
    		String sqlStatement = "SELECT * FROM activities WHERE patient=? AND appointment=? ORDER BY event_timestamp DESC";
    		
    		stmt = con.prepareStatement(sqlStatement);
    		
    		stmt.setInt(1, patientId);
    		stmt.setInt(2, appointmentId);
    		
    		ResultSet result = stmt.executeQuery();    		
    		
    		List<Activity> log = this.getActivitiesByResultSet(result);
//    		while (result.next()){
//    			Activity a = new Activity();
//    			a.setDate(result.getString("event_timestamp"));
//    			a.setDescription(result.getString("description"));    			
//    			    			
//    			a.setVolunteer(String.valueOf(result.getInt("volunteer")));
//    			a.setPatient(String.valueOf(patientId));
//    			a.setAppointment(appointmentId);
//
//    			log.add(a);
//    		}
    		return log;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve detailed activity log");
    		e.printStackTrace();
    		return null;
    	}finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e){
    			//Ignore
    		}
    	}
    }
    
    public void logActivity(String description, int volunteer){
    	try{
    		stmt = con.prepareStatement("INSERT INTO activities (description,volunteer) VALUES (?, ?)");
    		stmt.setString(1, description);
    		stmt.setInt(2, volunteer);
    		stmt.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not record event");
    		e.printStackTrace();
    	} finally {
    		try{
    			stmt.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void logActivity(String description, int volunteer, int patient){
    	try{
    		stmt = con.prepareStatement("INSERT INTO activities (description,volunteer,patient) VALUES (?, ?, ?)");
    		stmt.setString(1, description);
    		stmt.setInt(2, volunteer);
    		stmt.setInt(3, patient);
    		stmt.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not record event");
    		e.printStackTrace();
    	} finally {
    		try{
    			//close  statement
    			if (stmt != null)
    				stmt.close();  
    			
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void logActivity(Activity activity){
    	try{
    		stmt = con.prepareStatement("INSERT INTO activities (description,volunteer,"
    				+ "event_timestamp,start_Time,end_Time, patient) VALUES (?, ?, ?, ?, ?, ?)");
    		    		
    		stmt.setString(1, activity.getDescription());
    		stmt.setInt(2, Integer.parseInt(activity.getVolunteer()));   	
    		stmt.setString(3, activity.getDate());
    		stmt.setString(4, activity.getStartTime());
    		stmt.setString(5, activity.getEndTime()); 
    		stmt.setInt(6, 0);
    		
    		stmt.execute();
    		
    	} catch (SQLException e){
    		System.out.println("Error: Could not record event");
    		e.printStackTrace();
    	} finally {
    		try{
    			//close  statement
    			if (stmt != null)
    				stmt.close();  
    			
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void updateActivity( Activity activity){
		try{
			stmt = con.prepareStatement("UPDATE activities SET event_timestamp=?,description=?,"
					+ "start_Time=?, end_Time=? WHERE event_ID=?");
			
			stmt.setString(1, activity.getDate());
			stmt.setString(2, activity.getDescription());
			stmt.setString(3, activity.getStartTime());
			stmt.setString(4, activity.getEndTime());
			stmt.setInt(5, activity.getActivityId());
			
			stmt.execute();
		}catch (SQLException e){
			logger.error("Error: updating activity is failed ");		
			
			e.printStackTrace();
		}finally {
    		try{
    			//close resultSet and statement
    			if (stmt != null)
    				stmt.close();  
    
    		} catch (Exception e) {
    			//Ignore
    		}
		}
	}
    
    public void logActivity(String description, int volunteer, int patient, int appointment){
    	try{
    		stmt = con.prepareStatement("INSERT INTO activities (description,volunteer,patient,appointment) VALUES (?, ?, ?, ?)");
    		stmt.setString(1, description);
    		stmt.setInt(2, volunteer);
    		stmt.setInt(3, patient);
    		stmt.setInt(4, appointment);
    		stmt.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not record event");
    		e.printStackTrace();
    	} finally {
    		try{
    			//close  statement
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public int countEntries(){
    	try{
    		stmt = con.prepareStatement("SELECT COUNT(event_ID) AS c FROM activities");
    		ResultSet result = stmt.executeQuery();
    		result.first();
    		return result.getInt("c");
    	} catch (SQLException e){
    		System.out.println("Error: Could not count entries");
    		e.printStackTrace();
    		return 0;
    	} finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e){
    			//Ignore;
    		}
    	}
    }
    
   
    
    /**
     * Returns a list of n items starting at start
     * @param start The position in the log to start at
     * @param n The number of items to return
     */
    public ArrayList<Activity> getPage(int start, int n){
    	try{
    		stmt = con.prepareStatement("SELECT event_timestamp, description, volunteer FROM activities ORDER BY event_timestamp DESC LIMIT ?,?");
    		stmt.setInt(1, start);
    		stmt.setInt(2, n);
    		ResultSet result = stmt.executeQuery();
    		ArrayList<Activity> page = new ArrayList<Activity>();
    		while (result.next()){
    			Activity a = new Activity();
    			a.setDate(result.getString("event_timestamp"));
    			a.setDescription(result.getString("description"));
    			stmt = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
    			stmt.setInt(1, result.getInt("volunteer"));
    			ResultSet r = stmt.executeQuery();
    			if(r.isBeforeFirst()) {
    				r.first();
	    			a.setVolunteer(r.getString("name"));
    			} else {
    				a.setVolunteer("");
    			}
    			page.add(a);
    		}
    		return page;
    	} catch (SQLException e){
    		System.out.println("Error: Could not create page");
    		e.printStackTrace();
    		return null;
    	} finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e){
    			//Ignore
    		}
    	}
    }
    
    public ArrayList<Activity> getLastNActivitiesForVolunteer(int user, int n){
    	try{
    		stmt = con.prepareStatement("SELECT event_timestamp, description FROM activities WHERE volunteer=? ORDER BY event_timestamp DESC");
    		stmt.setInt(1, user);
    		ResultSet result = stmt.executeQuery();
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
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public Activity getActivityLogById(int activityId){
    	Activity activity = new Activity();
    	
    	try{
    		String sql = "SELECT event_ID, event_timestamp, description, start_Time, end_Time, volunteer FROM activities WHERE event_ID = ?";
    		stmt = con.prepareStatement(sql);
    		stmt.setInt(1, activityId);
    		
    		ResultSet rs = stmt.executeQuery();
    		List<Activity> activities = new ArrayList<Activity>();
    		activities = getActivitiesByResultSet(rs);
    		    		
    		activity = activities.get(0);
    		activity.setActivityId(activityId);
        		
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve activitie log");
    		e.printStackTrace();
    	
    	} finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return activity;
    }
    
    private List<Activity> getActivitiesByResultSet(ResultSet rs){
    	List<Activity> activities = new ArrayList<Activity>();
    	Activity activity = null;
    	String date = null;
    	String time = null;
    	String startTime = null;
    	String endTime = null;
    	
    	try{
    		while (rs.next()){    			
    			activity = new Activity();
    			
    			date = rs.getString("event_timestamp");    			
    			date = date.substring(0,10);    
    			
    			activity.setDate(date);
    			activity.setDescription(rs.getString("description")); 
    		
    			activity.setActivityId(rs.getInt("event_ID"));
    		
    			startTime = rs.getString("start_Time");    			
    			endTime = rs.getString("end_Time");  			
    			    			
    			//startTime and endTime are optional, could be 00:00  
    			String strStartTime = null;
    			if (!Utils.isNullOrEmpty(startTime)){
    				strStartTime = startTime.substring(11,16);
        			activity.setStartTime(strStartTime);
    			}
    			 
    			String strEndTime = null;
    			if (!Utils.isNullOrEmpty(endTime)){
    				strEndTime = endTime.substring(11,16);
        			activity.setEndTime(strEndTime);
    			}
    			
    			
    			if (startTime != null & startTime != "" && endTime != null & endTime != ""){  	    				    				
    				time = Utils.timeFormat(strStartTime) + " - " 
    						+ Utils.timeFormat(strEndTime);
    				
    				activity.setTime(time);
    			}    			
    			
    			if ((!Utils.isNullOrEmpty(strStartTime)) && strStartTime.equals("00:00"))
    			{
    				if(strEndTime.endsWith("00:00"))
    					activity.setTime("");
    				else
    					activity.setTime("- " + Utils.timeFormat(strEndTime));
    			}
    			else
    			{
    				if((!Utils.isNullOrEmpty(strEndTime)) &&strEndTime.endsWith("00:00"))
    					activity.setTime(Utils.timeFormat(strStartTime) + " -");
    			}
    			
    			//set volunteer    
    			int vId = rs.getInt("volunteer");
    			activity.setVolunteer(String.valueOf(vId));
    			stmt = con.prepareStatement("SELECT firstname, lastname FROM volunteers WHERE volunteer_ID = ? ORDER BY lastname DESC ");		
    			stmt.setInt(1, vId);
    			
    			ResultSet resultSet;
    			resultSet = stmt.executeQuery();
    			
    			while(resultSet.next())
    			{
    				StringBuffer sb = new StringBuffer();
    				sb.append(resultSet.getString("firstname"));
    				sb.append(" ");
    				sb.append(resultSet.getString("lastname"));
    				
    				activity.setVolunteerName(sb.toString());      				
    			}
    			
    			activities.add(activity);
        	}
    	}catch (SQLException e){
			e.printStackTrace();
		}     	
    	
    	return activities;
    }
    
    public void deleteActivityById(int activityId){
    	try{
			stmt = con.prepareStatement("DELETE FROM activities WHERE event_ID=?");
			stmt.setInt(1, activityId);
			stmt.execute();
		} catch (SQLException e){
			logger.error("Error: Could not delete activity");			
			e.printStackTrace();
		} finally {
    		try{    			
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    
    
}