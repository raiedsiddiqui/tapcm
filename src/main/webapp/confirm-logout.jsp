<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
			<link href="${pageContext.request.contextPath}/resources/css/custom.css" rel="stylesheet" />     
</head>
	
<body>
<div id="headerholder">	
	<img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
	<!-- <img id="logofam" src="<c:url value="/resources/images/fammed.png"/>" /> -->
	<img id="logofhs" src="<c:url value="/resources/images/fhs.png"/>" />
</div>
<div id="logouttext">
	<h2 id="logoutheader">Are you sure you want to log out?</h2>
	<button class="btn btn-info" onClick="history.back()" style="float:left">Cancel</button>
	<a href="<c:url value="/j_spring_security_logout"/>" class="btn btn-primary logoutbtn" style="float:right">Yes</a>
</div>
</body>
</html>
