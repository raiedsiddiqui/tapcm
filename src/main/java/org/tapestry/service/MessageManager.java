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
	@Transactional
	public List<Message> getAllMessagesForRecipient(int recipient);
	@Transactional
	public int countUnreadMessagesForRecipient(int recipient);
	@Transactional
	public Message getMessageByID(int id);
	@Transactional
	public void markAsRead(int id);
	@Transactional
	public void sendMessage(Message m);
	@Transactional
	public void deleteMessage(int id);
	@Transactional
	public List<Message> getAnnouncementsForUser(int userID);
}
