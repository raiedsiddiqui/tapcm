package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.tapestry.objects.Appointment;

/**
 * AppointmentDAO
 * Allow searching for appointments on the current date for a user,
 * all appointments for a user; adding new appointments.
 */
public class AppointmentDao {
	
	private PreparedStatement statement;
	private Connection con;
	
	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306/survey_app")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    public AppointmentDao(String url, String username, String password){
    	try{
    		con = DriverManager.getConnection(url, username, password);
    	} catch (SQLException e){
    		System.out.println("Error: Could not connect to database");
    		e.printStackTrace();
    	}
    }
    
    private Appointment createFromSearch(ResultSet result){
    	Appointment a = new Appointment();
    	try{
    		a.setAppointmentID(result.getInt("appointment_ID"));
    		a.setVolunteerID(result.getInt("volunteer"));
    		int id = result.getInt("patient");
    		a.setPatientID(id);
    		statement = con.prepareStatement("SELECT firstname, lastname FROM patients WHERE patient_ID=?");
    		statement.setInt(1, id);
    		ResultSet r = statement.executeQuery();
    		if (r.first()){
    			String firstName = r.getString("firstname");
    			String lastName = r.getString("lastname");
    			a.setPatient(firstName + " " + lastName.substring(0,1) + ".");
    		} else {
    			return null; //If we book an appointment for a patient that doesn't exist, the above query will fail
    		}
    		a.setDate(result.getString("appt_date"));
    		a.setTime(result.getString("appt_time"));
    		a.setDescription(result.getString("details"));
    		a.setApproved(result.getBoolean("approved"));
    		return a;
    	} catch (SQLException e){
    		System.out.println("Error: Could not create Appointment object");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public ArrayList<Appointment> getAllAppointments(){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, TIME(date_time) as appt_time, details, approved FROM appointments ORDER BY date_time DESC");
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			statement = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
       			statement.setInt(1, result.getInt("patient"));
       			ResultSet rs = statement.executeQuery();
       			rs.first();
       			a.setVolunteer(rs.getString("name"));
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all appointments");
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
    	
    public ArrayList<Appointment> getAllAppointmentsForVolunteer(int volunteer){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, TIME(date_time) as appt_time, details, approved FROM appointments WHERE volunteer=? ORDER BY date_time DESC");
    		statement.setInt(1, volunteer);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all appointments for user #" + volunteer);
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
    
    public ArrayList<Appointment> getAllAppointmentsForVolunteerForToday(int volunteer){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, TIME(date_time) as appt_time, details, approved FROM appointments WHERE volunteer=? AND DATE(date_time)=CURDATE() ORDER BY date_time DESC");
    		statement.setInt(1, volunteer);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all appointments for today for user #" + volunteer);
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
    
    public void createAppointment(Appointment a){
    	try{
    		statement = con.prepareStatement("INSERT INTO appointments (volunteer, patient, date_time) values (?,?,?)");
    		statement.setInt(1, a.getVolunteerID());
    		statement.setInt(2, a.getPatientID());
    		statement.setString(3, a.getDate() + " " + a.getTime());
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not create appointment");
    		e.printStackTrace();
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void approveAppointment(int id){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET approved=1 WHERE appointment_ID=?");
    		statement.setInt(1, id);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not approve appointment");
    		e.printStackTrace();
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void unapproveAppointment(int id){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET approved=0 WHERE appointment_ID=?");
    		statement.setInt(1, id);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not unapprove appointment");
    		e.printStackTrace();
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void deleteAppointment(int id){
    	try{
    		statement = con.prepareStatement("DELETE FROM appointments WHERE appointment_ID=?");
    		statement.setInt(1, id);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not delete appointment");
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