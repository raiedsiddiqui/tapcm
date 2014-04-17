<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of volunteer</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	
	<script type="text/javascript">
		function showdisplayActivityLog(){
			document.getElementById("displayActivityLogDiv").style.display="block";
		}
		
		function showdisplayMessage(){
			document.getElementById("displayMessageDiv").style.display="block";
		}
	</script>

</head>
<body>
<div><h4><a href="<c:url value="/view_volunteers"/>" >Volunteers</a> > ${volunteer.displayName}</h4></div>
<div>
<table>
	<tr>
		<td width="500">
			<h2>${volunteer.displayName}</h2><h3><a href="<c:url value="/modify_volunteer/${volunteer.volunteerId}"/>">Edit</a></h3>
		</td>
		<td>
			<a  href="#displayActivityLog" class="btn btn-primary" data-toggle="modal">Activity Log</a>
		</td>
		<td>
			<a href="#displayMessage" class="btn btn-primary" data-toggle="modal">Message</a>
		</td>
	</tr>
</table>
</div>

 

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
					<c:choose>
						<c:when test="${empty monAvailability }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${monAvailability}" var="moA">	
						    	<tr><td>${moA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>						
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Tuesday</th></tr>
					<c:choose>
						<c:when test="${empty tueAvailability }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${tueAvailability}" var="tuA">	
						    	<tr><td>${tuA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Wednesday</th></tr>
					<c:choose>
						<c:when test="${empty wedAvailability }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${wedAvailability}" var="weA">	
						    	<tr><td>${weA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Thursday</th></tr>
					<c:choose>
						<c:when test="${empty thuAvailability }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${thuAvailability}" var="thA">	
						    	<tr><td>${thA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
			
				<table>
					<tr><th>Friday</th></tr>					
					<c:choose>
						<c:when test="${empty friAvailability }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${friAvailability}" var="frA">	
						    	<tr><td>${frA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
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

<hr>

<div class="row-fluid" id="displayActivityLogDiv" style="display:none";>
			
</div>
<div class="row-fluid" id="displayMessageDiv" style="display:none";>
			
</div>

<!-- Modal -->
	<div id="displayActivityLog" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Activity Log</h3>
  		</div>
  		<div class="modal-body">
  		
  			<table border="1">
	  			<tr>
					<th width="200">Date</th>
					<th width="300">Time</th>
					<th width="400">Description</th>
				</tr>
				<c:forEach items="${activityLogs}" var="al">
				<tr>
					<td>${al.date}</td>
					<td>${al.time}</td>
					<td>${al.description}</td>
				</tr>
				</c:forEach>
  			</table>
  		
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			
  		</div>
	</div>
	
	<div id="displayMessage" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">In Box</h3>
  		</div>
  		<div class="modal-body">
  		
  			<table class="table">
						<tr>
							<th>Sender</th>
							<th>Subject</th>
							<th>Date</th>
							
						</tr>
						<c:forEach items="${messages}" var="m">
						<c:choose>
							<c:when test="${! m.read}"><tr onClick="document.location='<c:url value="/view_message/${m.messageID}"/>';" style="font-weight:bold;"></c:when>
							<c:otherwise><tr onClick="document.location='<c:url value="/view_message/${m.messageID}"/>';"></c:otherwise>
						</c:choose>
							<td>${m.sender}</td>
							<td>${m.subject}</td>
							<td>${m.date}</td>
							
						</tr>
						</c:forEach>
					</table>
  		
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			
  		</div>
	</div>
	

</body>
</html>