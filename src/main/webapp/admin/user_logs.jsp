<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
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
							<li><a href="<c:url value="/manage_survey_templates"/>">Manage Surveys Templates</a></li>
							<li><a href="<c:url value="/manage_surveys"/>">Manage Surveys</a></li>
							<li><a href="<c:url value="/user_logs"/>">User Logs</a><li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div class="row-fluid">
			<h2>Activity Log</h2>
			<div class="row-fluid">
				<form action="user_logs" method="post">
					<fieldset>
						<legend>Search Logs</legend>
						<label>Name:</label>
						<input type="text" name="name" /><br />
						<input class="btn btn-primary" type="submit" value="Search" />
					</fieldset>
				</form>
			</div>
			<table class="table">
				<tr>
					<th>Volunteer Name</th>
					<th>Activity</th>
					<th>Date</th>
				</tr>
				<c:forEach items="${activities}" var="a">
					<tr>
						<td>${a.volunteer}</td>
						<td>${a.description}</td>
						<td>${a.date}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>