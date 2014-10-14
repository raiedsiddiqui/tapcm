package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Activity;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;
/**
 * service for Model Activity
 * @author lxie *
 */
@Service
public interface ActivityManager {
	@Transactional
	public List<Activity> getActivitiesForVolunteer(int volunteer);
	@Transactional
	public List<Activity> getActivitiesForLocalAdmin(int id);
	@Transactional
	public List<Activity> getActivitiesForAdmin();
	@Transactional
	public void logActivity(String description, int volunteer);
	@Transactional
	public void logActivity(String description, int volunteer, int patient);
	@Transactional
	public void logActivity(Activity activity);
	@Transactional
	public void updateActivity(Activity activity);	
	@Transactional
	public List<Activity> getActivities(int patientId, int appointmentId);
	@Transactional
	public void deleteActivity(int id);	
	@Transactional
	public List<Activity> getPage(int start, int n);
	@Transactional
	public Activity getActivity(int activityId);
	@Transactional
	public int count();
	@Transactional
	public List<UserLog> getUserLogs(int start, int n);
	@Transactional
	public void addUserLog(String description, User user);
	@Transactional
	public List<UserLog> getUserLogsByPartialName(String partialName);
	

}
