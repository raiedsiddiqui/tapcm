<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
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
					<table>
							<tr>
								<td colspan='3'></td>
							</tr>
							<tr>
								<td><label>First Name:</label>
									<input type="text" name="firstname" value="${volunteer.firstName}" required/></td>
								<td><label>Last Name:</label>
									<input type="text" name="lastname" value="${volunteer.lastName}"  required/></td>
								<td><label>Street #:</label>
									<input name="streetnum" value="${volunteer.streetNumber}"/>
								</td>
							</tr>
							<tr>
								<td><label>Street:</label>
									<input type="text" name="street" value="${volunteer.street}"/></td>
								<td><label>Apt #:</label>
									<input type="text" name="aptnum" value="${volunteer.aptNumber}"/></td>
								<td><label>Country:</label>
									<select name="country" form="modify_volunteer">
										<option value="CA" <c:if test="${volunteer.country eq 'CA'}">selected</c:if>>Canada</option>
										<option value="ST" <c:if test="${volunteer.country eq 'ST'}">selected</c:if>>USA</option>
										<option value="CH" <c:if test="${volunteer.country eq 'CH'}">selected</c:if>>China</option>
										<option value="RU" <c:if test="${volunteer.country eq 'RU'}">selected</c:if>>Russia</option>
									</select>
								</td>
							</tr>
							<tr>
								<td>
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
								</td>
								<td>
									<label>City:</label>
									<input name="city" value="${volunteer.city}"/>
								</td>
								<td>
									<label>Postal Code:</label>
									<input  name="postalcode" value="${volunteer.postalCode}"/>
								</td>
							</tr>
							<tr>
								<td>
									<label>Home Phone:</label>
									<input  name="homephone" value="${volunteer.homePhone}"/>
								</td>
								<td>
									<label>Cell Phone:</label>
									<input  name="cellphone" value="${volunteer.cellPhone}"/>
								</td>
								<td>
									<label>Email:</label>
									<input name="email" value="${volunteer.email}" required/>
								</td>
							</tr>
							<tr>
								<td>
									<label>Emergency Contact:</label>
									<input name="emergencycontact" value="${volunteer.emergencyContact}"/>
								</td>
								<td>
									<label>Emergency #:</label>
									<input name="emergencyphone" value="${volunteer.emergencyPhone}"/>
								</td>
								<td>&nbsp</td>
							</tr>
							<tr>
								<td colspan='3'><h2>User Account</h2></td>
							</tr>
							
							<tr>
								<td>
									<label>Username:</label>
									<input name="username"value="${volunteer.userName}"/>
								</td>
								<td>
									<label>Password:</label>
									<input name=password" disabled/>									
								</td>
								<td>
									<label>Experience:</label>
									<select name="level" form="modify_volunteer">
										<option value='E' <c:if test="${volunteer.experienceLevel eq 'E'}">selected</c:if>>Experienced</option>
										<option value='I' <c:if test="${volunteer.experienceLevel eq 'I'}">selected</c:if>>Intermediate</option>
										<option value='B' <c:if test="${volunteer.experienceLevel eq 'B'}">selected</c:if> >Beginner</option>
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
									<tr><c:set var="availability" value="${volunteer.availability}"/>
										<td><input type="checkbox" name="availableTime" value="mon-09:00-10:00" <c:if test="${fn:contains(availability, 'mon-09:00-10:00')}">checked</c:if>>09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-09:00-10:00" <c:if test="${fn:contains(availability, 'tue-09:00-10:00')}">checked</c:if>>09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-09:00-10:00" <c:if test="${fn:contains(availability, 'wed-09:00-10:00')}">checked</c:if>>09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-09:00-10:00" <c:if test="${fn:contains(availability, 'thu-09:00-10:00')}">checked</c:if>>09:00--10:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-09:00-10:00" <c:if test="${fn:contains(availability, 'fri-09:00-10:00')}">checked</c:if>>09:00--10:00</td>
										
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-10:00-11:00" <c:if test="${fn:contains(availability, 'mon-10:00-11:00')}">checked</c:if>>10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-10:00-11:00" <c:if test="${fn:contains(availability, 'tue-10:00-11:00')}">checked</c:if>>10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-10:00-11:00" <c:if test="${fn:contains(availability, 'wed-10:00-11:00')}">checked</c:if>>10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-10:00-11:00" <c:if test="${fn:contains(availability, 'thu-10:00-11:00')}">checked</c:if>>10:00--11:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-10:00-11:00" <c:if test="${fn:contains(availability, 'fri-10:00-11:00')}">checked</c:if>>10:00--11:00</td>
										
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-11:00-12:00" <c:if test="${fn:contains(availability, 'mon-11:00-12:00')}">checked</c:if>>11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-11:00-12:00" <c:if test="${fn:contains(availability, 'tue-11:00-12:00')}">checked</c:if>>11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-11:00-12:00" <c:if test="${fn:contains(availability, 'wed-11:00-12:00')}">checked</c:if>>11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-11:00-12:00" <c:if test="${fn:contains(availability, 'thu-11:00-12:00')}">checked</c:if>>11:00--12:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-11:00-12:00" <c:if test="${fn:contains(availability, 'fri-11:00-12:00')}">checked</c:if>>11:00--12:00</td>
										
									</tr>
									<tr>
										<td><input type="checkbox" name="availableTime" value="mon-13:00-14:00" <c:if test="${fn:contains(availability, 'mon-13:00-14:00')}">checked</c:if>>13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="tue-13:00-14:00" <c:if test="${fn:contains(availability, 'tue-13:00-14:00')}">checked</c:if>>13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="wed-13:00-14:00" <c:if test="${fn:contains(availability, 'wed-13:00-14:00')}">checked</c:if>>13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="thu-13:00-14:00" <c:if test="${fn:contains(availability, 'thu-13:00-14:00')}">checked</c:if>>13:00--14:00</td>
										<td><input type="checkbox" name="availableTime" value="fri-13:00-14:00" <c:if test="${fn:contains(availability, 'fri-13:00-14:00')}">checked</c:if>>13:00--14:00</td>
										
									</tr>
								</table>										
							</tr>
							<tr>
								<td colspan='3'>Notes</td>
							</tr>
							<tr>
								<td colspan='3'><input type="textarea" maxlength="50" name="notes" value="${volunteer.notes}"/></td>
							</tr>
							
						</table>
			
						
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