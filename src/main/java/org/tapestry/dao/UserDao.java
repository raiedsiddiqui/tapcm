package org.tapestry.dao;

import org.tapestry.objects.User;
import org.tapestry.dao.AbstractUserDao;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao implements AbstractUserDao {

	private PreparedStatement statement;
	private Connection con;

	private final String DEFAULT_PASSWORD = "changeme";

	public UserDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			System.out.println(e.toString());
		}
	}

	private User createFromSearch(ResultSet result){
		User u = new User();
		try{
			u.setName(result.getString("name"));
			u.setUserID(Integer.parseInt(result.getString("user_ID")));
			u.setRole(result.getString("role"));
			u.setUsername(result.getString("username"));
			u.setEmail(result.getString("email"));
			u.setPassword(result.getString("password"));
			if (result.getString("enabled").equals("1"))
				u.setEnabled(true);
			else
				u.setEnabled(false);
		} catch (SQLException e) {
			System.out.println("Error: Failed to create User object");
			System.out.println(e.toString());
		}
		return u;
	}

	public User getUserById(int id){
		try{
			statement = con.prepareStatement("SELECT * FROM survey_app.users WHERE user_ID=?");
			statement.setString(1, "" + id);
			ResultSet result = statement.executeQuery();
			result.first();
			return createFromSearch(result);
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve user");
			System.out.println(e.toString());
			return null;
		}
	}

	public User getUserByUsername(String username){
		try{
			statement = con.prepareStatement("SELECT * FROM survey_app.users WHERE username=?");
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			result.first();
			return createFromSearch(result);
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve user");
			System.out.println(e.toString());
			return null;
		}
	}

	public void createUser(User u){
		try{
			statement = con.prepareStatement("INSERT INTO survey_app.users (username, name, password, role, email, enabled) VALUES (?, ?, ?, ?, ?, 1)");
			statement.setString(1, u.getUsername());
			statement.setString(2, u.getName());
			statement.setString(3, DEFAULT_PASSWORD);
			statement.setString(4, u.getRole());
			statement.setString(5, u.getEmail());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not create user");
			System.out.println(e.toString());
		}
	}

	public void removeUserWithId(int id){
		try{
			statement = con.prepareStatement("DELETE FROM survey_app.users WHERE user_ID=?");
			statement.setString(1, "" + id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not remove user");
			System.out.println(e.toString());
		}
	}

	public ArrayList<User> getAllUsers(){
		try{
			statement = con.prepareStatement("SELECT * FROM survey_app.users");
			ResultSet result = statement.executeQuery();
			ArrayList<User> allUsers = new ArrayList<User>();
			while(result.next()){
				User u = createFromSearch(result);
				allUsers.add(u);
			}
			return allUsers;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve users");
			System.out.println(e.toString());
			return null;
		}
	}

	public ArrayList<User> getAllUsersWithRole(String role){
		try{
			statement = con.prepareStatement("SELECT * FROM survey_app.users WHERE role=?");
			statement.setString(1, role);
			ResultSet result = statement.executeQuery();
			ArrayList<User> foundUsers = new ArrayList<User>();
			while(result.next()){
				User u = createFromSearch(result);
				foundUsers.add(u);
			}
			return foundUsers;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve users");
			System.out.println(e.toString());
			return null;
		}
	}
}
