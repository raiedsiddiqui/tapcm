<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
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
				
			div.dropdown_container {
   					 width:20px;
				}
			
			select.my_dropdown {
			    width:auto;
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
			<c:if test="${not empty userNameExist}">			
				<div class="alert alert-error"><spring:message code="message_username_exist"/></div>
			</c:if>		
			<c:if test="${not empty volunteerExist }">
				<div class="alert alert-error"><spring:message code="message_volunteer_exist"/></div>
			</c:if>
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
								<td><label class="col-md-4">Street:</label>
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
								<td colspan='3'><h2> User Account </h2></td>
							</tr>
							
							<tr>
								<td>
									<label>Username:</label>
									<input name="username"/>
								</td>
								<td>
									<label>Password:</label>
									<input type="password" name="password"/>									
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
								<td colspan='3' ><h2> Availability </h2></td>
							</tr>
							<tr>
								<table>
									<tr>
										<td width="180"><h4>Monday <input type="checkbox"> N/A | </h4></td>
										<td width="180"><h4>Tuesday <input type="checkbox" > N/A | </h4></td>
										<td width="180"><h4>Wednesday <input type="checkbox"> N/A | </h4></td>
										<td width="180"><h4>Thursday <input type="checkbox" > N/A |</h4></td>
										<td width="180"><h4>Friday <input type="checkbox"> N/A </h4></td>
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-09:00-10:00">09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-09:00-10:00">09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-09:00-10:00">09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-09:00-10:00">09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-09:00-10:00">09:00--10:00</td>
										
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-10:00-11:00">10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-10:00-11:00">10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-10:00-11:00">10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-10:00-11:00">10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-10:00-11:00">10:00--11:00</td>
										
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-11:00-12:00">11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-11:00-12:00">11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-11:00-12:00">11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-11:00-12:00">11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-11:00-12:00">11:00--12:00</td>
										
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-13:00-14:00">13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-13:00-14:00">13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-13:00-14:00">13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-13:00-14:00">13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-13:00-14:00">13:00--14:00</td>
										
									</tr>
								</table>								
							</tr>
							<tr>
								<td colspan='3'><h2> Comments </h2></td>
							</tr>
							<tr>
								<td colspan='3'><input type="textarea" rows="6" cols="80"  name="notes"/></td>
							</tr>
							
						</table>

						<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
						<input class="btn btn-primary" type="submit" value="Create" />

					</fieldset>
				</form>
			</div>		
		</div>
	</div>
	
	</body>
</html>