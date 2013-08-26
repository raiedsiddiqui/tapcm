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
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<%@include file="navbar.jsp" %>
		
		
		<div class="row-fluid">
			<h2>Appointments</h2>
			<table class="table">
				<tr>
					<th>Volunteer</th>
					<th>Patient</th>
					<th>Time</th>
					<th>Status</th>
					<th>Approve</th>
					<th>Decline</th>
					<th>Delete</th>
				</tr>
				<c:forEach items="${appointments}" var="a">
				<tr>
					<td>${a.volunteer}</td>
					<td>${a.patient}</td>
					<td>${a.date} ${a.time}</td>
					<td>${a.status}</td>
					<td><a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary">Approve</a></td>
					<td><a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-danger">Decline</a></td>
					<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger">Delete</a></td>
				</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>
