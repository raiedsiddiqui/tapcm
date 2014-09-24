package org.tapestry.dao;

import java.util.List;
import org.tapestry.objects.Message;

/**
 * Defines DAO operations for the Message model.
 * 
 * @author lxie 
*/
public interface MessageDAO {
	public List<Message> getAllMessagesForRecipient(int recipient);
	public int countUnreadMessagesForRecipient(int recipient);
	public Message getMessageByID(int id);
	public void markAsRead(int id);
	public void sendMessage(Message m);
	public void deleteMessage(int id);
	public List<Message> getAnnouncementsForUser(int userID);

}
