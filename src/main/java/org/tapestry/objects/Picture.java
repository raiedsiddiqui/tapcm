package org.tapestry.objects;

public class Picture {
	private String path;
	private int pictureID;
	
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

}