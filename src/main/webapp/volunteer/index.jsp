<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<title>Tapestry</title>


  	<%@include file="volunteer_head.jsp" %>


	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
/*			overflow-x:auto;
*/			border-radius:5px;
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
	// 	$(function(){
	// 		$('#tp').datetimepicker({
	// 			pickDate: false,
	// 			pickSeconds: false
	// 		});
			
	// 		$('#dp').datetimepicker({
	// 			pickTime: false,
	// 			startDate: new Date()
 //  			});
  			
 // 			$('#bookAppt').click(function(){
	// 	        var btn = $(this)
	// 	        btn.button('loading')
	// 	        setTimeout(function () {
	// 	            btn.button('reset')
	// 	        }, 3000)
	// 	    });
	// 	});


	function activenav() {
		var x = document.getElementById("navhome");
		x.style.backgroundColor="rgb(100, 100, 100)";
	}


	</script>
</head>
	
<body onload="activenav()">	

<%@include file="subNavi.jsp" %>


<!-- 	breadcrumb START-->	
<!-- 	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>
			<c:if test="${not empty patient}">
				<li><a href="">
						<c:choose>
							<c:when test="${not empty patient.preferredName}">
								<b>${patient.preferredName} (${patient.gender})</b>
							</c:when>
							<c:otherwise>
								<b>${patient.displayName} (${patient.gender})</b>
							</c:otherwise>
						</c:choose>
					</a>
				</li>
			</c:if>		
		</ul>


	</div> -->

<!-- 	<div id="visitandbook" class="span12 btn-group">
		<a href="#bookAppointment" role="button" class="btn btn-primary pull-right" data-toggle="modal">Book appointment</a>
	</div> -->
<!-- 	breadcrumb END-->	
	
	<div class="content">
<!-- 		<h2>Welcome, ${name}</h2>-->		
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
			<div class="col-md-6">
			
				<button id="homebtn"> <span class="glyphicon glyphicon-pencil"></span> <a href="<c:url value="/view_activityLogs"/>">Activity Log</a></button>
			
<!-- 			<a href="<c:url value="/view_activityLogs"/>" class="btn btn-primary">Activity Log</a>
-->
			</div>

			<div class="col-md-6">
				<button id="homebtn"> <a href="<c:url value="/view_narratives"/>">Narratives</a></button>
			</div>
			
		</div>

		<div class="row-fluid">
			<div class="span12">
				<c:choose>
					<c:when test="${not empty patient}">
						<h3> Select an appointment </h3>
					</c:when>
					<c:otherwise>
						<h3>My Appointments: </h3>

					</c:otherwise>
				</c:choose>
<!-- 				<ul class="nav nav-tabs" id="appointmentSelect">
					<li class="active"><a href="#today" data-toggle="tab">Today</a></li>
 					<li class="active"><a href="#all" data-toggle="tab">All</a></li>
				</ul>-->
				<div class="tab-content">
					<div class="tab-pane active" id="all">
<!-- 						<div class="sheading"><h4>Approved Appointments:</h4></div>
 -->						<table class="table">
							<c:forEach items="${approved_appointments}" var="aa">
								<div class="pname">
