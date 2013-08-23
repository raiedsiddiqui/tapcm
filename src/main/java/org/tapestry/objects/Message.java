package org.tapestry.objects;

public class Message {
	
	private int msgID;
	private String msgText;
	private boolean msgRead;
	private int recipient;
	private int senderID;
	private String sender;
	private String subject;
	private String date;
	
	public Message(){	
	}
	
	//Accessors
	public int getMessageID(){
		return msgID;
	}
	
	public String getText(){
		return msgText;
	}
	
	public boolean isRead(){
		return msgRead;
	}
	
	public int getRecipient(){
		return recipient;
	}
	
	public String getSender(){
		return sender;
	}
	
	public int getSenderID(){
		return senderID;
	}
	
	public String getSubject(){
		return subject;
	}
	
	public String getDate(){
		return date;
	}
	
	//Mutators
	public void setMessageID(int id){
		this.msgID = id;
	}
	
	public void setText(String text){
		this.msgText = text;
	}

	public void setRead(boolean read){
		this.msgRead = read;
	}
	
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public void setRecipient(int recipient){
		this.recipient = recipient;
	}
	
	public void setSender(String sender){
		this.sender = sender;
	}
	
	public void setSenderID(int senderID){
		this.senderID = senderID;
	}
	
	public void setDate(String date){
		this.date = date;
	}
}