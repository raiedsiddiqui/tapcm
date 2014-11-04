package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Message;

/**
 * service for Model Message
 * @author lxie 
 */
@Service
public interface MessageManager {
	/**
	 * 
	 * @param recipient
	 * @return a list of Message object for recipient
	 */
	@Transactional
	public List<Message> getAllMessagesForRecipient(int recipient);
	
	/**
	 * 
	 * @param recipient
	 * @return number of unread message for recipient
	 */
	@Transactional
	public int countUnreadMessagesForRecipient(int recipient);
	
	/**
	 * 
	 * @param id
	 * @return a message object
	 */
	@Transactional
	public Message getMessageByID(int id);
	
	/**
	 * set flag as read
	 * @param id
	 */
	@Transactional
	public void markAsRead(int id);
	
	/**
	 * save a message in DB
	 * @param m
	 */
	@Transactional
	public void sendMessage(Message m);
	
	/**
	 * delete a record from table
	 * @param id
	 */
	@Transactional
	public void deleteMessage(int id);
	
	/**
	 * 
	 * @param userID
	 * @return
	 */
	@Transactional
	public List<Message> getAnnouncementsForUser(int userID);
	
	/**
	 * Save a copy of deleted message
	 * @param m
	 * @param deletedBy
	 */
	@Transactional
	public void archiveMessage(Message m, String deletedBy);
}
