package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;

import org.tapestry.objects.SurveyResult;


public class SurveyResultDAOImpl extends JdbcDaoSupport implements SurveyResultDAO {
	
	public SurveyResultDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
	
	@Override
	public List<SurveyResult> getSurveysByPatientID(int patientId) {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=?"
				+ " ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getCompletedSurveysByPatientID(int patientId) {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND"
				+ " survey_results.completed = 1 ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getIncompleteSurveysByPatientID(int patientId) {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND"
				+ " survey_results.completed = 0 ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public SurveyResult getSurveyResultByID(int resultId) {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.result_ID=? "
				+ " ORDER BY survey_results.startDate ";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{resultId}, new SurveyResultMapper());
	}

	@Override
	public List<SurveyResult> getAllSurveyResults() {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int surveyId) {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.survey_ID=?"
				+ " ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{surveyId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public List<SurveyResult> getSurveyResultByPatientAndSurveyId(int patientId, int surveyId) {
		String sql = "SELECT survey_results.*, surveys.title, patients.firstname, patients.lastname FROM survey_results"
				+ " INNER JOIN surveys ON survey_results.survey_ID = surveys.survey_ID INNER JOIN patients"
				+ " ON survey_results.patient_ID=patients.patient_ID WHERE survey_results.patient_ID=? AND survey_results.survey_ID=?"
				+ " ORDER BY survey_results.startDate ";
		List<SurveyResult> results = getJdbcTemplate().query(sql, new Object[]{patientId,surveyId}, new SurveyResultMapper());
		
		return results;
	}

	@Override
	public String assignSurvey(final SurveyResult sr) {		
		final String sql = "INSERT INTO survey_results (patient_ID, survey_ID, data, startDate) values (?,?,?,?)";
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	getJdbcTemplate().update(
    	    new PreparedStatementCreator() {
    	        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
    	            PreparedStatement pst =
    	                con.prepareStatement(sql, new String[] {"id"});
    	            pst.setInt(1, sr.getPatientID());
    	            pst.setInt(2, sr.getSurveyID());
    	            pst.setBytes(3, sr.getResults());
    	            pst.setString(4, sr.getStartDate());
    	            
    	            return pst;
    	        }
    	    },
    	    keyHolder);
    	return String.valueOf((Long)keyHolder.getKey());
	}

	@Override
	public void deleteSurvey(int id) {
		String sql = "DELETE FROM survey_results WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public void markAsComplete(int id) {
		String sql = "UPDATE survey_results SET completed=1 WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public void updateSurveyResults(int id, byte[] data) {		
		String sql = "UPDATE survey_results SET data=?, editDate=now() WHERE result_ID=?";
		getJdbcTemplate().update(sql, data,id);
	}

	@Override
	public void updateStartDate(int id) {
		String sql = "UPDATE survey_results SET startDate=now() WHERE result_ID=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public int countCompletedSurveys(int patientId) {
		String sql = "SELECT COUNT(*) as c FROM survey_results WHERE (patient_ID=?) AND (completed=1)";
		return getJdbcTemplate().queryForInt(sql, new Object[]{patientId});
	}	
		
	class SurveyResultMapper implements RowMapper<SurveyResult> {
		public SurveyResult mapRow(ResultSet rs, int rowNum) throws SQLException{
			SurveyResult sr = new SurveyResult();
			
			sr.setResultID(rs.getInt("result_ID"));
			sr.setSurveyID(rs.getInt("survey_ID"));
			sr.setSurveyTitle(rs.getString("title"));
			sr.setPatientID(rs.getInt("patient_ID"));
			sr.setPatientName(rs.getString("firstname") + " " + rs.getString("lastname"));
			
			boolean completed = rs.getBoolean("completed");
   			sr.setCompleted(completed);
   			if (completed)
   				sr.setStrCompleted("COMPLETED");
   			else
   				sr.setStrCompleted("INCOMPLETED"); 
	     
   			sr.setStartDate(rs.getString("startDate"));            
   			sr.setEditDate(rs.getString("editDate"));
   			sr.setResults(rs.getBytes("data"));
	            
			return sr;
		}
	}

}
