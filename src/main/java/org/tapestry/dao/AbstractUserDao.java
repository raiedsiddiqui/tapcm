package org.tapestry.dao;

import org.tapestry.objects.User;
import java.sql.SQLException;
import java.util.ArrayList;

public interface AbstractUserDao {

	public User getUserById(int id);
	public User getUserByUsername(String username);
	public void createUser(User u);
	public void removeUserWithId(int id);
	public ArrayList<User> getAllUsers();

}
