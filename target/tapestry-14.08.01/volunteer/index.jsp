<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<title>TAPESTRY Healthy Lifestyle</title>


  	<%@include file="volunteer_head.jsp" %>


	<style type="text/css">
		html,body{
			height:100%;
		}
		.bootstrap-datetimepicker-widget{
			z-index:9999;
		}

		#welcometext {
			font-size: 22px;
			letter-spacing: 1px;
			line-height: 40px;
			font-weight: 200;
		}

		#startbuttonlink {
			color:white;
			background-color: #4A307A;
			padding: 10px 30px 10px 30px;
			font-size: 35px;;
		}

		a#startbuttonlink:hover {
			background-color: #6BB040;
			text-decoration: none;

		}

		.pname {
			margin-top: 5%;
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
	
	<div class="container">				
			
 		<h4 align="left">Welcome, ${name}</h4>		
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
			<div class="alert alert-info"><spring:message code="message_newAppointment_successful"/></div>
		</c:if>
		 <c:if test="${not empty noMachedTime }">
			 	<div  class="alert alert-info">
			 		<c:out value="${noMachedTime}" />
			 	</div>
			 </c:if>
		
		<div class="row-fluid">
			<!-- <div class="col-md-8"> -->
				<c:choose>
					<c:when test="${not empty patient}">
						<p> Select an appointment </p>
					</c:when>
					<c:otherwise>
						<!-- <p class="pageheader">Domains</p> -->

					</c:otherwise>
				</c:choose>
			<!-- </div> -->

			<!--<div class="col-md-2">
				 <a href="<c:url value="/view_activityLogs"/>" id="homebtn" class="btn">Activity Log</a> 
			</div>-->

			<!-- <div class="col-md-2">
				<a href="<c:url value="/view_narratives"/>" id="homebtn" class="btn">Narratives</a>
			</div> -->

		</div>

		<div class="row-fluid" style="margin: 0 auto; width: 70%; text-align: center;">	
		<p id="welcometext"> 
		Below you will see sections related to your health. In each section, you will complete some brief questions about your health goal and current health practices as well as get some tailored information sheets, suggestions and helpful links. You will receive a report within a week. The information you fill in will be shared with your family health team. You can start with any section you like; please compete all four (4) sections. If at any time you run into difficulty, please see our help resources or contact 905-525-9140 ext. 28508 for over-the-phone assistance. When you are ready please click "START" below.
		</p>

			<c:forEach items="${approved_appointments}" var="aa">
	<!-- 								<div class="pname">
					<button type="button" class="cbutton" onclick="location.href='<c:url value="/patient/${aa.patientID}?appointmentId=${aa.appointmentID}"/>'">${aa.patient} <span class="app-date">${aa.date}</span> <span class="tright"> ${aa.time}</button> -->

				<!-- custom -->
					
					<div class="pname">
						<a id="startbuttonlink" href="<c:url value="/patient/${aa.patientID}?appointmentId=${aa.appointmentID}"/>">
						START
						<!-- ${aa.patient} -->
							<!--<div class="row">
								<div id="startbutton"class="col-md-12 col-xs-12">
									
								</div>
								 <div class="col-sm-5 col-xs-5">
									${aa.date}
								</div>
								<div class="col-sm-1 col-xs-2">
									${aa.time}
								</div> 
							</div>-->
						</a>
					</div>
						
				<!-- custom -->
			</c:forEach>

		</div>

		<!-- <div class="row-fluid">				
			<p class="pageheader">Pending Completion</p>
			<c:forEach items="${pending_appointments}" var="pa">
				<div class="pname">
					<button type="button" class="pendingappt btn-lg btn-block pbutton">${pa.patient} <span class="app-date">${pa.date}</span> <span class="tright"> ${pa.time}</button>
				</div>
			</c:forEach>
		</div> -->

	
<!-- 		<div class="row-fluid">						
			<div class="panel-group" id="accordion"> 
			  <div class="panel panel-default">
			    <div class="panel-heading">
			      <h4 class="panel-title">
					<a class="accordion-toggle" data-toggle="collapse" href="#collapseDeclined">
			        	Declined Appointments
			      	</a>
			      </h4>
				</div>
			  
		        <div id="collapseDeclined" class="panel-collapse collapse">
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
		</div> -->
	</div>

<div id="footer">
	<div class="col-md-12">
	    <img id="logofhs" src="${pageContext.request.contextPath}/resources/images/fhs.png"/>
	    <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>
	</div>
</div>
</body>
</html>
