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
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.util.Map;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import org.yaml.snakeyaml.Yaml;
import org.tapestry.objects.Picture;

public class PictureDao {
	private PreparedStatement statement;
	private Connection con;
	private String uploadDir;
	
	public PictureDao(String database, String username, String password){
		try{
			con = DriverManager.getConnection(database, username, password);
			ClassPathResource configFile = new ClassPathResource("tapestry.yaml");
			Yaml yaml = new Yaml();
			Map<String, String> config = (Map<String, String>) yaml.load(configFile.getInputStream());
			uploadDir = config.get("uploadDir");
		} catch (SQLException se){
			System.out.println("Error: Could not connect to database");
			se.printStackTrace();
		} catch (IOException ie) {
			System.out.println("Error reading from config file");
			ie.printStackTrace();
		}
    }
	
	private String getExtension(String path) {
	    String[] str = path.split("\\.");
	    if(str.length > 1) {
	        return str[str.length - 1];
	    }
	    return null; //No extension on file
	}
	
    public void uploadPicture(MultipartFile pic, int owner, boolean isUser){
    	try{
    		String ext = getExtension(pic.getOriginalFilename());
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		byte[] filenameBytes = pic.getOriginalFilename().getBytes();
    		byte[] filenameDigest = md.digest(filenameBytes);
    		String uploadFilename = new BigInteger(1, filenameDigest).toString(16) + "." + ext; //Maybe a bit hackish, but gets the job done;
    		File f = new File(uploadDir + uploadFilename);
    		f.createNewFile();
    		pic.transferTo(f);
    		statement = con.prepareStatement("INSERT INTO pictures (pic, owner, owner_is_user) values (?,?,?)");
    		statement.setString(1, uploadFilename);
    		statement.setInt(2, owner);
    		statement.setBoolean(3, isUser);
    		statement.execute();
    	} catch (SQLException se){
    		System.out.println("Error: Could not add entry to database");
    		se.printStackTrace();
    	} catch (IOException ie) {
    		System.out.println("Error: Could not write file to disk");
    		ie.printStackTrace();
    	} catch (NoSuchAlgorithmException ne){
    		System.out.println("Error: Could not use MD5 algorithm to hash upload filename");
    		ne.printStackTrace();
    	} catch (Exception e){
    		System.out.println("Error");
    		e.printStackTrace();
    	}
    }
    
    public ArrayList<Picture> getPicturesForUser(int userID){
    	try{
    		statement = con.prepareStatement("SELECT * FROM pictures WHERE owner=? and owner_is_user=1");
    		statement.setInt(1, userID);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Picture> pics = new ArrayList<Picture>();
    		while(result.next()){
    			Picture p = new Picture();
    			p.setPictureID(result.getInt("picture_ID"));
    			p.setPath(result.getString("pic"));
    			pics.add(p);
    		}
    		return pics;
    	} catch (SQLException e) {
    		System.out.println("Error: Could not retrieve pictures");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public ArrayList<Picture> getPicturesForPatient(int patientID){
    	try{
    		statement = con.prepareStatement("SELECT pic FROM pictures WHERE owner=? and owner_is_user=0");
    		statement.setInt(1, patientID);
    		ResultSet result = statement.executeQuery();
    		ArrayList<Picture> pics = new ArrayList<Picture>();
    		while(result.next()){
    			Picture p = new Picture();
    			p.setPictureID(result.getInt("picture_ID"));
    			p.setPath(result.getString("pic"));
    			pics.add(p);
    		}
    		return pics;
    	} catch (SQLException e) {
    		System.out.println("Error: Could not retrieve pictures");
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public void removePicture(int pictureID){
    	try{
    		statement = con.prepareStatement("DELETE FROM pictures WHERE picture_ID=?");
    		statement.setInt(1, pictureID);
    		statement.execute();
    	} catch (SQLException e){
    		System.out.println("Error: Could not remove picture");
    		e.printStackTrace();
    	}
    }

}