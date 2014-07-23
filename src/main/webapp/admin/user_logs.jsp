<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
	
	<script type="text/javascript">
		function printTable(){
			$('.table').printThis();
		}
	</script>
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Activity Log <button class="btn btn-primary pull-right" onclick="printTable()" style="margin-right:25px;">Print</button></h2>
			<div class="row-fluid">
				<form action="user_logs" method="post">
					<fieldset>
						<legend>Search Logs</legend>
						<label>Name:</label>
						<input type="text" name="name" required/>
						<input class="btn btn-primary" type="submit" value="Search" />
					</fieldset>
				</form>
			</div>
			<table class="table">
				<tr>
					<th>User Name</th>
					<th>Activity</th>
					<th>Date</th>
				</tr>
				<c:forEach items="${logs}" var="l">
					<tr>
						<td>${l.userName}</td>
						<td>${l.description}</td>
						<td>${l.date}</td>
					</tr>
				</c:forEach>
			</table>
			<div class="pagination text-center">
				<ul class="pagination">
					<c:forEach begin="1" end="${numPages}" var="i">
					<li><a href="<c:url value="/user_logs/${i}"/>">${i}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
</body>
</html>
