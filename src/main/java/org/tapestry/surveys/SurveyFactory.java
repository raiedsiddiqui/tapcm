package org.tapestry.surveys;

import java.util.Hashtable;

import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyException;
import org.survey_component.source.SurveyParseException;
import org.tapestry.objects.SurveyTemplate;

public class SurveyFactory {
	//DO NOT MAKE THIS PUBLIC!!!!!
	private static Hashtable<String, PHRSurvey> loadedSurveys = new Hashtable<String, PHRSurvey>();
	
	public PHRSurvey getSurveyTemplate(SurveyTemplate surveyTemplate) throws SurveyException
	{
		//check if it's loaded, if not, try to load
		ensureLoaded(surveyTemplate);
		return loadedSurveys.get(Integer.toString(surveyTemplate.getSurveyID())); //write-protecting to the template survey
	}
	
	public PHRSurvey getSurveyTemplateById(int templateId) throws SurveyException
	{
		return loadedSurveys.get(Integer.toString(templateId));
	}
	
	//optimized
	public PHRSurvey getSurveyTemplateNoQuestions(SurveyTemplate surveyTemplate) throws SurveyException
	{
		//check if it's loaded, if not, try to load
		ensureLoaded(surveyTemplate);
		return loadedSurveys.get(Integer.toString(surveyTemplate.getSurveyID())).cloneSurveyNoQuestions(); //write-protecting to the template survey
	}
		
	private void ensureLoaded(SurveyTemplate surveyTemplate) throws SurveyException
	{
		if (surveyTemplate == null)
		{
			throw new SurveyException("Requested survey was null");
		}

		if (!loadedSurveys.containsKey(Integer.toString(surveyTemplate.getSurveyID())))
		{
			PHRSurvey loadedSurvey = SurveyActionMumps.loadSurveySource(surveyTemplate);
			if (loadedSurvey == null) throw new SurveyException("Requested survey '" + surveyTemplate.getSurveyID() + "' cannot be found");
			loadedSurveys.put(Integer.toString(surveyTemplate.getSurveyID()), loadedSurvey);
		}
	}
		
	public void reloadSurveyTemplate(SurveyTemplate surveyTemplate) throws SurveyParseException
	{
		PHRSurvey loadedSurvey = SurveyActionMumps.loadSurveySource(surveyTemplate);
		if (loadedSurvey == null) throw new SurveyParseException("Specified survey '" + surveyTemplate.getSurveyID() + "' cannot be found");
		loadedSurveys.put(Integer.toString(surveyTemplate.getSurveyID()), loadedSurvey);
	}
}
