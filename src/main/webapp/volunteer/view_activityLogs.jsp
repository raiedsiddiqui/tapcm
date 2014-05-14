<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry</title>
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
<div class="content">
		<div class="row-fluid">
			<div class="span12">
				<h3>My Activity Logs </h3>
				<a href="<c:url value="/new_activityLogs"/>" class="btn btn-primary" data-toggle="modal">New Activity</a>
				<c:if test="${not empty activityCreated}">					
					<div class ="alert alert-info"><spring:message code="message_newActivity"/></div>
				</c:if>
				<c:if test="${not empty activityDeleted }">
					<div class ="alert alert-info"><spring:message code="message_removeActivity"/></div>
				</c:if>
				<c:if test="${not empty activityUpdate }">
					<div class ="alert alert-info"><spring:message code="message_modifyActivity"/></div>
				</c:if>

				<div class="tab-content">
					<div class="tab-pane active" id="all">						
						<table class="table">
							<tr>
								<th>Date</th>
								<th>Time</th>
								<th>Description</th>
								<th></th>
							</tr>
	
							<c:forEach items="${activityLogs}" var="al">
								<tr>
									<td><a href="<c:url value="/modify_activityLog/${al.activityId}"/>">${al.date}</a></td>
									
									<td>${al.time}</td>
									<td width = 260>${al.description}</td>
									<td><a href="<c:url value="/delete_activityLog/${al.activityId}"/>" class="btn btn-danger">Delete</a></td>
								</tr>
							</c:forEach>
						</table>
	
					</div>
				</div>
				
			</div>
		</div>
		
	</div>

</body>
</html>