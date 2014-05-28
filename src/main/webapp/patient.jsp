<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

 
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-lightbox.js"></script>


	  	<%@include file="volunteer/volunteer_head.jsp" %>


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
	<%@include file="volunteer/subNavi.jsp" %>
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
<div class="modal fade" id="bookAppointment" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
<!--  					<input data-format="yyyy-MM-dd" type="text" name="appointmentDate">
 --> 					<input class="datepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentDate">

					<span class="add-on">
						<i class="icon-calendar"></i>
					</span>
				</div>
				<label>Time:</label>
				<div id="tp" class="input-append">
<!--  					<input data-format="hh:mm:00" type="text" name="appointmentTime">
 --> 					<input data-format="HH:i:00" class="timepicker form-control" type="text" placeholder="Try me&hellip;" name="appointmentTime">

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

	<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script>window.jQuery||document.write('<script src="tests/jquery.2.0.0.js"><\/script>')</script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.date.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/picker.time.js"></script>
    <script src="${pageContext.request.contextPath}/resources/lib/legacy.js"></script>


	<script type="text/javascript">
		$(function(){
			// $('#tp').datetimepicker({
			// 	pickDate: false,
			// 	pickSeconds: false
			// });
			// $('#dp').datetimepicker({
			// 	pickTime: false,
			// 	startDate: new Date()
  	// 		});
  			
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
		

		$('.timepicker').pickatime({
		    // Escape any “rule” characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})
		
	</script>
  
</div>
</body>
</html>
