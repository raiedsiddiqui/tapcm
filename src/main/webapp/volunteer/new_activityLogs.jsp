<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import='java.text.SimpleDateFormat'%>
<%@ page import='java.util.Date' %>
<%SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String currentDate = sdf.format(new Date()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tapestry Volunteer Add Activity for Appointment</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<%@include file="volunteer_head.jsp" %>


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



</head>
<body>
<%@ include file="subNavi.jsp" %>
<div class="content">
			<form id="newActivityLog" action="<c:url value="/add_activityLogs"/>" method="post">
				<div class="row-fluid">
					<div class="col-md-6">
						<h4>New Activity Log</h4>
					</div>
				</div>

				<div class="row-fluid">
					<div class="col-md-3">
						<label>Date:</label>
							<div id="dp1" class="input-append">
								<!-- <input id="activityDate" name="activityDate" data-format="yyyy-MM-dd" type="text" value = "${activityLog.date}" required> -->

								<input id="activityDate" class="datepickera form-control" data-format="yyyy-MM-dd" type="text" placeholder="CLICK" name="activityDate" value = "${activityLog.date}" required>
									<span class="add-on">
										<i class="icon-calendar"></i>
									</span>
							</div>
					</div>

					<div class="col-md-3">						
						<label>Start Time:</label>
							<div id="tp1" class="input-append" role="dialog">
								<!-- <input data-format="hh:mm:00" type="text" name="activityStartTime" id="activityStartTime" value="${activityLog.startTime}" > -->

								<input id="activityStartTime" data-format="hh:mm:00" class="timepickera form-control" type="text" placeholder="Try me&hellip;" name="activityStartTime" value="${activityLog.startTime}">

					    		<span class="add-on">
					    			<i class="icon-time"></i>
					   			 </span>
							</div>
					</div>

					<div class="col-md-3">
						<label>End Time:</label>
							<div id="tp2" class="input-append" role="dialog">
								<!-- <input data-format="hh:mm:00" type="text" name="activityEndTime" id="activityEndTime" value="${activityLog.endTime}"> -->


								<input id="activityEndTime" data-format="hh:mm:00" class="timepickerb form-control" type="text" placeholder="Try me&hellip;" name="activityEndTime" value="${activityLog.endTime}">


					    		<span class="add-on">
					    			<i class="icon-time"></i>
					   			 </span>
							</div>
					</div>
				</div>
				
			<div class="row-fluid">
				<div class="col-md-3">
					<h3>Activity Description:</h3>
				</div>
			</div>
			
			<div class="row-fluid">
				<div class="col-md-3">
					<textarea name="activityDesc"  maxlength="50">${activityLog.description}</textarea>
					<input type="hidden" name="activityId" value="${activityLog.activityId}"/>	
				</div>
			</div>
		</form>
		</div>
	</div>

		<div class="row-fluid">
			<div class="col-md-3">
				<button id="newActivityLogButton" data-loading-text="Loading..." type="submit"  form="newActivityLog" class="btn btn-primary">Finish</button>
			</div>
		</div>
	</div>


	<script type="text/javascript">
		$(function(){
  			
 			$('#newActivityLogButton').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
		});


			$('.datepickera').pickadate({
		    // Escape any “rule” characters with an exclamation mark (!).
		    format: 'You selecte!d: dddd, dd mmm, yyyy',
		    formatSubmit: 'yyyy-mm-dd',
		    hiddenName: true
		   	// hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
			})
		

		$('.timepickera').pickatime({
		    // Escape any “rule” characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})

		$('.timepickerb').pickatime({
		    // Escape any “rule” characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})
	</script>
</body>
</html>