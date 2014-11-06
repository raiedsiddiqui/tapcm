package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.SurveyResult;

/**
 * SurveyTemplateDAO
 * Defines DAO operations for the SurveyResult model.
 *
 * @author lxie 
 */
public interface SurveyResultDAO {
	/**
	* a List of Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getSurveysByPatientID(int patientId);
	
	/**
	* a List of completed Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getCompletedSurveysByPatientID(int patientId);	
	
	/**
	* a List of uncompleted Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getIncompleteSurveysByPatientID(int patientId);

	/**
	 * 
	 * A object of SurveyResult which result_Id is id
	 * @param id
	 * @return an object of SurveyResult
	 */
	public SurveyResult getSurveyResultByID(int id);
	
	/**
	 * Ordered by title ascending
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getAllSurveyResults();
	
	/**
	 * Retrieves all survey results by survey Id	
	 * @param id survey ID
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int id);
	
	/**
	 * Retrieves all survey results by survey Id and patientId
	 * @param itemsToReturn 
	 * @param id survey ID
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getSurveyResultByPatientAndSurveyId(int patientId, int surveyId);
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public String assignSurvey(SurveyResult sr);
	
	/**
	 * Delete a survey from the database
	 * @param id
	 */
	public void deleteSurvey(int id);
	
	/**
	 * Mark a survey as completed
	 * @param id
	 */
	public void markAsComplete(int id);
	
	/**
	 * Upload survey results
	 * @param id The ID of the survey result
	 * @param data The survey results
	 */
	public void updateSurveyResults(int id, byte[] data);
	
	/**
	 * Set start date of survey result
	 * @param id The ID of the survey result
	 */
	public void updateStartDate(int id) ;
	
	/**
	 * Count number of completed survey result for a patient
	 * @param patientId 
	 * @return number of completed survey result 
	 */
	public int countCompletedSurveys(int patientId);
	
	/**
	 * Keep a copy of deleted survey result
	 * @param sr
	 */
	public void archiveSurveyResult(SurveyResult sr, String patient, String deletedBy);
}
