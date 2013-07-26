package org.tapestry.objects;

import java.util.Calendar;

/**
* Survey Result bean
* Represents the results of a survey.
* @author Darryl Hui
* @version 1.0
*/
public class SurveyResult {

	private int resultID;
	private int surveyID;
	private int userID;
	private int patientID;
	private Calendar completedDate;
	private byte[] results;

	public SurveyResult(){
		//Default constructor
	}

	//Accessor methods
	/**
	*@return result_ID The numeric ID of the results
	*/
	public int getResultID(){
		return resultID;
	}
	
	/**
	*@return survey_ID The numeric ID of the survey
	*/
	public int getSurveyID(){
		return surveyID;
	}
	
	/**
	*@return user_ID The numeric ID of the user
	*/
	public int getUserID(){
		return userID;
	}
	
	/**
	*@return patient_ID The numeric ID of the patient
	*/
	public int getPatientID(){
		return patientID;
	}

	/**
	*@return complete_date The completed date of the survey
	*/
	public Calendar getCompletedDate(){
		return completedDate;
	}
	
	/**
	 * @param results The results of the survey
	 */
	public byte[] getResults(){
		return results;
	}

	//Mutator methods
	public void setResultID(int id){
		this.resultID = id;
	}
	
	/**
	 * @param id The new survey ID
	 */
	public void setSurveyID(int id){
		this.surveyID = id;
	}
	
	/**
	 * @param id The new user ID
	 */
	public void setUserID(int id){
		this.userID = id;
	}
	
	/**
	 * @param id The new patient ID
	 */
	public void setPatientID(int id){
		this.patientID = id;
	}

	/**
	*@param completedDate The new completed date
	*/
	public void setCompletedDate(Calendar completedDate){
		this.completedDate = completedDate;
	}
	
	/**
	 * @param results The new results
	 */
	public void setResults(byte[] results){
		this.results = results;
	}
}