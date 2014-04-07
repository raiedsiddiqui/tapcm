<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Volunteers in Admin</title>
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
		
		<script type="text/javascript">
			function printTable(){
				$('.table').printThis();
			}
		</script>
	</head>
	
	<body>
	<img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<%@include file="navbar.jsp" %>
		<c:if test="${not empty volunteerCreated}">
			<div class ="alert alert-info"><spring:message code="message_newVolunteer"/></div>
		</c:if>
		<c:if test="${not empty volunteerDeleted }">
			<div class ="alert alert-info"><spring:message code="message_removeVolunteer"/></div>
		</c:if>
		<c:if test="${not empty volunteerUpdate }">
			<div class ="alert alert-info"><spring:message code="message_modifyVolunteer"/></div>
		</c:if>
		<div class="row-fluid">
			<h2>Volunteers </h2>
			<div class="row-fluid">
				<form action="<c:url value="/view_volunteers"/>" method="POST">
					<fieldset>
						<label>Name:</label>
						<input type="text" name="searchName" value="${searchName}" required />
						<input class="btn btn-primary" type="submit" value="Search" />
					</fieldset>
				</form>
			</div>
			
			<a href="<c:url value="/new_volunteer"/>" class="btn btn-primary" data-toggle="modal">New Volunteer</a>
			
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Username</th>
					<th>Experience</th>
					<th>City</th>
					<th>Phone Number</th>
					<th></th>
				</tr>
				<c:forEach items="${volunteers}" var="vl">
					<tr>
						<td><a href="<c:url value="/display_volunteer/${vl.volunteerId}"/>">${vl.displayName}</a></td>						
						<td>${vl.userName}</td>
						<td>${vl.experienceLevel}</td>
						<td>${vl.city}</td>
						<td>${vl.homePhone}</td>
						<td><a href="<c:url value="/modify_volunteer/${vl.volunteerId}"/>" class="btn btn-danger">Edit</a></td>
						<td><a href="<c:url value="/delete_volunteer/${vl.volunteerId}"/>" class="btn btn-danger">Delete</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	
	</body>
</html>