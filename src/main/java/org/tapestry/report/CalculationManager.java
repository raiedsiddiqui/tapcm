package org.tapestry.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tapestry.controller.Utils;

public class CalculationManager {	
	
	public static int getScoreByQuestionsList(List<String> list){
		String ans ="";
		int score = 0;
		int iAns = 0;		
		String regex = "\\d+";
		
		for (int i=0; i< list.size(); i++){
			ans = list.get(i);
			//remove those answers with "-" or not digit
			if (!Utils.isNullOrEmpty(ans) && !ans.contains("-") && (ans.matches(regex)))
			{
				iAns = Integer.parseInt(ans.trim());
				score = score + iAns;
			}			
		}
		return score;
	}
	
	public static int getScoreForRAPA(List<String> qList){
		int scores = 0;		
		Integer[] answerArray = new Integer[6];
		//set answer into an array
		for(int i = 0; i < qList.size() - 2; i++)
			answerArray[i] = Integer.valueOf(qList.get(i));	
		
		Integer[] otherAnswers = removeFirstElementInArray(answerArray);
		
		if ((answerArray[0].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			scores = 2;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[1].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			scores = 3;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[2].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			scores = 4;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[3].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			scores = 5;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[4].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			scores = 6;		
	
		if (answerArray[5].intValue() == 1) 
			scores = 7;
		
		
		return scores;
	}
	
	
	private static Integer[] removeFirstElementInArray(Integer[] array){		
		final Integer[] EMPTY_Integer_ARRAY = new Integer[0];
		//convert array to ArrayList
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(array));
		//remove first element from list
		list.remove(0);	
		
		return list.toArray(EMPTY_Integer_ARRAY);
	}
	
	private static boolean isAllOtherAnswersNo(Integer[] answers){
		boolean allNo = true;
		for(Integer a: answers){
			if (a.equals(1))
			{
				allNo = false;
				break;
			}
		}
		
		return allNo;
	}
	
}
