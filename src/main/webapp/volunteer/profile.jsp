<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>

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
					<form id="volunteer-info" action="<c:url value="/update_user/${vol.userID}" />" method="POST">
						<label>Name</label>
						<input type="text" name="volName" value="${vol.name}" />
						<label>Username</label>
						<input type="text" name="volUsername" value="${vol.username}" />
						<label>Email</label>
						<input type="text" name="volEmail" value="${vol.email}" />
						<br />
						<a href="#changePassword" role="button" class="btn btn-success" data-toggle="modal">Change password</a>
						<input type="submit" class="btn btn-primary" value="Save changes" />
					</form>
				</div>
				
				<div class="span8">
					<h2>Pictures</h2>
					<ul class="thumbnails">
					 	<li>
    						<a href="#">
      							<img class="thumbnail" src="http://placehold.it/280x230" alt="">
    						</a>
  						</li>
					 	<li>
    						<a href="#">
      							<img class="thumbnail" src="http://placehold.it/280x230" alt="">
    						</a>
  						</li>
  					 	<li>
    						<a href="#">
      							<img class="thumbnail" src="http://placehold.it/280x230" alt="">
    						</a>
  						</li>
  						<li>
    						<a href="#">
      							<img class="thumbnail" src="http://placehold.it/280x230" alt="">
    						</a>
  						</li>
  						<li>
    						<a href="#">
      							<img class="thumbnail" src="http://placehold.it/280x230" alt="">
    						</a>
  						</li>
					</ul>
				</div>
			</div>
		</div>
		
		<!-- Modal -->
		<div id="changePassword" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
	  		<div class="modal-header">
	    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
	    		<h3 id="modalHeader">Change password</h3>
	  		</div>
	  		<div class="modal-body">
	  			<form id="change-password" method="post" action="<c:url value="/change_password"/>">
	  				<label>Current password:</label>
					<input type="password" name="currentPassword"/>
					<label>New password:</label>
					<input type="password" name="newPassword"/>
					<label>New password:</label>
					<input type="password" name="confirmPassword"/>
	  			</form>
	  		</div>
	  		<div class="modal-footer">
	    		<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
	    		<button type="submit" value="Book" form="change-password" class="btn btn-primary">Change</button>
	  		</div>
		</div>
		
		
	</div>

</body>
</html>
