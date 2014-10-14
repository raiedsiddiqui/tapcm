package org.tapestry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tapestry.dao.ActivityDAO;
import org.tapestry.objects.Activity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * Implementation for service ActivityManager
 * @author lxie *
 */
@Service
public class ActivityManagerImpl implements ActivityManager {
	@Autowired
	private ActivityDAO activityDAO;

	@Override
	public List<Activity> getActivitiesForVolunteer(int volunteer) {		
		return activityDAO.getAllActivitiesForVolunteer(volunteer);
	}

	@Override
	public List<Activity> getActivitiesForLocalAdmin(int id) {		
		return activityDAO.getAllActivitiesForLocalAdmin(id);
	}

	@Override
	public List<Activity> getActivitiesForAdmin() {
		return activityDAO.getAllActivitiesForAdmin();
	}

	@Override
	public void logActivity(String description, int volunteer) {
		activityDAO.logActivity(description, volunteer);

	}

	@Override
	public void logActivity(String description, int volunteer, int patient) {
		activityDAO.logActivity(description, volunteer, patient);

	}

	@Override
	public void logActivity(Activity activity) {
		activityDAO.logActivity(activity);

	}

	@Override
	public void updateActivity(Activity activity) {
		activityDAO.updateActivity(activity);

	}

	@Override
	public List<Activity> getActivities(int patientId, int appointmentId) {
		return activityDAO.getDetailedLog(patientId, appointmentId);
	}

	@Override
	public void deleteActivity(int id) {
		activityDAO.deleteActivityById(id);

	}

	@Override
	public List<Activity> getPage(int start, int n) {		
		return activityDAO.getPage(n, n);
	}

	@Override
	public Activity getActivity(int activityId) {		
		return activityDAO.getActivityLogById(activityId);
	}

	@Override
	public int count() {		
		return activityDAO.countEntries();
	}

	@Override
	public List<UserLog> getUserLogs(int start, int n) {		
		return activityDAO.getUserLogsPage(start, n);
	}

	@Override
	public void addUserLog(String description, User user) {		
		activityDAO.addUserLog(description, user);		
	}

	@Override
	public List<UserLog> getUserLogsByPartialName(String partialName) {		
		return activityDAO.getUserLogsByPartialName(partialName);
	}

}
