package org.tapestry.dao;

import java.util.ArrayList;
import java.util.List;

import org.tapestry.objects.User;

/**
 * Defines DAO operations for the User model.
 * 
 * @author lxie 
*/
public interface UserDAO {

	public int countActiveUsers();
	public int countAdministrators();
	public int countAllUsers();
	public User getUserByID(int id);
	public User getUserByUsername(String username);
	public boolean createUser(User u);
	public void modifyUser(User u);
	public void removeUserWithID(int id);
	public void disableUserWithID(int id);
	public void enableUserWithID(int id);
	public List<User> getAllUsers();
	public List<User> getUsersByPartialName(String partialName);
	public List<User> getAllUsersWithRole(String role);
	public List<User> getAllActiveUsersWithRole(String role);
	public boolean userHasPassword(int id, String pwd);
	public void setPasswordForUser(int id, String pwd);
	public List<Integer> getVolunteerCoordinatorByOrganizationId(int id);
}
