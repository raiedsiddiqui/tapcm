<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css" rel="stylesheet" />
				
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js"></script>
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
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
		<%@include file="navbar.jsp" %>

		<div class="row-fluid">
			<h2>Appointments</h2>
			<c:if test="${not empty success}">
				<div class="alert alert-info">Appointment has been successfully booked</div>
			</c:if>
			
			<c:forEach items="${patients}" var="p">
				<div class="accordion-group">
					<div class="accordion-heading">
				    	<a class="accordion-toggle" data-toggle="collapse" href="#collapse${p.firstName}${p.lastName}">
				        	${p.displayName}
				      	</a>
				    </div>
				    <div id="collapse${p.firstName}${p.lastName}" class="accordion-body collapse">
				    	<div class="accordion-inner">
				    		<div class="accordion-group">
				    			<c:forEach items="${appointments}" var="a">
				    				<c:if test="${p.patientID == a.patientID}">
				    					<a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary pull-right">Approve</a>
										<a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-danger pull-right">Decline</a>
										<a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger pull-right">Delete</a>
										<div class="accordion-heading">
									    	<a class="accordion-toggle" data-toggle="collapse" href="#collapse${a.appointmentID}">${a.date}</a>
									    </div>
									    
									    <div id="collapse${a.appointmentID}" class="accordion-body collapse">
				    						<div class="accordion-inner">
				    							<c:if test="${not empty a.comments}">
				    								Comments: ${a.comments} <br /><br />
				    							</c:if>
				    							<c:forEach items="${activities}" var="act">
				    								<c:if test="${a.appointmentID == act.appointment}">
				    									${act.description} <br />
				    								</c:if>
				    							</c:forEach>
				    						</div>
				    					</div>
				    				</c:if>
				    			</c:forEach>
				    		</div>
				    	</div>
				    </div>
				</div>
			</c:forEach>
			
			<br />
			<a href="#bookAppointment" class="btn btn-primary" data-toggle="modal">Book new appointment</a>
				    									
			<!-- <table class="table">
				<tr>
					<th>Volunteer</th>
					<th>Patient</th>
					<th>Time</th>
					<th>Status</th>
					<th>Incomplete/Complete</th>
					<th>Comments</th>
					<th>Approve</th>
					<th>Decline</th>
					<th>Delete</th>
				</tr>
				<c:forEach items="${appointments}" var="a">
				<tr>
					<td>${a.volunteer}</td>
					<td>${a.patient}</td>
					<td>${a.date} ${a.time}</td>
					<td>${a.status}</td>
					<td><c:choose>
						<c:when test="${!a.completed}">Incomplete</c:when>
						<c:otherwise>Complete</c:otherwise>
						</c:choose></td>
					<td>${a.comments}</td>
					<td><a href="<c:url value="/approve_appointment/${a.appointmentID}"/>" class="btn btn-primary">Approve</a></td>
					<td><a href="<c:url value="/decline_appointment/${a.appointmentID}"/>" class="btn btn-danger">Decline</a></td>
					<td><a href="<c:url value="/delete_appointment/${a.appointmentID}"/>" class="btn btn-danger">Delete</a></td>
				</tr>
				</c:forEach>
			</table>
			<a href="#bookAppointment" class="btn btn-primary" data-toggle="modal">Book new appointment</a>
		</div>
	</div> -->
	
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