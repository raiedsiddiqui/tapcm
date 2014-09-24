package org.tapestry.objects;

public class Picture {
	private String path;
	private int pictureID;
	String owner;
	boolean owner_is_user;
		
	public Picture(){
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public void setPictureID(int id){
		this.pictureID = id;
	}
	
	public String getPath(){
		return path;
	}
	
	public int getPictureID(){
		return pictureID;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isOwner_is_user() {
		return owner_is_user;
	}

	public void setOwner_is_user(boolean owner_is_user) {
		this.owner_is_user = owner_is_user;
	}

}