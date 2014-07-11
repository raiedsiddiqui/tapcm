<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Volunteer Appointment MyOscar Authentication</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />

		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.css" rel="stylesheet">
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />

		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-lightbox.js"></script>
		
		<!-- CUSTOM CSS -->
	<link href="${pageContext.request.contextPath}/resources/css/breadcrumb.css" rel="stylesheet" /> 
	<link href="${pageContext.request.contextPath}/resources/css/custom.css" rel="stylesheet" /> 
     

	  <link href='http://fonts.googleapis.com/css?family=Roboto+Slab' rel='stylesheet' type='text/css'>
	<!-- 	CUSTOM CSS END -->
		
		<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
/*			overflow-x:auto;
		overflow-y:auto;*/	
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;

		}
		.content a{
			color:#ffffff;
		}
		textarea{
			width:90%;
			margin-right:10px;
		}
		textarea { height: auto; width:auto }
		
		.modal-backdrop{
			z-index:0;
		}
		
		.lightbox{
			z-index:1;
		}
		.thumbnail{
			width:320px;
		}
		
	</style>
		
</head>
<body>
<div id="headerholder">	
<%@include file="subNavi.jsp" %>
</div>
<!-- 	breadcrumb START-->	
	<div id="crumbs"> 
		<ul>
			<li> <a href="<c:url value="/"/>">Appointments</a> </li>
			<li><a href="<c:url value="/?patientId=${patient.patientID}"/>">
				<c:choose>
					<c:when test="${not empty patient.preferredName}">
						<b>${patient.preferredName} (${patient.gender})</b>
					</c:when>
					<c:otherwise>
						<b>${patient.firstName}  ${patient.lastName}(${patient.gender})</b>
					</c:otherwise>
				</c:choose>
				</a>
			</li>			
			<li><a href="">	<b>Authenticate MyOscar</b>	</a> </li>
		</ul>

	</div>
<!-- 	breadcrumb END-->	

<h2>MyOscar Authentication</h2> 

<table width = "1020">
	<tr>
		<input type="textarea" style="width: 1200px; height: 80px;" value="${termsInfo }">

	</tr>
	<tr>
		<table>
			<tr>
				<td width="500"><label>e-signature</label></td>
				<td width="520">
				<form method="post" id="myoscarAuthentication-form" action="<c:url value="/authenticate_myoscar/${patient.volunteer}?patientId=${patient.patientID}"/>">
					<table>
						<tr>
							<td><label><h3>Client: </h3></label></td>
						</tr>
						<tr>
							<td><label>First Name:</label></td>
						</tr>
						<tr>
							<td><input id="client_first_name" name="client_first_name" value="${patient.firstName}"/></td>
						</tr>
						<tr>
							<td><label>Last Name:</label></td>
						</tr>
						<tr>
							<td><input id="client_last_name" name="client_last_name" value="${patient.lastName}"/></td>
						</tr>
						<tr>
							<td><label><h3>Volunteer: </h3></label></td>
						</tr>
						<tr>
							<td><label>First Name:: </label></td>							
						</tr>
						<tr>
							<td><input id="volunteer_first_name" name="volunteer_first_name" value="${vFirstName}"/></td>
						</tr>
						<tr>
							<td><label>Last Name:</label></td>
						</tr>
						<tr>
							<td><input d="volunteer_last_name" name="volunteer_last_name" value="${vLastName}"/></td>
						</tr>
						<tr>
							<td>&nbsp</td>
						</tr>
						<tr><!--  
							<td><a href="<c:url value="/authenticate_myoscar/${patient.volunteer}?patientId=${patient.patientID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Authenticate </a></td>
						-->
							<td>
								<input class="completevisitbtn btn btn-primary pull-right" type="submit" value="Authenticate" />
							</td>
						</tr>
					</table>
					</form>
				</td>
			</tr>
		</table>
	</tr>
</table>
  
</body>
</html>