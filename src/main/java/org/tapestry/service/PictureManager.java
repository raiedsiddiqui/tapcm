package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.tapestry.objects.Picture;

/**
 * service for Model Picture
 * @author lxie *
 */
@Service
public interface PictureManager {
	@Transactional
	public List<Picture> getPicturesForUser(int userID);
	@Transactional
	public List<Picture> getPicturesForPatient(int patientID);
	@Transactional
	public void uploadPicture(MultipartFile pic, int owner, boolean isUser);
	@Transactional
	public void removePicture(int pictureID);

}
