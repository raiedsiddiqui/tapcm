 <%@ include file="subNavi.jsp" %>

	<div class="content">
		<div class="row-fluid">
			<div class="span12">
			<form id="modifyNarrative" action="<c:url value="/update_narrative"/>" method="POST">
			<table>
				<tr><td><h4>My Narrative:   </h4></td><td></td></tr>
				<tr><td><label>Title:</label><input name="mNarrativeTitle" type="text" value="${narrative.title }" required></td>
					<td><label>EditDate:</label><input name="mNarrativeDate" type="text" value="${narrative.editDate}" readonly></td></tr>
				<tr><td colspan ="2"><hr/></td></tr>
				<tr><td colspan = "2"><textarea name="mNarrativeContent" maxlength="50">${narrative.contents}</textarea></td></tr>
				<tr><td colspan="2"><input type="hidden" name="narrativeId" value="${narrative.narrativeId}"/></td></tr>
				
			</table>
			
			</form>
				
			</div>
		</div>
		<div>
		<button id="newNarrativeButton" data-loading-text="Loading..." type="submit"  form="modifyNarrative" class="btn btn-primary">Save Changes</button>
		
		</div>
		
	</div>
	
	

</body>
</html>
