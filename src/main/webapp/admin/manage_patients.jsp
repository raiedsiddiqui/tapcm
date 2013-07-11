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
		function showAddPatient(){
			document.getElementById("addPatientDiv").style.display="block";
		}
	</script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>
	
<body>	
  <img src="${pageContext.request.contextPath}/resources/images/logo.png" />
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					
     					<a class="brand" href="${pageContext.request.contextPath}/">Home</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="${pageContext.request.contextPath}/manage_users">Manage Volunteers</a></li>
							<li><a href="${pageContext.request.contextPath}/manage_patients">Manage Patients</a></li>
							<li><a href="#">Manage Surveys</a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div class="row-fluid">
			<h2>Patients</h2>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Volunteer</th>
					<th></th>
				</tr>
                <c:forEach items="${patients}" var="p">
                <tr>
                    <td>${p.firstName} ${p.lastName}</td>
                    <td>${p.volunteer}</td>
                    <td><a href="${pageContext.request.contextPath}/remove_patient/${p.patientId}" class="btn btn-danger">Remove</a></td>
                </tr>
                </c:forEach>
			</table>
			<a class="btn btn-primary" onClick="showAddPatient()">Add new</a>
		</div>
		<div class="row-fluid" id="addPatientDiv" style="display:none";>
			<form id="newPatient" method="post" action="${pageContext.request.contextPath}/add_patient">
				<fieldset>
					<legend>Add new patient</legend>
					<label>First Name:</label>
					<input type="text" name="firstname"/>
					<label>Last Name:</label>
					<input type="text" name="lastname"/>
					<label>Volunteer</label>
					<select name="volunteer" form="newPatient">
						<c:forEach items="${volunteers}" var="v">
						<option value="${v.name}">${v.name}</option>
						</c:forEach>
					</select><br />
					<input class="btn btn-primary" type="submit" value="Add" />
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>
