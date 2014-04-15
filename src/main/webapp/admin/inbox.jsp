<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>

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

		tr:hover{
			background-color:#D9EDF7;
		}
	</style>
</head>
	
<body>	
	<div class="content">
		<%@include file="navbar.jsp" %>
		
		<div class="row-fluid">
			<div class="span12" style="padding:0px 15px;">
			<ul class="breadcrumb">
				<li><h2><a href="<c:url value="/inbox"/>">Inbox</a></h2></li>
				<a href="#modalSend" data-toggle="modal" class="btn btn-primary pull-right">Message Volunteers</a>
			</ul>
			<c:if test="${not empty success}">
			<div class="alert alert-info">
				<p>Message sent</p>
			</div>
			</c:if>
			<c:if test="${not empty failure}">
			<div class="alert alert-error">
				<p>Message failed to send: make sure you select a recipient</p>
			</div>
			</c:if>
				<c:choose>
					<c:when test="${not empty messages}">
					<table class="table">
						<tr>
							<th>Sender</th>
							<th>Subject</th>
							<th>Date</th>
							<th></th>
						</tr>
						<c:forEach items="${messages}" var="m">
						<c:choose>
							<c:when test="${! m.read}"><tr onClick="document.location='<c:url value="/view_message/${m.messageID}"/>';" style="font-weight:bold;"></c:when>
							<c:otherwise><tr onClick="document.location='<c:url value="/view_message/${m.messageID}"/>';"></c:otherwise>
						</c:choose>
							<td>${m.sender}</td>
							<td>${m.subject}</td>
							<td>${m.date}</td>
							<td><a href="<c:url value="/delete_message/${m.messageID}"/>" class="btn btn-danger">Delete</button><td>
						</tr>
						</c:forEach>
					</table>
					</c:when>
					<c:otherwise>
						<p style="margin-left:25px">You have no messages</p>
					</c:otherwise>
				</c:choose>
			</div>
			
			<div class="modal hide fade" id="modalSend">
				<div class="modal-header">
    				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
					<h3>Message Volunteers</h3>
				</div>
				<div class="modal-body">
					<form id="messageVolunteer" method="post" action="<c:url value="/send_message"/>">
						<label>Subject:</label>
						<input type="text" name="msgSubject" required/>
						<label>Send to:</label>
						<input type="checkbox" style="margin-bottom:10px;" value="true" name="isAnnouncement" onclick="document.getElementById('rec').disabled = !document.getElementById('rec').disabled;">Broadcast message</input><br/>
						<select multiple id="rec" name="recipient" form="messageVolunteer">
							<c:forEach items="${volunteers}" var="v">
							<option value="${v.volunteerId}">${v.firstName}</option>
							</c:forEach>
						</select><br />
						<label>Message:</label>
						<textarea name="msgBody"></textarea><br />
					</form>
				</div>
				<div class="modal-footer">
					<input type="submit" form="messageVolunteer" class="btn btn-primary" value="Send" />
  				</div>
			</div>
		</div>
	</div>


</body>
</html>
