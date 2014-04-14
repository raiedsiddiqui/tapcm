package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.tapestry.controller.Utils;
import org.tapestry.objects.Appointment;
import org.apache.log4j.Logger;

/**
 * AppointmentDAO
 * Allow searching for appointments on the current date for a user,
 * all appointments for a user; adding new appointments.
 */
public class AppointmentDao {
	protected static Logger logger = Logger.getLogger(AppointmentDao.class);
	
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
    		statement = con.prepareStatement("SELECT firstname, lastname, preferredName FROM patients WHERE patient_ID=?");
    		statement.setInt(1, id);
    		ResultSet r = statement.executeQuery();
    		if (r.first()){
    			String preferredName = r.getString("preferredName");
    			//add empty checking for preferedName
    			if(preferredName != null && !(preferredName.equals(""))) {
	    			a.setPatient(preferredName);	    			
    			} else {
    			
    				String firstName = r.getString("firstname");
	    			String lastName = r.getString("lastname");
	    			a.setPatient(firstName + " " + lastName.substring(0,1) + ".");	    		
    			}
    		} else {
    			return null; //If we book an appointment for a patient that doesn't exist, the above query will fail
    		}
    		a.setDate(result.getString("appt_date"));
    		String time = result.getString("appt_time");
    		a.setTime(time.substring(0, time.length() - 3));
    		a.setComments(result.getString("comments"));
    		a.setStatus(result.getString("status"));
    		a.setCompleted(result.getBoolean("completed"));
    		a.setContactedAdmin(result.getBoolean("contactedAdmin"));
    		return a;
    	} catch (SQLException e){
    		System.out.println("Error: Could not create Appointment object");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public ArrayList<Appointment> getAllAppointments(){
    	try{
//    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
//    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments ORDER BY date_time DESC");
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments ORDER BY date_time DESC");
    		
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		
       		while(result.next()){       			
       			Appointment a = createFromSearch(result);   
       			
       			if (a != null)
       			{
//      			statement = con.prepareStatement("SELECT name FROM users WHERE user_ID=?");
	       			statement = con.prepareStatement("SELECT firstname, lastname FROM volunteers WHERE volunteer_ID=?");
	       			statement.setInt(1, result.getInt("volunteer"));
	       			ResultSet rs = statement.executeQuery();
	       			rs.first();	       			
	       			
	       			StringBuffer sb = new StringBuffer();
	       			
	       			while(rs.next()){
	       				if (!Utils.isNullOrEmpty(rs.getString("firstname")))
		       			{
		       				sb.append(rs.getString("firstname"));
		       				sb.append(" ");		       				
		       			}
	       				
	       				if (!Utils.isNullOrEmpty(rs.getString("lastname")))
		       			{
		       				sb.append(rs.getString("lastname"));
		       			}
		       				
	       				if (!Utils.isNullOrEmpty(sb.toString()))
	       					a.setVolunteer(sb.toString());
	       			}	       			
	       			
	       			allAppointments.add(a);
       			
       			}
//       			if (a != null)
//       				allAppointments.add(a);
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
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE volunteer=? AND date_time>=CURDATE() AND completed=0 ORDER BY date_time ASC");
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
    
    public ArrayList<Appointment> getAllApprovedAppointmentsForVolunteer(int volunteerId){
    	try{    		
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE volunteer=? AND date_time>=CURDATE() AND completed=0 AND status='Approved' ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();       		
       		
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
       			
        	}     
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all approved appointments for volunteer #" + volunteerId);
    		e.printStackTrace();
    		return null;
    	} finally {
    		try{
    			if (statement != null)
    				statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public ArrayList<Appointment> getAllPendingAppointmentsForVolunteer(int volunteerId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE volunteer=? AND date_time>=CURDATE() AND completed=0 AND status='Awaiting Approval' ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all pending appointments for volunteer #" + volunteerId);
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
    
    public ArrayList<Appointment> getAllDeclinedAppointmentsForVolunteer(int volunteerId){
 
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE volunteer=? AND date_time>=CURDATE() AND completed=0 AND status='Declined' ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all declined appointments for volunteer #" + volunteerId);
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
    
    public ArrayList<Appointment> getAllApprovedAppointmentsForPatient(int patientId){
    	try{    		
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE patient=? AND date_time>=CURDATE() AND completed=0 AND status='Approved' ORDER BY date_time ASC");
    		statement.setInt(1, patientId);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all approved appointments for patient #" + patientId);
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
    
    public ArrayList<Appointment> getAllPendingAppointmentsForPatient(int patientId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE patient=? AND date_time>=CURDATE() AND completed=0 AND status='Awaiting Approval' ORDER BY date_time ASC");
    		statement.setInt(1, patientId);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all pending appointments for patient #" + patientId);
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
    
    public ArrayList<Appointment> getAllDeclinedAppointmentsForPatient(int patientId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE patient=? AND date_time>=CURDATE() AND completed=0 AND status='Declined' ORDER BY date_time ASC");
    		statement.setInt(1, patientId);
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all declined appointments for patient #" + patientId);
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
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE volunteer=? AND DATE(date_time)=CURDATE() AND completed=0 ORDER BY date_time ASC");
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
    
    public Appointment getAppointmentById(int appointmentId){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE appointment_ID=?");
    		statement.setInt(1, appointmentId);
    		ResultSet result = statement.executeQuery();
    		result.first();
       		return createFromSearch(result);
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve appointment with appointment ID:" + appointmentId);
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
    
    public Appointment getAppointmentByMostRecentIncomplete(int patientId){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments WHERE patient=? AND completed=0 AND status='Approved' ORDER BY date_time ASC LIMIT 1");
    		statement.setInt(1, patientId);
    		ResultSet result = statement.executeQuery();
    		result.first();
       		return createFromSearch(result);
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve oldest incomplete appointment for patient ID:" + patientId);
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
    
    public void completeAppointment(int id, String comments, boolean contactedAdmin) {
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET comments=?, completed=1, contactedAdmin=? WHERE appointment_ID=? ");
    		statement.setString(1, comments);
    		statement.setBoolean(2, contactedAdmin);
    		statement.setInt(3, id);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not book appointment");
    		e.printStackTrace();
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
    		statement = con.prepareStatement("INSERT INTO appointments (volunteer, patient, date_time, status) values (?,?,?,?)");
    		statement.setInt(1, a.getVolunteerID());
    		statement.setInt(2, a.getPatientID());
    		statement.setString(3, a.getDate() + " " + a.getTime());
    		statement.setString(4, "Awaiting Approval");
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not book appointment");
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
    		statement = con.prepareStatement("UPDATE appointments SET status='Approved' WHERE appointment_ID=?");
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
    
    public void declineAppointment(int id){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET status='Declined' WHERE appointment_ID=?");
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
    
    public List<Appointment> getAllCompletedAppointmentsForVolunteer(int volunteerId){
    	List<Appointment> appointments = new ArrayList<Appointment>();
    	Appointment apointment = null;
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments"
    				+ " WHERE volunteer=? AND completed=1 ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		
    		ResultSet rs = statement.executeQuery();
       		
       		while(rs.next()){
       			apointment = new Appointment();
       			apointment = createFromSearch(rs);
       			if (apointment != null)
       				appointments.add(apointment);
        	}
       		
       		//close result set
       		if (rs != null)
       			rs.close();
       		
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all completed appointments for volunteer #" + volunteerId);
    		e.printStackTrace();
    		
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    	
    	return appointments;
    }
    
    public List<Appointment> getAllUpcomingAppointmentsForVolunteer(int volunteerId){
    	List<Appointment> appointments = new ArrayList<Appointment>();
    	Appointment apointment = null;
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin FROM appointments"
    				+ " WHERE volunteer=? AND date_time>=CURDATE() AND completed=0 ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		
    		ResultSet rs = statement.executeQuery();
       		
       		while(rs.next()){
       			apointment = new Appointment();
       			apointment = createFromSearch(rs);
       			if (apointment != null)
       				appointments.add(apointment);
        	}
       		
       		//close result set
       		if (rs != null)
       			rs.close();
       		
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all upcoming appointments for volunteer #" + volunteerId);
    		e.printStackTrace();
    		
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    	
    	return appointments;
    }
}