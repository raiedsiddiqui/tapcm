package org.tapestry.surveys;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.survey_component.data.QuestionAnswerPair;
import org.survey_component.data.SurveyException;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.data.answer.SurveyAnswer;
import org.survey_component.data.answer.SurveyAnswerString;
import org.survey_component.services.SurveyServiceIndivo;
import org.survey_component.data.PHRSurvey;
import org.survey_component.source.SurveySourceMumps;
import org.survey_component.source.SurveyParseException;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;

public class SurveyActionMumps {
	public static PHRSurvey loadSurveySource(SurveyTemplate surveyTemplate) throws SurveyParseException
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(surveyTemplate.getContents());

			Long templateId = Long.parseLong(Integer.toString(surveyTemplate.getSurveyID()));

			return(SurveySourceMumps.loadSurveySource(bais, templateId));
		}
		catch (Exception e)
		{
			throw new SurveyParseException("Survey Construction failed, see previous errors", e);
		}
	}
	
	public static PHRSurvey toPhrSurvey(ArrayList<SurveyTemplate> surveyTemplates, SurveyResult surveyResult) throws SurveyException
	{
		SurveyTemplate st = new SurveyTemplate();
		for (SurveyTemplate tempTemplate : surveyTemplates)
		{
			if(tempTemplate.getSurveyID() == surveyResult.getSurveyID()) {
				st = tempTemplate;
			}
		}
		
		SurveyFactory surveyFactory = new SurveyFactory();
		PHRSurvey phrSurveyResult = surveyFactory.getSurveyTemplateNoQuestions(st);

		phrSurveyResult.setDocumentId(Integer.toString(surveyResult.getResultID()));
		phrSurveyResult.setSurveyId(Integer.toString(surveyResult.getSurveyID()));
		phrSurveyResult.setComplete(surveyResult.isCompleted());

		//        //load answers
		//        for (IndivoSurveyQuestionType iQuestion: iQuestions) {        
		PHRSurvey phrSurveyTemplate = surveyFactory.getSurveyTemplate(st);
		for (QuestionAnswerPair qaPair : surveyResult.getQuestionAnswerPairs())
		{

			//            if (iQuestion.getQuestionId().equals(this.INDIVO_SURVEY_ID)) continue;
			//            if (iQuestion.getQuestionId().equals(this.INDIVO_SURVEY_HASH)) continue;
			if (SurveyServiceIndivo.INDIVO_SURVEY_ID.equals(qaPair.questionId)) continue;
			if (SurveyServiceIndivo.INDIVO_SURVEY_HASH.equals(qaPair.questionId)) continue;

			//            //assuming that survey ID question is loaded by now
			//            if (mSurvey.getSurveyId() == null) {
			//                _log.warn("ID IS NULL, SKIPPING SURVEY TITLED '" + mSurvey.getTitle() +"'");
			//                return null;
			//            }
			//            
			//            SurveyQuestion templateQuestion = surveyFactory.getSurveyTemplate(mSurvey.getSurveyId()).getQuestionById(iQuestion.getQuestionId());
			//            if (templateQuestion == null) {
			//                _log.warn("Cannot find question: " + iQuestion.getQuestionId() + " for survey: " + mSurvey.getSurveyId());
			//                return null;
			//            }
			SurveyQuestion templateQuestion = phrSurveyTemplate.getQuestionById(qaPair.questionId);

			//            SurveyQuestion mQuestion = templateQuestion;
			SurveyQuestion mQuestion = templateQuestion;

			//            List<IndivoSurveyAnswerType> iAnswers = iQuestion.getQuestionAnswer();
			//            for (IndivoSurveyAnswerType iAnswer: iAnswers) {
			//                SurveyAnswer mAnswer = new SurveyAnswerString(iAnswer.getAnswerValue());
			//                mQuestion.getAnswers().add(mAnswer);
			//            }
			for (String answer : qaPair.answers)
			{
				SurveyAnswer mAnswer = new SurveyAnswerString(answer);
				mQuestion.getAnswers().add(mAnswer);
			}

			//            mSurvey.getQuestions().add(mQuestion);
			//        }
			phrSurveyResult.getQuestions().add(mQuestion);
		}

		return phrSurveyResult;
	}
}
