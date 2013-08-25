<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.tapestry.surveys.DoSurveyAction"%>
<%@page import="org.tapestry.surveys.SurveyFactory"%>
<%@page import="org.survey_component.data.*"%>
<%@page import="org.survey_component.data.answer.SurveyAnswerString"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.math.*"%>
			
<%
	String questionId = request.getAttribute("questionid").toString();
	//find the survey
	String documentId = request.getAttribute("resultid").toString();
			
	SurveyMap surveys = DoSurveyAction.getSurveyMap(request);
	PHRSurvey survey = (PHRSurvey) request.getAttribute("survey");
			
	//get question
	SurveyQuestion question = survey.getQuestionById(questionId);
	
	boolean completed;
	Object completedObject = request.getAttribute("survey_completed");
	if (completedObject == null)
		completed = false;
	else
		completed = true;
	
			
	String message;
	Object messageObject = request.getAttribute("message");
	if (messageObject == null) 
		message = "";
	else 
		message = messageObject.toString();
%>
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}

		#surveyQuestion{
			font-size:18px;
		}
		
		input[type="radio"], input[type="checkbox"]{
			height:50px;
			width:50px;
		}
		
		input[type="button"], input[type="submit"]{
			height:40px;
			font-size:18px;
		}
		
	</style>
</head>

<body>
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<div class="navbar">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        				<span class="icon-bar"></span>
        				<span class="icon-bar"></span>
       			 		<span class="icon-bar"></span>
     				</a>
     					
     				<a class="brand" href="<c:url value="/"/>">Home</a>
				</div>
			</div>
		</div>
		
		<div class="row-fluid">
			<div>
    			<div style="float: left;">
        			<!-- Look at the div with class="questionWidth" at the bottom to adjust question min-width) -->
        			<form action="/tapestry/show_survey/<%=documentId%>" name="surveyQuestion" id="surveyQuestion">
            			<input type="hidden" name="questionid" value="<%=question.getId()%>">
            			<input type="hidden" name="direction" value="forward">
            			<input type="hidden" name="documentid" value="<%=documentId%>">
            			
            			Question: 
            			
            			<div style="padding: 3px;border-top: 1px solid #cdcdcd;border-bottom: 1px solid #cdcdcd;font-family: Verdana, Arial, Helvetica, sans-serif;">
           				<%
           					String questionText = question.getQuestionTextRenderKeys(survey);
            				//put enterspaces into the text, except if the <script tag is unclosed (allows javascript)
            				int index = 0;
            				while ((index = questionText.indexOf("\n", index)) != -1) {
                				String questionTextBefore = questionText.substring(0, index);
                				String questionTextAfter = questionText.substring(index+1);
                				int scriptOpenBefore = questionTextBefore.lastIndexOf("<"+"%--");
                				int scriptCloseBefore = questionTextBefore.lastIndexOf("--%" + ">");
                				if (scriptOpenBefore == -1 || (scriptOpenBefore < scriptCloseBefore)) {
                    				questionText = questionTextBefore + "<br>" + questionTextAfter;
                				}
                				index++;
            				}
							questionText = questionText.replaceAll("<"+"%--", "");
							questionText = questionText.replaceAll("--%" + ">", "");
						%>
            			<%=questionText%>
            		</div>
            		<br/>
            		<div style="padding: 3px;background-color: #e1ebef;font-family: Verdana, Arial, Helvetica, sans-serif;">
                		<div class="notificationMessage"><%=message%></div>
                		Answer: 
                
                		<%
	           	 			String answer="";
	           	 			if (question.getAnswer()!=null) answer=StringUtils.trimToEmpty(question.getAnswer().toString());

                			if (question.getQuestionType().equals(SurveyQuestion.ANSWER_NUMBER) ||
                			question.getQuestionType().equals(SurveyQuestion.ANSWER_DECIMAL)) {                
                		%>
                			<input type="text" size="5" style="text-align: center;" id="answer" name="answer" value="<%=answer%>"<%if (survey.isComplete()) {%> readonly<%}%>> (Number)
                
                		<%} else if (question.getQuestionType().equals(SurveyQuestion.ANSWER_TEXT)) {%>
                			<input type="text" size="40" name="answer" id="answer" onload="this.focus()" value="<%=answer%>"<%if (survey.isComplete()) {%> readonly<%}%>> (Text)

                		<%} else if (question.getQuestionType().equals(SurveyQuestion.ANSWER_HIDDEN)) {%>
                			<input type="hidden" size="40" name="answer" id="answer" onload="this.focus()" value="<%=answer%>"<%if (survey.isComplete()) {%> readonly<%}%>>

                		<%} else if (question.getQuestionType().equals(SurveyQuestion.ANSWER_CHECK)) { %>
                  		<ul>
                  			<%
                  				for (SurveyAnswerChoice choice: question.getChoices()) {
                  				boolean selected = question.getAnswers().contains(new SurveyAnswerString(choice.getAnswerValue()));
                   			%>
                        		<li><input type="checkbox" name="answer" class="answerChoice" value="<%=choice.getAnswerValue()%>" <%if (selected) out.print("checked");%><%if (survey.isComplete()) {%> readonly<%}%>> <%=choice.getAnswerText()%></li>
                  			<%}%>
                  		</ul>
                  
                		<%} else if (question.isQuestionType(SurveyQuestion.ANSWER_SELECT)) {%>
                  			<br/>
                  			<%
                  				for (SurveyAnswerChoice choice: question.getChoices()) {
                  				boolean selected = question.getAnswers().contains(new SurveyAnswerString(choice.getAnswerValue()));
                   			%>
                   				<input type="radio" name="answer" class="answerChoice" value="<%=choice.getAnswerValue()%>" <%if (selected) out.print("checked");%><%if (survey.isComplete()) {%> readonly<%}%>> <%=choice.getAnswerText()%><br/>
                  		<%}%>
                
                		<%} else {%>
                    		<input type="hidden" name="answer" value="-">
                		<%}%>
                	<br/>
                	<input type="button" value="<%if (!survey.isComplete()) {%>Save and <%}%>Close" onclick="document.location='<c:url value="/save_survey/"/><%=documentId%>?survey_completed=<%=completed%>'">
                	<input type="button" value="Back" onclick="document.forms['surveyQuestion'].direction.value='backward'; document.forms['surveyQuestion'].submit();"> 
                	<input type="submit" value="Next">

		        </form>
		        <script type="text/javascript" language="JavaScript">
		            answerObj = document.getElementById("answer");
		            if (answerObj != undefined) {
		                answerObj.focus();
		            }
		        </script>
		        <!--Need the following div b/c IE is stupid (compensating for min-width absense)-->
		        <div class="questionWidth"></div>
		    </div>
		</div>
	</div>
</body>
</html>
