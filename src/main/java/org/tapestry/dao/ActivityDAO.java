package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Activity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * Defines DAO operations for the Activity model.
 * 
 * @author lxie 
*/

public interface ActivityDAO {
	
	public List<Activity> getAllActivitiesForVolunteer(int volunteer);
	public List<Activity> getAllActivitiesForAdmin();
	public List<Activity> getAllActivitiesForLocalAdmin(int id);
	
	public void logActivity(String description, int volunteer);
	public void logActivity(String description, int volunteer, int patient);
	public void logActivity(Activity activity);
	public void updateActivity( Activity activity);	
	public List<Activity> getDetailedLog(int patientId, int appointmentId);
	public int countEntries();
	public List<Activity> getPage(int start, int n);
	public Activity getActivityLogById(int activityId);
	public List<UserLog> getUserLogsPage(int start, int n);
	public void addUserLog(String description, User user);
	public List<UserLog> getUserLogsByPartialName(String partialName);
	public void deleteActivityById(int id);
}
