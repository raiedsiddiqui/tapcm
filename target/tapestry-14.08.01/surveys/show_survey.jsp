<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/custom.css">
</head>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.tapestry.surveys.DoSurveyAction"%>
<%@page import="org.tapestry.surveys.SurveyFactory"%>
<%@page import="org.tapestry.surveys.TapestrySurveyMap"%>
<%@page import="org.tapestry.surveys.TapestryPHRSurvey"%>
<%@page import="org.survey_component.data.*"%>
<%@page import="org.survey_component.data.answer.SurveyAnswerString"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.math.*"%>
			
<%
	String questionId = request.getAttribute("questionid").toString();
	//find the survey
	String documentId = request.getAttribute("resultid").toString();
			
//	SurveyMap surveys = DoSurveyAction.getSurveyMap(request);
//	PHRSurvey survey = (PHRSurvey) request.getAttribute("survey");
	TapestrySurveyMap surveys = DoSurveyAction.getSurveyMap(request);
	TapestryPHRSurvey survey = (TapestryPHRSurvey) request.getAttribute("survey");	
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
	<title>Survey Mode</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no"></meta>

	<%@include file="/volunteer/volunteer_head.jsp" %>
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		
		/*input[type="radio"], input[type="checkbox"]{
			height:50px;
			width:50px;
		}*/
		
		input[type="button"], input[type="submit"]{
			height:40px;
			font-size:1em;
		}
		input.answerChoice {
			padding-bottom: 20px;
			margin-bottom: 30px;
		}

		#qtext {
			text-align: left;
		}

		#surveynext {
			
			background-color: #4A307A;
			color:white;
			width:140px;
			font-size: 30px;
			/* padding: 10px 10px 10px 10px; */
			height:50px;
			text-transform: uppercase;;
			font-family: 'open sans';
			font-weight: 700;
			box-shadow: 0px white;
			border: 2px solid #4A307A;
			float:right;
		}

		#surveynext:hover {
			background-color: #6BB040;
			border: 2px solid #4A307A;
		}

		#surveyback {
			background-color: #4A307A;
			color:white;
			width:140px;
			font-size: 30px;
			/* padding: 10px 10px 10px 10px; */
			height:50px;
			text-transform: uppercase;;
			font-family: 'open sans';
			font-weight: 700;
			box-shadow: 0px white;
			border: 2px solid #4A307A;
		}

		#surveyback:hover {
			background-color: #6BB040;
			border: 2px solid #4A307A;
		}

		div.alert.alert-success {
			font-size: 14px;
			letter-spacing: 1px;
		}
		#headerholder {
			  background-color: white;
			  border-bottom: 7px solid #BF1F82;
			  padding-bottom: 10px; 
	}

	.progress {
		height:40px;
	}
	.progress-bar {
		line-height: 40px;
		font-size: 20px;
	}
	</style>
	
	<script type="text/javascript">
			
			function saveObserverNotes(){
				document.forms['surveyQuestion'].observernote.value = document.getElementById("observerNote").value;			
			}

			$('#qtext').addClass('animated bounceOutLeft');
		</script>
	
	
</head>

<body>
 
<div id="headerholder">	
	<div class="row">
      <div class="col-md-3 logoheight">
<!--         remove .tpurple -->        
      <img id="logo" src="<c:url value="/resources/images/tapcm.png"/>" />
      </div>

    <div class="col-md-9" style="height:63px;">
		<input id="saveclose" type="button" value="<%if (!survey.isComplete()) {%>Save & <%}%>Exit" onclick="document.location='<c:url value="/save_survey/"/><%=documentId%>?survey_completed=<%=completed%>'">
    </div>
 </div> 
</div>
 <!-- <img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
       <img id="logofam" src="<c:url value="/resources/images/fammed.png"/>" />
 <img id="logofhs" src="<c:url value="/resources/images/fhs.png"/>" />
        <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>-->    
<!--   <div class="navbar">
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
		</div>	 -->	
