<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Tapestry Admin -- Report</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
	
	
	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		
		h2 span{
			background-color: black;
			font-weight: bold;
			text-align:center;
			color: white;
			width:1200px;
			}
		table{
			border:1px solid black;
			width:1000px;
		}
		
		td
		{		 
		   border-bottom:1pt solid black;
		}
		
		th
		{
		    border: 1px solid black;
		}
	</style>
</head>
<body>

	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
		<H2>Patient Information</H2><br>
		
			Patient: ${patient.firstName} ${patient.lastName}
			Address:
			MrP:
			Date of Visit: ${appointment.date }
			Time: ${appointment.time }
			Visit:
			
			<hr/>
			
			<H1><b>TAPESTRY REPORT:   </b></H1><br/>
			<h2 align="center"><span>PATIENT GOAL(S)</span></h2><br>
			Wait for GAS app
			<h2 align="center"><span>ALERTS: Consider Case Review with IP-TEAM</span></h2><br>
			<table>
				<c:forEach items="${report.alerts}" var="a">
					<tr>
						<td ><h3>${a}</h3></td>						
					</tr>
				</c:forEach>
			</table>
			
			<h2 align="center"><span>KEY OBSERVATIONS </span></h2><br>
			<div class="col-md-10">		
				<input type="textarea" class="form-control" maxlength="50" name="keyObservation" value="${appointment.keyObservation }"/>
			</div>
			<br/><hr/>
			
			<h2 align="center"><span>PLAN</span></h2><br>
			<table border="1">
				<c:forEach items="${plans}" var="p">
					<tr>
						<td width="30">${p.key}</td>
						<td>${p.value}</td>
					</tr>
				</c:forEach>
			</table>
			<br/><hr/>
		</div>
		
		<h2 align="center"><span>HEALTH-RELATED GOALS </span></h2><br>		
		<table>
			<c:forEach items="${report.healthGoals}" var="h">					
					<tr>						
						<td valign = "top" width="15"> ${h.key}</td>
						<td>${h.value}</td>
					</tr>
				</c:forEach>
		</table>		
		<h2 align="center"><span>TAPESTRY QUESTIONS </span></h2><br>		
		<table >
			<c:forEach items="${report.dailyActivities}" var="d">					
					<tr>
						<td valign = "top" width="30"> ${d.key}</td>
						<td>${d.value}</td>
					</tr>
					
				</c:forEach>
		</table>
		<h2 align="center"><span>VOLUNTEER INFORMATION & NOTES </span></h2><br>	
		<table border="1">
			<c:forEach items="${report.volunteerInformations}" var="v">					
					<tr>
						<td valign = "top" width="30"> ${v.key}</td>
						<td>${v.value}</td>
					</tr>
					
				</c:forEach>
		</table>
	</div>
</body>
</html>