<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Scheduler in Appointment</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>

	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/themes/default.css" id="theme_base">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/themes/default.date.css" id="theme_date">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/themes/default.time.css" id="theme_time">

	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>	

	</head>
<body>
<div class="content">
	<%@include file="navbar.jsp" %>
	<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointments</a> > Scheduler</h4></div>	
	<h3>Appointment Scheduler</h3>
 <form id="appt-form" method="post" action="<c:url value="/find_volunteers"/>">
 	<table> 		
 		<tr>
 			<td>
 				<label>Date:</label>		
 			</td>
 			<td>
 				<div id="dp" class="input-append">
					<input class="datepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentDate">
					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Time:</label>
 			</td>
 			<td>
 				<div id="time" class="input-append">					
					<select name="appointmentTime" form="appt-form" class="form-control">
						<option value="0" >...</option>
						<option value="1" >08:00 AM</option>
						<option value="2" >08:30 AM</option>		
						<option value="3" >09:00 AM</option>
						<option value="4" >09:30 AM</option>					
						<option value="5" >10:00 AM</option>
						<option value="6" >10:30 AM</option>
						<option value="7" >11:00 AM</option>
						<option value="8" >11:30 AM</option>							
						<option value="9" >13:00 PM</option>
						<option value="10" >13:30 PM</option>
						<option value="11" >14:00 PM</option>
						<option value="12" >14:30 PM</option>							
						<option value="13" >15:00 PM</option>
						<option value="14" >15:30 PM</option>
						<option value="15" >16:00 PM</option>											
					</select>			   
				</div>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<a href="<c:url value="/manage_appointments"/>" class="btn btn-primary" >Cancel</a> 	
 			</td>
 			<td>
 				<button id="goButton" data-loading-text="Loading..." type="submit" value="Go..."  class="btn btn-primary">Go</button>
 				
 			</td>
 		</tr>
 	</table> 
					
  </form>
  
	<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script>window.jQuery||document.write('<script src="tests/jquery.2.0.0.js"><\/script>')</script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.date.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.time.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/legacy.js"></script>
  		<script type="text/javascript">
		$(function(){
  			
  			$('#bookAppt').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });


		});

		    $('.datepicker').pickadate({
			    // Escape any “rule” characters with an exclamation mark (!).
			    format: 'You selecte!d: dddd, dd mmm, yyyy',
			    formatSubmit: 'yyyy/mm/dd',
			    hiddenName: true
			   	// hiddenPrefix: 'prefix__',
			    // hiddenSuffix: '__suffix'
			})
		
	</script>
</div>
</body>
</html>