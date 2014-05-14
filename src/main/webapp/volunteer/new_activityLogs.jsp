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

</head>
<body>
<%@ include file="subNavi.jsp" %>
<div class="content">
		<div class="row-fluid">
			<div class="span12">
			<form id="newActivityLog" action="<c:url value="/add_activityLogs"/>" method="post">
			<table>
				<tr><td colspan="3"><h3><a href="<c:url value="/view_activityLogs"/>">Activity Log</a>  > New Activity  </h3></td></tr>
				<tr><td><label>Date:</label>
						<div id="dp" class="input-append">
							<input id="activityDate" name="activityDate" data-format="yyyy-MM-dd" type="text" value = <%= currentDate%> readonly required>
								<span class="add-on">
									<i class="icon-calendar"></i>
								</span>
						</div>
				    </td>
					<td><label>Start Time:</label>
						<div id="tp1" class="input-append" role="dialog">
							<input data-format="hh:mm:00" type="text" name="activityStartTime" id="activityStartTime" readonly>
				    		<span class="add-on">
				    			<i class="icon-time"></i>
				   			 </span>
						</div>
					</td>
					<td><label>End Time:</label>
						<div id="tp2" class="input-append" role="dialog">
							<input data-format="hh:mm:00" type="text" name="activityEndTime" id="activityEndTime" readonly>
				    		<span class="add-on">
				    			<i class="icon-time"></i>
				   			 </span>
						</div>
					</td>
				</tr>
				<tr><td colspan ="3"><hr/></td></tr>
				<tr><td colspan = "3"><label><h2>Activity Description:</h2></label></td></tr>
				
				<tr><td colspan = "3"><textarea name="activityDesc" maxlength="50"></textarea></td></tr>
				
				
			</table>
			</form>
				
			</div>
		</div>
		<div>
		<button id="newActivityLogButton" data-loading-text="Loading..." type="submit"  form="newActivityLog" class="btn btn-primary">Finish</button>
		
		</div>
		
	</div>

</body>
</html>