package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.tapestry.controller.Utils;
import org.tapestry.objects.User;
import org.tapestry.objects.Volunteer;

/**
 * 
 * @author lxie
 * 
 * VolunteerDAO
 * Allow searching, adding, modifying, and deleting volunteer by admin ,
 * .
 */

public class VolunteerDao {
	
	private PreparedStatement stmt;
	private Connection con;	
	private ResultSet rs;
	
	private static Logger logger = Logger.getLogger(Volunteer.class);
	
	/**  
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306/survey_app")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
	public VolunteerDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			logger.error("Error from  VolunteerDao: Could not connect to database");
			System.out.println("Error from  VolunteerDao: Could not connect to database");
			e.printStackTrace();
		}
	}
	
	public List<Volunteer> getAllVolunteers(){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM volunteers ORDER BY firstname DESC ");			
			rs = stmt.executeQuery();
			
			volunteers = getVolunteersByResultSet(rs, true);
			
			if (rs != null)
				rs.close();
			
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve volunteers");				
			e.printStackTrace();			
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return volunteers;
	}
	
	public List<Volunteer> getVolunteersByName(String partialName){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		try{
			String query = "SELECT * FROM volunteers WHERE UPPER(firstname) LIKE UPPER('%" + partialName + "%') OR"
					+ " UPPER(lastname) LIKE UPPER('%" + partialName + "%') OR UPPER(preferredname) LIKE "
							+ "UPPER('%" + partialName + "%')";

			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery();	
			
			volunteers = getVolunteersByResultSet(rs, true);
			
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve users by partial name " + partialName);
			e.printStackTrace();
			
		} finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return volunteers;
	}
	
	public Volunteer getVolunteerById(int id){
		Volunteer volunteer = new Volunteer();
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM volunteers WHERE volunteer_ID = ? ORDER BY lastname DESC ");		
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
						
			volunteers = getVolunteersByResultSet(rs, false);
			volunteer = volunteers.get(0);
			
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve volunteers");				
			e.printStackTrace();			
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return volunteer;
	}
	
	public int getVolunteerIdByUsername(String username){		
		List<Volunteer> volunteers = new ArrayList<Volunteer>();	
		Volunteer volunteer = new Volunteer();
		try{
			stmt = con.prepareStatement("SELECT * FROM volunteers WHERE username = ? ");		
			stmt.setString(1, username);
			rs = stmt.executeQuery();
						
			volunteers = getVolunteersByResultSet(rs, false);
			volunteer = volunteers.get(0);
				
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve volunteers");				
			e.printStackTrace();			
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return volunteer.getVolunteerId();
	}
	
	public List<String> getAllExistUsernames(){
		List<String> uList = new ArrayList<String>();
		String username;
		try{			
			stmt = con.prepareStatement("SELECT username FROM volunteers");					
			rs = stmt.executeQuery();
			
			try{
				while(rs.next())
				{
					username = rs.getString("username");
					uList.add(username);
				}
							
			}catch (SQLException e){
				e.printStackTrace();
			}
										
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve volunteers");				
			e.printStackTrace();			
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return uList;
	}
	
	public boolean addVolunteer(Volunteer volunteer){
		boolean success = false;
		//check if it is new record in DB
		if(!isExist(volunteer)){
			
			try{
				stmt = con.prepareStatement("INSERT INTO volunteers (firstname, lastname, street,"
						+ "username, email, experience_level, city, province, home_phone, cell_phone,"
						+ "postal_code, country, emergency_contact, emergency_phone, appartment, notes,"
						+ " availability, street_number) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
				stmt.setString(1, volunteer.getFirstName()); 
				stmt.setString(2, volunteer.getLastName());	
				stmt.setString(3, volunteer.getStreet());
				stmt.setString(4, volunteer.getUserName());
				stmt.setString(5, volunteer.getEmail());
				stmt.setString(6, volunteer.getExperienceLevel());
				stmt.setString(7, volunteer.getCity());
				stmt.setString(8, volunteer.getProvince());
				stmt.setString(9, volunteer.getHomePhone());
				stmt.setString(10, volunteer.getCellPhone());
				stmt.setString(11, volunteer.getPostalCode());
				stmt.setString(12, volunteer.getCountry());
				stmt.setString(13, volunteer.getEmergencyContact());
				stmt.setString(14, volunteer.getEmergencyPhone());
				stmt.setString(15, volunteer.getAptNumber());
				stmt.setString(16, volunteer.getNotes());					
				stmt.setString(17, volunteer.getAvailability());				
				stmt.setString(18, volunteer.getStreetNumber());
				
				stmt.execute();
				success = true;
			}catch (SQLException e){
				logger.error("Error: Could not create a volunteer");			
				e.printStackTrace();
			}finally {
	    		try{
	    			//close statement    			
	    			if (stmt != null)
	    				stmt.close();  
	    
	    		} catch (Exception e) {
	    			//Ignore
	    		}
			}				
		}
		
		
		return success;
	}
	
	public void updateVolunteer(Volunteer volunteer){
		
		try{

			stmt = con.prepareStatement("UPDATE volunteers SET firstname=?,lastname=?, username=?, street=?,"
					+ "email=?, experience_level=?, city=?, province=?, home_phone=?, cell_phone=?,"
					+ "postal_code=?, country=?, emergency_contact=?, emergency_phone=?, appartment=?, "
					+ "notes=?, availability=?, street_number=? WHERE volunteer_ID=?");
			
			stmt.setString(1, volunteer.getFirstName());
			stmt.setString(2, volunteer.getLastName());
			stmt.setString(3, volunteer.getUserName());
			stmt.setString(4, volunteer.getStreet());
			stmt.setString(5, volunteer.getEmail());
			stmt.setString(6, volunteer.getExperienceLevel());
			stmt.setString(7, volunteer.getCity());
			stmt.setString(8, volunteer.getProvince());
			stmt.setString(9, volunteer.getHomePhone());
			stmt.setString(10, volunteer.getCellPhone());
			stmt.setString(11, volunteer.getPostalCode());
			stmt.setString(12, volunteer.getCountry());
			stmt.setString(13, volunteer.getEmergencyContact());
			stmt.setString(14, volunteer.getEmergencyPhone());
			stmt.setString(15, volunteer.getAptNumber());
			stmt.setString(16, volunteer.getNotes());		
			stmt.setString(17, volunteer.getAvailability());
			stmt.setString(18,  volunteer.getStreetNumber());

			stmt.setInt(19, volunteer.getVolunteerId());
			
			stmt.execute();
		}catch (SQLException e){
			logger.error("Error: updating volunteer is failed ");		
			System.out.println("updating volunteer is failed");
			e.printStackTrace();
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    
    		} catch (Exception e) {
    			//Ignore
    		}
		}
		
	}
	
	public void deleteVolunteerById(int id){		
		try{
			stmt = con.prepareStatement("DELETE FROM volunteers WHERE volunteer_ID=?");
			stmt.setInt(1, id);
			stmt.execute();
		} catch (SQLException e){
			logger.error("Error: Could not delete volunteer");			
			e.printStackTrace();
		} finally {
    		try{    			
    			//close statement
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}	
	
	public int countAllVolunteers(){
		int count = 0;
		try{
			stmt = con.prepareStatement("SELECT COUNT(*) as c FROM volunteers");
			rs = stmt.executeQuery();
			rs.first();
			
			count = rs.getInt("c");
			
			if (rs != null)
				rs.close();
			
		} catch (SQLException e){
			System.out.println("Error: Could not count volunteers");
			e.printStackTrace();
			
		} finally {
    		try{//close statement
    			if (stmt != null)
    				stmt.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
		
		return count;
	}
		
	
	private List<Volunteer> getVolunteersByResultSet(ResultSet rs, boolean modified){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		Volunteer volunteer = null;
		String level = "";
//		String gender = "";
		
		try{
			while(rs.next())
			{
				volunteer = new Volunteer();	
				volunteer.setVolunteerId(rs.getInt("volunteer_ID"));
				volunteer.setFirstName(rs.getString("firstname"));
				volunteer.setLastName(rs.getString("lastname"));
				
				StringBuffer sb = new StringBuffer();
				sb.append(rs.getString("firstname"));
				sb.append(" ");
				sb.append(rs.getString("lastname"));
				
				volunteer.setDisplayName(sb.toString());
				volunteer.setUserName(rs.getString("username"));
				volunteer.setEmail(rs.getString("email"));
				

				level = rs.getString("experience_level");
//				gender = rs.getString("gender");
				
				if (modified)
				{					
//					if (!Utils.isNullOrEmpty(type))
//					{
//						if (type.equals("Y"))
//							volunteer.setAgeType("Younger");
//						else if (type.equals("O"))
//							volunteer.setAgeType("Older");
//					}
					
					if (!Utils.isNullOrEmpty(level)){
						
						if (level.equals("E"))
							volunteer.setExperienceLevel("Experienced");
						else if (level.equals("B"))
							volunteer.setExperienceLevel("Beginer");
						else if (level.equals("I"))
							volunteer.setExperienceLevel("Intermediate");
					}
					
//					if (!Utils.isNullOrEmpty(gender)){					
//						if(gender.equals("F"))
//							volunteer.setGender("Female");
//						else if(gender.equals("M"))
//							volunteer.setGender("Male");
//						else if(gender.equals("O"))
//							volunteer.setGender("Other");
//					}
				}
				else
				{		
					volunteer.setExperienceLevel(level);
		//			volunteer.setGender(gender);
				}
				
				volunteer.setCity(rs.getString("city"));
				volunteer.setProvince(rs.getString("province"));
				volunteer.setHomePhone(rs.getString("home_phone"));
				volunteer.setCellPhone(rs.getString("cell_phone"));
				volunteer.setStreet(rs.getString("street"));
				volunteer.setStreetNumber(rs.getString("street_number"));
				volunteer.setAptNumber(rs.getString("appartment"));
				volunteer.setCountry(rs.getString("country"));
				volunteer.setEmergencyContact(rs.getString("emergency_contact"));
				volunteer.setEmergencyPhone(rs.getString("emergency_phone"));
				volunteer.setPostalCode(rs.getString("postal_code"));
				volunteer.setNotes(rs.getString("notes"));
				volunteer.setAvailability(rs.getString("availability"));				
				
				volunteers.add(volunteer);
			}
						
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		return volunteers;
	}
	
	private boolean isExist(Volunteer volunteer){		
		int count = 0;
		
		try{
			stmt = con.prepareStatement("SELECT COUNT(*) as c FROM volunteers WHERE UPPER(firstname) = UPPER(?) "
					+ "AND UPPER(lastname) = UPPER(?) AND UPPER(home_phone) = UPPER(?)");
			stmt.setString(1, volunteer.getFirstName());
			stmt.setString(2, volunteer.getLastName());
			stmt.setString(3, volunteer.getHomePhone());
			
			rs = stmt.executeQuery();
			rs.first();
			
			count = rs.getInt("c");
			
			if (rs != null)
				rs.close();
			
		} catch (SQLException e){
			System.out.println("Error: Could not count volunteers");
			e.printStackTrace();
			
		} finally {
    		try{//close statement
    			if (stmt != null)
    				stmt.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}	
		
		if (count > 0)
			return true;
		else 
			return false;
		
	}
	
	
	public List<Volunteer> getMatchedVolunteers(String type){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
	
		if (type.equals("E"))
			volunteers = this.getAllVolunteers();
		else if (type.equals("I"))
		{
			try{
				stmt = con.prepareStatement("SELECT * FROM volunteers WHERE experience_level =? OR experience_level = ?");		
				stmt.setString(1, "E");
				stmt.setString(2, "I");
				rs = stmt.executeQuery();
							
				volunteers = getVolunteersByResultSet(rs, false);
			
				if (rs != null)
					rs.close();
				
			}catch (SQLException e){			
				logger.error("Error: Could not retrieve volunteers");				
				e.printStackTrace();			
			}finally {
	    		try{
	    			//close statement    			
	    			if (stmt != null)
	    				stmt.close();  
	    		} catch (Exception e) {
	    			//Ignore
	    		}
	    	}
		} else if (type.equals("B")){
			try{
				stmt = con.prepareStatement("SELECT * FROM volunteers WHERE experience_level =?");		
				stmt.setString(1, "E");				
				rs = stmt.executeQuery();
							
				volunteers = getVolunteersByResultSet(rs, false);
			
				if (rs != null)
					rs.close();
				
			}catch (SQLException e){			
				logger.error("Error: Could not retrieve volunteers");				
				e.printStackTrace();			
			}finally {
	    		try{
	    			//close statement    			
	    			if (stmt != null)
	    				stmt.close();  
	    		} catch (Exception e) {
	    			//Ignore
	    		}
	    	}
		}
		
		return volunteers;
		
	}

}
