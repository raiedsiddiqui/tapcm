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
			
			background-color:#C01FB1;
		}
		
		.nav-tabs.nav-stacked > li > a{
			background: -moz-linear-gradient(left, rgba(0,0,0,0) 0%, rgba(0,0,0,0.65) 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, right top, color-stop(0%,rgba(0,0,0,0)), color-stop(100%,rgba(0,0,0,0.65))); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(left, rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(left, rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(left, rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* IE10+ */
			background: linear-gradient(to right, rgba(0,0,0,0) 0%,rgba(0,0,0,0.65) 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#00000000', endColorstr='#a6000000',GradientType=1 ); /* IE6-9 */
		}

		.content a{
			color:#ffffff;
		}

		.nav-tabs.nav-stacked > li > a:hover{
			color:#000000;
		}
		
		.btn-primary{
			margin-bottom:10px;
		}
		
		#changePassword{
			color:black;
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
					<form id="volunteer-info">
						<label>Name</label>
						<input type="text" name="volName" value="${vol.name}" />
						<label>Username</label>
						<input type="text" name="volUsername" value="${vol.username}" /><br />
						<a href="#changePassword" role="button" class="btn btn-success" data-toggle="modal">Change password</a><br />
					</form>
					<a href="#" class="btn btn-primary">Save changes</a>
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
