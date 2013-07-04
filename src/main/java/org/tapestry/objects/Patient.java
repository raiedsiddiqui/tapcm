package org.tapestry.objects;

public class Patient{
	String color;
	String name;

	/**
	* Default constructor
	* Sets color to white and name to "Default Patient"
	*/
	public Patient(){
		color="ffffff";
		name="Default Patient";
	}

	/**
	* Full constuctor
	*/
	public Patient(String name, String color){
		this.name = name;
		this.color = color;
	}

}
