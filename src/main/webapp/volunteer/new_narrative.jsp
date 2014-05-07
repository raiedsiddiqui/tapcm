<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page import='java.util.Date' %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<%@ include file="subNavi.jsp" %>
	<div class="content">
		<div class="row-fluid">
			<div class="span12">
				<form id="newNarrative" action="<c:url value="/add_narrative"/>" method="POST">
					<table width="900">
						<tr>
							<td><a href="<c:url value="/view_narratives"/>"><h4>My Narrative: </a> > New Narraive </h4></td>
							<td></td>
						</tr>
						<tr><%SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String currentDate = sdf.format(new Date()); %>
							<td><label>Title : </label><input id="narrativeTitle" name="narrativeTitle" type="text" required></td>
							
							<td><label>Edit Time : </label><input id="narrativeDate" name="narrativeDate" data-format="yyyy-MM-dd" type="text" value = "<%= currentDate %>" readonly ></td>
						</tr>
						<tr>
							<td colspan ="2"><hr/></td>
						</tr>
						<tr>
							<td colspan = "2"><textarea name="narrativeContent" rows="10" cols="100"></textarea></td>
						</tr>				
					</table>
				</form>				
			</div>
		</div>
		<br/>
		<div>
		<button id="newNarrativeButton" data-loading-text="Loading..." type="submit"  form="newNarrative" class="btn btn-primary">Finish</button>		
		</div>		
	</div>

</body>
</html>
