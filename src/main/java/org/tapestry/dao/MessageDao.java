package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
			System.out.println(e.toString());
		}
	}
	
	private Message createFromSearch(ResultSet result){
		Message m = new Message();
		try{
			m.setRecipient(result.getInt("recipient"));
			m.setRead(result.getBoolean("msgRead"));
			m.setText(result.getString("msg"));
			m.setSubject(result.getString("subject"));
			m.setSender(result.getString("sender"));
			m.setMessageID(result.getInt("message_ID"));
			m.setDate(result.getString("sent"));
		} catch (SQLException e){
			System.out.println("Error: Could not create Message object");
			System.out.println(e.toString());
		}
		return m;
	}
	
	public ArrayList<Message> getAllMessagesForRecipient(int recipient){
		try{
			statement = con.prepareStatement("SELECT * FROM messages WHERE recipient=?");
			statement.setInt(1, recipient);
			ResultSet result = statement.executeQuery();
			ArrayList<Message> allMessages = new ArrayList<Message>();
			while(result.next()){
				Message m = createFromSearch(result);
				allMessages.add(m);
			}
			return allMessages;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve messages");
			System.out.println(e.toString());
			return null;
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
			System.out.println(e.toString());
			return 0;
		}
	}
	
	public Message getMessageByID(int id){
		try{
			statement = con.prepareStatement("SELECT * FROM messages WHERE message_ID=?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			result.first();
			Message m = createFromSearch(result);
			return m;
		} catch (SQLException e){
			System.out.println("Error: Could not retrieve message");
			System.out.println(e.toString());
			return null;
		}
	}
	
	public void markAsRead(int id){
		try{
			statement = con.prepareStatement("UPDATE messages SET msgRead=1 WHERE message_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not mark message as read");
			System.out.println(e.toString());
		}
	}
	
	public void sendMessage(Message m){
		try{
			statement = con.prepareStatement("INSERT INTO messages (recipient, sender, msg, subject) VALUES (?,?,?,?)");
			statement.setInt(1, m.getRecipient());
			statement.setString(2, m.getSender());
			statement.setString(3, m.getText());
			statement.setString(4, m.getSubject());
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not send message");
			System.out.println(e.toString());
		}
	}
	
	public void deleteMessage(int id){
		try{
			statement = con.prepareStatement("DELETE FROM messages WHERE message_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not delete message");
			System.out.println(e.toString());
		}
	}
	
}