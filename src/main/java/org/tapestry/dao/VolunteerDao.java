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
import org.tapestry.objects.Volunteer;
import org.tapestry.objects.Organization;
							
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
	
	public List<Volunteer> getVolunteersWithAvailability(){
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
		
		//remove those volunteers whose availability have not been set up
		List<Volunteer> vList = new ArrayList<Volunteer>();
		String vAvailability;
		
		for(Volunteer v: volunteers){			
			vAvailability = v.getAvailability();
			if( (!Utils.isNullOrEmpty(vAvailability))&& (!vAvailability.equals("1non,2non,3non,4non,5non")))
				vList.add(v);
		}			
		return vList;
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
	
	public List<Volunteer> getAllVolunteersByOrganization(int id){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		
		try{			
			stmt = con.prepareStatement("SELECT * FROM volunteers WHERE organization=? ORDER BY firstname DESC ");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();	
			
			volunteers = getVolunteersByResultSet(rs, true);
			
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve users by organization # " + id);
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
		if(!isExist(volunteer))
		{			
			try{
				stmt = con.prepareStatement("INSERT INTO volunteers (firstname, lastname, street,"
						+ "username, email, experience_level, city, province, home_phone, cell_phone,"
						+ "postal_code, country, emergency_contact, emergency_phone, appartment, notes,"
						+ " availability, street_number, password, organization) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
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
				stmt.setString(19, volunteer.getPassword());
				stmt.setInt(20, volunteer.getOrganizationId());
				
				
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
					+ "notes=?, availability=?, street_number=?, password=?, organization=? WHERE volunteer_ID=?");
			
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
			stmt.setString(19,  volunteer.getPassword());
			stmt.setInt(20, volunteer.getOrganizationId());
			stmt.setInt(21, volunteer.getVolunteerId());
			
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
		
	public String getEmailByVolunteerId(int volunteerId){
		Volunteer volunteer = new Volunteer();
		String emailAddress = "";		
		
		volunteer = getVolunteerById(volunteerId);
		emailAddress = volunteer.getEmail();
		
		return emailAddress;
	}
	
	public String getVolunteerNameById(int volunteerId){
		Volunteer volunteer = new Volunteer();
		String name = "";		
		
		volunteer = getVolunteerById(volunteerId);
		name = volunteer.getDisplayName();
		
		return name;
	}
	
	public List<Organization> getAllOrganizations(){
		List<Organization> list = new ArrayList<Organization>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM organizations ORDER BY name DESC ");			
			rs = stmt.executeQuery();
			
			list = getOrganizationsByResultSet(rs);
			
			if (rs != null)
				rs.close();
			
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve organizations");				
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
		
		return list;
	}
	
	public Organization getOrganizationById(int id){
		Organization organization = new Organization();
		List<Organization> organizations = new ArrayList<Organization>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM organizations WHERE organization_ID = ? ORDER BY name DESC ");		
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
						
			organizations = getOrganizationsByResultSet(rs);
			organization = organizations.get(0);
			
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve organization by id # " + id);				
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
		
		return organization;
	}
	
	public boolean addOrganization(Organization organization){
		boolean success = false;
		
		//check if it is new record in DB		
		if(!isExist(organization))
		{			
			try{
				stmt = con.prepareStatement("INSERT INTO organizations (name, street_number, street, city, province,"
						+ "country, postal_code, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				
				stmt.setString(1, organization.getName()); 
				stmt.setString(2, organization.getStreetNumbet());	
				stmt.setString(3, organization.getStreetName());
				stmt.setString(4, organization.getCity());
				stmt.setString(5, organization.getProvince());
				stmt.setString(6, organization.getCountry());
				stmt.setString(7, organization.getPostCode());
				stmt.setString(8, organization.getPhone());									
				
				stmt.execute();
				success = true;
			}catch (SQLException e){
				logger.error("Error: Could not create a organization");			
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
	
	public void updateOrganization(Organization organization){		
		try{
			stmt = con.prepareStatement("UPDATE organizations SET name=?, street_number=?, street=?, city=?, "
					+ "province=?,postal_code=?, phone=? WHERE organization_ID=?");
			
			stmt.setString(1, organization.getName());
			stmt.setString(2, organization.getStreetNumbet());
			stmt.setString(3, organization.getStreetName());
			stmt.setString(4, organization.getCity());
			stmt.setString(5, organization.getProvince());
			stmt.setString(6, organization.getPostCode());
			stmt.setString(7, organization.getPhone());
			stmt.setInt(8, organization.getOrganizationId());			
			
			stmt.execute();
		}catch (SQLException e){
			logger.error("Error: updating organization is failed ");		
			System.out.println("updating organization is failed");
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
	
	public void deleteOrganizationById(int id){		
		try{
			stmt = con.prepareStatement("DELETE FROM organizations WHERE organization_ID=?");
			stmt.setInt(1, id);
			stmt.execute();
		} catch (SQLException e){
			logger.error("Error: Could not delete organization");			
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
	
	public int getUserIdByVolunteerId(int volunteerId){
		int userId = 0;
		try{
			stmt = con.prepareStatement("SELECT users.user_ID FROM users INNER JOIN volunteers ON "
					+ "users.username = volunteers.username WHERE volunteers.volunteer_ID = ?");
			
			stmt.setInt(1, volunteerId);				
			rs = stmt.executeQuery();
			
			while(rs.next())
				userId = rs.getInt("user_ID");
			
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){
			logger.error("Error: Could not find volunteer ID #" + volunteerId);			
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
		
		return userId;
	}
	
	
	private List<Organization> getOrganizationsByResultSet(ResultSet rs){
		List<Organization> organizations = new ArrayList<Organization>();
		
		Organization organization = null;
		
		try{
			while(rs.next())
			{
				organization = new Organization();
				organization.setOrganizationId(rs.getInt("organization_ID"));
				organization.setName(rs.getString("name"));
				organization.setStreetNumbet(rs.getString("street_number"));
				organization.setStreetName(rs.getString("street"));
				organization.setCity(rs.getString("city"));
				organization.setProvince(rs.getString("province"));
				organization.setPostCode(rs.getString("postal_code"));
				organization.setPhone(rs.getString("phone"));
				
				organizations.add(organization);
			}			
		}catch (SQLException e){
			e.printStackTrace();
		}
		return organizations;
	}
	
	private List<Volunteer> getVolunteersByResultSet(ResultSet rs, boolean modified){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		Volunteer volunteer = null;
		String level = "";
		
		try{
			while(rs.next())
			{
				volunteer = new Volunteer();	
				volunteer.setVolunteerId(rs.getInt("volunteer_ID"));
				volunteer.setFirstName(rs.getString("firstname"));
				volunteer.setLastName(rs.getString("lastname"));
				
				level = rs.getString("experience_level");
				
				StringBuffer sb = new StringBuffer();
				sb.append(rs.getString("firstname"));
				sb.append(" ");
				sb.append(rs.getString("lastname"));
				sb.append("(");
				sb.append(level);
				sb.append(")");
				
				volunteer.setDisplayName(sb.toString());
				volunteer.setUserName(rs.getString("username"));
				volunteer.setEmail(rs.getString("email"));
				
				if (modified)
				{	
					if (!Utils.isNullOrEmpty(level)){
						
						if (level.equals("E"))
							volunteer.setExperienceLevel("Experienced");
						else if (level.equals("B"))
							volunteer.setExperienceLevel("Beginer");
						else if (level.equals("I"))
							volunteer.setExperienceLevel("Intermediate");
					}
				}
				else						
					volunteer.setExperienceLevel(level);		
				
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
				
				int organizationId = rs.getInt("organization");
				volunteer.setOrganizationId(rs.getInt("organization"));
				
				Organization organization = getOrganizationById(organizationId);
				if (organization != null)
					volunteer.setOrganization(organization.getName());
				
				
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
	
	private boolean isExist(Organization organization){		
		int count = 0;
		
		try{
			stmt = con.prepareStatement("SELECT COUNT(*) as c FROM organizations WHERE UPPER(name) = UPPER(?) AND phone= ?");
			stmt.setString(1, organization.getName());
			stmt.setString(2, organization.getPhone());
		
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
	
	public List<Volunteer> getMatchedVolunteersByLevel(String level){
		List<Volunteer> volunteers = new ArrayList<Volunteer>();
		if (level.equals("E"))
			volunteers = getAllVolunteers();
		
		if (level.equals("I")){
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
		}
		
		if (level.equals("B"))
		{
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
