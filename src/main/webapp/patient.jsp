<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet">
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>

	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			overflow-y:auto;
			color:#ffffff;
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;
			
			background: -moz-linear-gradient(-45deg,  rgba(0,0,0,0) 0%, rgba(0,0,0,0.65) 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, right bottom, color-stop(0%,rgba(0,0,0,0)), color-stop(100%,rgba(0,0,0,0.65))); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(-45deg,  rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(-45deg,  rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(-45deg,  rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* IE10+ */
			background: linear-gradient(135deg,  rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#00000000', endColorstr='#a6000000',GradientType=1 ); /* IE6-9 fallback on horizontal gradient */
			
			background-color:${patient.color};
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
	<img src="${pageContext.request.contextPath}/resources/images/logo.png"/>
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					<a class="brand" href="${pageContext.request.contextPath}/">Home</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="${pageContext.request.contextPath}/j_spring_security_logout">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div style="padding: 0px 15px;">
			<div class="row-fluid">
				<div class="span3"><h2>${patient.displayName}.</h2></div>
				<div class="span3 btn-group">
					<a href="#modalInfo" class="btn btn-large btn-inverse" role="button" data-toggle="modal"><i class="icon-info-sign icon-white"></i></a>
					<a href="#modalWarn" class="btn btn-large btn-inverse" role="button" data-toggle="modal"><i class="icon-exclamation-sign icon-white"></i></a>
				</div>
			</div>
			<div class="row-fluid">
				<a class="btn span3 btn-large btn-primary" href="#">EQ5D</a>
				<a class="btn span3 btn-large btn-info" href="#">Survey 1</a>
				<a class="btn span3 btn-large btn-warning" href="#">Survey 2</a>
				<a class="btn span3 btn-large btn-success" href="#">Survey 3</a>
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
</body>
</html>
