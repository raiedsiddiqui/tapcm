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
	/**
	 * 
	 * @param volunteer
	 * @return a list of Activities by selected volunteer
	 */
	public List<Activity> getAllActivitiesForVolunteer(int volunteer);
	/**
	 * 
	 * @return a list of all activities by Admin
	 */
	public List<Activity> getAllActivitiesForAdmin();
	/**
	 * 
	 * @param id organizationId
	 * @return a list of activities by central Admin 
	 */
	public List<Activity> getAllActivitiesForLocalAdmin(int id);	
	/**
	 * Log activity with description by volunteer
	 * @param description
	 * @param volunteer
	 */
	public void logActivity(String description, int volunteer);
	
	/**
	 * Log activity
	 * @param activity
	 */			
	public void logActivity(Activity activity);
	/**
	 * update activity
	 * @param activity
	 */
	public void updateActivity(Activity activity);	
	/**
	 * 
	 * @param patientId
	 * @param appointmentId
	 * @return a list of activities for patient and appointment
	 */
//	public List<Activity> getDetailedLog(int patientId, int appointmentId);
	/**
	 * 
	 * @param activityId
	 * @return an activity by id
	 */
	public Activity getActivityLogById(int activityId);
	
	/**
	 * delete activity by id
	 * @param id
	 */
	public void deleteActivityById(int id);	
	
	 /**
     * Returns a list of n items starting at start
     * @param start The position in the log to start at
     * @param n The number of items to return
     */
	public List<Activity> getPage(int start, int n);	
	
	/**
	 * 
	 * @return number of user logs
	 */
	public int countEntries();
	
	/**
	 * 
	 * @param organizationId
	 * @return number of user logs
	 */
	public int countEntriesByGroup(int organizationId);
	
	/**
	 * 
	 * @param start
	 * @param n
	 * @return 
	 */
	public List<UserLog> getUserLogsPage(int start, int n);
	/**
	 * 
	 * @param description
	 * @param user
	 */
	public void addUserLog(String description, User user);
	/**
	 * 
	 * @param partialName
	 * @return
	 */
	public List<UserLog> getUserLogsByPartialName(String partialName);
	
	/**
	 * 
	 * @param start
	 * @param n
	 * @param organizationId
	 * @return
	 */
	public List<UserLog> getUserLogsPageByGroup(int start, int n, int organizationId);
	
	/**
	 * 
	 * @param partialName
	 * @param organizationId
	 * @return
	 */
	public List<UserLog> getGroupedUserLogssByPartialName(String partialName, int organizationId);
	
	/**
	 * Save a copy of deleted activity
	 * @param activity
	 * @param deletedBy
	 * @param volunteer
	 */
	public void archivedActivity(Activity activity, String deletedBy, String volunteer);
}
