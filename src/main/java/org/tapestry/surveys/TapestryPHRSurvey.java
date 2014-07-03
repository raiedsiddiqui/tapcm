package org.tapestry.surveys;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyDirectionStatement;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.logic.LogicFactory;
import org.survey_component.source.SurveyParseException;

public class TapestryPHRSurvey extends PHRSurvey {

	//IF YOU ADD MUTABLE DATA MEMBERS, MAKE SURE YOU ADD IT TO THE SURVEY CLONE METHOD
		private static Logger logger = Logger.getLogger(TapestryPHRSurvey.class);
	
	//gets the next question in the survey (parses and uses the logic module)
	public String getNextQuestionId(String currentQuestionId) throws SurveyParseException
	{
		LogicFactory logicFactory = new LogicFactory();
		SurveyQuestion question = this.getQuestionById(currentQuestionId);
		List<SurveyDirectionStatement> logicStatements = question.getNextQuestionLogic();
		logger.debug("Number of survey directives: " + logicStatements.size());			
	
		for (SurveyDirectionStatement curStatement : logicStatements)
		{
			String condition = curStatement.getCondition();
			String conditionNoQuotes = logicFactory.removeQuoted(condition);
		
			ArrayList<String> variableNames = logicFactory.extractVariableNames(conditionNoQuotes);
			//define all variables to execute the condition
			logger.debug("Number of variables found: " + variableNames.size());
							
			for (String variableName : variableNames)
			{				
				// only supporting integers, can do strings later
				SurveyQuestion variableQuestion = this.getQuestionById(variableName);
					
				if (variableQuestion == null)
					continue;						
			
				if (variableQuestion.isQuestionType(SurveyQuestion.ANSWER_CHECK)) 
					logicFactory.defineArray(variableName, variableQuestion.getAnswers());
				else
					logicFactory.defineVariable(variableName, "int", variableQuestion.getAnswer());
						
			}
			logger.debug("Logic conditon: " + condition);
			boolean evalResult = logicFactory.evaluateCondition(condition);
			logger.debug("result of logic: " + evalResult);
			if (evalResult) return curStatement.getResultQuestionId();
		}
		return null;
	}
}
