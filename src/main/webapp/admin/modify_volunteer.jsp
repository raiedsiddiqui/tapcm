<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Modify Volunteer</title>
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
		<div class="row-fluid">
			<h2>Volunteers </h2>
			<div class="row-fluid">
				<form id="modify_volunteer" action="<c:url value="/update_volunteer"/>" method="POST">
					<fieldset>
						<label>First Name:</label>
						<input type="text" name="firstname" value="${volunteer.firstName}" required/>
						<label>Last Name:</label>
						<input type="text" name="lastname" value="${volunteer.lastName}"  required/>
						<label>Preferred Name:</label>
						<input type="text" name="preferredname" value="${volunteer.preferredName}"/>
						<label>Gender:</label>
						<select name="gender" form="modify_volunteer">
							<option value="M" <c:if test="${volunteer.gender eq 'M'}">selected</c:if>>Male</option>	
							<option value="F" <c:if test="${volunteer.gender eq 'F'}">selected</c:if>>Female</option>
							<option value="O" <c:if test="${volunteer.gender eq 'O'}">selected</c:if>>Other</option>
						</select>						
						<label>Email:</label>
						<input id="email" name="email" value="${volunteer.email}" required>
						<label>Experience:</label>
						<select name="level" form="modify_volunteer">
							<option value='E' <c:if test="${volunteer.experienceLevel eq 'E'}">selected</c:if>>Experienced</option>
							<option value='I' <c:if test="${volunteer.experienceLevel eq 'I'}">selected</c:if>>Intermediate</option>
							<option value='B' <c:if test="${volunteer.experienceLevel eq 'B'}">selected</c:if> >Beginner</option>
						</select>	
						<select name="type" form="modify_volunteer">
							<option value='Y' <c:if test="${volunteer.ageType eq 'Y'}">selected</c:if>>Younger</option>
							<option value='O' <c:if test="${volunteer.ageType eq 'O'}">selected</c:if>>Older</option>							
						</select>					
						<label>City:</label>
						<input id="city" name="city" value="${volunteer.city}">
						<label>Province:</label>
						<select name="province" form="modify_volunteer">
							<option value='AB' <c:if test="${volunteer.province eq 'AB'}">selected</c:if>>Alberta</option>
							<option value='BC' <c:if test="${volunteer.province eq 'BC'}">selected</c:if>>British Colunmbia</option>							
							<option value='MB' <c:if test="${volunteer.province eq 'MB'}">selected</c:if>>Manitoba</option>
							<option value='NB' <c:if test="${volunteer.province eq 'NB'}">selected</c:if>>New Brunswik</option>
							<option value='NL' <c:if test="${volunteer.province eq 'NL'}">selected</c:if>>Newfoundland and Labrador</option>
							<option value='NS' <c:if test="${volunteer.province eq 'NS'}">selected</c:if>>Nova Scotia</option>							
							<option value='ON' <c:if test="${volunteer.province eq 'ON'}">selected</c:if>>Ontario</option>
							<option value='PE' <c:if test="${volunteer.province eq 'PE'}">selected</c:if>>PrinceEdword Island</option>
							<option value='QC' <c:if test="${volunteer.province eq 'QC'}">selected</c:if>>Quebec</option>
							<option value='SK' <c:if test="${volunteer.province eq 'SK'}">selected</c:if>>Saskatchewan</option>							
							<option value='NT' <c:if test="${volunteer.province eq 'NT'}">selected</c:if>>Northwest Terriotories</option>
							<option value='NU' <c:if test="${volunteer.province eq 'NU'}">selected</c:if>>Nunavut</option>
							<option value='YT' <c:if test="${volunteer.province eq 'YT'}">selected</c:if>>Yukon</option>
							
						</select>
						<label>Phone Number:</label>
						<input id="phoneNum" name="phoneNum" value="${volunteer.phoneNumber}"><br/>
						<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
						<input class="btn btn-primary" type="submit" value="Save Change" />
						<input id="volunteerId" name="volunteerId" type="hidden" value="${volunteer.volunteerId}"/>
					</fieldset>
				</form>
			</div>		
		</div>
	</div>
	
	</body>
</html>