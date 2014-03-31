  <%@ include file="subNavi.jsp" %>

	<div class="content">
		<div class="row-fluid">
			<div class="span12">
				<h3>My Narrative </h3><a href="<c:url value="/new_narrative"/>" class="btn btn-primary" data-toggle="modal">New Narrative</a>  
				<c:if test="${not empty narrativeCreated}">					
					<div class ="alert alert-info"><spring:message code="message_newNarrative"/></div>
				</c:if>				
				<c:if test="${not empty narrativeDeleted }">
					<div class ="alert alert-info"><spring:message code="message_removeNarrative"/></div>
				</c:if>
				<c:if test="${not empty narrativeUpdate }">
					<div class ="alert alert-info"><spring:message code="message_modifyNarrative"/></div>
				</c:if>
						
				<div class="tab-content">
					<div class="tab-pane active" id="all">						
						<table class="table">
							<tr>
								<th>Title</th><th>Edit Date</th><th></th>
							</tr>
							<c:forEach items="${narratives}" var="na">
								<tr>
									<td><a href="<c:url value="/modify_narrative/${na.narrativeId}"/>">${na.title}</a></td>
									
									<td>${na.editDate}</td>
									<td><a href="<c:url value="/delete_narrative/${na.narrativeId}"/>" class="btn btn-danger">Delete</a></td>
								</tr>
							</c:forEach>
						</table>	
					</div>
					
					<!--  div>
						<h3>Display tag Pagination and Sorting Example</h3>
           			 		
            					<display:table name="narratives" pagesize="5"
                         			  export="true" sort="list" uid="one">
               			 		<display:column property="title" title="Title"
                              		  sortable="true" headerClass="sortable" />
               					 <display:column property="editDate" title="Edit Date"
                              		  sortable="true" headerClass="sortable" />                			
           						 </display:table>
				
					</div> -->
					
				</div>
			
			</div>
		</div>
		
	</div>
	
	

</body>
</html>
