<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Tapestry Admin</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet"></link>
	<script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>
	<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.simple-color.js"></script>
	
	<script  type="text/javascript">
		$(document).ready(function(){
  			$('.simple_color').simpleColor();

  			$('.simple_color_color_code').simpleColor({ displayColorCode: true });

  			$('.simple_color_custom_cell_size').simpleColor({ cellWidth: 30, cellHeight: 10 });

  			$('.simple_color_live_preview').simpleColor({ livePreview: true });

			$('.simple_color_callback').simpleColor({
    			onSelect: function( hex ) {
      			alert("You selected #" + hex);
    			}
  			});

  			$('.simple_color_mouse_enter').simpleColor({
    			onCellEnter: function( hex ) {
      			console.log("You just entered #" + hex);
    			}
  			});

  			$('.simple_color_kitchen_sink').simpleColor({
    			cellWidth: 20,
    			cellHeight: 20,
    			border: '1px solid #660033',
    			buttonClass: 'button',
    			displayColorCode: true,
    			livePreview: true,
    			onSelect: function( hex ) {
      			alert("You selected #" + hex);
    			},
    			onCellEnter: function( hex ) {
      				console.log("You just entered #" + hex);
    			},
    			onClose: function() {
      				alert("color selector closed");
    			}
  			});

		});
	</script>

	<style type="text/css">
		.row-fluid{
			margin:10px;
		}
		.simpleColorDisplay{
			height:50px;
			border-radius:4px;
			margin-bottom:10px;
		}
	</style>
</head>
	
<body>	
  <img src="<c:url value="/resources/images/logo.png"/>" />
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Edit Patient</h2>
		  	<form id="editPatient" method="post" action="<c:url value="/submit_edit_patient/${patient.patientID}"/>">
				<label>First Name:</label>
				<input type="text" name="firstname" value="${patient.firstName}" required/>
				<label>Last Name:</label>
				<input type="text" name="lastname" value="${patient.lastName}" required/>
				<label>Volunteer</label>
				<select name="volunteer" form="editPatient">
					<c:forEach items="${volunteers}" var="v">
					<option value="${v.userID}" <c:if test="${v.name eq patient.volunteerName}">selected</c:if>>${v.name}</option>
					</c:forEach>
				</select><br />
				<label>Background color</label>
				<input class='simple_color_live_preview' value='${patient.color}' name="backgroundColor"/>
				<label>Gender</label>
				<select name="gender" form="editPatient">
					<option value="M" <c:if test="${patient.gender eq 'M'}">selected</c:if>>Male</option>	
					<option value="F" <c:if test="${patient.gender eq 'F'}">selected</c:if>>Female</option>
					<option value="O" <c:if test="${patient.gender eq 'O'}">selected</c:if>>Other</option>
				</select>
				<label>Birth Date</label>
				<label>Warnings</label>
				<textarea name="warnings">${patient.warnings}</textarea><br/>
				<input type="submit" value="Save changes" class="btn btn-primary"/>
			</form>
		</div>
	</div>
</body>
</html>
