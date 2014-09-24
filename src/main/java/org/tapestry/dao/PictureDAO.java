package org.tapestry.dao;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.tapestry.objects.Picture;

/**
 * Defines DAO operations for the Picture model.
 * 
 * @author lxie *
 */
public interface PictureDAO {	
	public List<Picture> getPicturesForUser(int userID);
	public List<Picture> getPicturesForPatient(int patientID);
	public void uploadPicture(MultipartFile pic, int owner, boolean isUser);
	public void removePicture(int pictureID);

}
