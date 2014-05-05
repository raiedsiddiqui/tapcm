<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of Appointment</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	
	<script type="text/javascript">
		
	</script>

</head>
<body>
<div class="content">
<%@include file="navbar.jsp" %>

<div class="row-fluid">

<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointment ></a> Client Name</h4></div>

<table>
	<tr>
		<td colspan="2">
			<label>&nbsp Client Name: </label>${appointment.patient}
		</td>		
	</tr>
	<tr>
		<td colspan="2">
			<label>&nbsp Date of Visit:</label>&nbsp ${appointment.date}
		</td>
	<c:if test="${isCentralAdmin}">
		<tr>
			<td colspan="2">
				<label >&nbsp Report :</label><a href="">View Report</a>
			</td>
		</tr>
	</c:if>	
	
	<tr>
		<td>
			<label>&nbsp Volunteer One: </label>&nbsp${appointment.volunteer}
		</td>
		<td>
			<label>&nbsp Volunteer Two :</label>&nbsp${appointment.partner}
		</td>
	</tr>
	<tr>
		<td>
			<label >&nbsp Narrative :</label><a href="">View/Download</a>
		</td>
		<td>
			<label>&nbsp Narrative :</label><a href="">View/Download</a>
	</tr>
</table>

	<h3>Alerts</h3>		${alerts}

	<h2>Detailed Log</h2>
	<table   width="970" border="1">
		<tr>
			
			<th width="500">User Activity</th>
			
			<th>Date</th>
			<th>Time</th>
			
		</tr>
		<c:forEach items="${activities}" var="a">
		
		<tr >
			<td>${a.description }</td>
			<td>${a.date}</td>			
			<td>${a.time}</td>
		</tr>
		</c:forEach>
	</table>	
</div>
</div>

</body>
</html>