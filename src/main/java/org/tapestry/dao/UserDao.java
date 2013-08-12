package org.tapestry.dao;

import org.tapestry.objects.User;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
* UserDAO class
* Allows the application to interface with the 'survey_app.users' table.
*/
public class UserDao {

	private PreparedStatement statement;
	private Connection con;


	/**
	* Constructor
	* @param url The url of the database, prefaced with 'jdbc:' (most likely 'jdbc:mysql://localhost:3306/survey_app')
	* @param username The username for the database user
	* @param password The password for the database user
	*/
	public UserDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			e.printStackTrace();
		}
	}
	
	public int countActiveUsers(){
		try{
			statement = con.prepareStatement("SELECT COUNT(*) as c FROM users WHERE enabled=1");
			ResultSet result = statement.executeQuery();
			result.first();
			return result.getInt("c");
		} catch (SQLException e){
			System.out.println("Error: Could not count active users");
			e.printStackTrace();
			return 0;
		}
	}
	
	public int countAllUsers(){
		try{
			statement = con.prepareStatement("SELECT COUNT(*) as c FROM users");
			ResultSet result = statement.executeQuery();
			result.first();
			return result.getInt("c");
		} catch (SQLException e){
			System.out.println("Error: Could not count active users");
			e.printStackTrace();
			return 0;
		}
	}

	/**
	* Creates a User object from the result of executing an SQL query
	* @param result The ResultSet returned by calling PreparedStatement.executeQuery()
	* @return A User object representing the person
	*/
	private User createFromSearch(ResultSet result){
		User u = new User();
		try{
			u.setName(result.getString("name"));
			u.setUserID(result.getInt("user_ID"));
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
			e.printStackTrace();
		}
		return u;
	}

	/**
	* Selects a user based off the user ID
	* @param id The ID of the user to search for
	* @return A User object representing the person
	*/
	public User getUserByID(int id){
		try{
			statement = con.prepareStatement("SELECT * FROM users WHERE user_ID=?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			result.first();
			return createFromSearch(result);
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve user");
			e.printStackTrace();
			return null;
		}
	}

	/**
	* Selects a user based off the username
	* @param username The username to search for
	* @return A User object representing the person, or null if the request fails
	*/
	public User getUserByUsername(String username){
		try{
			statement = con.prepareStatement("SELECT * FROM users WHERE username=?");
			statement.setString(1, username);
			ResultSet result = statement.executeQuery();
			result.first();
			return createFromSearch(result);
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve user");
			e.printStackTrace();
			return null;
		}
	}

	/**
	* Stores a user in the database
	* @param u The User object to store
	*/
	public void createUser(User u){
		try{
			statement = con.prepareStatement("INSERT INTO users (username, name, password, role, email, enabled) VALUES (?, ?, ?, ?, ?, 1)");
			statement.setString(1, u.getUsername());
			statement.setString(2, u.getName());
			statement.setString(3, u.getPassword());
			statement.setString(4, u.getRole());
			statement.setString(5, u.getEmail());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not create user");
			e.printStackTrace();
			}
	}
	
	/**
	 * Modifies a user in the database
	 * @param u The User object containing the new data (the user_ID should match the object to update)
	 */
	public void modifyUser(User u){
		try{
			statement = con.prepareStatement("UPDATE users SET username=?, name=?, email=? WHERE user_ID=?");
			statement.setString(1, u.getUsername());
			statement.setString(2, u.getName());
			statement.setString(3, u.getEmail());
			statement.setInt(4, u.getUserID());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not update user");
			e.printStackTrace();
			}
	}

	/**
	* Removes a user from the database
	* @param id The ID of the user to remove
	*/
	public void removeUserWithID(int id){
		try{
			statement = con.prepareStatement("DELETE FROM users WHERE user_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not remove user");
			e.printStackTrace();
			}
	}
	
	/**
	 * Disables a user
	 * Useful for closing access to an account without losing data associated with it
	 * @param id The ID of the user to disable
	 */
	public void disableUserWithID(int id){
		try{
			statement = con.prepareStatement("UPDATE users SET enabled=0 WHERE user_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not disable user");
			e.printStackTrace();
		}
	}
	
	/**
	 * Enables a user
	 * Logically follows from above
	 * @param id The ID of the user to disable
	 */
	public void enableUserWithID(int id){
		try{
			statement = con.prepareStatement("UPDATE users SET enabled=1 WHERE user_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not enable user");
			e.printStackTrace();
		}
	}

	/**
	* Lists all the users in the database
	* @return An ArrayList of User objects, or null if the request fails
	*/
	public ArrayList<User> getAllUsers(){
		try{
			statement = con.prepareStatement("SELECT * FROM users");
			ResultSet result = statement.executeQuery();
			ArrayList<User> allUsers = new ArrayList<User>();
			while(result.next()){
				User u = createFromSearch(result);
				allUsers.add(u);
			}
			return allUsers;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve users");
			e.printStackTrace();
			return null;
		}
	}

	/**
	* Lists all the users in the database with the specified role
	* @param role The role to search for ("ROLE_USER" or "ROLE_ADMIN")
	* @return An ArrayList of User objects, or null if the request fails
	*/
	public ArrayList<User> getAllUsersWithRole(String role){
		try{
			statement = con.prepareStatement("SELECT * FROM users WHERE role=?");
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
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Checks if the specified user has the given password
	 * @param id The ID of the user to check
	 * @param pwd The password to check (plaintext)
	 */
	public boolean userHasPassword(int id, String pwd){
		ShaPasswordEncoder enc = new ShaPasswordEncoder();
		String hashedPassword = enc.encodePassword(pwd, null);
		try{
			statement = con.prepareStatement("SELECT password FROM users WHERE user_ID=?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			result.first();
			return hashedPassword.equals(result.getString("password"));
		} catch (SQLException e){
			System.out.println("Error: Could not compare passwords");
			e.printStackTrace();
			return false;
		}
	}
	
	public void setPasswordForUser(int id, String pwd){
		try{
			statement = con.prepareStatement("UPDATE users SET password=? WHERE user_ID=?");
			statement.setString(1, pwd);
			statement.setInt(2, id);
			statement.execute();
		} catch (SQLException e) {
			System.out.println("Error: Could not set password");
			e.printStackTrace();
			}
	}
}
