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
					<li <c:if test="${pageContext.request.requestURI.contains('view_volunteers')}">class="active"</c:if>><a href="<c:url value="/view_volunteers"/>">Volunteers</a></li>
					
					<li <c:if test="${pageContext.request.requestURI.contains('manage_users')}">class="active"</c:if>><a href="<c:url value="/manage_users"/>">Manage Users</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_patients')}">class="active"</c:if>><a href="<c:url value="/manage_patients"/>">Manage Patients</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_appointments')}">class="active"</c:if>><a href="<c:url value="/manage_appointments"/>">Manage Appointments</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_survey_templates')}">class="active"</c:if>><a href="<c:url value="/manage_survey_templates"/>">Manage Survey Templates</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('manage_surveys')}">class="active"</c:if>><a href="<c:url value="/manage_surveys"/>">Manage Surveys</a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('inbox')}">class="active"</c:if>><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
					<li <c:if test="${pageContext.request.requestURI.contains('user_logs')}">class="active"</c:if>><a href="<c:url value="/user_logs/1"/>">User Logs</a><li>
					<li><a href="<c:url value="/logout"/>">Log Out</a></li>
				</ul>
			</div>
		</div>	
	</div>
</div>