</div>
	<div class="content">
	<% if (!message.equals("")) { %>
    <div id="survendmessage" class="alert alert-success notificationMessage"><%=message%>
    </div>
   <div>
   	<input id="saveclose2" style="float:left;font-size:30px;height:70px;color:white;background-color:#BF1F82;" type="button" value="<%if (!survey.isComplete()) {%>SUBMIT<%}%>" onclick="document.location='<c:url value="/save_survey/"/><%=documentId%>?survey_completed=<%=completed%>'">
   </div>
    <script type="text/javascript">
		window.onload=function changeLink() {
			var str = document.getElementById("survendmessage").textContent;
	    	var n = str.indexOf("SUBMIT");
	    	if (n > 0) {
	    		document.getElementById('surveybackground').style.display="none";
	    		document.getElementById('qtext').style.display="none";
	    		document.getElementById('saveclose').style.display="none";
	    	}

	    	else {
	    		document.getElementById('saveclose2').style.display="none";
	    	}
		}
    </script>
    <% } %>	

	</div>
		<div class="row-fluid">
			<div class="squestion">
    			<div id="squestion2" > <!-- style="float: left;" -->
        			<!-- Look at the div with class="questionWidth" at the bottom to adjust question min-width) -->
        			
        			<form action="/tapcm/show_survey/<%=documentId%>" name="surveyQuestion" id="surveyform">
            			<input type="hidden" name="questionid" value="<%=question.getId()%>">
            			<input type="hidden" name="direction" value="forward">

            			<!-- <div class="row"> 
            				<div class="col-md-6 pull-left">
            					<b>${surveyTitle}</b><br/>
									${description}
            				</div>-->
            		<!-- 		<div class="row">
            				<div class="col-md-6 pull-right">
            					<input id="saveclose" type="button" value="<%if (!survey.isComplete()) {%>Save and <%}%>Exit" onclick="document.location='<c:url value="/save_survey/"/><%=documentId%>?survey_completed=<%=completed%>'">
            				</div>
            			</div>  -->
            			
            			<input type="hidden" name="documentid" value="<%=documentId%>">
            			<!-- <input type="hidden" name="observernote" value="" > -->
            			
            		
					
            		
            		<div class="row">
            			<div id="qtext" class="col-md-12 animated fadeIn">
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
								
								if (questionText.contains("/observernote"))
								{
									int ind = questionText.indexOf("/observernote/");
									if (ind > 0)
										questionText = questionText.substring(0, ind);
								}
							%>
							
	            			<%=questionText%>
            			</div>
            		</div>

            		<br/>
            		<div id="surveybackground" class="animated fadeIn">
                		
                		<!-- Answer:  -->
                
                		<%
	           	 			String answer="";
	           	 			if (question.getAnswer()!=null) answer=StringUtils.trimToEmpty(question.getAnswer().toString());	           	 			
	           	 			
		           	 		String separator = "/observernote/";
		           			String observernotes = "";
		           			int ind = answer.indexOf(separator);
			           		int l = separator.length();
			           		
			           		if (ind != -1)
			           		{
			           			observernotes = answer.substring(ind + l);
			           			answer = answer.substring(0, ind);
			           		}

                			if (question.getQuestionType().equals(SurveyQuestion.ANSWER_NUMBER) ||
                			question.getQuestionType().equals(SurveyQuestion.ANSWER_DECIMAL)) {                
                		%> 
                			<input type="number" style="text-align: center;" id="answer" name="answer" value="<%=answer%>"<%if (survey.isComplete()) {%> readonly<%}%>> (Number)
                
                		<%} else if (question.getQuestionType().equals(SurveyQuestion.ANSWER_TEXT)) {%>
                			<textarea name="answer" id="answer" onload="this.focus()" <%if (survey.isComplete()) {%> readonly<%}%>><%=answer%></textarea>

                		<%} else if (question.getQuestionType().equals(SurveyQuestion.ANSWER_HIDDEN)) {%>
                			<input type="hidden" size="40" name="answer" id="answer" onload="this.focus()" value="<%=answer%>"<%if (survey.isComplete()) {%> readonly<%}%>>

                		<%} else if (question.getQuestionType().equals(SurveyQuestion.ANSWER_CHECK)) { %>
                  		<ul>
                  			<div id="surveyquestion" class="row">
                   					<div class="col-md-6">
                  			<%
                  				for (SurveyAnswerChoice choice: question.getChoices()) {
                  				boolean selected = question.getAnswers().contains(new SurveyAnswerString(choice.getAnswerValue()));
                   			%>
                   				
                        				<li><input type="checkbox" name="answer" class="answerChoice" value="<%=choice.getAnswerValue()%>" <%if (selected) out.print("checked");%><%if (survey.isComplete()) {%> readonly<%}%>> <%=choice.getAnswerText()%></li>
                        		
                  			
                  			<%}%>
                  				</div>
                        	</div>
                  		</ul>
                  
                		<%} else if (question.isQuestionType(SurveyQuestion.ANSWER_SELECT)) {%>
                  			<br/>
                  			<div id="surveyquestion" class="row">
                  			<%
                  				for (SurveyAnswerChoice choice: question.getChoices()) {
                  				boolean selected = question.getAnswers().contains(new SurveyAnswerString(choice.getAnswerValue()));
                   			%>
                   				
                   					<div class="col-md-12">
                   						<input type="radio" name="answer" class="answerChoice" value="<%=choice.getAnswerValue()%>" <%if (selected) out.print("checked");%><%if (survey.isComplete()) {%> readonly<%}%>> <%=choice.getAnswerText()%><br/>
                  					</div>
                  		<%}%>
                		</div>
                		<%} else {%>
                    		<input type="hidden" name="answer" value="-">
                		<%}%>
                	<br/>

                	<!-- 
                	<input id="saveclose" type="button" value="<%if (!survey.isComplete()) {%>Save and <%}%>Close" onclick="document.location='<c:url value="/save_survey/"/><%=documentId%>?survey_completed=<%=completed%>'">
                	-->	
                	<div id="answer-buttons">
                		<div class="row animated fadeIn">
                			<div class="col-md-6">
	                			<input id="surveyback" class="tleft btn" type="button" value="Back" onclick="document.forms['surveyQuestion'].direction.value='backward'; document.forms['surveyQuestion'].submit();">
	                		<div id="eq5dcopyright" style="float:left"></div>
	                		</div>
	                		

	                	
<!-- 	                		<div class="col-md-4">
		                		<c:if test="${not hideObservernote}">
		                			<a href="#modalObserverNotes" data-toggle="modal" id="observernote">Observer Notes</a>
		                		</c:if>
	                		</div> -->


	                		<div class="col-md-6"> 
	                			<input id="surveynext" type="submit" value="Next">
	                		</div>
                		</div>
                		
                	</div>
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
		    
		    <div id="observer_note" >
		    <div class="modal fade" id="modalObserverNotes" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			  <div class="modal-dialog">
	  			  <div class="modal-content">
						<div class="modal-header">
    						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
						<h3 class="pull-left">Observer Note</h3>
				 </div>
				 <div class="modal-body">
					<div class="col-md-12">
						
						<textarea class="form-control" id="observerNote"><%=observernotes%></textarea><br />		
					</div>	
				</div>				
				<div class="modal-footer">
					<input type="button"  class="btn lgbtn" data-dismiss="modal" value="Save"  onclick="saveObserverNotes();" />
  				</div>
			</div>
		</div>
	</div>
		    
		    </div>
		</div>
	</div>


</body>
</html>
