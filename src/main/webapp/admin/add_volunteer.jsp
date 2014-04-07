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
						<input type="text" name="preferredname"/>

						<label class="col-md-4">Street:</label>
						<input type="text" name="street"  required/>

						<label>Apt#:</label>
						<input type="text" name="apt"  required/>

						<label>Country:</label>
						<input type="text" name="country"  required/>

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

						<label>City:</label>
						<input id="city" name="city">

						<label>Postal Code:</label>
						<input id="city" name="city">

<!-- 						<label>Gender:</label>
						<select name="gender" form="add_volunteer">
							<option value="M" selected>Male</option>	
							<option value="F" >Female</option>
							<option value="O" >Other</option>
						</select>	 -->	

						<label>Home Phone:</label>
						<input id="phoneNum" name="phoneNum">
						
						<label>Cell Phone:</label>
						<input id="city" name="city">

						<label>Email:</label>
						<input id="email" name="email" required>

						<label>Emergency Contact:</label>
						<input id="city" name="city">

						<label>Emergency #:</label>
						<input id="city" name="city">

						<h2> User Account </h2>

						<label>Username:</label>
						<input id="city" name="city">

						<label>Password:</label>
						<input id="city" name="city">

						<label>Experience:</label>
						<select name="level" form="add_volunteer">
							<option value="E" selected>Experienced</option>
							<option value="I" >Intermediate</option>
							<option value="B" >Beginner</option>
						</select>	
<!-- 						<select name="type" form="add_volunteer">
							<option value="Y" selected>Younger</option>
							<option value="O">Older</option>							
						</select>	 -->				
	
						<h2> Availability </h2>
						<div>
							<h4>Monday <input type="checkbox"> N/A</h4>
							<select></select> TO <select></select> <br>
							<select></select> TO <select></select>

						<div>

						<div>
							<h4>Tuesday <input type="checkbox" > N/A </h4>
							<select></select> TO <select></select><br>
							<select></select> TO <select></select>
						<div>

						<div>
							<h4>Wednesday <input type="checkbox"> N/A</h4>
							<select></select> TO <select></select><br>
							<select></select> TO <select></select>
						<div>

						<div>
							<h4>Thursday <input type="checkbox" > N/A</h4>
							<select></select> TO <select></select><br>
							<select></select> TO <select></select>
						<div>

						<div>
							<h4>Friday <input type="checkbox"> N/A</h4>
							<select></select> TO <select></select><br>
							<select></select> TO <select></select>
						<div>
						<h2> Comments </h2>
						<input type="text">

						<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
						<input class="btn btn-primary" type="submit" value="Create" />
					</fieldset>
				</form>
			</div>		
		</div>
	</div>
	
	</body>
</html>