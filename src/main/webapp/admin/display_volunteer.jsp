<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of volunteer</title>
</head>
<body>
<div><h4><a href="<c:url value="/view_volunteers"/>" >Volunteers</a> > ${volunteer.displayName}</h4></div>
<h2>${volunteer.displayName}</h2> <h3><a href="<c:url value="/modify_volunteer/${volunteer.volunteerId}"/>">Edit</a> &nbsp<a href="">Change Password</a></h3>
<table width="970">
	<tr>
		<td width="500"><label>Username : ${volunteer.userName}</label></td>
		<td><label>Address : ${volunteer.address}</label></td>
	</tr>
	<tr>
		<td><label>Experience :</label><c:if test="${volunteer.experienceLevel eq 'E'}">Experienced</c:if>
		<c:if test="${volunteer.experienceLevel eq 'B'}">Beginner</c:if><c:if test="${volunteer.experienceLevel eq 'I'}">Intermediate</c:if>
		</td>
		<td><label>City: ${volunteer.city}</label></td>
	</tr>
	<tr>
		<td><label>Home Phone # : ${volunteer.homePhone} </label></td>
		<td><label>Postal Code : ${volunteer.postalCode}</label></td>
	</tr>
	<tr>
		<td><label>Cell Phone # : ${volunteer.cellPhone}</label></td>
		<td><label>Emergency Contact : ${volunteer.emergencyContact}</label></td>
	</tr>
	<tr>
		<td><label>Email : ${volunteer.email}</label></td>
		<td><label>Emergency # : ${volunteer.emergencyPhone}</label></td>
	</tr>
</table>

<h2>Notes/Comments</h2><br/>
<div>${volunteer.notes}</div><br/>

<h2>Availability</h2>
<div>
	<table  width="1170" border="1">
		<tr>
			<td width ="200" valign="top">
				<table>
					<tr><th>Monday</th></tr>
					<c:forEach items="${availabilities}" var="a">					
						<c:if test="${a.key eq 'Monday'}">
							<c:forEach items="${a.value}" var="al">
								<c:choose>
									<c:when test="${empty al}">
										<tr><td>N/A</td></tr>
									</c:when>
									<c:otherwise>
										<tr><td>${al}</td></tr>
									</c:otherwise>
								</c:choose>								
							</c:forEach>
							
						</c:if>
					</c:forEach>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Tuesday</th></tr>
					<c:forEach items="${availabilities}" var="a">
						<c:if test="${a.key eq 'Tuesday'}">
							<c:forEach items="${a.value}" var="al">
									<c:choose>
										<c:when test="${empty al}">
											<tr><td>N/A</td></tr>
										</c:when>
										<c:otherwise>
											<tr><td>${al}</td></tr>
										</c:otherwise>
									</c:choose>								
								</c:forEach>
						</c:if>
					</c:forEach>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Wednesday</th></tr>
					<c:forEach items="${availabilities}" var="a">
						<c:if test="${a.key eq 'Wednesday'}">
							<c:forEach items="${a.value}" var="al">
									<c:choose>
										<c:when test="${empty al}">
											<tr><td>N/A</td></tr>
										</c:when>
										<c:otherwise>
											<tr><td>${al}</td></tr>
										</c:otherwise>
									</c:choose>								
								</c:forEach>
							</c:if>
					</c:forEach>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Thursday</th></tr>
					<c:forEach items="${availabilities}" var="a">
						<c:if test="${a.key eq 'Thursday'}">
							<c:forEach items="${a.value}" var="al">
								<c:choose>
									<c:when test="${empty al}">
										<tr><td>N/A</td></tr>
									</c:when>
									<c:otherwise>
										<tr><td>${al}</td></tr>
									</c:otherwise>
								</c:choose>								
							</c:forEach>
						</c:if>
					</c:forEach>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Friday</th></tr>					
					<c:forEach items="${availabilities}" var="a">
						<c:if test="${a.key eq 'Friday'}">
							<c:forEach items="${a.value}" var="al">
								<c:choose>
									<c:when test="${!empty al}">
										<tr><td>${al}</td></tr>
									</c:when>
									<c:otherwise>
										<tr><td>N/A</td></tr>
									</c:otherwise>
								</c:choose>											
							</c:forEach>
						</c:if>
					</c:forEach>
				</table>
			</td>
		
		</tr>
		
	</table>
</div>
<h2>Upcoming Visits</h2>
<table  width="970" border="1">
	<tr>
		
		<th width="500">Visit Date</th>
		
		<th>Client</th>
	</tr>
	<c:forEach items="${upcomingVisits}" var="uVistits">
	<tr >
		<td>${uVistits.date}</td>
		
		<td>${uVistits.patient}</td>
	</tr>
	</c:forEach>
</table>
<h2>Completed Visits</h2>
<table  width="970" border="1">
	<tr>
		<th width="300">Visit #</th>
		<th width="300"> Visit Date</th>		
		<th>Client</th>
	</tr>
	<c:forEach items="${completedVisits}" var="cVistits">
	<tr >
		<td>${cVistits.appointmentID}</td>
		<td>${cVistits.date}</td>
		
		<td>${cVistits.patient}</td>
	</tr>
	</c:forEach>
</table>


</body>
</html>