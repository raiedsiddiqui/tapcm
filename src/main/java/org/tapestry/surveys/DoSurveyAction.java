/**
 * Copyright (c) 2001-2012. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
/*
 * NewClass.java
 *
 * Created on May 9, 2007, 4:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.tapestry.surveys;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.survey_component.actions.SurveyAction;
import org.survey_component.data.PHRSurvey;
import org.survey_component.data.SurveyException;
import org.survey_component.data.SurveyMap;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.data.answer.SurveyAnswer;
import org.survey_component.data.answer.SurveyAnswerFactory;

/**
 * Created on December 21, 2006, 10:47 AM
 * @author apavel (Paul)
 */
public class DoSurveyAction
{
	private static Logger logger = Logger.getLogger(DoSurveyAction.class);

	public static final int SAVE_INTERVAL = 4; //number of questions

	/** 
	 * @return the next url to go to, excluding contextPath
	 */
	public static ModelAndView execute(HttpServletRequest request, String documentId, PHRSurvey currentSurvey, PHRSurvey templateSurvey) throws Exception
	{
		ModelAndView m = new ModelAndView();
		final String questionId = request.getParameter("questionid");
		String direction = request.getParameter("direction");
		if (direction == null)
			direction = "forward";

		if (documentId == null)
		{
			logger.error("no selected survey? documentId=" + documentId);
			m.setViewName("failed");
			return m;
		}

		String[] answerStrs = request.getParameterValues("answer");

		String nextQuestionId = questionId;
		//if requested survey does not exist
		if (currentSurvey == null)
		{
			logger.error("Cannot find requested survey. documentId=" + documentId);
			m.setViewName("failed");
			return m;
		}

		//if requested survey is completed
		if (currentSurvey.isComplete())
		{
			logger.error("trying to complete already completed survey?");
		}

		boolean saved = false;

		//if starting/continuing survey, clear session

		if (questionId == null)
		{
			//if just starting/continuing(frombefore) the survey, direct to last question
			String lastQuestionId;

			if (currentSurvey.getQuestions().size() == 0)
			{
				boolean moreQuestions = addNextQuestion(null, currentSurvey, templateSurvey);
				if (!moreQuestions)
				{
					logger.error("Survey has no questions?");
					m.setViewName("failed");
					return m;
				}
			}

			if (currentSurvey.isComplete())
			{ //if complete show first question
				lastQuestionId = currentSurvey.getQuestions().get(0).getId();
			}
			else
			{ //if not complete show next question
				lastQuestionId = currentSurvey.getQuestions().get(currentSurvey.getQuestions().size() - 1).getId();
			}

			m.addObject("survey", currentSurvey);
			m.addObject("templateSurvey", templateSurvey);
			m.addObject("questionid", lastQuestionId);
			m.addObject("resultid", documentId);
			m.setViewName("/surveys/show_survey");
			return m;
		}

		String errMsg = null;

		//if continuing survey (just submitted an answer)
		if (questionId != null && direction.equalsIgnoreCase("forward"))
		{
			if (currentSurvey.getQuestionById(questionId).getQuestionType().equals(SurveyQuestion.ANSWER_CHECK) && answerStrs == null)
			{
				answerStrs = new String[0];
			}
			if (answerStrs != null && (currentSurvey.getQuestionById(questionId).getQuestionType().equals(SurveyQuestion.ANSWER_CHECK) || !answerStrs[0].equals("")))
			{
				SurveyQuestion question = currentSurvey.getQuestionById(questionId);
				ArrayList<SurveyAnswer> answers = convertToSurveyAnswers(answerStrs, question);
				boolean goodAnswerFormat = true;
				if (answers == null)
				{
					goodAnswerFormat = false;
				}
				//check each answer for validation
				// if answer passes validation
				if (goodAnswerFormat && question.validateAnswers(answers))
				{
					boolean moreQuestions;
					//see if the user went back (if current question the last question in user's question profile)
					if (!currentSurvey.getQuestions().get(currentSurvey.getQuestions().size() - 1).equals(question))
					{
						ArrayList<SurveyAnswer> existingAnswers = currentSurvey.getQuestionById(questionId).getAnswers();
						//if user hit back, and then forward, and answer wasn't changed
						if (StringUtils.join(answerStrs, ", ").equals(StringUtils.join(existingAnswers, ", ")) || currentSurvey.isComplete())
						{
							logger.debug("user hit back and went forward, no answer was changed");
							moreQuestions = true;
							//if the user hit "back" and changed the answer - remove all questions after it
						}
						else
						{
							logger.debug("user hit back and changed an answer");
							//clear all questions following it
							int currentSurveySize = currentSurvey.getQuestions().size();
							int currentQuestionIndex = currentSurvey.getQuestions().indexOf(question);
							for (int i = currentQuestionIndex + 1; i < currentSurveySize; i++)
							{
								currentSurvey.getQuestions().remove(currentQuestionIndex + 1);
							}
							question.setAnswers(answers);
							saved = true;
							moreQuestions = addNextQuestion(questionId, currentSurvey, templateSurvey);
						}
						//if user didn't go back, and requesting the next question
					}
					else
					{
						logger.debug("user hit forward, and requested the next question");
						question.setAnswers(answers);
						saved = true;
						moreQuestions = addNextQuestion(questionId, currentSurvey, templateSurvey);
					}
					//finished survey
					if (!moreQuestions)
					{
						if (!currentSurvey.isComplete()){
							SurveyAction.updateSurveyResult(currentSurvey);
							m.setViewName("redirect:/");
							m.addObject("survey_completed", true);
							return m;
						} else {
							m.setViewName("redirect:/");
							return m;
						}
					}
					int questionIndex = currentSurvey.getQuestionIndexbyId(questionId);
					nextQuestionId = currentSurvey.getQuestions().get(questionIndex + 1).getId();
					logger.debug("Next question id: " + nextQuestionId);

					//save to indivo
					if (saved && questionIndex % SAVE_INTERVAL == 0 && !currentSurvey.isComplete()) SurveyAction.updateSurveyResult(currentSurvey);

					//if answer fails validation
				}
				else {
					m.addObject("survey", currentSurvey);
					m.addObject("templateSurvey", templateSurvey);
					m.addObject("questionid", questionId);
					m.addObject("resultid", documentId);
					m.addObject("message", URLEncoder.encode(question.getRestriction().getInstruction(), "UTF-8"));

					m.setViewName("/surveys/show_survey");
					return m;
				}

				//if answer not specified, and hit forward
			}
			else errMsg = "You must supply an answer";
		}
		else if (direction.equalsIgnoreCase("backward"))
		{
			int questionIndex = currentSurvey.getQuestionIndexbyId(questionId);
			if (questionIndex > 0) nextQuestionId = currentSurvey.getQuestions().get(questionIndex - 1).getId();
		}

		m.addObject("survey", currentSurvey);
		m.addObject("templateSurvey", templateSurvey);
		m.addObject("questionid", nextQuestionId);
		m.addObject("resultid", documentId);
		if (errMsg != null) m.addObject("message", URLEncoder.encode(errMsg, "UTF-8"));	

		m.setViewName("/surveys/show_survey");
		return m;
	}

