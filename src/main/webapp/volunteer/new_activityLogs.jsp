<%@ include file="subNavi.jsp" %>
<%@ page import='java.text.SimpleDateFormat'%>
<%@ page import='java.util.Date' %>
<%SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); String currentDate = sdf.format(new Date()); %>

<div class="content">
		<div class="row-fluid">
			<div class="span12">
			<form id="newActivityLog" action="<c:url value="/add_activityLogs"/>" method="POST">
			<table>
				<tr><td><h4>Activity Log > New Activity  </h4></td><td></td><td></td></tr>
				<tr><td><label>Date:</label>
						<div id="dp" class="input-append">
							<input id="activityDate" name="activityDate" data-format="yyyy-MM-dd" type="text" value = <%= currentDate%> readonly required>
								<span class="add-on">
									<i class="icon-calendar"></i>
								</span>
						</div>
				    </td>
					<td><label>Start Time:</label>
						<div id="tp1" class="input-append" role="dialog">
							<input data-format="hh:mm:00" type="text" name="activityStartTime" id="activityStartTime" readonly>
				    		<span class="add-on">
				    			<i class="icon-time"></i>
				   			 </span>
						</div>
					</td>
					<td><label>End Time:</label>
						<div id="tp2" class="input-append" role="dialog">
							<input data-format="hh:mm:00" type="text" name="activityEndTime" id="activityEndTime" readonly>
				    		<span class="add-on">
				    			<i class="icon-time"></i>
				   			 </span>
						</div>
					</td>
				</tr>
				<tr><td colspan ="3"><hr/></td></tr>
				<tr><td colspan = "3"><label><h2>Activity Description:</h2></label></td></tr>
				
				<tr><td colspan = "3"><textarea name="activityDesc" maxlength="50"></textarea></td></tr>
				
				
			</table>
			</form>
				
			</div>
		</div>
		<div>
		<button id="newActivityLogButton" data-loading-text="Loading..." type="submit"  form="newActivityLog" class="btn btn-primary">Finish</button>
		
		</div>
		
	</div>

</body>
</html>