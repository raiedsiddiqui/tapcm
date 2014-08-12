<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Organizations in Admin</title>
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
		<c:if test="${not empty organizationCreated}">
			<div class ="alert alert-info"><spring:message code="message_newOrganization"/></div>
		</c:if>		
		
		<div class="row">		
			<div class="col-md-9">
				<h2>Volunteer Organizations </h2>
			</div>
			<div class="col-md-3">
				<a href="<c:url value="/new_organization"/>" class="btn btn-primary" data-toggle="modal">New Organization</a>
			</div>
		</div>
		<div class="row-fluid">
			<form action="<c:url value="/view_organizations"/>" method="POST">
				<fieldset>
					<label>Name:</label>
					<input type="text" name="searchName" value="${searchName}" required />
					<input class="btn btn-primary" type="submit" value="Search" />
				</fieldset>
			</form>
		</div>		
		<div id="organization_list">
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Address</th>
					<th>Primary Contact</th>
					<th>Primary Phone</th>
					<th>Secondary Contact</th>
					<th>Secondary Phone</th>
					<th></th>
				</tr>
				<c:forEach items="${organizations}" var="o">
					<tr>
						<td>${o.name}</td>						
						<td>${o.streetNumbet} ${o.streetName}, ${o.city}, ${o.province}</td>
						<td>${o.primaryContact}</td>
						<td>${o.primaryPhone}</td>
						<td>${o.secondaryContact}</td>
						<td>${o.secondaryPhone}</td>						
					</tr>
				</c:forEach>
			</table>
		</div>			
	</div>
	
	</body>
</html>