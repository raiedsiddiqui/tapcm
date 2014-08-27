<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Tapestry</title>
  		<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
    <%@include file="volunteer/volunteer_head.jsp" %>
    <link rel="shortcut icon" href="/resources/images/favicon.ico" />
 	</head>
 	<body>
  <div id="headerholder"> 
    <div class="row">
      <div class="col-md-3 tpurple logoheight">
        <img id="logo" src="<c:url value="/resources/images/logow.png"/>" />
      </div>

    <div class="col-md-9 tblack" style="height:63px;">

    </div>
  </div>
</div>


    <div id="content">
  	<!-- 	<img id="logo" src="${pageContext.request.contextPath}/resources/images/logo.png" />
        <img id="logofam" src="${pageContext.request.contextPath}/resources/images/fammed.png"/> 
        <img id="logofhs" src="${pageContext.request.contextPath}/resources/images/fhs.png"/>
        <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>-->


  		<div class="container-fluid">
   			<div class="row">
	    		<div class="col-md-12">
					 <c:if test="${not empty error}">
					<div class="alert alert-danger">Login failed <br /> Caused :
							${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
					</c:if>
					<c:if test="${not empty usernameChanged}">
					<div class="alert alert-info">Your username has changed. Please log in again using the new credentials.</div>
					</c:if>
        </div>
      </div>

          <div class="area">
            <form class="form-horizontal" action="<c:url value="j_spring_security_check" />" method="POST">
            <div class="row-fluid">
              <div class="col-md-6">
                <label class="control-label" for="username">Username</label>
              </div>
            
              <div class="col-md-6">
                <input id="logininput" type="text" name="j_username" placeholder="username" />
              </div>
            </div>
            <br>
            <div class="row-fluid">
              <div class="col-md-6">
                <label class="control-label" for="password">Password</label>
              </div>
            
              <div class="col-md-6">
                <input id="logininput" type="password" name="j_password" placeholder="password" />
              </div>
            </div>
            <br>
            <input id="loginbtn" type="submit" value="Login" style="margin-bottom:10px;"></input>
          </form>

          </div>


<!--      				<div class="area">
	      				<form class="form-horizontal" action="<c:url value="j_spring_security_check" />" method="POST">
       						<div class="heading">
        						<h4 class="form-heading">Sign In Please</h4>
       						</div>
       						<div class="control-group">
	        					<label class="control-label" for="username">Username</label>
        						<div class="controls">
	         						<input id="logininput" type="text" name="j_username" placeholder="username" />
        						</div>
       						</div>
       						<div class="control-group">
								<label class="control-label" for="password">Password</label>
        						<div class="controls">
	         						<input id="logininput" type="password" name="j_password" placeholder="password" />
        						</div>
       						</div>
       						<div class="control-group">
	        					<div class="controls">
									<input id="logininput" type="submit" value="Login" style="margin-bottom:10px;"></input>
									<p>Tapestry 13.11.14</p>
        						</div>
       						</div>
      					</form>
     				</div> -->
    			</div>
          <div id="footer row-fluid">
            <div class="col-md-12">
                <img id="logofhs" src="${pageContext.request.contextPath}/resources/images/fhs.png"/>
                <img id="logodeg" src="${pageContext.request.contextPath}/resources/images/degroote.png"/>
            <div>
          </div>
   			</div>

 	</body>
</html>
