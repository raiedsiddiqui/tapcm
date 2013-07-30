<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>

	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;
		}
	</style>
</head>
	
<body>	
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					
     					<a class="brand" href="<c:url value="/"/>">Home</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
							<c:forEach items="${patients}" var="p">
							<li><a href="<c:url value="/patient/${p.patientID}"/>">${p.displayName}</a></li>
							</c:forEach>
							<li><a href="<c:url value="/profile"/>">My Profile</a></li>
							<li><a href="<c:url value="/inbox"/>">Inbox <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<h2>Welcome, ${name}</h2>
		<div class="row-fluid">
			<div class="span12" style="padding:0px 15px;">
				<p>
					<strong>Appointments:</strong>
					<a href="#bookAppointment" role="button" class="btn btn-primary" data-toggle="modal">Book appointment</a>
				</p>
				<ul class="nav nav-tabs" id="appointmentSelect">
					<li class="active"><a href="#today" data-toggle="tab">Today</a></li>
					<li><a href="#all" data-toggle="tab">All</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="today">	
					<c:choose>
						<c:when test="${not empty appointments_today}">
						<table class="table">
							<c:forEach items="${appointments_today}" var="a">
							<tr>
								<td>${a.patient}</td>
								<td>${a.time}</td>
							</tr>
							</c:forEach>
						</table>
						</c:when>
						<c:otherwise>
							<p style="margin-left:25px">No appointments for today</p>
						</c:otherwise>
					</c:choose>
					</div>					
					<div class="tab-pane" id="all">	
					<c:choose>
						<c:when test="${not empty appointments_all}">
						<table class="table">
							<c:forEach items="${appointments_all}" var="a">
							<tr>
								<td>${a.patient}</td>
								<td>${a.date} ${a.time}</td>
							</tr>
							</c:forEach>
						</table>
						</c:when>
						<c:otherwise>
							<p style="margin-left:25px">No appointments</p>
						</c:otherwise>
					</c:choose>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12" style="padding:0px 15px;">
				<p><strong>Recent Activities:</strong></p>
				<table class="table">
				<c:choose>
						<c:when test="${not empty appointments_all}">
						<table class="table">
						<c:forEach items="${activities}" var="a">
							<tr>
								<td>${a.date}</td>
								<td>${a.description}</td>
							</tr>
							</c:forEach>
						</table>
						</c:when>
						<c:otherwise>
							<p style="margin-left:25px">No activities</p>
						</c:otherwise>
					</c:choose>
				</table>
			</div>
		</div>
	</div>

	<!-- Modal -->
	<div id="bookAppointment" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Book Appointment</h3>
  		</div>
  		<div class="modal-body">
  			<form id="appt-form" method="post" action="<c:url value="/book_appointment"/>">
  				<label>With patient:</label>
				<select name="patient" form="appt-form">
					<c:forEach items="${patients}" var="p">
					<option value="${p.patientID}">${p.displayName}</option>
					</c:forEach>
				</select><br />
				<label>Date:</label>
				<input type="date" name="appointmentDate"/>
				<label>Time:</label>
				<input type="time" name="appointmentTime"/>
  			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    		<button type="submit" value="Book" form="appt-form" class="btn btn-primary">Book</button>
  		</div>
	</div>
</body>
</html>
