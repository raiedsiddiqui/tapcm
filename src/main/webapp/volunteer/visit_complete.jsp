<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

		<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />

		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.css" rel="stylesheet">
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-lightbox.js"></script>
	

	<link href="${pageContext.request.contextPath}/resources/css/breadcrumb.css" rel="stylesheet" /> 
	<link href="${pageContext.request.contextPath}/resources/css/custom.css" rel="stylesheet" />      


	  		<link href='http://fonts.googleapis.com/css?family=Roboto+Slab' rel='stylesheet' type='text/css'>

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
		
		.btn-primary{
			margin-bottom:10px;
		}
		
		.modal-backdrop{
			z-index:0;
		}
		
		.lightbox{
			z-index:1;
		}
		.thumbnail{
			width:320px;
		}
		input[type="radio"] {
			height:50px;	
			width:50px;
		}
	</style>
	
	<script type="text/javascript">
		function showChangePassword(){
			document.getElementById("changePassword").style.display="block";
		}
	</script>
	
</head>
	
<body>
<div id="headerholder">	
  <img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
    <!-- <img id="logofam" src="<c:url value="/resources/images/fammed.png"/>" /> -->
  	<img id="logofhs" src="<c:url value="/resources/images/fhs.png"/>" />
  	<img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>
  <div class="navbar">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					
<!--      					<a class="brand" href="<c:url value="/"/>">Home</a>
 -->     					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a class="brand" href="<c:url value="/"/>">Appointments</a></li>
<!-- 							<li><a href="<c:url value="/profile"/>">My Profile</a></li>-->							
						<li><a href="<c:url value="/"/>">Activity Log <!-- <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if> --></a></li>
						<li><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
							<li><a href="<c:url value="/logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>
			</div>

	</div>
</div>
<!-- 	breadcrumb START-->	
	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>">My Clients</a> </li>
			<li><a href="<c:url value="/?patientId=${patient.patientID}"/>">
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
			<li><a href="<c:url value="/patient/${patient.patientID}?appointmentId=${appointment.appointmentID}"/>">${appointment.date}</a></li>
			<li><a href="">
					<b>Visit Complete</b>
				</a>
			</li>
		</ul>	
	</div>
<!-- 	breadcrumb END-->
	<div class="content">
		<form id="appt-form" method="post" action="<c:url value="/complete_visit/${appointment.appointmentID}"/>">
	 		<h2> Date: ${appointment.date} <h2> <!-- <br /> 
 	 		<input id="contactedcheck" type="radio" name="contacted_admin" id="contacted_admin" value="true" /> Contacted Ernie
	 		<br />-->
	 		<h3> What does the clinic need to know about today's visit?
			<br/>Example: unsafe walking, poor access to food, patient not behaving appropriately </h3><br />
	 		<textarea  name="visitAlerts" id="visitAlerts"></textarea><br />
	 		<a href="<c:url value="/patient/${appointment.patientID}?appointmentId=${appointment.appointmentID}"/>" class="tleft btn btn-danger">Cancel</a>
 			<input class="completevisitbtn btn btn-primary pull-right" type="submit" value="Submit" />
 		</form>
 		
 	</div>
 </body>
 </html>