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
import org.tapestry.objects.Appointment;

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
    
    /**
     * 
     * @return all appointments in a list
     */
    public ArrayList<Appointment> getAllAppointments(){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, "
    				+ "type FROM appointments ORDER BY date_time DESC");
    		
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		
       		while(result.next()){       			
       			Appointment a = createFromSearch(result);   
       			
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
    
    /**
     * 
     * @return all past appointments in a list
     */
    
    public List<Appointment> getAllPastAppointments(){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, "
    				+ "type FROM appointments WHERE date_time < CURDATE() ORDER BY date_time DESC");
    		
    		ResultSet result = statement.executeQuery();
       		List<Appointment> allAppointments = new ArrayList<Appointment>();
       		
       		while(result.next()){       			
       			Appointment a = createFromSearch(result);
       			
       			if (a != null)
       				allAppointments.add(a);     
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all past appointments");
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
     * all awaiting approved appointments 
     * @param patientId
     * @return
     */
    public ArrayList<Appointment> getAllPendingAppointments(){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, "
    				+ "type FROM appointments WHERE date_time>=CURDATE() AND completed=0 AND status='Awaiting Approval' "
    				+ "ORDER BY date_time ASC");
    		ResultSet result = statement.executeQuery();
       		ArrayList<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve all pending appointments " );
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
     * 
     * @param volunteer id
     * @return all appointments filtered by volunteer
     */
    	
    public ArrayList<Appointment> getAllAppointmentsForVolunteer(int volunteer){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative,"
    				+ " type FROM appointments WHERE volunteer=? AND date_time>=CURDATE() AND completed=0 ORDER BY date_time ASC");
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
    
    /**
     * get all approved and not completed appointments filtered by volunteer
     * @param volunteerId
     * @return
     */    
    public ArrayList<Appointment> getAllApprovedAppointmentsForVolunteer(int volunteerId){
    	try{    		
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments "
    				+ "WHERE (volunteer=? OR partner=?) AND date_time>=CURDATE() AND completed=0 AND status='Approved' ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		statement.setInt(2, volunteerId);
    		
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
    
    /**
     * all awaiting approve appointments filtered by volunteer
     * @param volunteerId
     * @return
     */
    public ArrayList<Appointment> getAllPendingAppointmentsForVolunteer(int volunteerId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, "
    				+ "type FROM appointments WHERE (volunteer=? OR partner=?) AND date_time>=CURDATE() AND completed=0 AND "
    				+ "status='Awaiting Approval' ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		statement.setInt(2, volunteerId);
    		
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
    
    /**
     * all declined appointments filtered by volunteer
     * @param volunteerId
     * @return
     */    
    public ArrayList<Appointment> getAllDeclinedAppointmentsForVolunteer(int volunteerId){
 
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, "
    				+ "hasNarrative, type FROM appointments WHERE (volunteer=? OR partner=?) AND date_time>=CURDATE() AND "
    				+ "completed=0 AND status='Declined' ORDER BY date_time ASC");
    		statement.setInt(1, volunteerId);
    		statement.setInt(2, volunteerId);
    		
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
    
    /**
     * all today's appointments filtered by volunteer
     * @param volunteer
     * @return
     */
    public ArrayList<Appointment> getAllAppointmentsForVolunteerForToday(int volunteer){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date,"
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, "
    				+ "hasNarrative, type FROM appointments WHERE volunteer=? AND DATE(date_time)=CURDATE() AND "
    				+ "completed=0 ORDER BY date_time ASC");
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
    
    /**
     * all approved appointments filtered by patient
     * @param patientId
     * @return
     */
    
    public ArrayList<Appointment> getAllApprovedAppointmentsForPatient(int patientId){
    	try{    		
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner,"
    				+ " hasNarrative, type FROM appointments WHERE patient=? AND date_time>=CURDATE() AND"
    				+ "  status='Approved' ORDER BY date_time ASC");
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
    
    /**
     * all approved appointments filtered by patient, volunteer
     * @param patientId
     * @return
     */
    
    public List<Appointment> getAllApprovedAppointmentsForPatient(int patientId, int volunteerId){
    	try{    		
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments WHERE patient=? AND (volunteer=? OR partner=?) AND date_time>=CURDATE() "
    				+ "AND completed=0 AND status='Approved' ORDER BY date_time ASC");
    		statement.setInt(1, patientId);
    		statement.setInt(2, volunteerId);
    		statement.setInt(3, volunteerId);
    		
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
    
    /**
     * all awaiting approved appointments filtered by volunteer
     * @param patientId
     * @return
     */
    public ArrayList<Appointment> getAllPendingAppointmentsForPatient(int patientId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative"
    				+ ", type FROM appointments WHERE patient=? AND date_time>=CURDATE() AND completed=0 AND "
    				+ "status='Awaiting Approval' ORDER BY date_time ASC");
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
    
    /**
     * all awaiting approved appointments filtered by volunteer, patient
     * @param patientId
     * @return
     */
    public List<Appointment> getAllPendingAppointmentsForPatient(int patientId, int volunteerId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments WHERE patient=? AND (volunteer=? OR partner=?) AND date_time>=CURDATE() "
    				+ "AND completed=0 AND status='Awaiting Approval' ORDER BY date_time ASC");
    		statement.setInt(1, patientId);
    		statement.setInt(2, volunteerId);
    		statement.setInt(3, volunteerId);
    		
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
    
    
    /**
     * all declined appointments filtered by patient
     * @param patientId
     * @return
     */
    public ArrayList<Appointment> getAllDeclinedAppointmentsForPatient(int patientId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments WHERE patient=? AND date_time>=CURDATE() AND completed=0 AND status='Declined'"
    				+ " ORDER BY date_time ASC");
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
    
    /**
     * all declined appointments filtered by patient, volunteer
     * @param patientId
     * @return
     */
    public List<Appointment> getAllDeclinedAppointmentsForPatient(int patientId, int volunteerId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative,type "
    				+ "FROM appointments WHERE patient=? AND (volunteer=? OR partner=?) AND date_time>=CURDATE() AND completed=0 "
    				+ "AND status='Declined' ORDER BY date_time ASC");
    		statement.setInt(1, patientId);
    		statement.setInt(2, volunteerId);
    		statement.setInt(3, volunteerId);
    		
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
    
    /**
     * recent approved, not completed appointments filtered by patient
     * @param patientId
     * @return
     */
    public Appointment getAppointmentByMostRecentIncomplete(int patientId){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments"
    				+ " WHERE patient=? AND completed=0 AND status='Approved' ORDER BY date_time ASC LIMIT 1");
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
    
    /**
     * all approved and not completed appointments filtered by patient
     * @param patientId
     * @return
     */
    public List<Appointment> getAllUpcommingAppointmentForPatient(int patientId){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments"
    				+ " WHERE patient=? AND date_time>=CURDATE() AND completed=0 AND status='Approved' ORDER BY date_time ASC ");

    		statement.setInt(1, patientId);
    		ResultSet result = statement.executeQuery();

    		List<Appointment> allAppointments = new ArrayList<Appointment>();
       		while(result.next()){       			
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
    		
       		return allAppointments;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve any upcoming appointment for patient ID:" + patientId);
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
    
    /**
     * get all completed appointment filtered by patient
     * @param patientId
     * @return
     */
    public List<Appointment> getAllCompletedAppointmentsForPatient(int patientId){
    	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments"
    				+ " WHERE patient=? AND completed=1 ORDER BY date_time ASC ");
    		statement.setInt(1, patientId);
    		ResultSet result = statement.executeQuery();
    		
    		List<Appointment> allAppointments = new ArrayList<Appointment>();
    		
       		while(result.next()){
       			Appointment a = createFromSearch(result);
       			if (a != null)
       				allAppointments.add(a);
        	}
    		
       		return allAppointments;
       		
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve complete appointment for patient ID:" + patientId);
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
    
    public Appointment getAppointmentById(int appointmentId){
       	try{
    		statement = con.prepareStatement("SELECT appointment_ID, volunteer, patient, DATE(date_time) as appt_date, "
    				+ "TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative, type"
    				+ " FROM appointments WHERE appointment_ID=?");
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
    
    public void completeAppointment(int id, String comments, boolean contactedAdmin) {
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET comments=?, completed=1, contactedAdmin=? WHERE appointment_ID=? ");
    		statement.setString(1, comments);
    		statement.setBoolean(2, contactedAdmin);
    		statement.setInt(3, id);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not set complete status for an appointment");
    		e.printStackTrace();
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    public void completeAppointment(int appointmentId, String comments){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET comments=?, completed=1 WHERE appointment_ID=? ");
    		statement.setString(1, comments);    		
    		statement.setInt(2, appointmentId);
    		
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not set complete status for an appointment");
    		e.printStackTrace();
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    
    public boolean createAppointment(Appointment a){
    	boolean success = false;
    	try{
    		statement = con.prepareStatement("INSERT INTO appointments (volunteer, patient, date_time, partner, status, type)"
    				+ " values (?,?,?,?,?, ?)");
    		statement.setInt(1, a.getVolunteerID());
    		statement.setInt(2, a.getPatientID());
    		statement.setString(3, a.getDate() + " " + a.getTime());
    		statement.setInt(4, a.getPartnerId());	
    		statement.setString(5, "Awaiting Approval");
    		statement.setInt(6, a.getType());
    		
    		statement.execute();
    		
    		success = true;
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
    	
    	return success;
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
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative,"
    				+ " type FROM appointments"
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
    				+ " TIME(date_time) as appt_time, comments, status, completed, contactedAdmin, partner, hasNarrative,"
    				+ " type FROM appointments"
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
    
    public String getKeyObservationByAppointmentId(int id){
    	try{
    		statement = con.prepareStatement("SELECT key_observations FROM appointments"
    				+ " WHERE appointment_ID=? ORDER BY date_time ASC");
    		statement.setInt(1, id);
    		
    		ResultSet rs = statement.executeQuery();
    		String keyObservations = null;
    		
    		while(rs.next()){
    			keyObservations = rs.getString("key_observations");
        	}
       		
       		//close result set
       		if (rs != null)
       			rs.close();
       		
       		return keyObservations;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve key_observation for appointment# is  " + id);
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
    
    public boolean addAlertsAndKeyObservations(int id, String alerts, String keyObservations){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET alerts=?, key_observations=? WHERE appointment_ID=? ");
    		statement.setString(1, alerts);
    		statement.setString(2, keyObservations);
    		statement.setInt(3, id);
    		statement.execute();
    		
    		return true;
    	} catch (SQLException e){
    		System.out.println("Error: Could not add alerts and key observations");
    		e.printStackTrace();
    		return false;
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    	
    }
    
    public String getPlanByAppointmentId(int id){
    	try{
    		statement = con.prepareStatement("SELECT plan FROM appointments"
    				+ " WHERE appointment_ID=? ORDER BY date_time ASC");
    		statement.setInt(1, id);
    		
    		ResultSet rs = statement.executeQuery();
    		String plan = null;
    		
    		while(rs.next()){
    			plan = rs.getString("plan");
        	}
       		//close result set
       		if (rs != null)
       			rs.close();
       		
       		return plan;
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve plan for appointment# is  " + id);
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
    
    //addPlans(iAppointmentId, plans)
    public boolean addPlans(int id, String plan){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET plan=? WHERE appointment_ID=? ");
    		statement.setString(1, plan);    		
    		statement.setInt(2, id);
    		statement.execute();
    		
    		return true;
    	} catch (SQLException e){
    		System.out.println("Error: Could not add plan");
    		e.printStackTrace();
    		return false;
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    	
    }
    
    public void completeNarrative(int id){
    	try{
    		statement = con.prepareStatement("UPDATE appointments SET hasNarrative=1 WHERE appointment_ID=? ");
    		statement.setInt(1, id);
    		
    		statement.execute();
    		
    	} catch (SQLException e){
    		System.out.println("Error: Could not add hasNarrative");
    		e.printStackTrace();
    		
    	} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
    }
    
    private Appointment createFromSearch(ResultSet result){
    	Appointment a = new Appointment();
    	try{
    		a.setAppointmentID(result.getInt("appointment_ID"));
    		
    		//set volunteer and partner names and ids
    		int vId = result.getInt("volunteer");
    		a.setVolunteerID(vId);  
    		String vName = getVolunteerName(vId);
    		if (!Utils.isNullOrEmpty(vName))
    			a.setVolunteer(vName);
    		vId = result.getInt("partner");
    		a.setPartnerId(vId);
    		vName = getVolunteerName(vId);
    		if (!Utils.isNullOrEmpty(vName))
    			a.setPartner(vName);
    		
    		int id = result.getInt("patient");
    		a.setPatientID(id);
    		statement = con.prepareStatement("SELECT firstname, lastname, preferredName FROM patients WHERE patient_ID=?");
    		statement.setInt(1, id);
    		ResultSet r = statement.executeQuery();
    		if (r.first()){
    			String preferredName = r.getString("preferredName");
    			//add empty checking for preferedName
    			if(!Utils.isNullOrEmpty(preferredName)) {
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
    		a.setHasNarrative(result.getBoolean("hasNarrative"));
    		
    		int type = result.getInt("type");
    		a.setType(type);
    		
    		if (type == 0)
    			a.setStrType("First Visit");
    		else
    			a.setStrType("Follow up Visit");
    		
    		return a;
    	} catch (SQLException e){
    		System.out.println("Error: Could not create Appointment object");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    private String getVolunteerName(int id){
    	try{    		
    		statement = con.prepareStatement("SELECT firstname, lastname FROM volunteers WHERE volunteer_ID=?");
    		statement.setInt(1, id);
    		ResultSet r = statement.executeQuery();
    		if (r.first()){
    				    		
	    		StringBuffer sb = new StringBuffer();
	    		sb.append(r.getString("firstname"));
	    		sb.append(" ");
	    		sb.append(r.getString("lastname"));
	    			
	    		return sb.toString();
    			
    		} else {
    			return null; 
    		}
    		
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrive a volunteer, id is " + id);
    		e.printStackTrace();
    		return null;
    	}
    }
}