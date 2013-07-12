package org.tapestry.objects;

/**
* User bean
* Represents a user, who can be either a normal user or an admin.
* @author Adam Gignac
* @version 1.0
*/
public class User implements java.io.Serializable {

	private int userID;
	private String name;
	private String username;
	private String password;
	private boolean enabled;
	private String email;
	private String role;

	public User(){
		//Default constructor
	}

	//Accessor methods
	/**
	*@return user_ID The numeric ID of the user
	*/
	public int getUserID(){
		return userID;
	}

	/**
	*@return name The user's full name
	*/
	public String getName(){
		return name;
	}

	/**
	*@return username The username used to log in
	*/
	public String getUsername(){
		return username;
	}

	/**
	*@return password The user's password
	*/
	public String getPassword(){
		return password;
	}

	/**
	*@return enabled Whether the user's account is active
	*/
	public boolean isEnabled(){
		return enabled;
	}

	/**
	*@return email The user's email address
	*/
	public String getEmail(){
		return email;
	}

	/**
	*@return role The user's role. One of:
	*<ul>
	*<li>ROLE_USER</li>
	*<li>ROLE_ADMIN</li>
	*</ul>
	*/
	public String getRole(){
		return role;
	}

	//Mutator methods
	public void setUserID(int id){
		this.userID = id;
	}

	/**
	*@param name The new full name for the user
	*/
	public void setName(String name){
		this.name = name;
	}

	/**
	*@param username The new username for the user
	*/
	public void setUsername(String username){
		this.username = username;
	}

	/**
	* @param password The new password for the user
	* NOT YET IMPLEMENTED
	*/
	public void setPassword(String password){
	}

	/**
	* @param enabled The new status for the user
	*/
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}


	/**
	* @param email The new email address for the user
	*/
	public void setEmail(String email){
		this.email = email;
	}

	/**
	* @param role The new role for the user (either "ROLE_USER" or "ROLE_ADMIN")
	*/
	public void setRole(String role){
		this.role = role;
	}

}
