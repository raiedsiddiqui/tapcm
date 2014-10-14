package org.tapestry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tapestry.dao.PictureDAO;
import org.tapestry.objects.Picture;
/**
 * Implementation for service PictureManager
 * @author lxie 
 */
@Service
public class PictureManagerImpl implements PictureManager {
	@Autowired
	private PictureDAO pictureDao;
	
	@Override
	public List<Picture> getPicturesForUser(int userID) {
		return pictureDao.getPicturesForUser(userID);
	}

	@Override
	public List<Picture> getPicturesForPatient(int patientID) {
		return pictureDao.getPicturesForPatient(patientID);
	}

	@Override
	public void uploadPicture(MultipartFile pic, int owner, boolean isUser) {
		pictureDao.uploadPicture(pic, owner, isUser);
	}

	@Override
	public void removePicture(int pictureID) {
		pictureDao.removePicture(pictureID);
	}

}
