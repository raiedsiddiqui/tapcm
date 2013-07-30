package org.tapestry.objects;

public class Activity{
	private String description;
	private String date;
	
	public Activity(){
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getDate(){
		return date;
	}
}