<!-- 									<div class="app-date"> ${aa.date} </div>
 -->									<button type="button" class="cbutton" onclick="location.href='<c:url value="/patient/${aa.patientID}?appointmentId=${aa.appointmentID}"/>'">${aa.patient} <span class="app-date">${aa.date}</span> <span class="tright"> ${aa.time}</button>
								</div>
							</c:forEach>
						</table>


			      		<h4 class="panel-title">
			        		Pending Completion
			      		</h4>
						<c:forEach items="${pending_appointments}" var="pa">
							<div class="pname">
								<button type="button" class="pendingappt btn-lg btn-block cbutton">${pa.patient} <span class="app-date">${pa.date}</span> <span class="tright"> ${pa.time}</button>
							</div>
						</c:forEach>


						
						<div class="panel-group" id="accordion"> 
						  <div class="panel panel-default">
						    <div class="panel-heading">
						      <h4 class="panel-title">
								<a class="accordion-toggle" data-toggle="collapse" href="#collapseDeclined">
						        	Declined Appointments
						      	</a>
						      </h4>
    						</div>
						  
					        <div id="collapseOne" class="panel-collapse collapse">
	  							<div class="panel-body">
				    				<c:forEach items="${declined_appointments}" var="da">
										<div class="pname">
										<div class="app-date"> ${da.date} </div>
											<button type="button" class="inactiveclr btn-lg btn-block cbutton">${da.patient} <span class="tright"> ${da.time}</button>
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>

						    
<!-- 					<div class="tab-pane active" id="today">	
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
					</div>	 -->				
<!--					<c:choose>
						<c:when test="${not empty appointments_patient}">
							<table class="table">
								<c:forEach items="${appointments_patient}" var="a">
									<div class="pname">
										<div class="app-date"> ${a.date} </div>
										<button type="button" class="btn btn-primary btn-lg btn-block cbutton" onclick="location.href='<c:url value="/patient/${a.patientID}?appointmentId=${a.appointmentID}"/>'">${a.patient} <span class="tright"> ${a.time}</button>
									</div>
								</c:forEach>
							</table>
						</c:when>
						
						<c:when test="${not empty appointments_all}">
							<table class="table"> -->
		<!-- 						<tr>
									<th>Patient</th>
									<th>Time</th>
									<th>Approval Status</th>
									<th>Delete</th>
								</tr> -->
	<!--							<c:forEach items="${appointments_all}" var="a"> -->
	<!-- 							<tr>
									<td><a href="<c:url value="/patient/${a.patientID}"/>">${a.patient}</a></td>
									<td>${a.date} ${a.time}</td>
									<td>${a.status}</td>
									<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger">Delete</a></td>
								</tr> -->
	
	<!--							<div class="pname">
									<div class="app-date"> ${a.date} </div> -->
	<!-- 								<div class="patient-info"><a class="patientinfo" href="<c:url value="/patient/${a.patientID}"/>">${a.patient}</a></div>
	 -->								
	<!--								<button type="button" class="btn btn-primary btn-lg btn-block cbutton" onclick="location.href='<c:url value="/patient/${a.patientID}?appointmentId=${a.appointmentID}"/>'">${a.patient} <span class="tright"> ${a.time}</button>								 
	
								</div>
								</c:forEach>
							</table>
						</c:when>
						
						<c:otherwise>
							<p style="margin-left:25px">No appointments</p>
						</c:otherwise>
					</c:choose> -->
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- OLD Modal -->
<!-- 	<div id="bookAppointment" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
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
				<div id="tp" class="input-append" role="dialog">
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
	</div> -->

<!-- OLD MODAL-->

<!-- <div class="modal fade" id="bookAppointment" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Book Appointment</h4>
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
					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate">
					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append">
					<input data-format="hh:mm:00" type="text" name="appointmentTime">
				    <span class="add-on">
				    	<i class="icon-time"></i>
				    </span>
				</div>
  			</form>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="appt-form" class="btn btn-primary">Book</button>
      </div>
    </div>
  </div>
</div> -->




<!-- <div class="modal fade" id="bookAppointment" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Book Appointment</h4>
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
				<div id="dp" class="input-append"> -->
<!--  					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate">
 --> <!-- 					<input class="datepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentDate">

					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append"> -->
<!--  					<input data-format="hh:mm:00" type="text" name="appointmentTime">
 --> 					<!-- <input data-format="HH:i:00" class="timepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentTime">

				    <span class="add-on">
				    	<i class="icon-time"></i>
				    </span>
				</div>
  			</form>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="appt-form" class="btn btn-primary">Book</button>
      </div>
    </div>
  </div> 


  
</div>-->
</body>
</html>
