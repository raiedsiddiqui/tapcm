package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.tapestry.controller.Utils;
import org.tapestry.objects.Patient;

/**
 * An implementation of the PatientDAO interface.
 * 
 * lxie
 */
public class PatientDAOImpl implements PatientDAO {
	private JdbcTemplate jdbcTemplate;
	
	public PatientDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
	@Override
	public Patient getPatientByID(int id) {
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID WHERE p.patient_ID=?";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{id}, new PatientMapper());
	}

	@Override
	public Patient getNewestPatient() {
		// SELECT * FROM patients ORDER BY patient_ID DESC LIMIT 1
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID ORDER BY p.patient_ID DESC LIMIT 1";
		
		return jdbcTemplate.queryForObject(sql, new PatientMapper());
	}

	@Override
	public List<Patient> getAllPatients() {
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID ";
		
		return jdbcTemplate.query(sql, new PatientMapper());
	}

	@Override
	public List<Patient> getPatientsForVolunteer(int volunteerId) {
		// "SELECT * FROM patients WHERE volunteer=? OR volunteer2=?"
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID WHERE p.volunteer=? OR p.volunteer2=? ";
		
		return jdbcTemplate.query(sql, new Object[]{volunteerId, volunteerId}, new PatientMapper());
	}

	@Override
	public List<Patient> getPatientssByPartialName(String partialName) {	
		String sql = "SELECT p.*, v1.firstname AS v1_firstname, v1.lastname AS v1_lastname, "
				+ "v2.firstname AS v2_firstname, v2.lastname AS v2_lastname FROM patients "
				+ "AS p INNER JOIN volunteers AS v1 ON p.volunteer=v1.volunteer_ID INNER JOIN "
				+ "volunteers AS v2 ON p.volunteer2=v2.volunteer_ID WHERE UPPER(p.firstname) "
				+ "LIKE UPPER('%" + partialName + "%') OR UPPER(p.lastname) LIKE UPPER('%" + partialName + "%') ";
		
		return jdbcTemplate.query(sql, new Object[]{partialName}, new PatientMapper());
	}

	@Override
	public void createPatient(Patient p) {
		String sql = "INSERT INTO patients (firstname, lastname, preferredname, volunteer,"
				+ " gender, notes, volunteer2, alerts, myoscar_verified, clinic, username) VALUES (?, ?, ?, ?,"
				+ " ?, ?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql, p.getFirstName(),  p.getLastName(), p.getPreferredName(), p.getVolunteer(), p.getGender(),
				p.getNotes(), p.getPartner(), p.getAlerts(), p.getMyoscarVerified(), p.getClinic(), "tapestry_patient");
	}

	@Override
	public void updatePatient(Patient p) {
		String sql = "UPDATE patients SET firstname=?, lastname=?, preferredname=?, volunteer=?, "
				+ "gender=?, notes=?, clinic=?, myoscar_verified=?, alerts=?, volunteer2=? WHERE patient_ID=?";
		jdbcTemplate.update(sql, p.getFirstName(),  p.getLastName(), p.getPreferredName(), p.getVolunteer(), p.getGender(),
				p.getNotes(), p.getClinic(), p.getMyoscarVerified(), p.getAlerts(), p.getPartner(), p.getPatientID());

	}

	@Override
	public void deletePatientWithId(int id) {
		String sql = "DELETE FROM patients WHERE patient_ID=?";
		jdbcTemplate.update(sql, id);
	}
	
	class PatientMapper implements RowMapper<Patient> {
		public Patient mapRow(ResultSet rs, int rowNum) throws SQLException{
			Patient patient = new Patient();			
			
			patient.setPatientID(rs.getInt("patient_ID"));
			patient.setFirstName(rs.getString("firstname"));
			patient.setLastName(rs.getString("lastname"));
			patient.setPreferredName(rs.getString("preferredname"));
			//set gender
			String gender = rs.getString("gender");
			if ("M".equals(gender))
				patient.setGender("Male");
			else if ("F".equals(gender))
				patient.setGender("Female");
			else
				patient.setGender("Other");
			//set clinic
			String clinicCode = rs.getString("clinic");
			patient.setClinic(clinicCode);
			patient.setClinicName(Utils.getClinicName(clinicCode));			
			patient.setVolunteer(rs.getInt("volunteer"));
			patient.setNotes(rs.getString("notes"));
			patient.setAlerts(rs.getString("alerts"));	
			
			String myOscarVerfied = rs.getString("myoscar_verified");
			patient.setMyoscarVerified(myOscarVerfied);    
			//set myoscar authentication for display in client's detail page
			if ("1".equals(myOscarVerfied))
				patient.setMyOscarAuthentication("Authenticated");
			else
				patient.setMyOscarAuthentication("Not Authenticated");
			patient.setPartner(rs.getInt("volunteer2"));
			//Set volunteer name and partner name
			StringBuffer sb = new StringBuffer();
			sb.append(rs.getString("v1_firstname"));
			sb.append(" ");
			sb.append(rs.getString("v1_lastname"));
			patient.setVolunteerName(sb.toString());
			sb = new StringBuffer();
			sb.append(rs.getString("v2_firstname"));
			sb.append(" ");
			sb.append(rs.getString("v2_lastname"));
			patient.setPartnerName(sb.toString());
			patient.setUserName(rs.getString("username"));//user name in MyOscar			
			
			return patient;
		}
	}

}
