package org.tapestry.dao;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import org.tapestry.objects.Picture;

/**
 * An implementation of the PictureDAO interface.
 * 
 * lxie
 */

@Repository
public class PictureDAOImpl extends JdbcDaoSupport implements PictureDAO {
	private String uploadDir;
		
	@Autowired
	public PictureDAOImpl(DataSource dataSource) {
		setDataSource(dataSource);
    }

	@Override
	public List<Picture> getPicturesForUser(int userID) {
		String sql = "SELECT * FROM pictures WHERE owner=? and owner_is_user=1";
		List<Picture> pics = getJdbcTemplate().query(sql, new Object[]{userID}, new PictureMapper());
		return pics;
	}

	@Override
	public List<Picture> getPicturesForPatient(int patientID) {		
		String sql = "SELECT * FROM pictures WHERE owner=? and owner_is_user=0";
		List<Picture> pics = getJdbcTemplate().query(sql, new Object[]{patientID}, new PictureMapper());
		return pics;
	}

	@Override
	public void uploadPicture(MultipartFile pic, int owner, boolean isUser) {
		try{
    		String ext = getExtension(pic.getOriginalFilename());
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		byte[] filenameBytes = pic.getOriginalFilename().getBytes();
    		byte[] filenameDigest = md.digest(filenameBytes);
    		String uploadFilename = new BigInteger(1, filenameDigest).toString(16) + "." + ext; //Maybe a bit hackish, but gets the job done;
    		File f = new File(uploadDir + uploadFilename);
    		f.createNewFile();
    		pic.transferTo(f);    
    		
    		String sql = "INSERT INTO pictures (pic, owner, owner_is_user) values (?,?,?)";
    		getJdbcTemplate().update(sql, uploadFilename, owner, isUser);    		
    		
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

	@Override
	public void removePicture(int pictureID) {		
		String sql = "DELETE FROM pictures WHERE picture_ID=?";
		getJdbcTemplate().update(sql, pictureID);

	}
	
	class PictureMapper implements RowMapper<Picture> {
		public Picture mapRow(ResultSet rs, int rowNum) throws SQLException{
			Picture picture = new Picture();
			
			picture.setPath(rs.getString("path"));
			picture.setPictureID(rs.getInt("picture_ID"));
			picture.setOwner(rs.getString("owner"));
			picture.setOwner_is_user(rs.getBoolean("owner_is_user"));
			
			return picture;
		}
	}
	
	private String getExtension(String path) {
	    String[] str = path.split("\\.");
	    if(str.length > 1) {
	        return str[str.length - 1];
	    }
	    return null; //No extension on file
	}
}
