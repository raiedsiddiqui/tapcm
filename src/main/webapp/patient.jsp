<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
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
     					<div class="nav-collapse collapse">
						<ul class="nav">
     					<li><a class="brand" href="<c:url value="/"/>">Appointments</a></li>
     					
							<li class="dropdown">
<!-- 								<a href="#" class="dropdown-toggle" data-toggle="dropdown">Appointments<b class="caret"></b></a>
 -->								<ul class="dropdown-menu">
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
							<!-- <li><a href="<c:url value="/profile"/>">My Profile</a></li> -->
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
			<li> <a href="<c:url value="/client"/>">My Clients</a> </li>
<!-- 			<li> <a href="<c:url value="#"/>">Appt Date</a> </li> -->
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
			<li><a href="">${appointment.date}</a></li>
		</ul>

	<div id="visitandbook" class="span12 btn-group">
			<c:if test="${not empty patient.notes}">
				<a href="#modalNotes" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
			</c:if>
			<a href="<c:url value="/goMyOscarAuthenticate/${appointment.appointmentID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Authenticate MyOscar</a>
			<c:if test="${not empty appointment}">
				<a href="<c:url value="/visit_complete/${appointment.appointmentID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Visit Complete</a>
			</c:if>
			<a href="#bookAppointment" role="button" class="btn btn-primary pull-right lgbtn" data-toggle="modal">Book appointment</a>
	</div>	
	</div>
<!-- 	breadcrumb END-->	
	<div class="content">
		<div style="padding: 0px 15px;">
			<div class="row-fluid">
<!-- 				<div class="span3">
					<c:choose>
						<c:when test="${not empty patient.preferredName}">
							<h2>${patient.preferredName} (${patient.gender})</h2>
						</c:when>
						<c:otherwise>
							<h2>${patient.displayName} (${patient.gender})</h2>
						</c:otherwise>
					</c:choose>
				</div> -->
				<!-- <div id="visitandbook" class="span12 btn-group">
					<c:if test="${not empty patient.notes}">
						<a href="#modalNotes" class="btn btn-large btn-inverse lgbtn" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
					</c:if>
					<c:if test="${not empty appointment}">
						<a href="<c:url value="/visit_complete/${appointment.appointmentID}"/>" role="button" class="btn btn-primary pull-right lgbtn">Visit Complete</a>
					</c:if>
					<a href="#bookAppointment" role="button" class="btn btn-primary pull-right lgbtn" data-toggle="modal">Book appointment</a>
				</div> -->
			</div>
			<c:if test="${not empty completed}">
				<p class="alert alert-success">Completed survey: ${completed}</p>
			</c:if>
			<c:if test="${not empty inProgress}">
				<p class="alert alert-warning">Exited survey: ${inProgress}</p>
			</c:if>

			<!-- <div class="accordion" id="accordionSurveys">
			  <div class="accordion-group">
			    <div class="accordion-heading">
			      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordionSurveys" href="#collapseOne"> -->
			       
			        <div class="sheading">Active Surveys</div>


			        			      <!-- </a>
			    </div>
			    <div id="collapseOne" class="accordion-body collapse">
			      <div class="accordion-inner"> -->
			        <c:forEach items="${inProgressSurveys}" var="ips">
						<div class="row-fluid">
							<a href="<c:url value="/open_survey/${ips.resultID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
								<b>${ips.surveyTitle}</b><br/>
								${ips.description}
							</a>
						</div>
					</c:forEach>
<!-- 			      </div>
			    </div>
			  </div> -->
			  
<!-- 			  <div class="accordion-group">
			    <div class="accordion-heading"> -->
<!-- 			      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordionSurveys" href="#collapseTwo">
 -->			        <div class="sheading">Completed Surveys</div>
<!-- 			      </a>
			    </div> -->
<!-- 			    <div id="collapseTwo" class="accordion-body collapse">
 -->			      <c:forEach items="${completedSurveys}" var="cs">
						<div class="row-fluid">
							<div class="span12 survey-list inactiveclr" style="height:50px; margin-bottom:10px;">
								<b>${cs.surveyTitle}</b><br/>
								${cs.description}
							</div>
<!-- 						</div>
 -->					</c:forEach>
<!-- 			    </div>
			  </div>
 -->			  
			  <!-- <div class="accordion-group">
			    <div class="accordion-heading">
			      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordionSurveys" href="#collapseThree">
			        <div class="sheading">Assign Survey</div>
			      </a>
			    </div>
			    <div id="collapseThree" class="accordion-body collapse">
			      <c:forEach items="${surveys}" var="s">
					<div class="row-fluid">
						<a href="<c:url value="/assign_survey/${s.surveyID}/${patient.patientID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
							<b>${s.title}</b><br/>
							${s.description}
						</a>
					</div>
				  </c:forEach>
			    </div>
			  </div> -->
			</div>
			
			<!--
			<div class="sheading">Incomplete Surveys</div>
			<c:forEach items="${inProgressSurveys}" var="ips">
				<div class="row-fluid">
					<a href="<c:url value="/open_survey/${ips.resultID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
						<b>${ips.surveyTitle}</b><br/>
						${ips.description}
					</a>
				</div>
			</c:forEach>
			
			<div class="sheading">Completed Surveys</div>
			<c:forEach items="${completedSurveys}" var="cs">
				<div class="row-fluid">
					<div class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
						<b>${cs.surveyTitle}</b><br/>
						${cs.description}
					</div>
				</div>
			</c:forEach>
			
			<div class="sheading">Assign Survey</div>
			<c:forEach items="${surveys}" var="s">
				<div class="row-fluid">
					<a href="<c:url value="/assign_survey/${s.surveyID}/${patient.patientID}"/>" class="span12 btn btn-primary survey-list" style="height:50px; margin-bottom:10px;">
						<b>${s.title}</b><br/>
						${s.description}
					</a>
				</div>
			</c:forEach>
			-->
			

		</div>
		<!--
		<div class="span8">
			<h2>Pictures</h2>
			<form id="uploadPic" action="<c:url value="/upload_picture_for_patient/${patient.patientID}" />" method="POST" enctype="multipart/form-data">
				<label>Upload picture</label>
  				<input form="uploadPic" type="file" name="pic" accept="image/*" required /><br/>
  				<input form="uploadPic" type="submit" value="Upload" />
			</form>
			<c:choose>
				<c:when test="${not empty pictures}">
					<ul class="thumbnails">
						<c:forEach items="${pictures}" var="pic">
							<li>
	    						<a href="#${fn:replace(pic.path, ".", "-")}" data-toggle="lightbox">
	      							<img class="thumbnail" src="<c:url value="/uploads/${pic.path}"/>"/>
	    						</a>
	    						<a href="<c:url value="/remove_picture/${pic.pictureID}"/>" class="btn btn-danger" style="width:92%;">Remove</a>
	    						<div id="${fn:replace(pic.path, ".", "-")}" class="lightbox hide fade" role="dialog" aria-hidden="true" tab-index="-1">
	    							<div class="lightbox-content">
	    								<img src="<c:url value="/uploads/${pic.path}"/>">
	    							</div>
	    						</div>
	  						</li>
						</c:forEach>
					</ul>
				</c:when>
				<c:otherwise>
					<p>No pictures uploaded</p>
				</c:otherwise>
			</c:choose>
		</div>
		-->
	</div>

	<div id="modalNotes" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="warnLabel" aria-hidden="true">
		<div class="modal-header">
   			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
   			<h3 id="warnLabel" style="color:#000000;">
   				<c:choose>
					<c:when test="${not empty patient.preferredName}">
						${patient.preferredName}
					</c:when>
					<c:otherwise>
						${patient.displayName}
					</c:otherwise>
				</c:choose>
   			</h3>
  		</div>
  		<div class="modal-body">
  			<p class="text-warning">${patient.notes}</p>
  		</div>
  		<div class="modal-footer">
   			<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</button>
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
