package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			stmt = con.prepareStatement("SELECT * FROM volunteers ORDER BY lastname DESC ");			
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
	
	public void addVolunteer(Volunteer volunteer){
		System.out.println("coming here...");
		try{
			stmt = con.prepareStatement("INSERT INTO volunteers (firstname, lastname, preferredname,"
					+ "gender, email, experience_level, city, province, phone_number, age_type) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)");
			
			stmt.setString(1, volunteer.getFirstName());
			stmt.setString(2, volunteer.getLastName());	
			stmt.setString(3, volunteer.getPreferredName());
			stmt.setString(4, volunteer.getGender());
			stmt.setString(5, volunteer.getEmail());
			stmt.setString(6, volunteer.getExperienceLevel());
			stmt.setString(7, volunteer.getCity());
			stmt.setString(8, volunteer.getProvince());
			stmt.setString(9, volunteer.getPhoneNumber());
			stmt.setString(10, volunteer.getAgeType());
			
			stmt.execute();
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
	
	public void updateVolunteer(Volunteer volunteer){
		
		try{
			stmt = con.prepareStatement("UPDATE volunteers SET firstname=?,lastname=?,preferredname=?,"
					+ "gender=?, email=?, experience_level=?, city=?, province=?, phone_number=?, age_type=?"
					+ " WHERE volunteer_ID=?");
			
			stmt.setString(1, volunteer.getFirstName());
			stmt.setString(2, volunteer.getLastName());
			stmt.setString(3, volunteer.getPreferredName());
			stmt.setString(4, volunteer.getGender());
			stmt.setString(5, volunteer.getEmail());
			stmt.setString(6, volunteer.getExperienceLevel());
			stmt.setString(7, volunteer.getCity());
			stmt.setString(8, volunteer.getProvince());
			stmt.setString(9, volunteer.getPhoneNumber());
			stmt.setString(10, volunteer.getAgeType());
			stmt.setInt(11, volunteer.getVolunteerId());
			
			System.out.println("Experience Level is   " +volunteer.getExperienceLevel() + "and it's length is " +volunteer.getExperienceLevel().toString().length());
			
			
			
			stmt.execute();
		}catch (SQLException e){
			logger.error("Error: updating volunteer is failed ");		
			
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
		String gender = "";
		String type = "";
		
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
				volunteer.setPreferredName(rs.getString("preferredname"));
				volunteer.setEmail(rs.getString("email"));
				
				type = rs.getString("age_type");
				level = rs.getString("experience_level");
				gender = rs.getString("gender");
				
				if (modified)
				{					
					if (!Utils.isNullOrEmpty(type))
					{
						if (type.equals("Y"))
							volunteer.setAgeType("Younger");
						else if (type.equals("O"))
							volunteer.setAgeType("Older");
					}
					
					if (!Utils.isNullOrEmpty(level)){
						
						if (level.equals("E"))
							volunteer.setExperienceLevel("Experienced");
						else if (level.equals("B"))
							volunteer.setExperienceLevel("Beginer");
						else if (level.equals("I"))
							volunteer.setExperienceLevel("Intermediate");
					}
					
					if (!Utils.isNullOrEmpty(gender)){					
						if(gender.equals("F"))
							volunteer.setGender("Female");
						else if(gender.equals("M"))
							volunteer.setGender("Male");
						else if(gender.equals("O"))
							volunteer.setGender("Other");
					}
				}
				else
				{
					volunteer.setAgeType(type);
					volunteer.setExperienceLevel(level);
					volunteer.setGender(gender);
				}
				
				volunteer.setCity(rs.getString("city"));
				volunteer.setProvince(rs.getString("province"));
				volunteer.setPhoneNumber(rs.getString("phone_number"));
				
				volunteers.add(volunteer);
			}
						
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		return volunteers;
	}

}
