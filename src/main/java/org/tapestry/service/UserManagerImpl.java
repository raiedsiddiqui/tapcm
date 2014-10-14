package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tapestry.dao.UserDAO;
import org.tapestry.objects.User;

/**
 * Implementation for service UserManager
 * @author lxie *
 */
@Service
public class UserManagerImpl implements UserManager {
	@Autowired
	private UserDAO userDao;
	
	@Override
	public int countActiveUsers() {
		return userDao.countActiveUsers();
	}

	@Override
	public int countAdministrators() {
		return userDao.countAdministrators();
	}

	@Override
	public int countAllUsers() {		
		return userDao.countAllUsers();
	}

	@Override
	public User getUserByID(int id) {		
		return userDao.getUserByID(id);
	}

	@Override
	public User getUserByUsername(String username) {		
		return userDao.getUserByUsername(username);
	}

	@Override
	public boolean createUser(User u) {		
		return userDao.createUser(u);
	}

	@Override
	public void modifyUser(User u) {
		userDao.modifyUser(u);
	}

	@Override
	public void removeUserWithID(int id) {
		userDao.removeUserWithID(id);

	}

	@Override
	public void disableUserWithID(int id) {
		userDao.disableUserWithID(id);

	}

	@Override
	public void enableUserWithID(int id) {
		userDao.enableUserWithID(id);

	}

	@Override
	public List<User> getAllUsers() {		
		return userDao.getAllUsers();
	}

	@Override
	public List<User> getUsersByPartialName(String partialName) {		
		return userDao.getUsersByPartialName(partialName);
	}

	@Override
	public List<User> getAllUsersWithRole(String role) {		
		return userDao.getAllUsersWithRole(role);
	}

	@Override
	public List<User> getAllActiveUsersWithRole(String role) {		
		return userDao.getAllActiveUsersWithRole(role);
	}

	@Override
	public boolean userHasPassword(int id, String pwd) {		
		return userDao.userHasPassword(id, pwd);
	}

	@Override
	public void setPasswordForUser(int id, String pwd) {
		userDao.setPasswordForUser(id, pwd);
	}

	@Override
	public List<Integer> getVolunteerCoordinatorByOrganizationId(int id) {		
		return userDao.getVolunteerCoordinatorByOrganizationId(id);
	}

}
