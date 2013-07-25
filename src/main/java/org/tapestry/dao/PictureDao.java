package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

public class PictureDao {
	private PreparedStatement statement;
	private Connection con;
	
	public PictureDao(String database, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			System.out.println(e.toString());
		}
    }
    
    public void uploadPicture(MultipartFile pic, int owner, boolean isUser){
    	String uploadFilename;
    	try{
    		pic.transferTo(new File(filename));
    		statement = con.prepareStatement("INSERT INTO pictures (pic, owner, owner_is_user) values (?,?,?)");
    		statement.setString(1, filename);
    		statement.setInt(2, owner);
    		statement.setBoolean(3, isUser);
    	}
    }
    
    public ArrayList<String> getPicturesForUser(int userID){
    	try{
    		statement = con.prepateStatement("SELECT pic FROM pictures WHERE owner=? and owner_is_user=1");
    		statement.setInt(1, userID);
    		ResultSet result = statement.executeQuery;
    		ArrayList<String> pics = new ArrayList<String>();
    		while(result.next()){
    			String p = result.getString("pic");
    			pics.add(p);
    		}
    		return pics;
    	} catch (SQLException e) {
    		System.out.println("Error: Could not retrieve pictures");
    		System.out.println(e.toString());
    		return null;
    	}
    }
    
    public ArrayList<String> getPicturesForPatient(int patientID){
    	try{
    		statement = con.prepateStatement("SELECT pic FROM pictures WHERE owner=? and owner_is_user=0");
    		statement.setInt(1, patientID);
    		ResultSet result = statement.executeQuery;
    		ArrayList<String> pics = new ArrayList<String>();
    		while(result.next()){
    			String p = result.getString("pic");
    			pics.add(p);
    		}
    		return pics;
    	} catch (SQLException e) {
    		System.out.println("Error: Could not retrieve pictures");
    		System.out.println(e.toString());
    		return null;
    	}
    }

}