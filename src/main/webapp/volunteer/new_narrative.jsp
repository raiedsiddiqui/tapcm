
<%@page contentType="text/html" import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import='java.util.Date' %>
<%SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String currentDate = sdf.format(new Date()); %>

<%@ include file="subNavi.jsp" %>


	<div class="content">
		<div class="row-fluid">
			<div class="span12">
			<form id="newNarrative" action="<c:url value="/add_narrative"/>" method="POST">
			<table>
				<tr><td><h4>My Narrative: > New Narraive  </h4></td><td></td></tr>
				<tr><td><label>Title:</label><input id="narrativeTitle" name="narrativeTitle" type="text" required></td>
				<td><input id="narrativeDate" name="narrativeDate" data-format="yyyy-MM-dd" type="text" value = <%= currentDate%> readonly ></td>
				<td><%= new SimpleDateFormat("yyyy-MM-dd").format(new Date()) %></td></tr>
				<tr><td colspan ="2"><hr/></td></tr>
				<tr><td colspan = "2"><textarea name="narrativeContent" maxlength="50"></textarea></td></tr>
				
				
			</table>
			</form>
				
			</div>
		</div>
		<div>
		<button id="newNarrativeButton" data-loading-text="Loading..." type="submit"  form="newNarrative" class="btn btn-primary">Finish</button>
		
		</div>
		
	</div>
	
	


</body>
</html>
