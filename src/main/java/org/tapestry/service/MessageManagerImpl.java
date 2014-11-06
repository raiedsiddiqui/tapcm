package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tapestry.dao.MessageDAO;
import org.tapestry.objects.Message;

/**
 * Implementation for service MessageManager
 * @author lxie 
 */
@Service
public class MessageManagerImpl implements MessageManager {
	@Autowired
	private MessageDAO messageDao;
	@Override
	public List<Message> getAllMessagesForRecipient(int recipient) {		
		return messageDao.getAllMessagesForRecipient(recipient);
	}

	@Override
	public int countUnreadMessagesForRecipient(int recipient) {		
		return messageDao.countUnreadMessagesForRecipient(recipient);
	}

	@Override
	public Message getMessageByID(int id) {
		return messageDao.getMessageByID(id);
	}

	@Override
	public void markAsRead(int id) {
		messageDao.markAsRead(id);
	}

	@Override
	public void sendMessage(Message m) {
		messageDao.sendMessage(m);
	}

	@Override
	public void deleteMessage(int id) {
		messageDao.deleteMessage(id);
	}

	@Override
	public void archiveMessage(Message m, String deletedBy) {
		messageDao.archiveMessage(m, deletedBy);		
	}
	
	@Override
	public List<Message> getAnnouncementsForUser(int userID) {
		return messageDao.getAnnouncementsForUser(userID);
	}

}
