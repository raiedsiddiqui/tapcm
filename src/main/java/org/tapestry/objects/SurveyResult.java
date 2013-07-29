package org.tapestry.objects;

/**
* Survey Result bean
* Represents the results of a survey.
* @author Darryl Hui
* @version 1.0
*/
public class SurveyResult {

	private int resultID;
	private int surveyID;
	private String surveyTitle;
	private int patientID;
	private String patientName;
	private String status;
	private String startDate;
	private String endDate;
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
	 * @return status The status of the survey
	 */
	public String getSurveyTitle(){
		return surveyTitle;
	}
	
	/**
	*@return patient_ID The numeric ID of the patient
	*/
	public int getPatientID(){
		return patientID;
	}
	
	/**
	 * @return status The status of the survey
	 */
	public String getPatientName(){
		return patientName;
	}
	
	/**
	 * @return status The status of the survey
	 */
	public String getStatus(){
		return status;
	}

	/**
	*@return start_date The start date of the survey
	*/
	public String getStartDate(){
		return startDate;
	}
	
	/**
	 * @return end_date The date the survey was completed
	 */
	public String getEndDate(){
		return endDate;
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
	 * @param status The new status
	 */
	public void setSurveyTitle(String surveyTitle){
		this.surveyTitle = surveyTitle;
	}
	
	/**
	 * @param id The new patient ID
	 */
	public void setPatientID(int id){
		this.patientID = id;
	}
	
	/**
	 * @param status The new status
	 */
	public void setPatientName(String patientName){
		this.patientName = patientName;
	}
	
	/**
	 * @param status The new status
	 */
	public void setStatus(String status){
		this.status = status;
	}

	/**
	* @param startDate The new start date
	*/
	public void setStartDate(String startDate){
		this.startDate = startDate;
	}
	
	/**
	 * @param endDate The new completed date
	 */
	public void setEndDate(String endDate){
		this.endDate = endDate;
	}
	
	/**
	 * @param results The new results
	 */
	public void setResults(byte[] results){
		this.results = results;
	}
}