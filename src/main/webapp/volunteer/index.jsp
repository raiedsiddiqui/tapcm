<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css" rel="stylesheet" />
		  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js"></script>

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
		.span12{
			padding:0px 15px;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}
	</style>
	
	<script type="text/javascript">
		$(function(){
			$('#tp').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			
			$('#dp').datetimepicker({
				pickTime: false,
				startDate: new Date()
  			});
  			
 			$('#bookAppt').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
		});
	</script>
</head>
	
<body>	
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<div class="navbar">
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
							<li class="dropdown">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown">My Visits 1<b class="caret"></b></a>
								<ul class="dropdown-menu">
								<c:forEach items="${patients}" var="p">
									<c:choose>
										<c:when test="${not empty p.preferredName}">
											<li><a href="<c:url value="/patient/${p.patientID}"/>">${p.preferredName}</a></li>
										</c:when>
										<c:otherwise>
											<li><a href="<c:url value="/patient/${p.patientID}"/>">${p.displayName}</a></li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
								</ul>
							</li>
							<li><a href="<c:url value="/profile"/>">My Profile</a></li>
							<li><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
							<li><a href="<c:url value="/logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<h2>Welcome, ${name}</h2>
		<c:if test="${not empty announcements}">
		<div class="row-fluid">
			<div class="span12">
				<p><strong>Announcements</strong></p>
				<div class="accordion" id="announcementsAccordion">
					<c:forEach items="${announcements}" var="a">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-parent="announcementsAccordion" href="#msg${a.messageID}">${fn:substringAfter(a.subject, "ANNOUNCEMENT: ")}</a>
						</div>
						<div id="msg${a.messageID}" class="accordion-body collapse">
							<div class="accordion-inner">
								<p>${a.text}</p>
								<a class="btn btn-danger" href="<c:url value="/dismiss/${a.messageID}"/>">Dismiss</a>
							</div>
						</div>
					</div>
					</c:forEach>
				</div>
			</div>
		</div>
		</c:if>
		<c:if test="${not empty booked}">
			<div class="alert alert-info">The appointment was successfully booked</div>
		</c:if>
		<div class="row-fluid">
			<div class="span12">
				<h3>Appointments: <a href="#bookAppointment" role="button" class="btn btn-primary pull-right" data-toggle="modal">Book appointment</a></h3>
				<ul class="nav nav-tabs" id="appointmentSelect">
					<li class="active"><a href="#today" data-toggle="tab">Today</a></li>
					<li><a href="#all" data-toggle="tab">All</a></li>
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="today">	
					<c:choose>
						<c:when test="${not empty appointments_today}">
						<table class="table">
							<tr>
								<th>Patient</th>
								<th>Time</th>
								<th>Approval Status</th>
								<th>Delete</th>
							</tr>
							<c:forEach items="${appointments_today}" var="a">
							<tr>
								<td><a href="<c:url value="/patient/${a.patientID}"/>">${a.patient}</a></td>
								<td>${a.time}</td>
								<td>${a.status}</td>
								<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger" onclick="return confirm('Are you sure you want to remove this appointment?')">Delete</a></td>
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
							<tr>
								<th>Patient</th>
								<th>Time</th>
								<th>Approval Status</th>
								<th>Delete</th>
							</tr>
							<c:forEach items="${appointments_all}" var="a">
							<tr>
								<td>${a.patient}</td>
								<td>${a.date} ${a.time}</td>
								<td>${a.status}</td>
								<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger">Delete</a></td>
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
				<div id="dp" class="input-append">
					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate" readonly>
					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append">
					<input data-format="hh:mm:00" type="text" name="appointmentTime" readonly>
				    <span class="add-on">
				    	<i class="icon-time"></i>
				    </span>
				</div>
  			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    		<button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="appt-form" class="btn btn-primary">Book</button>
  		</div>
	</div>
</body>
</html>
