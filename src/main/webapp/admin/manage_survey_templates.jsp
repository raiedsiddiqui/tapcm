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
	
	<script type="text/javascript">
		function showAddSurvey(){
			document.getElementById("addSurveyDiv").style.display="block";
		}
	</script>
	
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
							<li><a href="<c:url value="/manage_survey_templates"/>">Manage Surveys</a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		
		
		<div class="row-fluid">
			<h2>Survey Templates</h2>
			<table class="table">
				<tr>
					<th>Title</th>
					<th>Type</th>
					<th>Remove</th>
				</tr>
				<c:forEach items="${survey_templates}" var="st">
				<tr>
					<td>${st.title}</td>
					<td>${st.type}</td>
					<td><a href="<c:url value="/delete_survey_template/${st.surveyID}"/>" class="btn btn-danger">Remove</a></td>
				</tr>
				</c:forEach>
			</table>
			<a class="btn btn-primary" onClick="showAddSurvey()">Add new</a>
		</div>

		<div class="row-fluid" id="addSurveyDiv" style="display:none";>
			<form action="upload_survey_template" method="post" enctype="multipart/form-data">
				<fieldset>
					<legend>Add new survey</legend>
					<label>Title:</label>
					<input type="text" name="title"/>
					<label>Type:</label>
					<select name="type">
						<option value="MUMPS">MUMPS</option>
					</select>
					<label>File:</label>
					<input type="hidden" name="MAX_FILE_SIZE" value="2000000">
					<input type="file" name="file"/>
					<input class="btn btn-primary" type="submit" value="Upload" />
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>