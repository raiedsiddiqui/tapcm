<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
			
			background-color:#4A307B;
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
	</style>
</head>
	
<body>	
  <img src="${pageContext.request.contextPath}/resources/images/logo.png" />
	<div class="content">
		<div class="navbar navbar-inverse">
			<div class="navbar-inner">
				<div class="container">
					<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
        					<span class="icon-bar"></span>
        					<span class="icon-bar"></span>
       				 		<span class="icon-bar"></span>
     					</a>
     					
     					<a class="brand" href="/tapestry/caretaker/index">Home</a>
     					<div class="nav-collapse collapse">
						<ul class="nav">
							<li><a href="#">Angie O.</a></li>
							<li><a href="#">Kandi A.</a></li>
							<li><a href="#">Earl B.</a></li>
							<li><a href="#">Tess T.</a></li>
							<li><a href="#">My Profile</a></li>
							<li><a href="<c:url value="/j_spring_security_logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6" style="padding:0px 15px;">
				<h2>Welcome, ${name}</h2>
				<p><strong>Today's Appointments:</strong></p>
				<table class="table">
					<tr>
						<td>Kandi A.</td>
						<td>8:00</td>
					</tr>
					<tr>
						<td>Tess T.</td>
						<td>12:00</td>
					</tr>
				</table>
				<p><strong>Recent Activities:</strong></p>
				<table class="table">
					<tr>
						<td>June 9, 2013</td>
						<td>Filled out Survey 2 for Angie O.</td>
					</tr>
					<tr>
						<td>June 11, 2013</td>
						<td>Filled out general symptoms for Angie O.</td>
					</tr>
					<tr>
						<td>June 11, 2013</td>
						<td>Added new patient Tess T.</td>
					</tr>
				</table>
			</div>
			<div class="span4 offset2" style="padding:0px 15px;">
				<h2>Managing:</h2>
				<ul class="nav nav-stacked nav-tabs">
					<li><a href="1.html" style="background-color:#6aaf41;">Angie O.</a></li>
					<li><a href="2.html" style="background-color:#FBD500;">Kandi A.</a></li>
					<li><a href="3.html" style="background-color:#00ABD3;">Earl B.</a></li>
					<li><a href="4.html" style="background-color:#D61B83;">Tess T.</a></li>
					<li><a href="#">All</a></li>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>
