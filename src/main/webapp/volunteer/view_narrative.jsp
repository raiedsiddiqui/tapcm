<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Narrative in Volunteer</title>
<%@include file="volunteer_head.jsp" %>
</head>
<body>
  <%@ include file="subNavi.jsp" %>

	<div class="content">
		<div class="row-fluid">
			<div class="span12">
				<h3>My Narrative </h3><!--  <a href="<c:url value="/new_narrative"/>" class="btn btn-primary" data-toggle="modal">New Narrative</a>-->  
							
				<c:if test="${not empty narrativeDeleted }">
					<div class ="alert alert-info"><spring:message code="message_removeNarrative"/></div>
				</c:if>
				<c:if test="${not empty narrativeUpdate }">
					<div class ="alert alert-info"><spring:message code="message_modifyNarrative"/></div>
				</c:if>
						
				<div class="tab-content">
					<div class="tab-pane active" id="all">						
						<table class="table">
							<tr>
								<th>Title</th>
								<th>Edit Date</th>
								<th>Client </th>
							<!--  	<th></th>-->
							</tr>
							<c:forEach items="${narratives}" var="na">
								<tr>
									<td><a href="<c:url value="/modify_narrative/${na.narrativeId}"/>">${na.title}</a></td>									
									<td>${na.editDate}</td>
									<td>${na.patientName}</td>
									<!--<td><a href="<c:url value="/delete_narrative/${na.narrativeId}"/>" class="btn btn-danger">Delete</a></td>-->
								</tr>
							</c:forEach>
						</table>	
					</div>
					
				</div>
			
			</div>
		</div>
		
	</div>
	
	

</body>
</html>
