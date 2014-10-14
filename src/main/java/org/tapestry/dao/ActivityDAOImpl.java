package org.tapestry.dao;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.tapestry.controller.Utils;
import org.tapestry.objects.Activity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * An implementation of the ActivityDAO interface.
 * 
 * lxie
 */
@Repository
public class ActivityDAOImpl extends JdbcDaoSupport implements ActivityDAO {
	@Autowired
	public ActivityDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }
 
	@Override
	public List<Activity> getAllActivitiesForVolunteer(int volunteer) {			
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime, "
				+ "activities.organization, activities.event_ID, "
				+ "volunteers.firstname, volunteers.lastname FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " WHERE volunteer=?";
		
		List<Activity> activities = getJdbcTemplate().query(sql, new Object[]{volunteer}, new ActivityMapper());	
		
		return activities;
	}

	@Override
	public List<Activity> getAllActivitiesForAdmin() {		
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime,"
				+ " activities.organization, activities.event_ID,"
				+ " volunteers.firstname, volunteers.lastname  FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " ORDER BY event_timestamp";
		
		List<Activity> activities = getJdbcTemplate().query(sql, new ActivityMapper());		
		
		return activities;
	}

	@Override
	public List<Activity> getAllActivitiesForLocalAdmin(int id) {		
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime, "
				+ "activities.organization, activities.event_ID, "
				+ "volunteers.firstname, volunteers.lastname  FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " WHERE organization = ? ORDER BY event_timestamp";
		
		List<Activity> activities = getJdbcTemplate().query(sql, new Object[]{id}, new ActivityMapper());	
		
		return activities;
	}
	
	@Override
	public List<Activity> getDetailedLog(int patientId, int appointmentId){
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, "
				+ "TIME(activities.start_Time) AS sTime, TIME(activities.end_Time) AS eTime, "
				+ "activities.organization, activities.event_ID, "
				+ "volunteers.firstname, volunteers.lastname  FROM activities "
				+ "INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " WHERE patient = ? AND appointment = ? ORDER BY event_timestamp";
		
		List<Activity> activities = getJdbcTemplate().query(sql, new Object[]{patientId, appointmentId}, new ActivityMapper());	
		
		return activities;
	}

	@Override
	public void logActivity(String description, int volunteer) {
		//volunteer add activity
		String sql = "INSERT INTO activities (description,volunteer) VALUES (?, ?)";
		getJdbcTemplate().update(sql,description, volunteer);		
	}

	@Override
	public void logActivity(String description, int volunteer, int patient) {
		// volunteer add activity 
		String sql = "INSERT INTO activities (description,volunteer,patient) VALUES (?, ?, ?)";
		getJdbcTemplate().update(sql,description, volunteer, patient);		
	}

	@Override
	public void logActivity(Activity activity) {		
		String sql = "INSERT INTO activities (description,volunteer, event_timestamp,start_Time,end_Time, "
				+ " organization) VALUES (?, ?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, activity.getDescription(), activity.getVolunteer(), activity.getDate(), 
				activity.getStartTime(), activity.getEndTime(), activity.getOrganizationId());		
	}

	@Override
	public void updateActivity(Activity activity) {
	
		String sql = "UPDATE activities SET event_timestamp=?,description=?, start_Time=?, end_Time=? WHERE event_ID=?";
		getJdbcTemplate().update(sql, activity.getDate(), activity.getDescription(),activity.getStartTime(), 
				activity.getEndTime(), activity.getActivityId());	
	}

	@Override
	public int countEntries() {
		String sql = "SELECT COUNT(log_ID) AS c FROM user_logs";
		
		return getJdbcTemplate().queryForInt(sql);				
	}

	 /**
     * Returns a list of n items starting at start
     * @param start The position in the log to start at
     * @param n The number of items to return
     */
	@Override
	public List<Activity> getPage(int start, int n) {
		//Returns a list of n items starting at start
		String sql = "SELECT DATE(activities.event_timestamp) AS date, activities.description, activities.organization,"
				+ " activities.event_ID, volunteers.firstname, volunteers.lastname ,"
				+ " FROM activities INNER JOIN volunteers ON activities.volunteer=volunteers.volunteer_ID"
				+ " ORDER BY event_timestamp DESC LIMIT ?,?";
		
		List<Activity> activities = getJdbcTemplate().query(sql, new Object[]{start,n}, new ActivityMapper());		

		return activities;
	}

	@Override
	public Activity getActivityLogById(int activityId) {
		String sql = "SELECT DATE(event_timestamp) AS date, description, event_ID, "
				+ " TIME(start_Time) AS sTime, TIME(end_Time) AS eTime FROM activities WHERE event_ID = ?";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{activityId}, new DetailedActivityMapper());		
	}

	@Override
	public List<UserLog> getUserLogsPage(int start, int n) {		
		String sql = "SELECT * FROM user_logs ORDER BY event_timestamp DESC LIMIT ?,?";		
		List<UserLog> logs = getJdbcTemplate().query(sql, new Object[]{start,n}, new UserLogMapper());
		
		return logs;
	}

	@Override
	public void addUserLog(String description, User user) {		
		String sql = "INSERT INTO user_logs (description,user,user_name) VALUES (?, ?, ?)";
		getJdbcTemplate().update(sql,description, user.getUserID(), user.getName());
	}

	@Override
	public List<UserLog> getUserLogsByPartialName(String partialName) {		
		String sql = "SELECT * FROM user_logs WHERE UPPER(user_name) LIKE UPPER('%" + partialName + "%')";
		List<UserLog> logs = getJdbcTemplate().query(sql, new UserLogMapper());

		return logs;
	}

	@Override
	public void deleteActivityById(int id) {		
		String sql = "DELETE FROM activities WHERE event_ID=?";
	    getJdbcTemplate().update(sql, id);

	}
	
	//RowMapper
	class ActivityMapper implements RowMapper<Activity> {
		public Activity mapRow(ResultSet rs, int rowNum) throws SQLException{
			Activity activity = new Activity();
			activity.setActivityId(rs.getInt("event_ID"));
			activity.setDate(rs.getString("date"));
			activity.setDescription(rs.getString("description"));			
			String sTime = rs.getString("sTime");
			sTime = sTime.substring(0, sTime.length() - 3);
			String eTime = rs.getString("eTime");
			eTime = eTime.substring(0, eTime.length() - 3);
			
			StringBuffer sb = new StringBuffer();
			sb.append(sTime);
			sb.append("--");
			sb.append(eTime);
			activity.setTime(sb.toString());
			
			if (!Utils.isNullOrEmpty(rs.getString("firstname")) && !Utils.isNullOrEmpty(rs.getString("lastname")))
			{
				sb = new StringBuffer();
				sb.append(rs.getString("firstname"));
				sb.append(" ");
				sb.append(rs.getString("lastname"));
				
				activity.setVolunteerName(sb.toString());	
			}		
			
			return activity;			
		}
	}
	
	class DetailedActivityMapper implements RowMapper<Activity> {
		public Activity mapRow(ResultSet rs, int rowNum) throws SQLException{
			Activity activity = new Activity();
			activity.setActivityId(rs.getInt("event_ID"));
			activity.setDate(rs.getString("date"));
			activity.setDescription(rs.getString("description"));
			String time = rs.getString("stime");
			activity.setStartTime(time.substring(0, time.length() - 3));			
			time = rs.getString("eTime");
			activity.setEndTime(time.substring(0, time.length() - 3));
			
			return activity;			
		}
	}
		
	class UserLogMapper implements RowMapper<UserLog> {
		public UserLog mapRow(ResultSet rs, int rowNum) throws SQLException{
			UserLog log = new UserLog();
			
			log.setDate(rs.getString("event_timestamp"));
			log.setDescription(rs.getString("description"));
			log.setUserId(rs.getInt("user"));
			log.setUserName(rs.getString("user_name"));
			
			return log;
		}
	}

}
