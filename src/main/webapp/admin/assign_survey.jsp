<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin/ Survey Management</title>
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
		<a href="#" >Survey Management</a> > Assign Survey<br/>
		<c:if test="${not empty no_survey_selected}">
			<div class ="alert alert-info"><spring:message code="message_noSurveySelected"/></div>
		</c:if>
		<c:if test="${not empty no_patient_selected}">
			<div class ="alert alert-info"><spring:message code="label_patient_ID_null"/></div>
		</c:if>
		<c:if test="${not empty successful}">
			<div class ="alert alert-info"><spring:message code="message_assign_survey_successful"/></div>
		</c:if>
		<h2>Assign Survey</h2><br/>
		<form id="assignSurveyForm" action="<c:url value="/assign_selectedsurvey"/>" method="post" >
			<label>Select Survey : </label><br/>
			<select multiple id="survey_template" name="surveyTemplates" class="form-control" style="max-width:50%;">
				<c:forEach items="${surveyTemplates}" var="st">
					<option value="${st.surveyID}">${st.title}</option>
				</c:forEach>
			</select><br/>
			<label>Select Client : </label><br/>
			<input type="checkbox" name="assignAllClinets" style="margin-bottom:10px;" value="true" >Assign to All clients</input><br/>
			<table>			
				<tr>
					<th width="5%"></th>
					<th width="20%">Name </th>
					<th width="15%">DOB </th>
					<th width="5%">Age </th>
					<th width="5%">Gender </th>
					<th width="20%">Clinic </th>
					<th width="10%">MRP </th>
					<th width="10%">City </th>
					<th width="10%">Phone Number</th>
				</tr>
				
					<c:forEach items="${patients}" var="p">
						<tr>
							<td style="text-align:center;"><input type="checkbox" name="patientId" value="${p.patientID}" /></td>
							<td>${p.firstName} ${p.lastName} </td>
							<td>${p.bod}</td>
							<td>${p.age}</td>
							<td>${p.gender}</td>
							<td>${p.clinicName}</td>
							<td>${p.mrp}</td>
							<td>${p.city}</td>
							<td>${p.homePhone}</td>
						</tr>	
					</c:forEach>	
						
			</table>
			<input type="submit" class="btn btn-primary" value="Assign" />
		</form>
		
	</div>

</body>
</html>