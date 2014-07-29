<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Tapestry book appointment</title>	
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>
	<%@ include file="volunteer_head.jsp" %>
	
</head>
<body>
<div class="content">
	<div id = "sub_navigation" >
		<%@ include file="subNavi.jsp" %>
	</div>
	
	<div id = "bookAppt">
	 	<%@ include file="../book_appointment.jsp"%>
	</div>
 

</div>
</body>
</html>