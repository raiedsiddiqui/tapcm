<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<link href="http://cdn.jsdelivr.net/bootstrap.lightbox/0.6/bootstrap-lightbox.css"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
	<script src="http://cdn.jsdelivr.net/bootstrap.lightbox/0.6/bootstrap-lightbox.js"></script>
	

	<style type="text/css">
		html,body{
			height:100%;
		}
		.content{
			overflow-x:auto;
			border-radius:5px;
			-moz-border-radius:5px;
			-webkit-border-radius:5px;
			-o-border-radius:5px;
			-ms-border-radius:5px;
		}
		
		.btn-primary{
			margin-bottom:10px;
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
		function showChangePassword(){
			document.getElementById("changePassword").style.display="block";
		}
	</script>
	
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
		
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span4">
					<h2>${vol.name}'s Profile</h2>	
					<form id="volunteer-info" action="<c:url value="/update_user" />" method="POST">
						<fieldset>
							<legend>Edit details</legend>
							<label>Name</label>
							<input type="text" name="volName" value="${vol.name}" required />
							<label>Username</label>
							<input type="text" name="volUsername" value="${vol.username}" required />
							<label>Email</label>
							<input type="email" name="volEmail" value="${vol.email}" required />
							<br />
							<input type="submit" class="btn btn-primary" value="Save changes" />
						</fieldset>
					</form>
					<form id="change-password" method="post" action="<c:url value="/change_password"/>">
						<fieldset>
							<c:if test="${not empty errors}">
							<c:choose>
								<c:when test="${errors == 'confirm'}">
								<div class="alert alert-error">Passwords do not match</div>
								</c:when>
								<c:when test="${errors == 'current'}">
								<div class="alert alert-error">Password is incorrect</div>
								</c:when>
							</c:choose>
							</c:if>
							<legend>Change password</legend>
	  						<label>Current password:</label>
							<input type="password" name="currentPassword" required />
							<label>New password:</label>
							<input type="password" name="newPassword" required />
							<label>Confirm password:</label>
							<input type="password" name="confirmPassword" required />
							<br />
							<input type="submit" class="btn btn-primary" value="Save changes" />
						</fieldset>
	  				</form>
				</div>
				
				<div class="span8">
					<h2>Pictures</h2>
					<form id="uploadPic" action="<c:url value="upload_picture_to_profile" />" method="POST" enctype="multipart/form-data">
						<label>Upload picture</label>
  						<input form="uploadPic" type="file" name="pic" accept="image/*" required><br/>
  						<input form="uploadPic" type="submit">
					</form>
					<c:choose>
					<c:when test="${not empty pictures}">
					<ul class="thumbnails">
						<c:forEach items="${pictures}" var="pic">
					 	<li>
    						<a href="#${fn:replace(pic, ".", "-")}" data-toggle="lightbox">
      							<img class="thumbnail" src="<c:url value="/uploads/${pic}"/>"/>
    						</a>
    						<div id="${fn:replace(pic, ".", "-")}" class="lightbox hide fade" role="dialog" aria-hidden="true" tab-index="-1">
    							<div class="lightbox-content">
    								<img src="<c:url value="/uploads/${pic}"/>">
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
		</div>

	</div>

</body>
</html>
