package org.tapestry.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.tapestry.objects.Message;

public class MessageDao {
	
	private PreparedStatement statement;
	private Connection con;
	
	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306/survey_app")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
	public MessageDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			System.out.println("Error: Could not connect to database");
			e.printStackTrace();
		}
	}
	
	public List<Message> getAllMessagesForRecipient(int recipient){
		try{
			String query = "SELECT messages.message_ID, messages.sender, messages.recipient, messages.msg, messages.subject, messages.msgRead, messages.sent, users.name "
					+ "FROM messages INNER JOIN users ON messages.sender = users.user_ID WHERE messages.recipient=?";
			
			statement = con.prepareStatement(query);
			statement.setInt(1, recipient);
			ResultSet result = statement.executeQuery();
			Message	m;
			List<Message> allMessages = new ArrayList<Message>();
			
			while(result.next()){				
				m = new Message();
				m = createFromSearch(result);
				allMessages.add(m);
			}
			
			return allMessages;
			
		}catch (SQLException e){
			System.out.println("Error: Could not retrieve messages");
			e.printStackTrace();
			return null;
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}

	
	public int countUnreadMessagesForRecipient(int recipient){
		try{
			statement = con.prepareStatement("SELECT COUNT(*) as total FROM messages WHERE recipient=? and msgRead=0");
			statement.setInt(1, recipient);
			ResultSet result = statement.executeQuery();
			result.first();
			int total = result.getInt("total");
			return total;
		} catch (SQLException e){
			System.out.println("Error: Could not count messages");
			e.printStackTrace();
			return 0;
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	public Message getMessageByID(int id){
		try{			
			String query = "SELECT messages.message_ID,  messages.sender, messages.msg, messages.subject, messages.msgRead, messages.sent, messages.recipient, users.name "
						+ "FROM messages INNER JOIN users ON messages.sender = users.user_ID WHERE message_ID=?";
			
			statement = con.prepareStatement(query);
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			result.first();
			Message m = createFromSearch(result);
			return m;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve message by ID " + id );
			e.printStackTrace();
			return null;
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	public void markAsRead(int id){
		try{
			statement = con.prepareStatement("UPDATE messages SET msgRead=1 WHERE message_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not mark message as read");
			e.printStackTrace();
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	public void sendMessage(Message m){
		try{
			statement = con.prepareStatement("INSERT INTO messages (recipient, sender, msg, subject) VALUES (?,?,?,?)");
			statement.setInt(1, m.getRecipient());
			statement.setInt(2, m.getSenderID());
			statement.setString(3, m.getText());
			statement.setString(4, m.getSubject());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not send message");
			e.printStackTrace();
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	public void deleteMessage(int id){
		try{
			statement = con.prepareStatement("DELETE FROM messages WHERE message_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not delete message");
			e.printStackTrace();
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	public ArrayList<Message> getAnnouncementsForUser(int userID){
		try{
			String query= "SELECT messages.message_ID,  messages.sender, messages.recipient, messages.msg, messages.subject, messages.msgRead, messages.sent, users.name "
						+ "FROM messages INNER JOIN users ON messages.sender = users.user_ID WHERE recipient=? AND msgRead=0 AND "
						+ "subject LIKE 'ANNOUNCEMENT:%'";
			
			statement = con.prepareStatement(query);			
			statement.setInt(1, userID);
			ResultSet result = statement.executeQuery();
			ArrayList<Message> announcements = new ArrayList<Message>();
			while(result.next()){
				Message m = createFromSearch(result);
				announcements.add(m);
			}
			return announcements;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve announcements");
			e.printStackTrace();
			return null;
		} finally {
    		try{
    			statement.close();
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	private Message createFromSearch(ResultSet result){
		Message m = new Message();
		try{			
			m.setRecipient(result.getInt("recipient"));
			m.setRead(result.getBoolean("msgRead"));
			m.setText(result.getString("msg"));
			m.setSubject(result.getString("subject"));
			m.setMessageID(result.getInt("message_ID"));
			m.setDate(result.getString("sent"));			
			m.setSender(result.getString("name"));			
			
			m.setSenderID(result.getInt("sender"));
			
		} catch (SQLException e){
			System.out.println("Error: Could not create Message object");
			e.printStackTrace();
		}
		return m;
	}
}