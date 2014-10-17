package org.tapestry.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.User;
import org.tapestry.objects.UserLog;

/**
 * service for Model User, Activity
 * @author lxie *
 */
public interface UserManager {
	@Transactional
	public int countActiveUsers();
	@Transactional
	public int countAdministrators();
	@Transactional
	public int countAllUsers();
	@Transactional
	public User getUserByID(int id);
	@Transactional
	public User getUserByUsername(String username);
	@Transactional
	public boolean createUser(User u);
	@Transactional
	public void modifyUser(User u);
	@Transactional
	public void removeUserWithID(int id);
	@Transactional
	public void disableUserWithID(int id);
	@Transactional
	public void enableUserWithID(int id);
	@Transactional
	public List<User> getAllUsers();
	@Transactional
	public List<User> getUsersByPartialName(String partialName);
	@Transactional
	public List<User> getAllUsersWithRole(String role);
	@Transactional
	public List<User> getAllActiveUsersWithRole(String role);
	@Transactional
	public boolean userHasPassword(int id, String pwd);
	@Transactional
	public void setPasswordForUser(int id, String pwd);
	@Transactional
	public List<Integer> getVolunteerCoordinatorByOrganizationId(int id);
	
	/**
	 * 
	 * @param start
	 * @param n
	 * @return a list of userLogs
	 */
	@Transactional
	public List<UserLog> getUserLogs(int start, int n);
	/**
	 * log user activity with description
	 * @param description
	 * @param user
	 */
	@Transactional
	public void addUserLog(String description, User user);
	/**
	 * 
	 * @param partialName
	 * @return a list of userLogs 
	 */
	@Transactional
	public List<UserLog> getUserLogsByPartialName(String partialName);
	/**
	 * 
	 * @return number of user logs
	 */
	@Transactional
	public int count();

}
