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
		
	</script>

</head>
<body>
<div class="content">
<%@include file="navbar.jsp" %>

<div class="row-fluid">

<div><h4><a href="<c:url value="/view_clients_admin"/>" >Client</a> ${patient.displayName}</h4></div>
<div>
<table>
	<tr>
		<td width="400">
			<h2>${patient.displayName}<a href="<c:url value="/edit_patient/${patient.patientID}"/>">Edit</a></h2>
		</td>
		
	</tr>
</table>
</div> 

	<div class="row form-group">
		<div class="col-md-2">
	    	<label class="control-label">Date of birth:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${patient.bod}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Gender:</label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${patient.gender}</p>
		</div>
		<div class="col-md-2">
	    	<label class="control-label">Address :</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${patient.address}</p>
	  	</div>
 		<div class="col-md-2">
	    	<label class="control-label">MRP:</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${patient.mrp}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Phone #:</label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${patient.homePhone}</p>
		</div>
		<div class="col-md-2">
	    	<label class="control-label">Clinic :</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${patient.clinic}</p>
	  	</div>
	  	
	  	<div class="col-md-2">
			<label class="control-label">Email :</label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${patient.email}</p>
		</div>
		<div class="col-md-2">
	    	<label class="control-label">MyOscar Verified :</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${patient.myOscarAuthentication}</p>
	  	</div>
  </div>


	<div class="row form-group">
		<table>
			<tr>
				<td><div class="col-md-2">
				    	<label class="control-label">Alerts: </label>
				    </div>
				    <div class="col-md-2">
				      	<p class="form-control-static">${patient.alerts}</p>
				  	</div>
				</td>
				<td>
					<div class="col-md-2">
				    	<label class="control-label">Assigned Volunteers:</label>
				    </div>
				</td>
			</tr>
		</table>
		<div class="col-md-2">
	    	<label class="control-label">Cell Phone #</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.cellPhone}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Email : </label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.email}</p>
		</div>
  </div>

	<div class="row form-group">
		<div class="col-md-2">
	    	<label class="control-label">Emergency Contact :</label>
	    </div>
	    <div class="col-md-2">
	      	<p class="form-control-static">${volunteer.emergencyContact}</p>
	  	</div>
	
		<div class="col-md-2">
			<label class="control-label">Emergency # : </label>
		</div>
		<div class="col-md-2">
	  		<p class="form-control-static">${volunteer.emergencyPhone}</p>
		</div>
  </div>

<h2>Notes/Comments</h2>
<div class="row">
	<div class="col-md-6">
  		<p class="form-control-static">${volunteer.notes}</p>
	</div>
</div>
<h2>Availability</h2>
<div>
	<table width="100%" border="1">
		<tr>
			<td width ="200" valign="top">
				<table>
					<tr><th>Monday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(monAvailability, 'non')}">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${monAvailability}" var="moA">	
						    	<tr><td align="center">${moA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>						
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Tuesday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(tueAvailability, 'non') }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${tueAvailability}" var="tuA">	
						    	<tr><td align="center">${tuA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Wednesday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(wedAvailability, 'non') }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${wedAvailability}" var="weA">	
						    	<tr><td align="center">${weA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
				<table>
					<tr><th>Thursday</th></tr>
					<c:choose>
						<c:when test="${fn:contains(thuAvailability, 'non') }">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${thuAvailability}" var="thA">	
						    	<tr><td align="center">${thA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
			<td width ="200" valign="top">
			
				<table>
					<tr><th>Friday</th></tr>		
					<c:choose>
						<c:when test="${fn:contains(friAvailability, 'non')}">
							<tr><td>N/A</td></tr>		
						</c:when>
						<c:otherwise >
						    <c:forEach items="${friAvailability}" var="frA">	
						    	<tr><td align="center">${frA}</td></tr>		
						    </c:forEach>							
						</c:otherwise>
					</c:choose>
				</table>
			</td>
		
		</tr>
		
	</table>
</div>
	<h2>Upcoming Visits</h2>
	<table  class="table table-stripe" width="970" border="1">
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
</div>

<!-- Modal -->
<div class="modal fade" id="displayActivityLog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
 	<div class="modal-dialog">
	    <div class="modal-content">
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
	</div>
</div>	

<div class="modal fade" id="displayMessage" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
	    <div class="modal-content">
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
	</div>
</div>

</div>

</body>
</html>