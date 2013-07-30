<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
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
	</style>
</head>
	
<body>	
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        				<span class="icon-bar"></span>
        				<span class="icon-bar"></span>
       			 		<span class="icon-bar"></span>
     				</a>
     					
     				<a class="brand" href="<c:url value="/"/>">Home</a>
     				<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="<c:url value="/manage_users"/>">Manage Volunteers</a></li>
							<li><a href="<c:url value="/manage_patients"/>">Manage Patients</a></li>
							<li><a href="<c:url value="/manage_survey_templates"/>">Manage Survey Templates</a></li>
							<li><a href="<c:url value="/manage_surveys"/>">Manage Surveys</a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span6">
				<h2>Tapestry Admin</h2>
				<p>Click a link in the navbar to begin</p>
				<p>You can:</p>
				<ul>
					<li>Add a patient</li>
					<li>Add a caretaker</li>
					<li>Manage installed surveys</li>
					<li>Send messages to volunteers</li>
				</ul>
			</div>
			<div class="span6">
				<h3>Message volunteer</h3>
				<c:if test="${not empty success}">
					<div class="alert alert-info" style="width:170px;">
						<p>Message sent</p>
					</div>
				</c:if>
				<form id="messageVolunteer" method="post" action="<c:url value="/send_message"/>">
					<label>Subject:</label>
					<input type="text" name="msgSubject" required/>
					<label>Send to:</label>
					<select name="recipient" form="messageVolunteer">
						<c:forEach items="${volunteers}" var="v">
						<option value="${v.userID}">${v.name}</option>
						</c:forEach>
					</select><br />
					<label>Message:</label>
					<textarea name="msgBody"></textarea><br />
					<input type="submit" class="btn btn-primary" value="Send" />
				</form>
			</div>
		</div>
	</div>
</body>
</html>
