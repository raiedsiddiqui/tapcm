<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Details of Appointment</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css" rel="stylesheet" />	
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/themes/default.css" id="theme_base">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/themes/default.date.css" id="theme_date">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/lib/themes/default.time.css" id="theme_time">
		
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>				
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>	
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
 	   <script>window.jQuery||document.write('<script src="tests/jquery.2.0.0.js"><\/script>')</script>
 	   <script src="${pageContext.request.contextPath}/resources/lib/picker.js"></script>
	    <script src="${pageContext.request.contextPath}/resources/lib/picker.date.js"></script>
	    <script src="${pageContext.request.contextPath}/resources/lib/picker.time.js"></script>
 	   <script src="${pageContext.request.contextPath}/resources/lib/legacy.js"></script>	
 	   <script type="text/javascript">
 	  function showV1Narratives(){
			document.getElementById("displayV1NarrativeDiv").style.display="block";
		}
		
		function showV2Narratives(){
			document.getElementById("displayV2NarrativeDiv").style.display="block";
		}
		
	</script>
 	   
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}

		div.modal-dialog {
			width:90%;
			height:600px;
		}

		.picker--opened .picker__holder {
			background: none;
		}

		.modal-content {
			height: 100%;
		}
	</style>

</head>
<body>
<div class="content">
<%@include file="navbar.jsp" %>

<div class="row-fluid">

<div><h4><a href="<c:url value="/manage_appointments"/>" >Appointment ></a> Client Name</h4></div>

<table>
	<tr>
		<td colspan="2">
			<label>&nbsp Client Name: </label>${appointment.patient}
		</td>		
	</tr>
	<tr>
		<td colspan="2">
			<label>&nbsp Date of Visit:</label>&nbsp ${appointment.date}
		</td>
	<c:if test="${isCentralAdmin}">
		<tr>
			<td colspan="2">
				<label >&nbsp Report :</label><a href="">View Report</a>
			</td>
		</tr>
	</c:if>	
	
	<tr>
		<td>
			<label>&nbsp Volunteer One: </label>&nbsp${appointment.volunteer}
		</td>
		<td>
			<label>&nbsp Volunteer Two :</label>&nbsp${appointment.partner}
		</td>
	</tr>
	<tr>
		<td>
			<label >&nbsp Narrative :</label><a href="#narrative1" class="btn btn-primary" data-toggle="modal">View</a>
		</td>
		<td>
			<label>&nbsp Narrative :</label><a href="#narrative2" class="btn btn-primary" data-toggle="modal">View</a>
	</tr>
</table>

	<h3>Alerts</h3>		${alerts}

	<h2>Detailed Log</h2>
	<table   width="970" border="1">
		<tr>
			
			<th width="500">User Activity</th>
			
			<th>Date</th>
			<th>Time</th>
			
		</tr>
		<c:forEach items="${activities}" var="a">
		
		<tr >
			<td>${a.description }</td>
			<td>${a.date}</td>			
			<td>${a.time}</td>
		</tr>
		</c:forEach>
	</table>	
</div>
</div>
<div class="row-fluid" id="displayV1NarrativeDiv" style="display:none">				
</div>
<div class="row-fluid" id="displayV2NarrativeDiv" style="display:none">				
</div>

<div class="modal fade" id="#narrative1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
 <div class="modal-dialog">
	    <div class="modal-content">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">Narrative</h3>
	  		</div>
	  		<div class="modal-body">
	  			<table border="1">
		  			<tr>
						<th width="200">Title</th>
						<th width="300">Content</th>
						<th width="400">Edit Date</th>
					</tr>
					<c:forEach items="${v1Narratives}" var="n">
					<tr>
						<td>${n.title}</td>
						<td>${al.contents}</td>
						<td>${al.editDate}</td>
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

<div class="modal fade" id="#narrative2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
 <div class="modal-dialog">
	    <div class="modal-content">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">Narrative</h3>
	  		</div>
	  		<div class="modal-body">
	  			<table border="1">
		  			<tr>
						<th width="200">Title</th>
						<th width="300">Content</th>
						<th width="400">Edit Date</th>
					</tr>
					<c:forEach items="${v2Narratives}" var="n">
					<tr>
						<td>${n.title}</td>
						<td>${al.contents}</td>
						<td>${al.editDate}</td>
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


</body>
</html>