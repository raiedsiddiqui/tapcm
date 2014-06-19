package org.tapestry.surveys;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.survey_component.data.SurveyAnswerChoice;
import org.survey_component.data.SurveyDirectionStatement;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.source.SurveyParseException;
import org.survey_component.source.SurveySourceMumps;

public class TapestrySurveySourceMumps extends SurveySourceMumps {
	protected static Logger logger = Logger.getLogger(TapestrySurveySourceMumps.class);

	public static TapestryPHRSurvey loadSurveySource(InputStream templateContents, Long templateId) throws SurveyParseException
	{
		try
		{
			//FileInputStream fstream = new FileInputStream(filePath);
			//DataInputStream finput = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(templateContents));

			TapestryPHRSurvey survey = new TapestryPHRSurvey();
			Properties surveyParameters = new Properties();
			ArrayList<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();

			String strLine = "";
			//Read File Line By Line
			while ((strLine != null && (strLine.startsWith(".qu=")) || (strLine = nextLine("--", strLine, br, false)) != null))
			{ //loops through questions
				strLine = StringUtils.trimToNull(strLine);
				if (strLine==null) continue;
				
				//if there are properties before question definition (i.e. title=)y
				if (questions.size() == 0 && !strLine.startsWith(".qu=") && (strLine.startsWith(".")))
				{
					surveyParameters = extractParameter(surveyParameters, strLine);
					//if new question
				}
				else if (strLine.startsWith(".qu="))
				{
					String qid = strLine.substring(4, strLine.indexOf(",")); //extract id

					//ignore all warnings ect...  (ie. E5bwarn)
					//if (qid.length() > 3) continue;
					String questionType = strLine.substring(strLine.indexOf(",type=") + 6, strLine.trim().length()); //extract type=

					//set up these parameters to be filled
					ArrayList<SurveyDirectionStatement> nextQuestionLogic = new ArrayList<SurveyDirectionStatement>();
					Properties questionParameters = new Properties(); //for params like .hi=6
					ArrayList<SurveyAnswerChoice> choices = new ArrayList<SurveyAnswerChoice>();
					String questionText = "";

					strLine = br.readLine().trim();

					//read in this question...
					while (strLine != null && !strLine.startsWith(".qu="))
					{ //loops through each line in question
						boolean readingQuestionText = true;
						if (strLine.startsWith("."))
						{ //if line is a property
							readingQuestionText = false;
							if (strLine.startsWith(".an"))
							{
								//read in answer choices (if there are any)
								strLine = nextLine(qid, strLine, br, true);
								while (!strLine.startsWith("."))
								{
									Hashtable<String, String> choiceHt = parseChoice(strLine, choices.size() + 1 + "");
									choices.add(new SurveyAnswerChoice(choiceHt.get("value"), choiceHt.get("valueText")));
									strLine = nextLine(qid, strLine, br, true);
								}
							}
							if (strLine.startsWith(".if"))
							{ //if if statement
								nextQuestionLogic.add(new SurveyDirectionStatement(strLine));
							}
							else
							{ //if its a param .hi=5
								questionParameters = extractParameter(questionParameters, strLine);
							}
						}
						else
						{ //if plain text
							if (readingQuestionText) questionText = questionText + "\n" + strLine;
						}
						strLine = br.readLine();
						if (strLine != null && !readingQuestionText) strLine = strLine.trim();

					}

					logger.debug("read question: " + qid);

					String paramNext = questionParameters.getProperty("next");
					if (paramNext != null && strLine != null)
					{
						if (paramNext.equals(""))
						{
							String nextQid = extractQid(strLine);
							nextQuestionLogic.add(new SurveyDirectionStatement("if (true) " + nextQid));
						}
						else
						{
							nextQuestionLogic.add(new SurveyDirectionStatement("if (true) " + paramNext));
						}
					}

					SurveyQuestion question = new SurveyQuestion();
					question.setId(qid);
					question.setQuestionText(questionText.trim());
					question.setQuestionType(questionType);
					question = setQuestionType(question, questionType);
					question.setParameters(questionParameters);
					question.setNextQuestionLogic(nextQuestionLogic);
					question.setChoices(choices);
					questions.add(question);
				}
			}
			survey.setSurveyId(templateId.toString());
			survey = setSurveyParameters(surveyParameters, survey);
			survey.setIssueDate(Calendar.getInstance());
			survey.setQuestions(questions);
			return survey;
		}
		catch (Exception e)
		{
			throw new SurveyParseException("Survey Construction failed, see previous errors", e);
		}
	}
	
	protected static TapestryPHRSurvey setSurveyParameters(Properties parameters, TapestryPHRSurvey survey) throws SurveyParseException
	{
		if (parameters.containsKey("title"))
		{
			survey.setTitle(parameters.getProperty("title"));
			logger.debug("Setting title: " + survey.getTitle());
		}
		if (parameters.containsKey("instances"))
		{
			try
			{
				Integer instances = Integer.parseInt(parameters.getProperty("instances"));
				survey.setMaxInstances(instances);
			}
			catch (NumberFormatException nfe)
			{
				throw new SurveyParseException("Value 'instances' must be an integer", ".instances=" + parameters.getProperty("instances"), nfe);
			}

		}
		//if survey marked .menuhidden=true then the survey won't show up in the "Add survey"
		if (parameters.containsKey("menuhidden"))
		{
			if (parameters.getProperty("menuhidden").equalsIgnoreCase("true"))
			{
				survey.setMenuHidden(true);
			}
		}
		if (parameters.containsKey("booleanResultStatement") && !parameters.getProperty("booleanResultStatement").equals(""))
		{
			survey.setBooleanResultStatement(parameters.getProperty("booleanResultStatement"));
		}
		if (parameters.containsKey("geomapping"))
		{
			if (parameters.getProperty("geomapping").equalsIgnoreCase("true"))
			{
				survey.setGeomapping(true);
			}
		}
		//don't need to add properties as above, use the properties data member of survey
		survey.getProperties().putAll(parameters);
		return survey;
	}
}
