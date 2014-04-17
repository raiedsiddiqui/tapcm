<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Scheduler in Appointment</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
			<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
			<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
			<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
			<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>

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
		<c:if test="${not empty noAvailableTime}">			
			<div class="alert alert-error"><spring:message code="message_volunteer_noAvailable"/></div>
		</c:if>		
		<c:if test="${not empty noMatchTime}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_noMatchTime"/></div>
		</c:if>
		<c:if test="${not empty misMatchedVolunteer}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_misMatchedVolunteer"/></div>
		</c:if>			
		<c:if test="${not empty sameVolunteer}">			
			<div class="alert alert-error"><spring:message code="message_volunteers_sameVolunteer"/></div>
		</c:if>	
		<c:if test="${not empty successToCreateAppointment}">			
			<div class="alert alert-error"><spring:message code="message_newAppointment_successful"/></div>
		</c:if>			
		<c:if test="${not empty failedToCreateAppointment}">			
			<div class="alert alert-error"><spring:message code="message_newAppointment_failed"/></div>
		</c:if>		
	</div>
	
	<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointments</a> > Scheduler</h4></div>
	
	<h3>Appointment Scheduler</h3>

	<form  id="schedulerForm" method="post" action="<c:url value="/view_matchTime"/>">	
		
		<label><h4>Select Patient/Client: </h4></label>
		<select name="patient" form="schedulerForm">
			<c:forEach items="${patients}" var="p">
				<option value="${p.patientID}">${p.displayName}</option>
			</c:forEach>
		</select>
		<table>
			<tr>
				<td>
					<label><h4>Volunteer 1:</h4></label>
					<select name="volunteer1" form="schedulerForm" id ="volunteer1">
						<c:forEach items="${allvolunteers}" var="v">
							<option value="${v.volunteerId}">${v.displayName}</option>
						</c:forEach>
						
					</select>
				</td>
				<td>
					<label><h4>Volunteer 2:</h4></label>
					<select name="volunteer2" form="schedulerForm" id="volunteer2">
						<c:forEach items="${allvolunteers}" var="v">
							<option value="${v.volunteerId}">${v.displayName}</option>
						</c:forEach>
						
					</select>
				</td>
				<td>
					<input class="btn btn-primary" form="schedulerForm" type="submit" value="Go" />	
				</td>
			</tr>
		</table>
		
		
	</form>

</div>
</body>
</html>