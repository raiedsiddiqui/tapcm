<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/custom.css" rel="stylesheet" />      
		<link href="${pageContext.request.contextPath}/resources/css/breadcrumb.css" rel="stylesheet" />      

		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js"></script>

		<!-- FONTS -->
		<link href='http://fonts.googleapis.com/css?family=Krona+One' rel='stylesheet' type='text/css'>
		<!-- FONTS -->
  		<link href='http://fonts.googleapis.com/css?family=Roboto+Slab' rel='stylesheet' type='text/css'>

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
		.span12{
			padding:0px 15px;
		}
	</style>
</head>

<body>	
<div id="headerholder">	
  <img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
  		<div class="navbar">
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
							<li><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
							<li><a href="<c:url value="/logout"/>">Log Out</a></li>
						</ul>
					</div>
				</div>	
			</div>
		</div>
	</div>

	<!-- 	breadcrumb START-->	
		<div id="crumbs"> 
			<ul>
				<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>
			</ul>	
		</div>
	<!-- 	breadcrumb END-->	
	
	<div class="content">
		<div class="row-fluid">
			<div class="span12">
				<h3>Select a client</h3>
				<div class="tab-content">
					<div class="tab-pane active" id="all">
						<c:forEach items="${clients}" var="c">
							<div class="pname">
								<c:choose>
									<c:when test="${not empty p.preferredName}">
										<button type="button" class="btn btn-primary btn-lg btn-block cbutton" onclick="location.href='<c:url value="/patient/${c.patientID}"/>'">${c.preferredName}</button>
									</c:when>
									<c:otherwise>
										<button type="button" class="btn btn-primary btn-lg btn-block cbutton" onclick="location.href='<c:url value="/patient/${c.patientID}"/>'">${c.displayName}</button>
									</c:otherwise>
								</c:choose>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>