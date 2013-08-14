<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
	
	<script type="text/javascript">
		function showAddUser(){
			document.getElementById("addUserDiv").style.display="block";
		}
	</script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
	</style>
</head>
	
<body>	
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Users <small>${total} users, ${active} enabled</small></h2>
			<c:if test="${not empty failed}">
				<div class="alert alert-error">Failed to create user: Username already exists</div>
			</c:if>
			<table class="table">
				<tr>
					<th>Name</th>
					<th>Username</th>
					<th>Role</th>
					<th>Status</th>
					<th></th>
				</tr>
				<c:forEach items="${users}" var="u">
				<c:if test="${u.role eq 'ROLE_ADMIN'}">
				<tr class="info">
				</c:if>
				<c:if test="${u.role eq 'ROLE_USER'}">
				<tr>
				</c:if>
					<td>${u.name}</td>
					<td>${u.username}</td>
					<td>
						<c:if test="${u.role eq 'ROLE_ADMIN'}">Administrator</c:if>
						<c:if test="${u.role eq 'ROLE_USER'}">Volunteer</c:if>
					</td>
					<td>
						<c:if test="${u.enabled eq 'true'}"><a href="<c:url value="/disable_user/${u.userID}"/>" class="btn btn-danger">Click to disable</a></c:if>
						<c:if test="${u.enabled eq 'false'}"><a href="<c:url value="/enable_user/${u.userID}"/>" class="btn">Click to enable</a></c:if>
					</td>
					<td><a href="<c:url value="/remove_user/${u.userID}"/>" class="btn btn-danger">Remove</a></td>
				</tr>
				</c:forEach>
			</table>
			<a href="#addUser" class="btn btn-primary" data-toggle="modal">Add new</a>
		</div>

		<div class="row-fluid" id="addUserDiv" style="display:none";>
			
		</div>
	</div>
	
	<!-- Modal -->
	<div id="addUser" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="modalHeader" aria-hidden="true">
  		<div class="modal-header">
    		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    		<h3 id="modalHeader">Add User</h3>
  		</div>
  		<div class="modal-body">
  			<form id="newUser" action="<c:url value="/add_user"/>" method="post">
						<label>Name:</label>
						<input type="text" name="name" required/>
						<label>Username:</label>
						<input type="text" name="username" required/>
						<label>Email</label>
						<input type="email" name="email" required/>
						<label>Role</label>
						<input type="radio" name="role" value="ROLE_ADMIN">Administrator</input> <br/>
						<input type="radio" name="role" value="ROLE_USER" checked>Volunteer</input> <br/>
			</form>
  		</div>
  		<div class="modal-footer">
    		<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
			<input class="btn btn-primary" form="newUser" type="submit" value="Add" />
  		</div>
	</div>
</body>
</html>
