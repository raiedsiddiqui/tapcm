<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet">
	<link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
	<script src="http://cdn.jsdelivr.net/bootstrap.lightbox/0.6/bootstrap-lightbox.js"></script>

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
							<li><a href="<c:url value="/logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div style="padding: 0px 15px;">
			<div class="row-fluid">
				<div class="span3"><h2>${patient.displayName} (${patient.gender})</h2></div>
				<div class="span3 btn-group">
					<c:if test="${not empty patient.warnings}">
					<a href="#modalWarn" class="btn btn-large btn-inverse" role="button" data-toggle="modal"><i class="icon-exclamation-sign icon-white"></i></a>
					</c:if>
				</div>
			</div>
			
			<c:set var="count" value="1" scope="page" />
			<c:forEach items="${surveys}" var="s">
			<div class="row-fluid">
				<a class="btn span12 btn-large btn-primary" href="<c:url value="/open_survey/${s.resultID}"/>">Survey #${count}</a></td>
				<c:set var="count" value="${count + 1}" scope="page"/>
			</div>
			</c:forEach>
		</div>
		<div class="span8">
					<h2>Pictures</h2>
					<form id="uploadPic" action="<c:url value="/upload_picture_for_patient/${patient.patientID}" />" method="POST" enctype="multipart/form-data">
						<label>Upload picture</label>
  						<input form="uploadPic" type="file" name="pic" accept="image/*" required><br/>
  						<input form="uploadPic" type="submit">
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
	</div>

	<div id="modalWarn" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="warnLabel" aria-hidden="true">
		<div class="modal-header">
   			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
   			<h3 id="warnLabel" style="color:#000000;">Patient Warnings: ${patient.displayName}</h3>
  		</div>
  		<div class="modal-body">
  			<p class="text-warning">${patient.warnings}</p>
  		</div>
  		<div class="modal-footer">
   			<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Close</button>
  		</div>
	</div>
</body>
</html>
