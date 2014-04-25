<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Edit Patient</h2>
		  	<form id="editPatient" method="post" action="<c:url value="/submit_edit_patient/${patient.patientID}"/>">

				<div class="row form-group">
  					<div class="col-md-6">
						<label>First Name:</label>
						<input type="text" name="firstname" class="form-control" value="${patient.firstName}" required/>
					</div>
					<div class="col-md-6">
						<label>Last Name:</label>
						<input type="text" name="lastname" class="form-control" value="${patient.lastName}" required/>
					</div>
					<div class="col-md-6">
						<label>Preferred Name:</label>
						<input type="text" name="preferredname" class="form-control" value="${patient.preferredName}"/>
					</div>
				</div>	

				<div class="row form-group">	
					<div class="col-md-6">
						<label>Volunteer</label>
						<select name="volunteer" form="newPatient" class="form-control">
							<c:forEach items="${volunteers}" var="v">
								<option value="${v.volunteerId}" <c:if test="${v.displayName eq patient.volunteerName}">selected</c:if>>${v.displayName}</option>
							</c:forEach>
						</select><br />
					</div>
					<div class="col-md-6">
						<label>Gender</label>
						<select name="gender" form="newPatient" class="form-control">
							<option value="M" <c:if test="${patient.gender eq 'M'}">selected</c:if>>Male</option>	
							<option value="F" <c:if test="${patient.gender eq 'F'}">selected</c:if>>Female</option>
							<option value="O" <c:if test="${patient.gender eq 'O'}">selected</c:if>>Other</option>
						</select>
					</div>
				</div>

				<div class="row form-group">	
					<div class="col-md-10">
						<label>Notes</label>
						<textarea name="notes" class="form-control">${patient.notes}s</textarea>
					</div>
				</div>	


				<!-- <label>First Name:</label>
				<input type="text" name="firstname" value="${patient.firstName}" required/>
				<label>Last Name:</label>
				<input type="text" name="lastname" value="${patient.lastName}" required/>
				<label>Preferred Name:</label>
				<input type="text" name="preferredname" value="${patient.preferredName}"/>
				<label>Volunteer</label>
				<select name="volunteer" form="editPatient">
					<c:forEach items="${volunteers}" var="v">
					<option value="${v.volunteerId}" <c:if test="${v.displayName eq patient.volunteerName}">selected</c:if>>${v.displayName}</option>
					</c:forEach>
				</select><br />
				<label>Gender</label>
				<select name="gender" form="editPatient">
					<option value="M" <c:if test="${patient.gender eq 'M'}">selected</c:if>>Male</option>	
					<option value="F" <c:if test="${patient.gender eq 'F'}">selected</c:if>>Female</option>
					<option value="O" <c:if test="${patient.gender eq 'O'}">selected</c:if>>Other</option>
				</select>
				<label>Notes</label>
				<textarea name="notes">${patient.notes}</textarea><br/> -->
				<input type="submit" value="Save changes" class="btn btn-primary"/>
			</form>
		</div>
	</div>
</body>
</html>
