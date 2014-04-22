<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
		<link rel="icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico" type="image/x-icon" />

		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.css" rel="stylesheet" />
		<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" /> 
		<link href="${pageContext.request.contextPath}/resources/css/custom.css" rel="stylesheet" />      
 		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>

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

		tr:hover{
			background-color:#D9EDF7;
		}
	</style>
	<script type="text/javascript">
		$(function(){
			$('#tp1').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			
			$('#tp2').datetimepicker({
				pickDate: false,
				pickSeconds: false
			});
			
			$('#dp').datetimepicker({
				pickTime: false,
				startDate: new Date()
  			});
  			
 			$('#newActivityLogButton').click(function(){
		        var btn = $(this)
		        btn.button('loading')
		        setTimeout(function () {
		            btn.button('reset')
		        }, 3000)
		    });
		});
	</script>
</head>
<body>
	<div id="headerholder">	
		<img id="logo" src="<c:url value="/resources/images/logo.png"/>" />
		<img id="logofhs" src="<c:url value="/resources/images/fhs.png"/>" />
		<img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/> 
	</div>

<!-- OLD NAVBAR-->	
<!--	<div class="navbar">
		<div class="navbar-inner">
			<div class="container">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				 	<span class="icon-bar"></span>
				 	<span class="icon-bar"></span>
				</a>
					
				<a class="brand" href="<c:url value="/"/>">Appointments</a>
				<div class="nav-collapse collapse">
					<ul class="nav">					
						<li class="active"><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
					  li><a href="<c:url value="/view_narratives"/>">Narratives</a></li>
						<li><a href="<c:url value="/view_activityLogs"/>">Activity Logs</a></li>
						<li><a href="<c:url value="/logout"/>">Log Out</a></li>
					</ul>
				</div>
		 	</div>	
		</div>
	</div>
-->
<!-- OLD NAVBAR-->	

<div class="navbar">      
	<ul class="nav navbar-nav">
    	
    	<li><a class="brand" href="<c:url value="/"/>">Appointments</a></li>
    	<li class="active"><a href="<c:url value="/inbox"/>">Messages <c:if test="${unread > 0}"> <span class="badge badge-info">${unread}</span> </c:if></a></li>
    	<li><a href="<c:url value="/view_activityLogs"/>">Activity Logs</a></li>
		<li><a href="<c:url value="/logout"/>">Log Out</a></li>

    </ul>
 </div>

	
	<!-- 	breadcrumb START-->	
<!-- 	<div id="crumbs"> 
		<ul>
			<li><a href="<c:url value="/client"/>"><img src="${pageContext.request.contextPath}/resources/images/home.png" height="20" width="20" />My Clients</a> </li>
			<c:if test="${not empty patient}">
				<li><a href="">
						<c:choose>
							<c:when test="${not empty patient.preferredName}">
								<b>${patient.preferredName} (${patient.gender})</b>
							</c:when>
							<c:otherwise>
								<b>${patient.displayName} (${patient.gender})</b>
							</c:otherwise>
						</c:choose>
					</a>
				</li>
			</c:if>		
		</ul>


	</div>
 -->
	
<!-- 	breadcrumb END-->	