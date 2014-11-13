package org.tapestry.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.tapestry.objects.Message;

/**
 * An implementation of the MessageDAO interface.
 * 
 * lxie
 */
@Repository
public class MessageDAOImpl extends JdbcDaoSupport implements MessageDAO {
	@Autowired
	public MessageDAOImpl(DataSource dataSource) {
 		setDataSource(dataSource);
    }

	@Override
	public List<Message> getAllMessagesForRecipient(int recipient) {
		String sql = "SELECT messages.message_ID, messages.sender, messages.recipient, messages.msg,"
				+ " messages.subject, messages.msgRead, messages.sent, users.name "
				+ "FROM messages INNER JOIN users ON messages.sender = users.user_ID WHERE messages.recipient=? "
				+ "ORDER BY messages.msgRead ASC, messages.sent DESC";
		
		List<Message> messages = getJdbcTemplate().query(sql, new Object[]{recipient}, new MessageMapper());			
		return messages;
	}

	@Override
	public int countUnreadMessagesForRecipient(int recipient) {		
		String sql = "SELECT COUNT(*) as total FROM messages WHERE recipient=? and msgRead=0";
		return getJdbcTemplate().queryForInt(sql, new Object[]{recipient});	
	}

	@Override
	public Message getMessageByID(int id) {
		String sql = "SELECT messages.message_ID,  messages.sender, messages.msg, messages.subject, "
				+ "messages.msgRead, messages.sent, messages.recipient, users.name "
				+ "FROM messages INNER JOIN users ON messages.sender = users.user_ID WHERE message_ID=?";
		
		return getJdbcTemplate().queryForObject(sql, new Object[]{id}, new MessageMapper());			
	}

	@Override
	public void markAsRead(int id) {
		String sql = "UPDATE messages SET msgRead=1 WHERE message_ID=?";
		getJdbcTemplate().update(sql,id);

	}

	@Override
	public void sendMessage(Message m) {
		String sql = "INSERT INTO messages (recipient, sender, msg, subject) VALUES (?,?,?,?)";
		getJdbcTemplate().update(sql, m.getRecipient(), m.getSenderID(), m.getText() ,m.getSubject());
	}

	@Override
	public void deleteMessage(int id) {
		String sql = "DELETE FROM messages WHERE message_ID=?";
		getJdbcTemplate().update(sql, id);

	}
	
	@Override
	public void archiveMessage(Message m, String deletedBy) {
		String sql = "INSERT INTO messages_archive (recipient, sender, msg, subject, deleted_message_ID, deleted_by, "
				+ "sent, msgRead) VALUES (?,?,?,?,?,?,?,?)";
		getJdbcTemplate().update(sql, m.getRecipient(), m.getSenderID(), m.getText() ,m.getSubject(), m.getMessageID(), 
				deletedBy, m.getDate(), m.isRead());
		
	}


	@Override
	public List<Message> getAnnouncementsForUser(int userID) {
		String sql= "SELECT messages.message_ID,  messages.sender, messages.recipient, messages.msg, messages.subject,"
				+ " messages.msgRead, messages.sent, users.name FROM messages INNER JOIN users ON messages.sender = users.user_ID "
				+ "WHERE recipient=? AND msgRead=0 AND subject LIKE 'ANNOUNCEMENT:%'";
		List<Message> messages = getJdbcTemplate().query(sql, new Object[]{userID}, new MessageMapper());			
		return messages;
	}
	
	class MessageMapper implements RowMapper<Message> {
		public Message mapRow(ResultSet rs, int rowNum) throws SQLException{
			Message m = new Message();
			m.setMessageID(rs.getInt("message_ID"));
			m.setRecipient(rs.getInt("recipient"));
			m.setRead(rs.getBoolean("msgRead"));
			m.setText(rs.getString("msg"));
			m.setSubject(rs.getString("subject"));			
			m.setDate(rs.getString("sent"));			
			m.setSender(rs.getString("name"));	
			m.setSenderID(rs.getInt("sender"));
			
			return m;
		}
	}

}
