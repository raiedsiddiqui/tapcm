<%@ include file="subNavi.jsp" %>
<%@page contentType="text/html" import="java.util.*" %>


	<div class="content">
		<div class="row-fluid">
			<div class="span12">
			<form id="modifyActivity" action="<c:url value="/update_activityLog"/>" method="POST">
			<table>
								
				<tr><td><h4>Activity Log > Activity Log</h4></td><td></td><td></td></tr>
				<tr><td><label>Date:</label>
						<div id="dp" class="input-append">
							<input id="activityDate" name="activityDate" data-format="yyyy-MM-dd" type="text" value = "${activityLog.date}" required>
								<span class="add-on">
									<i class="icon-calendar"></i>
								</span>
						</div>
				    </td>
					<td><label>Start Time:</label>
						<div id="tp1" class="input-append" role="dialog">
							<input data-format="hh:mm:00" type="text" name="activityStartTime" id="activityStartTime" value="${activityLog.startTime}" >
				    		<span class="add-on">
				    			<i class="icon-time"></i>
				   			 </span>
						</div>
					</td>
					<td><label>End Time:</label>
						<div id="tp2" class="input-append" role="dialog">
							<input data-format="hh:mm:00" type="text" name="activityEndTime" id="activityEndTime" value="${activityLog.endTime}">
				    		<span class="add-on">
				    			<i class="icon-time"></i>
				   			 </span>
						</div>
					</td>
				</tr>
				<tr><td colspan ="3"><hr/></td></tr>
				<tr><td colspan = "3"><label><h2>Activity Description:</h2></label></td></tr>
				
				<tr><td colspan = "3"><textarea name="activityDesc"  maxlength="50">${activityLog.description}</textarea></td></tr>
				<tr><td colspan="3"><input type="hidden" name="activityId" value="${activityLog.activityId}"/></td></tr>
				
			</table>
			
			</form>
				
			</div>
		</div>
		<div>
		<button id="mActivityLog" data-loading-text="Loading..." type="submit"  form="modifyActivity" class="btn btn-primary">Save Changes</button>
		
		</div>
		
	</div>
	
	

</body>
</html>