package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class PictureDao {
	private PreparedStatement statement;
	private Connection con;
	
	public PictureDao(String database, String username, String password){
		try{
			con = DriverManager.getConnection(database, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			System.out.println(e.toString());
		}
    }
    
    public void uploadPicture(MultipartFile pic, int owner, boolean isUser){
    	String uploadFilename = "resources/uploads/" + pic.getOriginalFilename();
    	try{
    		pic.transferTo(new File(uploadFilename));
    		statement = con.prepareStatement("INSERT INTO pictures (pic, owner, owner_is_user) values (?,?,?)");
    		statement.setString(1, uploadFilename);
    		statement.setInt(2, owner);
    		statement.setBoolean(3, isUser);
    	} catch (SQLException se){
    		System.out.println("Error: Could not add entry to database");
    		System.out.println(se.toString());
    	} catch (IOException ie) {
    		System.out.println("Error: Could not write file to disk");
    		System.out.println(ie.toString());
    	} catch (Exception e){
    		System.out.println("Error");
    		System.out.println(e.toString());
    	}
    }
    
    public ArrayList<String> getPicturesForUser(int userID){
    	try{
    		statement = con.prepareStatement("SELECT pic FROM pictures WHERE owner=? and owner_is_user=1");
    		statement.setInt(1, userID);
    		ResultSet result = statement.executeQuery();
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
    		statement = con.prepareStatement("SELECT pic FROM pictures WHERE owner=? and owner_is_user=0");
    		statement.setInt(1, patientID);
    		ResultSet result = statement.executeQuery();
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