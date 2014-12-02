package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.SurveyTemplate;
/**
 * Defines DAO operations for the SurveyTemplate model.
 * 
 * @author lxie 
*/
public interface SurveyTemplateDAO {
	/**
	* Returns the survey template with the given ID
	* @param id The ID of the survey template to find
	* @return A SurveyTemplate object representing the result
	*/
	public SurveyTemplate getSurveyTemplateByID(int id);
	
	/**
	 * A list of Survey template
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getAllSurveyTemplates();
	
	/**
	 * A list of Survey template which title contains partialTitle
	 * @param partialTitle
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle);
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public void uploadSurveyTemplate(SurveyTemplate st);
	
	/**
	 * Modify a survey template on title and description fields
	 * @param st
	 */
	public void updateSurveyTemplate(SurveyTemplate st);
	
	/**
	 * Delete a survey template from the database
	 * @param id
	 */
	public void deleteSurveyTemplate(int id);
	
	/**
	 * Count how many survey template 
	 * @return number of survey template
	 */
	public int countSurveyTemplate();
	
	/**
	 * Keep a copy of deleted survey template
	 * @param st
	 */
	public void archiveSurveyTemplate(SurveyTemplate st, String deletedBy);
}
