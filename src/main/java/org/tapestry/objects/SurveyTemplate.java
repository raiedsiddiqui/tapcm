package org.tapestry.objects;

/**
* Survey Template bean
* Represents a survey template.
* @author Darryl Hui
* @version 1.0
*/
public class SurveyTemplate {

	private int surveyID;
	private String title;
	private String type;
	private int priority;
	private byte[] contents;

	public SurveyTemplate(){
		//Default constructor
	}
	
	public static SurveyTemplate getTransfer(SurveyTemplate surveyTemplate){
		if (surveyTemplate == null) return(null);

		SurveyTemplate surveyTemplateTransfer = new SurveyTemplate();

		surveyTemplateTransfer.setContents(surveyTemplate.getContents());
		surveyTemplateTransfer.setSurveyID(surveyTemplate.getSurveyID());
		surveyTemplateTransfer.setTitle(surveyTemplate.getTitle());
		surveyTemplateTransfer.setType(surveyTemplate.getType());

		return(surveyTemplateTransfer);
	}

	//Accessor methods
	/**
	*@return survey_ID The numeric ID of the survey
	*/
	public int getSurveyID(){
		return surveyID;
	}

	/**
	*@return title The survey's title
	*/
	public String getTitle(){
		return title;
	}

	/**
	*@return type The survey's type. One of:
	*<ul>
	*<li>MUMPS</li>
	*</ul>
	*/
	public String getType(){
		return type;
	}
	
	/**
	 * @return the survey contents
	 */
	public byte[] getContents(){
		return contents;
	}
	
	/**
	 * @return the survey priority
	 */
	public int getPriority(){
		return priority;
	}

	//Mutator methods
	public void setSurveyID(int id){
		this.surveyID = id;
	}

	/**
	*@param name The new title for the survey
	*/
	public void setTitle(String title){
		this.title = title;
	}

	/**
	* @param type The new type for the survey ("MUMPS")
	*/
	public void setType(String type){
		this.type = type;
	}
	
	/**
	 * @param contents The new contents for the survey
	 */
	public void setContents(byte[] contents){
		this.contents = contents;
	}
	
	/**
	 * @param priority The new priority for the survey
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}
}