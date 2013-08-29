package org.tapestry.surveys;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;
import java.io.StringReader;
import java.util.Scanner;

public class ResultParser {

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public static LinkedHashMap<String, String> getResults(String surveyData) {
        LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
        try{
            Document doc = loadXMLFromString(surveyData);
            doc.getDocumentElement().normalize();
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            String title = titleNode.getTextContent().trim();
            results.put("title", title);
            Node dateNode = doc.getElementsByTagName("IssueDate").item(0);
            String date = dateNode.getTextContent().trim();
            results.put("date", date);
            NodeList questions = doc.getElementsByTagName("IndivoSurveyQuestion");
            for (int i = 0; i < questions.getLength(); i++){
                Element question = (Element) questions.item(i);
                String questionString = "";
                String questionAnswerString = "";
                NodeList questionIDList = question.getElementsByTagName("QuestionId");
                if (questionIDList.getLength() > 0){
                    Element questionID = (Element) questionIDList.item(0);
                    if (questionID.getTextContent().trim().equals("surveyHash"))
                        continue;
                    questionString += questionID.getTextContent().trim();
                }
                NodeList questionTextList = question.getElementsByTagName("QuestionText");
                if (questionTextList.getLength() > 0){
                    Element questionText = (Element) questionTextList.item(0);
                    //questionAnswerString += questionText.getTextContent().trim();
                }
                NodeList questionAnswerList = question.getElementsByTagName("QuestionAnswer");
                if (questionAnswerList.getLength() > 0){
                	for (int j = 0; j < questionAnswerList.getLength(); j++){
                		Element questionAnswer = (Element) questionAnswerList.item(j);
                		questionAnswerString += questionAnswer.getTextContent().trim();
                		if (questionAnswer.getNextSibling() != null)
                			questionAnswerString += ", ";
                	}
                }
                results.put(questionString, questionAnswerString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * Concatenate a LinkedHashMap of results with the specified characters
     * @param results The results list returned by getResults()
     * @param join The character(s) to use to separate the question ID from the answer
     * @return The results
     */
    private static String joinResults(LinkedHashMap<String, String> results, String join){
		String ret = "";
		for (Map.Entry<String, String> r : results.entrySet()){
			ret += r.getKey() + join + r.getValue() + "\n";
		}
		return ret;
	}
    
    /**
     * Converts a result set to a series of comma-separated values that
     * can then be loaded into a spreadsheet or something.
     * @param results The LinkedHashMap returned by getResults()
     * @return The results as comma-separated values
     */
    public static String resultsAsCSV(LinkedHashMap<String, String> results){
		return joinResults(results, ",");
	}
	
	/**
	 * Converts a result set to a relatively human-readable list of values
	 * @param results The LinkedHashMap returned by getResults()
	 * @return The results as Question: Answer pairs
	 */
	public static String resultsAsText(LinkedHashMap<String, String> results){
		return joinResults(results, ": ");
	}
	
	public static String resultsAsHTML(LinkedHashMap<String, String> results){
		String s = joinResults(results, ": ");
		return s.replace("\n", "<br/>");
	}

}