package org.tapestry.dao;

import java.util.List;
import org.tapestry.objects.Message;

/**
 * Defines DAO operations for the Message model.
 * 
 * @author lxie 
*/
public interface MessageDAO {
	/**
	 * 
	 * @param recipient
	 * @return
	 */
	public List<Message> getAllMessagesForRecipient(int recipient);
	/**
	 * 
	 * @param recipient
	 * @return number of unread message for user
	 */
	public int countUnreadMessagesForRecipient(int recipient);
	/**
	 * 
	 * @param id
	 * @return a Message by id
	 */
	public Message getMessageByID(int id);
	/**
	 * mark a message as 'Read'
	 * @param id
	 */
	public void markAsRead(int id);
	/**
	 * Send a message
	 * @param m
	 */
	public void sendMessage(Message m);
	/**
	 * Delete a message
	 * @param id
	 */
	public void deleteMessage(int id);
	/**
	 * 
	 * @param userID
	 * @return
	 */
	public List<Message> getAnnouncementsForUser(int userID);
	
	/**
	 * Save a copy of deleted message
	 * @param m
	 */
	public void archiveMessage(Message m, String deletedBy);

}
