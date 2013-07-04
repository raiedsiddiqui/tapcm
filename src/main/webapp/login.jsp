<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <head>
  <title>
   Tapestry
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet" />
  <script src="http://code.jquery.com/jquery-2.0.0.min.js">
  </script>
  <script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js">
  </script>
 </head>
 <body>
  <img src="${pageContext.request.contextPath}/resources/images/logo.png" />
  <div class="container-fluid">
   <div class="row-fluid">
    <div class="span12">
	<c:if test="${not empty error}">
		<div class="alert alert-error">Login failed</div>
	</c:if>
     <div class="area">
      <form class="form-horizontal" action="j_spring_security_check" method="POST">
       <div class="heading">
        <h4 class="form-heading">
         Sign In
        </h4>
       </div>
       <div class="control-group">
        <label class="control-label" for="username">
         Username
        </label>
        <div class="controls">
         <input type="text" name="j_username" placeholder="E.g. ashwinhegde" />
        </div>
       </div>
       <div class="control-group">
        <label class="control-label" for="password">
         Password
        </label>
        <div class="controls">
         <input type="password" name="j_password" placeholder="Min. 8 Characters" />
        </div>
       </div>
       <div class="control-group">
        <div class="controls">
         <a href="#" class="link">
          Forgot my password
         </a>
         <br />
         <br />
	<input type="submit" value="Login"></input>
        </div>
       </div>
      </form>
     </div>
    </div>
    <div id="modalWarnLogin" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="warnLabel" aria-hidden="true">
     <div class="modal-header">
      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
       x
      </button>
      <h3 id="warnLabel" style="color:#000000;">
       Invalid Login
      </h3>
     </div>
     <div class="modal-body">
      <p class="muted">
       Invalid Login/Password. Please try again.
      </p>
     </div>
     <div class="modal-footer">
      <button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">
       Close
      </button>
     </div>
    </div>
   </div>
  </div>
  <div class="text-center">
    <p>Tapestry version 0.1</p>
  </div>
 </body>
</html>
