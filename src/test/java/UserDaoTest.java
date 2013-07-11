package org.tapestry.tests;

import org.tapestry.dao.UserDao;
import org.tapestry.objects.User;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
* This class tests the UserDao to see if an object can be created,
* edited, saved, and retrieved.
*/
public class UserDaoTest{

	private final String DB = "jdbc:mysql://localhost";
	private final String UN = "root";
	private final String PW = "root";

	@Test
	public void testGetById(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		User u = dao.getUserById(1);
		assertNotNull("User is null", u);
		assertEquals("User name is not correct", "Adam Gignac", u.getName());
		System.out.println("User with ID 1 is: " + u.getName());
	}

	@Test
	public void testGetByUsername(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		User u = dao.getUserByUsername("admin");
		assertNotNull("User is null", u);
		assertEquals("User name is not correct", "Adam Gignac", u.getName());
		System.out.println("User with username admin is: " + u.getName());
	}

	@Test
	public void testGetAllUsers(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		ArrayList<User> users = dao.getAllUsers();
		assertNotNull("No users returned", users);
		System.out.println("Users:");
		for (User u : users){
			System.out.println("| " + u.getUserID() + " | "  + u.getName() + " | " + u.getUsername() + " | " + u.getEmail() + " | " + u.getRole() + " | " + u.isEnabled() + " | " +  u.getPassword() +" |");
		}
	}

	@Test
	public void testGetByRole(){
		UserDao dao = new UserDao(DB, UN, PW);
		assertNotNull("DAO is null", dao);
		ArrayList<User> volunteers = dao.getAllUsersWithRole("ROLE_USER");
		assertNotNull("No users returned", volunteers);
		System.out.println("Volunteers:");
		for (User u : volunteers){
			System.out.println(u.getName());
		}
	}

}
