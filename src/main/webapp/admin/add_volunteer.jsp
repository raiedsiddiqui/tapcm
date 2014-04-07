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
						<table>
							<tr>
								<td colspan='3'></td>
							</tr>
							<tr>
								<td><label>First Name:</label>
									<input type="text" name="firstname"  required/></td>
								<td><label>Last Name:</label>
									<input type="text" name="lastname"  required/></td>
								<td><label>Street #:</label>
									<input name="streetnum" />
								</td>
							</tr>
							<tr>
								<td><label>Street:</label>
									<input type="text" name="street"/></td>
								<td><label>Apt #:</label>
									<input type="text" name="aptnum"/></td>
								<td><label>Country:</label>
									<select name="country" form="add_volunteer">
										<option value="CA" selected>Canada</option>
										<option value="ST">USA</option>
										<option value="CH">China</option>
										<option value="RU">Russia</option>
									</select>
								</td>
							</tr>
							<tr>
								<td>
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
								</td>
								<td>
									<label>City:</label>
									<input name="city">
								</td>
								<td>
									<label>Postal Code:</label>
									<input  name="postalcode"/>
								</td>
							</tr>
							<tr>
								<td>
									<label>Home Phone:</label>
									<input  name="homephone">
								</td>
								<td>
									<label>Cell Phone:</label>
									<input  name="cellphone">
								</td>
								<td>
									<label>Email:</label>
									<input name="email" required>
								</td>
							</tr>
							<tr>
								<td>
									<label>Emergency Contact:</label>
									<input name="emergencycontact">
								</td>
								<td>
									<label>Emergency #:</label>
									<input name="emergencyphone">
								</td>
								<td>&nbsp</td>
							</tr>
							<tr>
								<td colspan='3'>User Account</td>
							</tr>
							
							<tr>
								<td>
									<label>Username:</label>
									<input name="username"/>
								</td>
								<td>
									<label>Password:</label>
									<input name=password"/>									
								</td>
								<td>
									<label>Experience:</label>
									<select name="level" form="add_volunteer">
										<option value="E" selected>Experienced</option>
										<option value="I" >Intermediate</option>
										<option value="B" >Beginner</option>
									</select>	
								</td>
							</tr>
							<tr>
								<td colspan='3' >Availability</td>
							</tr>
							<tr>
								<table>
									<tr>
										<td>Monday <input type="checkbox" name="noAvailable" value="noMonday"/> N/A</td>
										<td>Tuesday <input type="checkbox" name="noAvailable" value="noTuesday"/> N/A</td>
										<td>Wednesday <input type="checkbox" name="noAvailable" value="nowednesday"/> N/A</td>
										<td>Thursday <input type="checkbox" name="noAvailable" value="noThursday"/> N/A</td>
										<td>Friday <input type="checkbox" name="noAvailable" value="noFriday"/> N/A</td>
									</tr>
									<tr>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
									</tr>
								</table>								
							</tr>
							<tr>
								<td colspan='3'>Notes</td>
							</tr>
							<tr>
								<td colspan='3'><input type="textarea" maxlength="50" name="notes"/></td>
							</tr>
							
						</table>
						
						
						<!--  label>Preferred Name:</label>
						<input type="text" name="preferredname" />
						<label>Gender:</label>
						<select name="gender" form="add_volunteer">
							<option value="M" selected>Male</option>	
							<option value="F" >Female</option>
							<option value="O" >Other</option>
						</select>						
						
						
						<!--  select name="type" form="add_volunteer">
							<option value="Y" selected>Younger</option>
							<option value="O">Older</option>							
						</select>-->					
												
						
						<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
						<input class="btn btn-primary" type="submit" value="Create Volunteer" />
					</fieldset>
				</form>
			</div>		
		</div>
	</div>
	
	</body>
</html>