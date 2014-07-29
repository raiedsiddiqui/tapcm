<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=no"></meta>

	<title>Book Appointment</title>
	
	
	<style type="text/css">
		html,body{
			height:100%;
		}
		
	</style>

</head>
<body>
<div class="content">
	<h4 >Book Appointment</h4>
	
	<div>
		 <form id="book-appointment-form" method="post" action="<c:url value="/book_appointment"/>">
  				<label>With patient:</label>
				<select name="patient" form="book-appointment-form">
					<c:forEach items="${patients}" var="p">
					<option value="${p.patientID}">${p.displayName}</option>
					</c:forEach>
				</select><br />
				
					<label>Date:</label>		
					<div id="dp" class="input-append" role="dialog">
						<input id="appointmentDate" class="datepickera form-control" data-format="yyyy-MM-dd" type="text" placeholder="Select Date" name="appointmentDate" value = "${appointment.date}" required>
										<span class="add-on">
											<!-- <i class="icon-calendar"></i> -->
										</span>
					</div>
				
					<label>Time:</label>
					<div id="tp" class="input-append" role="dialog">
	 					<input id="appointmentTime" data-format="hh:mm:00" class="timepickera form-control" type="text" placeholder="Select Time" name="appointmentTime" value="${appointment.time}">
					    		<span class="add-on">
						    			<!-- <i class="icon-time"></i> -->
					   			 </span>
					</div>
				
  			</form>
	</div>
	<div class="modal-footer">
		<a href = "<c:url value="/out_book_appointment"/>" class="btn btn-default" data-dismiss="modal">Cancel</a>  
        <button id="bookAppt" data-loading-text="Loading..." type="submit" value="Book" form="book-appointment-form" class="btn btn-primary">Book</button>
      </div>
</div>
<script type="text/javascript">
$(function(){
		
		$('#newActivityLogButton').click(function(){
        var btn = $(this)
        btn.button('loading')
        setTimeout(function () {
            btn.button('reset')
        }, 3000)
    });
});

			$('.datepickera').pickadate({
		    // Escape any âruleâ characters with an exclamation mark (!).
		    format: 'You selecte!d: dddd, dd mmm, yyyy',
		    formatSubmit: 'yyyy-mm-dd',
		    hiddenName: true
		   	// hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
			})
		

		$('.timepickera').pickatime({
		    // Escape any âruleâ characters with an exclamation mark (!).
		    formatSubmit: 'HH:i:00',
		   	hiddenName: true,
		   	min: [8,0],
		   	max: [17,0]

		    // hiddenPrefix: 'prefix__',
		    // hiddenSuffix: '__suffix'
		})

		
	</script>

</body>
</html>