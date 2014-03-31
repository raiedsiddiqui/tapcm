<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Add Volunteer</title>
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
				<form id="add_volunteer" action="<c:url value="/add_volunteer"/>" method="POST">
					<fieldset>
						<label>First Name:</label>
						<input type="text" name="firstname"  required/>
						<label>Last Name:</label>
						<input type="text" name="lastname"  required/>
						<label>Preferred Name:</label>
						<input type="text" name="preferredname" "/>
						<label>Gender:</label>
						<select name="gender" form="add_volunteer">
							<option value="M" selected>Male</option>	
							<option value="F" >Female</option>
							<option value="O" >Other</option>
						</select>						
						<label>Email:</label>
						<input id="email" name="email" required>
						<label>Experience:</label>
						<select name="level" form="add_volunteer">
							<option value="E" selected>Experienced</option>
							<option value="I" >Intermediate</option>
							<option value="B" >Beginner</option>
						</select>	
						<select name="type" form="add_volunteer">
							<option value="Y" selected>Younger</option>
							<option value="O">Older</option>							
						</select>					
						<label>City:</label>
						<input id="city" name="city">
						<label>Province:</label>
						<select name="province" form="add_volunteer">
							<option value="AB" selected>Alberta</option>
							<option value="BC" >British Colunmbia</option>							
							<option value="MB" >Manitoba</option>
							<option value="NB" >New Brunswik</option>
							<option value="NL" >Newfoundland and Labrador</option>
							<option value="NS" >Nova Scotia</option>							
							<option value="ON" >Ontario</option>
							<option value="PE" >PrinceEdword Island</option>
							<option value="QC" >Quebec</option>
							<option value="SK" >Saskatchewan</option>							
							<option value="NT" >Northwest Terriotories</option>
							<option value="NU" >Nunavut</option>
							<option value="YT" >Yukon</option>
							
						</select>
						<label>Phone Number:</label>
						<input id="phoneNum" name="phoneNum"><br/>
						<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
						<input class="btn btn-primary" type="submit" value="Save" />
					</fieldset>
				</form>
			</div>		
		</div>
	</div>
	
	</body>
</html>