	private static boolean addNextQuestion(String currentQuestionId, PHRSurvey currentSurvey, PHRSurvey templateSurvey) throws SurveyException
	{
		SurveyQuestion nextQuestion;
		if (currentQuestionId == null)
		{
			if (templateSurvey.getQuestions().size() == 0) return false;
			nextQuestion = templateSurvey.getQuestions().get(0);
		}
		else
		{
			String nextQuestionId = currentSurvey.getNextQuestionId(currentQuestionId);
			if (nextQuestionId == null) return false;
			logger.debug("going to question id: " + nextQuestionId);
			nextQuestion = templateSurvey.getQuestionById(nextQuestionId);

		}
		currentSurvey.getQuestions().add(nextQuestion);
		return true;
	}

	private static ArrayList<SurveyAnswer> convertToSurveyAnswers(String[] answers, SurveyQuestion question)
	{
		ArrayList<SurveyAnswer> surveyAnswers = new ArrayList<SurveyAnswer>();
		SurveyAnswerFactory answerFactory = new SurveyAnswerFactory();
		for (String answer : answers)
		{
			SurveyAnswer answerObj = answerFactory.getSurveyAnswer(question.getQuestionType(), answer);
			if (answerObj == null) return null;
			else surveyAnswers.add(answerObj);
		}
		return surveyAnswers;
	}
	
	public static SurveyMap getSurveyMapAndStoreInSession(HttpServletRequest request, ArrayList<SurveyResult> surveyResults, ArrayList<SurveyTemplate> surveyTemplates)
	{
		SurveyMap userSurveys = (SurveyMap) request.getSession().getAttribute("session_survey_list");

		//if survey list not in the session, retrieve from server
		if (userSurveys == null)
		{
			userSurveys = new SurveyMap(getSurveyResultsList(surveyResults, surveyTemplates));
			request.getSession().setAttribute("session_survey_list", userSurveys);
		}

		return(userSurveys);
	}
	
	public static SurveyMap getSurveyMap(HttpServletRequest request) {
		SurveyMap userSurveys = (SurveyMap) request.getSession().getAttribute("session_survey_list");
		return userSurveys;
	}
	
	public static List<PHRSurvey> getSurveyResultsList(ArrayList<SurveyResult> surveyResults, ArrayList<SurveyTemplate> surveyTemplates)
	{
		List<PHRSurvey> results = new ArrayList<PHRSurvey>();

		for (SurveyResult tempResult : surveyResults)
		{
			try
			{
				tempResult.processMumpsResults(tempResult);
				PHRSurvey temp = SurveyActionMumps.toPhrSurvey(surveyTemplates, tempResult);
				results.add(temp);
			}
			catch (Exception e)
			{
				logger.error("Error", e);
			}
		}

		return(results);
	}
}