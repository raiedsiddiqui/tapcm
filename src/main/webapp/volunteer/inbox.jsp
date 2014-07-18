<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Inbox</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
 	<%@include file="volunteer_head.jsp" %>
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
<div id="headerholder">	
	<%@include file="subNavi.jsp" %>
</div>

<!-- 	breadcrumb START-->	
<!-- 	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>
		</ul>	
	</div> -->
<!-- 	breadcrumb END-->	
	<div class="content">
		</div>
		<div class="row-fluid">
			<div class="span12" style="padding:0px 15px;">
			<ul class="breadcrumb">
				<li><h2><a href="<c:url value="/inbox"/>">Inbox</a></h2></li>
				<a href="#modalSend" data-toggle="modal" class="btn btn-primary pull-right">Message Administrator</a>
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
<!-- 			<div class="modal hide fade" id="modalSend">
				<div class="modal-header">
    				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
					<h3>Message Administrator</h3>
				</div>
				<div class="modal-body">
					<form id="messageAdministrator" method="post" action="<c:url value="/send_message"/>">
						<label>Subject:</label>
						<input type="text" name="msgSubject" required/>
						<label>Send to:</label>
						<select multiple id="rec" name="recipient" form="messageAdministrator">
							<c:forEach items="${administrators}" var="a">
							<option value="${a.userID}">${a.name}</option>
							</c:forEach>
						</select><br />
						<label>Message:</label>
						<textarea name="msgBody"></textarea><br />
					</form>
				</div>
				<div class="modal-footer">
					<input type="submit" form="messageAdministrator" class="btn btn-primary" value="Send" />
  				</div>
			</div>
		</div>
	</div> -->






<div class="modal fade" id="modalSend" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
		<h3>Message Administrator</h3>
      </div>
      <div class="modal-body">
        
		<form id="messageAdministrator" method="post" action="<c:url value="/send_message"/>">

			<div class="row-fluid">
				<div class="col-md-3">
					<label>Send to:</label>
				</div>

				<div class="col-md-9">
					<select multiple id="rec" name="recipient" form="messageAdministrator">
						<c:forEach items="${administrators}" var="a">
						<option value="${a.userID}">${a.name}</option>
						</c:forEach>
					</select><br />
				</div>
			</div>

			<div class="row-fluid">
				<div class="col-md-3">
					<label>Subject:</label>
				</div>
				<div class="col-md-9">
					<input type="text" name="msgSubject" required/>
				</div>
			</div>

			<div class="row-fluid">
				<div class="col-md-3">
					<label>Message:</label>
				</div>

				<div class="col-md-9">
					<textarea name="msgBody"></textarea><br />
				</div>
			</div>
		</form>

      </div>
      <div class="modal-footer">
        <input type="submit" form="messageAdministrator" class="btn btn-primary" value="Send" />
      </div>
    </div>
  </div>
</div>

</body>
</html>
