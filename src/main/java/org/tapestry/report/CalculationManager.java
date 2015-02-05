package org.tapestry.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tapestry.utils.Utils;

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
	
	public static int getGeneralHealthyScaleScore(List<String> qList){
		int score = 0;
	
		int iAnswer = 0;
		
		for (int i = 0; i < qList.size(); i++)
		{
			iAnswer = Integer.valueOf(qList.get(i).toString());
			if (i == 0 || i == 1 || i ==3 || i ==4)
				score = score + countPoints(iAnswer,new int[]{0,1,2}, 3);
			else if (i == 2)
				score = score + countPoints(iAnswer, new int[]{0,0,0,1,2}, 5);
			else if (i > 4 && iAnswer < 10)
				score = score + countPoints(iAnswer, new int[]{0,1},2);
			else if (i == 10)
				score = score + countPoints(iAnswer, new int[]{0,1,2,2,2}, 5);
		}		
		
		return score;		
	}
	
	static int countPoints(int a, int[] b, int n){
		int result = 0;
		
		if (n == 2)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;								
				default:break;
			}	
		} 
		else if (n == 3)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;
				case 3: result = result + b[2];
						break;				
				default:break;
			}	
		}
		else if (n == 5)
		{
			switch (a) {
			case 1: result = result + b[0];
					break;
			case 2: result = result + b[1];
					break;
			case 3: result = result + b[2];
					break;	
			case 4: result = result + b[3];
					break;
			case 5: result = result + b[4];
					break;
			default:break;
		}		
		}
		return result;
	}
	

	
	public static int getNutritionScore(List<String> qList){
		int score = 0;
		//first answer
		int answer = Integer.valueOf(qList.get(0));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: break;
			case 3: break;
			case 4: score = score + 1;
					break;
			case 5: score = score + 2;
					break;
			case 6: break;
			case 7: score = score + 1;
					break;
			case 8: score = score + 2;
					break;			
			default:break;
		}
			
		//second answer		
		if (!"3".equals(qList.get(1)))
			score = score + 4;
		
		//third answer
		if(!"2".equals(qList.get(2)))			
			
		//4	
		answer = Integer.valueOf(qList.get(3));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 2;
					break;
			case 3: score = score + 1;
					break;
			case 4: break;				
			default:break;
		}
		//5
		answer = Integer.valueOf(qList.get(4));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 2;
					break;
			case 3: break;
						
			default:break;
		}
		//6
		answer = Integer.valueOf(qList.get(5));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 2;
					break;
			case 4: break;				
			default:break;
		}
		//7
		answer = Integer.valueOf(qList.get(6));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 2;
					break;
			case 4: score = score + 1;
					break;
			case 5: break;				
			default:break;
		}
		//8
		answer = Integer.valueOf(qList.get(7));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 1;
					break;
			case 4: break;
					
			default:break;
		}
		//9
		answer = Integer.valueOf(qList.get(8));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 2;
					break;
			case 4: score = score + 1;
					break;
			case 5: break;				
			default:break;
		}
		//10
		answer = Integer.valueOf(qList.get(9));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 2;
					break;
			case 4: score = score + 1;
					break;
			case 5: break;				
			default:break;
		}
		//11
		answer = Integer.valueOf(qList.get(10));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 1;
					break;
			case 4: break;					
			default:break;
		}
		//12
		answer = Integer.valueOf(qList.get(11));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 3;
					break;
			case 3: score = score + 2;
					break;
			case 4: break;				
			default:break;
		}
		//13
		answer = Integer.valueOf(qList.get(12));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 2;
					break;
			case 3: break;
						
			default:break;
		}
		//14
		answer = Integer.valueOf(qList.get(13));
		switch (answer) {
			case 1: break;
			case 2: score = score + 2;
					break;
			case 3: score = score + 3;
					break;
			case 4: score = score + 4;
					break;				
			default:break;
		}
		//16
		answer = Integer.valueOf(qList.get(15));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 2;
					break;
			case 3: break;
			case 4: score = score + 4;
					break;
			case 5: break;				
			default:break;
		}
		//17
		answer = Integer.valueOf(qList.get(16));
		switch (answer) {
			case 1: score = score + 4;
					break;
			case 2: score = score + 2;
					break;
			case 3: score = score + 1;
					break;
			case 4: break;				
			default:break;
		}		
		return score;
	}
	
	public static int getAScoreForRAPA(List<String> qList){
		int score = 0;		
		Integer[] answerArray = new Integer[6];
		//set answer into an array from first 6 questions
		for(int i = 0; i < 6; i++)
			answerArray[i] = Integer.valueOf(qList.get(i));	
		
		Integer[] otherAnswers = removeFirstElementInArray(answerArray);
		
		if ((answerArray[0].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			score = 2;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[1].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			score = 3;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[2].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			score = 4;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[3].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			score = 5;
		
		otherAnswers = removeFirstElementInArray(otherAnswers);
		
		if ((answerArray[4].intValue() == 1) && isAllOtherAnswersNo(otherAnswers))
			score = 6;		
	
		if (answerArray[5].intValue() == 1) 
			score = 7;		
		
		return score;
	}
	
	public static int getSFScoreForRAPA(List<String> qList){
		int score = 0;				
		int size = qList.size();
		
		if (("1".equals(qList.get(size - 2))) && ("2".equals(qList.get(size - 1))))
			score = 1;
		else if (("1".equals(qList.get(size - 2))) && ("1".equals(qList.get(size - 1))))
			score = 3;
		else if (("2".equals(qList.get(size - 2))) && ("1".equals(qList.get(size - 1))))
			score = 2;
		else if (("2".equals(qList.get(size - 2))) && ("2".equals(qList.get(size - 1))))
			score = 0;
		
		return score;
	}	
	
	public static ScoresInReport getMobilityScore(Map<String, String> qMap, ScoresInReport score){		
		String key;
		String value;
		String a2aValue = "";
		String a2bValue = ""; 
		String a3aValue = "";
		String a3bValue ="";
		String a4aValue = ""; 
		String a4bValue = "";
		StringBuffer sb = new StringBuffer();
		
		Iterator iterator = qMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			key = mapEntry.getKey().toString();
			value = mapEntry.getValue().toString();			
			
			if ("a2a".equalsIgnoreCase(key))
				a2aValue = value;
			
			if ("a2b".equalsIgnoreCase(key))
				a2bValue = value;
			
			if ("a3a".equalsIgnoreCase(key))
				a3aValue = value;
			
			if ("a3b".equalsIgnoreCase(key))
				a3bValue = value;
			
			if ("a4a".equalsIgnoreCase(key))
				a4aValue = value;
			
			if ("a4b".equalsIgnoreCase(key))
				a4bValue = value;		
		}
		
		//score for walking in 2.0 km
		if (a2aValue.equals("1"))
		{
			sb.append("no limition ");
			sb = getModificationInfo(sb, a2bValue);			
		}
		else if (a2aValue.equals("2"))
		{
			sb.append("preclinical limition ");
			sb = getModificationInfo(sb, a2bValue);
		}
		else if (a2aValue.equals("3"))
		{
			sb.append("minor manifest limition ");
			sb = getModificationInfo(sb, a2bValue);
		}
		else if (a2aValue.equals("4"))
		{
			sb.append("major manifest limition ");
			sb = getModificationInfo(sb, a2bValue);
		} 
		
		if (!Utils.isNullOrEmpty(sb.toString()))
			score.setMobilityWalking2(sb.toString());
		//score for walking in 0.5 km
		sb = new StringBuffer();
		if (a3aValue.equals("1"))
		{
			sb.append("no limition ");
			sb = getModificationInfo(sb, a3bValue);	
		}
		else if (a3aValue.equals("2"))
		{
			sb.append("preclinical limition ");
			sb = getModificationInfo(sb, a3bValue);
		}
		else if (a3aValue.equals("3"))
		{
			sb.append("minor manifest limition ");
			sb = getModificationInfo(sb, a3bValue);
		}
		else if (a3aValue.equals("4"))
		{
			sb.append("major manifest limition ");
			sb = getModificationInfo(sb, a3bValue);
		} 
		
		if (!Utils.isNullOrEmpty(sb.toString()))
			score.setMobilityWalkingHalf(sb.toString());
		
		//score for climbing one flight of starirs
		sb = new StringBuffer();
		if (a4aValue.equals("1"))
		{
			sb.append("no limition ");
			sb = getModificationInfo(sb, a4bValue);	
		}
		else if (a4aValue.equals("2"))
		{
			sb.append("preclinical limition ");
			sb = getModificationInfo(sb, a4bValue);
		}
		else if (a4aValue.equals("3"))
		{
			sb.append("minor manifest limition ");
			sb = getModificationInfo(sb, a4bValue);
		}
		else if (a4aValue.equals("4"))
		{
			sb.append("major manifest limition ");
			sb = getModificationInfo(sb, a4bValue);
		} 
		
		if (!Utils.isNullOrEmpty(sb.toString()))
			score.setMobilityClimbing(sb.toString());
		
		return score;
	}
	
	private static StringBuffer getModificationInfo(StringBuffer sb, String bAnswer){
		if("1".equals(bAnswer))
			sb.append("not using any modifications");
		else 
			sb.append("using modifications");
		
		return sb;
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
