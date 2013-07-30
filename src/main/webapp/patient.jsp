<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet">
	<link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>

	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			overflow-y:auto;
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
	</style>
</head>
	
<body>
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					<a class="brand" href="<c:url value="/"/>">Home</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="<c:url value="/profile"/>">My Profile</a></li>
							<li><a href="<c:url value="/inbox"/>">Inbox <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div style="padding: 0px 15px;">
			<div class="row-fluid">
				<div class="span3"><h2>${patient.displayName}</h2></div>
				<div class="span3 btn-group">
					<a href="#modalInfo" class="btn btn-large btn-inverse" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
					<a href="#modalWarn" class="btn btn-large btn-inverse" role="button" data-toggle="modal"><i class="icon-exclamation-sign icon-white"></i></a>
					<a href="#modalCamera" class="btn btn-large btn-inverse" role="button" data-toggle="modal"><i class="icon-camera icon-white"></i></a>
				</div>
			</div>
			<div class="row-fluid">
				<c:forEach items="${surveys}" var="s">
					<a class="btn span3 btn-large btn-primary" href="/start_survey/${s.resultID}">${s.surveyTitle}</a>
				</c:forEach>
			</div>
			<br/>

			<div class="row-fluid" style="padding-bottom:15px; padding-right:15px;">
				<div class="span12">
					<p>General Symptoms:</p>
					<textarea></textarea><br />
					<button class="btn">Save</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Modal dialogs -->
	<div id="modalInfo" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="infoLabel" aria-hidden="true">
		<div class="modal-header">
   		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
   		<h3 id="infoLabel" style="color:#000000;">Patient Information: ${patient.displayName}</h3>
  		</div>
  		<div class="modal-body">
  			<div class="row">
  				<div class="span3"><img src="http://placehold.it/200x200"></img></div>
				<div class="span2"> 			
   				<p class="text-info">Gender: ${patient.gender}</p>
   				<p class="text-info">Age: ${patient.age}</p>
   			</div>
   		</div>
  		</div>
  		<div class="modal-footer">
   		<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</button>
  		</div>
	</div>
	<div id="modalWarn" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="warnLabel" aria-hidden="true">
		<div class="modal-header">
   			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
   			<h3 id="warnLabel" style="color:#000000;">Patient Warnings: ${patient.displayName}</h3>
  		</div>
  		<div class="modal-body">
  			<p class="text-warning"></p>
  		</div>
  		<div class="modal-footer">
   			<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</button>
  		</div>
	</div>
	<div id="modalCamera" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="cameraLabel" aria-hidden="true">
		<div class="modal-header">
   			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
			<h3 id="cameraLabel" style="color:#000000;">Take Picture</h3>
		</div>
		<div class="modal-body">
			<input type="file" accept="image/*" capture="camera">
		</div>
		<div class="modal-footer">
			<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
			<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Save</button>
		</div>
	</div>
</body>
</html>
