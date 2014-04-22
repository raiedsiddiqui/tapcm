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
		$(function(){
			$('#tp').datetimepicker({
				pickDate: false,
				pickSeconds: false
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
<div class="content">
<%@include file="navbar.jsp" %>
 <form id="appt-form" method="post" action="<c:url value="/schedule_appointment"/>">
 	<table>
 		<tr>
 			<td>
 				<label>Patient:</label>
 			</td>
 			<td>
 				<select name="patient" form="appt-form">
					<c:forEach items="${patients}" var="p">							
						<option value="${p.patientID}" <c:if test="${p.patientID eq selectedPatient}">selected</c:if>>${p.displayName}</option>
					</c:forEach>
				</select>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Volunteer:</label>
 			</td>
 			<td>
 				<select name="volunteer" form="appt-form">
					<c:forEach items="${allvolunteers}" var="v">
						<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq selectedVolunteer}">selected</c:if>>${v.displayName}</option>
					</c:forEach>
				</select>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Partner:</label>
 			</td>
 			<td>
 				<select name="partner" form="appt-form">
					<c:forEach items="${allvolunteers}" var="v">
						<option value="${v.volunteerId}" <c:if test="${v.volunteerId eq selectedPartner}">selected</c:if>>${v.displayName}</option>
					</c:forEach>
				</select>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<label>Date:</label>		
 			</td>
 			<td>
 				<div id="dp" class="input-append">
					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate">
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
 				<div id="tp" class="input-append">
					<input data-format="hh:mm:00" type="text" name="appointmentTime" value="${selectedTime}">				   
				</div>
 			</td>
 		</tr>
 		<tr>
 			<td>
 				<button type="button" >Close</button>
 			</td>
 			<td>
 				<button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book"  class="btn btn-primary">Book</button>
 				
 			</td>
 		</tr>
 	</table> 
				
  </form>
</div>
</body>
</html>