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
			<h2>Patients</h2><a href="#addPatient" class="btn btn-primary" data-toggle="modal">Add New</a>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Preferred Name</th>
					<th>Volunteer</th>
					<th>Edit</th>
					<!-- <th>Remove</th> -->
				</tr>
                <c:forEach items="${patients}" var="p">
                <tr>
                    <td>${p.firstName} ${p.lastName} (${p.gender})</td>
                    <td>${p.preferredName}</td>
                    <td>${p.volunteerName}</td>
                    <td><a href="<c:url value="/edit_patient/${p.patientID}"/>" class="btn btn-info">Edit</a></td>
                    <!-- Disabling the ability to delete patients as data relating to a patient should not be deleted -->
                    <!-- <td><a href="<c:url value="/remove_patient/${p.patientID}"/>" class="btn btn-danger">Remove</a></td> -->
                </tr>
                </c:forEach>
			</table>
			<!--  a href="#addPatient" class="btn btn-primary" data-toggle="modal">Add New</a>-->
		</div>
	</div>
	
	<!-- Modal -->

<div class="modal fade" id="addPatient" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add Patient</h3>
  		</div>
  		<div class="modal-body">
  			<form id="newPatient" method="post" action="<c:url value="/add_patient"/>">
  				<div class="row form-group">
  					<div class="col-md-6">
						<label>First Name:</label>
						<input type="text" name="firstname" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>Last Name:</label>
						<input type="text" name="lastname" class="form-control" required/>
					</div>
					<div class="col-md-6">
						<label>Preferred Name:</label>
						<input type="text" name="preferredname" class="form-control"/>
					</div>
					<div class="col-md-6">
					<label>Gender:</label>
						<select name="gender" form="newPatient" class="form-control">
							<option value="M">Male</option>
							<option value="F">Female</option>
							<option value="O">Other</option>
						</select>
					</div>
					<div class="col-md-6">
						<label>Volunteer1:</label>
						<select name="volunteer1" form="newPatient" class="form-control">
							<c:forEach items="${volunteers}" var="v">
								<option value="${v.volunteerId}">${v.firstName}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-md-6">
						<label>Volunteer2:</label>
						<select name="volunteer2" form="newPatient" class="form-control">
							<c:forEach items="${volunteers}" var="v">
								<option value="${v.volunteerId}">${v.firstName}</option>
							</c:forEach>
						</select>
					</div>
				</div>		
					<label>MyOscar verified? </label>
					<input type="radio" name="myoscar_verified" value="1" checked/>Yes
					<input type="radio" name="myoscar_verified" value="0"/>No
					<br/>
					<label>Clinic:</label>
					<select name="clinic" form="newPatient" class="form-control">
						<option value="1">West End Clinic</option>
						<option value="2">Stonechurch Family Health Center</option>
					</select>
					<label>Availability:</label><br/>
					<label>Notes</label>
					<textarea name="notes" class="form-control"></textarea>
					<label>Alerts</label>
					<textarea name="alerts" class="form-control"></textarea>
			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			<input class="btn btn-primary" form="newPatient" type="submit" value="Add" />
  		</div>
  	</div>
  </div>
</div>
</body>
</html>
