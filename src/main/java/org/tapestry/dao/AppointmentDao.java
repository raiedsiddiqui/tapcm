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
    		System.out.println(e.toString());
    	}
    }
    
    private Appointment createFromSearch(ResultSet result){
    	Appointment a = new Appointment();
    	try{
    		a.setVolunteer(result.getInt("volunteer"));
    		int id = result.getInt("patient");
    		a.setPatientID(id);
    		statement = con.prepareStatement("SELECT firstname, lastname FROM patients WHERE patient_ID=?");
    		statement.setInt(1, id);
    		ResultSet r = statement.executeQuery();
    		r.first();
    		String firstName = r.getString("firstname");
    		String lastName = r.getString("lastname");
    		a.setPatient(firstName + " " + lastName.substring(0,1) + ".");
    		a.setDate(result.getString("appt_date"));
    		a.setTime(result.getString("appt_time"));
    		a.setDescription(result.getString("details"));
    	} catch (SQLException e){
    		System.out.println("Error: Could not create Appointment object");
    		System.out.println(e.toString());
    	}
		return a;
    }
    	
    public ArrayList<Appointment> getAllAppointmentsForVolunteer(int volunteer){
    	try{
    		statement = con.prepareStatement("SELECT volunteer, patient, DATE(date_time) as appt_date, TIME(date_time) as appt_time, details FROM appointments WHERE volunteer=?");
    		statement.setInt(1, volunteer);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve appointments");
    		System.out.println(e.toString());
    		return null;
    	}
    }
    
    public ArrayList<Appointment> getAllAppointmentsForVolunteerForToday(int volunteer){
       	try{
    		statement = con.prepareStatement("SELECT volunteer, patient, DATE(date_time) as appt_date, TIME(date_time) as appt_time, details FROM appointments WHERE volunteer=? AND DATE(date_time)=CURDATE()");
    		statement.setInt(1, volunteer);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve appointments");
    		System.out.println(e.toString());
    		return null;
    	}   	
    }
    
    public void createAppointment(Appointment a){
    	try{
    		statement = con.prepareStatement("INSERT INTO appointments (volunteer, patient, date_time) values (?,?,?)");
    		statement.setInt(1, a.getVolunteer());
    		statement.setInt(2, a.getPatientID());
    		statement.setString(3, a.getDate() + " " + a.getTime());
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not save appointment");
    		System.out.println(e.toString());
    	}
    }
}