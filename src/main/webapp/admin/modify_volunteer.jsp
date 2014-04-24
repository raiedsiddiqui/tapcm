<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Modify Volunteer</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"></meta>
			<link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
			<link href="${pageContext.request.contextPath}/resources/css/bootstrap-responsive.min.css" rel="stylesheet" />  		
			<script src="${pageContext.request.contextPath}/resources/js/jquery-2.0.3.min.js"></script>
			<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	
			<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/printelement.js"></script>
	

		<style type="text/css">
			.row-fluid{
				margin:10px;
			}
		</style>
		
		<script type="text/javascript">
			function printTable(){
				$('.table').printThis();
			}
		</script>
	</head>
	
	<body>
	<div class="content">
		<%@include file="navbar.jsp" %>
		<div class="row-fluid">
			<h2>Volunteers </h2>
			<div class="row-fluid">
				<form id="modify_volunteer" action="<c:url value="/update_volunteer/${volunteer.volunteerId}"/>" method="POST">
					<fieldset>
							<div class="row form-group">
								<div class="col-md-4">
									<label>First Name:</label>
									<input type="text" name="firstname" class="form-control" value="${volunteer.firstName}" required/>
								</div>
								<div class="col-md-4">
									<label>Last Name:</label>
									<input type="text" name="lastname" class="form-control" value="${volunteer.lastName}" required/>
								</div>
							</div>

							<div class="row form-group">
								<div class="col-md-4">
									<label>Street #:</label>
									<input type="text" name="streetnum" class="form-control" value="${volunteer.streetNumber}" />
								</div>
								<div class="col-md-4">	
									<label>Street:</label>
									<input type="text" name="street" class="form-control" value="${volunteer.street}"/>
								</div>
								<div class="col-md-4">	
									<label>Apt #:</label>
									<input type="text" name="aptnum" class="form-control" value="${volunteer.aptNumber}"/>
								</div>
							</div>		



							<div class="row form-group">
								<div class="col-md-4">	
									<label>Country:</label>
										<select name="country" form="modify_volunteer" class="form-control"s>
										<option value="CA" <c:if test="${volunteer.country eq 'CA'}">selected</c:if>>Canada</option>
										<option value="ST" <c:if test="${volunteer.country eq 'ST'}">selected</c:if>>USA</option>
										<option value="CH" <c:if test="${volunteer.country eq 'CH'}">selected</c:if>>China</option>
										<option value="RU" <c:if test="${volunteer.country eq 'RU'}">selected</c:if>>Russia</option>
										</select>
								</div>
								<div class="col-md-4">	
									<label>Province:</label>
										<select name="province" form="modify_volunteer" class="form-control">
										<option value='AB' <c:if test="${volunteer.province eq 'AB'}">selected</c:if>>Alberta</option>
										<option value='BC' <c:if test="${volunteer.province eq 'BC'}">selected</c:if>>British Colunmbia</option>							
										<option value='MB' <c:if test="${volunteer.province eq 'MB'}">selected</c:if>>Manitoba</option>
										<option value='NB' <c:if test="${volunteer.province eq 'NB'}">selected</c:if>>New Brunswik</option>
										<option value='NL' <c:if test="${volunteer.province eq 'NL'}">selected</c:if>>Newfoundland and Labrador</option>
										<option value='NS' <c:if test="${volunteer.province eq 'NS'}">selected</c:if>>Nova Scotia</option>							
										<option value='ON' <c:if test="${volunteer.province eq 'ON'}">selected</c:if>>Ontario</option>
										<option value='PE' <c:if test="${volunteer.province eq 'PE'}">selected</c:if>>PrinceEdword Island</option>
										<option value='QC' <c:if test="${volunteer.province eq 'QC'}">selected</c:if>>Quebec</option>
										<option value='SK' <c:if test="${volunteer.province eq 'SK'}">selected</c:if>>Saskatchewan</option>							
										<option value='NT' <c:if test="${volunteer.province eq 'NT'}">selected</c:if>>Northwest Terriotories</option>
										<option value='NU' <c:if test="${volunteer.province eq 'NU'}">selected</c:if>>Nunavut</option>
										<option value='YT' <c:if test="${volunteer.province eq 'YT'}">selected</c:if>>Yukon</option>
											
										</select>
								</div>	
								<div class="col-md-4">	
									<label>City:</label>
									<input name="city" class="form-control" value="${volunteer.city}" type="text">
								</div>
							</div>


							<div class="row form-group">
								<div class="col-md-4">	
									<label>Postal Code:</label>
									<input name="postalcode" class="form-control" type="text" value="${volunteer.postalCode}"/>
								</div>
							
							
								<div class="col-md-4">		
									<label >Home Phone:</label>
									<input name="homephone" class="form-control" type="text" value="${volunteer.homePhone}">
								</div>

								<div class="col-md-4">	
									<label>Cell Phone:</label>
									<input name="cellphone" class="form-control" type="text" value="${volunteer.cellPhone}">
								</div>
							</div>

			
							<div class="row form-group">
								<div class="col-md-4">
									<label>Email:</label>
									<input name="email" class="form-control" type="text" value="${volunteer.email}"  required>
								</div>
								<div class="col-md-4">
									<label>Emergency Contact:</label>
									<input name="emergencycontact" class="form-control" type="text" value="${volunteer.emergencyContact}">
								</div>
								<div class="col-md-4">		
									<label>Emergency #:</label>
									<input name="emergencyphone" class="form-control" type="text" value="${volunteer.emergencyPhone}">
								</div>
							</div>		



						<h2>User Account </h2>

								<div class="row form-group">

								<div class="col-md-4">
									<div class="input-group input-group-lg">
										<span class="input-group-addon">Username</span>
								 		<input name="username" type="text" class="form-control" value="${volunteer.userName}">
									</div>
								</div>
								
								<div class="col-md-4">
									<div class="input-group input-group-lg">
								  		<span class="input-group-addon">Password</span>
								  		<input type="password" name="password" class="form-control" >
									</div>
								</div>
																							
								<div class="col-md-4">
									<label>Experience:</label>
									<select class="form-control" name="level" form="modify_volunteer">
										<option value='E' <c:if test="${volunteer.experienceLevel eq 'E'}">selected</c:if>>Experienced</option>
										<option value='I' <c:if test="${volunteer.experienceLevel eq 'I'}">selected</c:if>>Intermediate</option>
										<option value='B' <c:if test="${volunteer.experienceLevel eq 'B'}">selected</c:if> >Beginner</option>
									</select>	
								</div>
							</div>



						<h2>Availability </h2>
						<c:set var="availability" value="${volunteer.availability}"/>
							<table>
										<tr>
											<td class="col-md-2"><h4>Monday <input type="checkbox" name="mondayNull" <c:if test="${fn:contains(availability, '1non')}">checked</c:if> value = "non"> N/A</h4></td>
											<td class="col-md-2"><h4>Tuesday <input type="checkbox" name="tuesdayNull" <c:if test="${fn:contains(availability, '2non')}">checked</c:if> value = "non"> N/A </h4></td>
											<td class="col-md-2"><h4>Wednesday <input type="checkbox" name="wednesdayNull" <c:if test="${fn:contains(availability, '3non')}">checked</c:if> value = "non"> N/A</h4></td>
											<td class="col-md-2"><h4>Thursday <input type="checkbox" name="thursdayNull" value = "non" <c:if test="${fn:contains(availability, '4non')}">checked</c:if>> N/A</h4>	</td>
											<td class="col-md-2"><h4>Friday <input type="checkbox" name="fridayNull" value = "non" <c:if test="${fn:contains(availability, '5non')}">checked</c:if>> N/A</h4></td>
										</tr>
										<tr>
											<td class="col-md-2"><select name="monFrom1" form="modify_volunteer">
										<option value="0">...</option>
										<option value="11" >08:00 AM</option>
										<option value="12" >08:30 AM</option>
										<option value="13" >09:00 AM</option>
										<option value="14" >09:30 AM</option>
										<option value="15" >10:00 AM</option>
										<option value="16" >10:30 AM</option>
										<option value="17" >11:00 AM</option>
										<option value="18" >11:30 AM</option>
										<option value="19" >13:00 PM</option>
										<option value="110" >13:30 PM</option>
										<option value="111" >14:00 PM</option>
										<option value="112" >14:30 PM</option>
										<option value="113" >15:00 PM</option>
										<option value="114" >15:30 PM</option>
										<option value="115" >16:00 PM</option>
									</select> TO <select name="monTo1" form="modify_volunteer">
										<option value="0">...</option>
										<option value="11">08:00 AM</option>
										<option value="12">08:30 AM</option>
										<option value="13">09:00 AM</option>
										<option value="14">09:30 AM</option>
										<option value="15">10:00 AM</option>
										<option value="16">10:30 AM</option>
										<option value="17">11:00 AM</option>
										<option value="18">11:30 AM</option>
										<option value="19">13:00 PM</option>
										<option value="110">13:30 PM</option>
										<option value="111">14:00 PM</option>
										<option value="112">14:30 PM</option>
										<option value="113">15:00 PM</option>
										<option value="114">15:30 PM</option>
										<option value="115">16:00 PM</option>
									</select>
									<select name="monFrom2" form="modify_volunteer">
										<option value="0">...</option>
										<option value="11">08:00 AM</option>
										<option value="12">08:30 AM</option>
										<option value="13">09:00 AM</option>
										<option value="14">09:30 AM</option>
										<option value="15">10:00 AM</option>
										<option value="16">10:30 AM</option>
										<option value="17">11:00 AM</option>
										<option value="18">11:30 AM</option>
										<option value="19">13:00 PM</option>
										<option value="110">13:30 PM</option>
										<option value="111">14:00 PM</option>
										<option value="112">14:30 PM</option>
										<option value="113">15:00 PM</option>
										<option value="114">15:30 PM</option>
										<option value="115">16:00 PM</option>
									</select> TO <select name="monTo2" form="modify_volunteer">
									<option value="0"></option>
										<option value="0">...</option>
										<option value="11">08:00 AM</option>
										<option value="12">08:30 AM</option>
										<option value="13">09:00 AM</option>
										<option value="14">09:30 AM</option>
										<option value="15">10:00 AM</option>
										<option value="16">10:30 AM</option>
										<option value="17">11:00 AM</option>
										<option value="18">11:30 AM</option>
										<option value="19">13:00 PM</option>
										<option value="110">13:30 PM</option>
										<option value="111">14:00 PM</option>
										<option value="112">14:30 PM</option>
										<option value="113">15:00 PM</option>
										<option value="114">15:30 PM</option>
										<option value="115">16:00 PM</option>
									</select>
								</td>
											<td>
											<select name="tueFrom1" form="modify_volunteer">
									<option value="0"></option>
										<option value="21">08:00 AM</option>
										<option value="22">08:30 AM</option>
										<option value="23">09:00 AM</option>
										<option value="24">09:30 AM</option>
										<option value="25">10:00 AM</option>
										<option value="26">10:30 AM</option>
										<option value="27">11:00 AM</option>
										<option value="28">11:30 AM</option>
										<option value="29">13:00 PM</option>
										<option value="210">13:30 PM</option>
										<option value="211">14:00 PM</option>
										<option value="212">14:30 PM</option>
										<option value="213">15:00 PM</option>
										<option value="214">15:30 PM</option>
										<option value="215">16:00 PM</option>
									</select> TO <select name="tueTo1" form="modify_volunteer">
									<option value="0"></option>
										<option value="21">08:00 AM</option>
										<option value="22">08:30 AM</option>
										<option value="23">09:00 AM</option>
										<option value="24">09:30 AM</option>
										<option value="25">10:00 AM</option>
										<option value="26">10:30 AM</option>
										<option value="27">11:00 AM</option>
										<option value="28">11:30 AM</option>
										<option value="29">13:00 PM</option>
										<option value="210">13:30 PM</option>
										<option value="211">14:00 PM</option>
										<option value="212">14:30 PM</option>
										<option value="213">15:00 PM</option>
										<option value="214">15:30 PM</option>
										<option value="215">16:00 PM</option>
									</select>
									<select name="tueFrom2" form="modify_volunteer">
									<option value="0"></option>
										<option value="21">08:00 AM</option>
										<option value="22">08:30 AM</option>
										<option value="23">09:00 AM</option>
										<option value="24">09:30 AM</option>
										<option value="25">10:00 AM</option>
										<option value="26">10:30 AM</option>
										<option value="27">11:00 AM</option>
										<option value="28">11:30 AM</option>
										<option value="29">13:00 PM</option>
										<option value="210">13:30 PM</option>
										<option value="211">14:00 PM</option>
										<option value="212">14:30 PM</option>
										<option value="213">15:00 PM</option>
										<option value="214">15:30 PM</option>
										<option value="215">16:00 PM</option>
									</select> TO <select name="tueTo2" form="modify_volunteer">
									<option value="0"></option>
										<option value="21">08:00 AM</option>
										<option value="22">08:30 AM</option>
										<option value="23">09:00 AM</option>
										<option value="24">09:30 AM</option>
										<option value="25">10:00 AM</option>
										<option value="26">10:30 AM</option>
										<option value="27">11:00 AM</option>
										<option value="28">11:30 AM</option>
										<option value="29">13:00 PM</option>
										<option value="210">13:30 PM</option>
										<option value="211">14:00 PM</option>
										<option value="212">14:30 PM</option>
										<option value="213">15:00 PM</option>
										<option value="214">15:30 PM</option>
										<option value="215">16:00 PM</option>
									</select>
											</td>
											<td>
											<select name="wedFrom1" form="modify_volunteer">
										<option value="0"></option>
											<option value="31">08:00 AM</option>
											<option value="32">08:30 AM</option>
											<option value="33">09:00 AM</option>
											<option value="34">09:30 AM</option>
											<option value="35">10:00 AM</option>
											<option value="36">10:30 AM</option>
											<option value="37">11:00 AM</option>
											<option value="38">11:30 AM</option>
											<option value="39">13:00 PM</option>
											<option value="310">13:30 PM</option>
											<option value="311">14:00 PM</option>
											<option value="312">14:30 PM</option>
											<option value="313">15:00 PM</option>
											<option value="314">15:30 PM</option>
											<option value="315">16:00 PM</option>
										</select> TO <select name="wedTo1" form="modify_volunteer">
										<option value="0"></option>
											<option value="31">08:00 AM</option>
											<option value="32">08:30 AM</option>
											<option value="33">09:00 AM</option>
											<option value="34">09:30 AM</option>
											<option value="35">10:00 AM</option>
											<option value="36">10:30 AM</option>
											<option value="37">11:00 AM</option>
											<option value="38">11:30 AM</option>
											<option value="39">13:00 PM</option>
											<option value="310">13:30 PM</option>
											<option value="311">14:00 PM</option>
											<option value="312">14:30 PM</option>
											<option value="313">15:00 PM</option>
											<option value="314">15:30 PM</option>
											<option value="315">16:00 PM</option>
										</select>
										<select name="wedFrom2" form="modify_volunteer">
										<option value="0"></option>
											<option value="31">08:00 AM</option>
											<option value="32">08:30 AM</option>
											<option value="33">09:00 AM</option>
											<option value="34">09:30 AM</option>
											<option value="35">10:00 AM</option>
											<option value="36">10:30 AM</option>
											<option value="37">11:00 AM</option>
											<option value="38">11:30 AM</option>
											<option value="39">13:00 PM</option>
											<option value="310">13:30 PM</option>
											<option value="311">14:00 PM</option>
											<option value="312">14:30 PM</option>
											<option value="313">15:00 PM</option>
											<option value="314">15:30 PM</option>
											<option value="315">16:00 PM</option>
										</select> TO <select name="wedTo2" form="modify_volunteer">
										<option value="0"></option>
											<option value="31">08:00 AM</option>
											<option value="32">08:30 AM</option>
											<option value="33">09:00 AM</option>
											<option value="34">09:30 AM</option>
											<option value="35">10:00 AM</option>
											<option value="36">10:30 AM</option>
											<option value="37">11:00 AM</option>
											<option value="38">11:30 AM</option>
											<option value="39">13:00 PM</option>
											<option value="310">13:30 PM</option>
											<option value="311">14:00 PM</option>
											<option value="312">14:30 PM</option>
											<option value="313">15:00 PM</option>
											<option value="314">15:30 PM</option>
											<option value="315">16:00 PM</option>
										</select>
											</td>
											<td>
											<select name="thuFrom1" form="modify_volunteer">
										<option value="0"></option>
											<option value="41">08:00 AM</option>
											<option value="42">08:30 AM</option>
											<option value="43">09:00 AM</option>
											<option value="44">09:30 AM</option>
											<option value="45">10:00 AM</option>
											<option value="46">10:30 AM</option>
											<option value="47">11:00 AM</option>
											<option value="48">11:30 AM</option>
											<option value="49">13:00 PM</option>
											<option value="410">13:30 PM</option>
											<option value="411">14:00 PM</option>
											<option value="412">14:30 PM</option>
											<option value="413">15:00 PM</option>
											<option value="414">15:30 PM</option>
											<option value="415">16:00 PM</option>
										</select> TO <select name="thuTo1" form="modify_volunteer">
										<option value="0"></option>
											<option value="41">08:00 AM</option>
											<option value="42">08:30 AM</option>
											<option value="43">09:00 AM</option>
											<option value="44">09:30 AM</option>
											<option value="45">10:00 AM</option>
											<option value="46">10:30 AM</option>
											<option value="47">11:00 AM</option>
											<option value="48">11:30 AM</option>
											<option value="49">13:00 PM</option>
											<option value="410">13:30 PM</option>
											<option value="411">14:00 PM</option>
											<option value="412">14:30 PM</option>
											<option value="413">15:00 PM</option>
											<option value="414">15:30 PM</option>
											<option value="415">16:00 PM</option>
										</select>
										<select name="thuFrom2" form="modify_volunteer">
										<option value="0"></option>
											<option value="41">08:00 AM</option>
											<option value="42">08:30 AM</option>
											<option value="43">09:00 AM</option>
											<option value="44">09:30 AM</option>
											<option value="45">10:00 AM</option>
											<option value="46">10:30 AM</option>
											<option value="47">11:00 AM</option>
											<option value="48">11:30 AM</option>
											<option value="49">13:00 PM</option>
											<option value="410">13:30 PM</option>
											<option value="411">14:00 PM</option>
											<option value="412">14:30 PM</option>
											<option value="413">15:00 PM</option>
											<option value="414">15:30 PM</option>
											<option value="415">16:00 PM</option>
										</select> TO <select name="thuTo2" form="modify_volunteer">
										<option value="0"></option>
											<option value="41">08:00 AM</option>
											<option value="42">08:30 AM</option>
											<option value="43">09:00 AM</option>
											<option value="44">09:30 AM</option>
											<option value="45">10:00 AM</option>
											<option value="46">10:30 AM</option>
											<option value="47">11:00 AM</option>
											<option value="48">11:30 AM</option>
											<option value="49">13:00 PM</option>
											<option value="410">13:30 PM</option>
											<option value="411">14:00 PM</option>
											<option value="412">14:30 PM</option>
											<option value="413">15:00 PM</option>
											<option value="414">15:30 PM</option>
											<option value="415">16:00 PM</option>
										</select>
											</td>
											<td>
											<select name="friFrom1" form="modify_volunteer">
										<option value="0"></option>
											<option value="51">08:00 AM</option>
											<option value="52">08:30 AM</option>
											<option value="53">09:00 AM</option>
											<option value="54">09:30 AM</option>
											<option value="55">10:00 AM</option>
											<option value="56">10:30 AM</option>
											<option value="57">11:00 AM</option>
											<option value="58">11:30 AM</option>
											<option value="59">13:00 PM</option>
											<option value="510">13:30 PM</option>
											<option value="511">14:00 PM</option>
											<option value="512">14:30 PM</option>
											<option value="513">15:00 PM</option>
											<option value="514">15:30 PM</option>
											<option value="515">16:00 PM</option>
										</select> TO <select name="friTo1" form="modify_volunteer">
										<option value="0"></option>
										<option value="51">08:00 AM</option>
											<option value="52">08:30 AM</option>
											<option value="53">09:00 AM</option>
											<option value="54">09:30 AM</option>
											<option value="55">10:00 AM</option>
											<option value="56">10:30 AM</option>
											<option value="57">11:00 AM</option>
											<option value="58">11:30 AM</option>
											<option value="59">13:00 PM</option>
											<option value="510">13:30 PM</option>
											<option value="511">14:00 PM</option>
											<option value="512">14:30 PM</option>
											<option value="513">15:00 PM</option>
											<option value="514">15:30 PM</option>
											<option value="515">16:00 PM</option>
										</select>
										<select name="friFrom2" form="modify_volunteer">
										<option value="0"></option>
											<option value="51">08:00 AM</option>
											<option value="52">08:30 AM</option>
											<option value="53">09:00 AM</option>
											<option value="54">09:30 AM</option>
											<option value="55">10:00 AM</option>
											<option value="56">10:30 AM</option>
											<option value="57">11:00 AM</option>
											<option value="58">11:30 AM</option>
											<option value="59">13:00 PM</option>
											<option value="510">13:30 PM</option>
											<option value="511">14:00 PM</option>
											<option value="512">14:30 PM</option>
											<option value="513">15:00 PM</option>
											<option value="514">15:30 PM</option>
											<option value="515">16:00 PM</option>
										</select> TO <select name="friTo2" form="modify_volunteer">
										<option value="0"></option>
										<option value="401">08:00 AM</option>
											<option value="51">08:00 AM</option>
											<option value="52">08:30 AM</option>
											<option value="53">09:00 AM</option>
											<option value="54">09:30 AM</option>
											<option value="55">10:00 AM</option>
											<option value="56">10:30 AM</option>
											<option value="57">11:00 AM</option>
											<option value="58">11:30 AM</option>
											<option value="59">13:00 PM</option>
											<option value="510">13:30 PM</option>
											<option value="511">14:00 PM</option>
											<option value="512">14:30 PM</option>
											<option value="513">15:00 PM</option>
											<option value="514">15:30 PM</option>
											<option value="515">16:00 PM</option>
										</select>
											</td>
										</tr>
									</table>
	

					<h2> Comments </h2>
					<div class="col-md-10">		
						<input type="textarea" class="form-control" maxlength="50" name="notes" value="${volunteer.notes}"/>
					</div>				

						<a href="<c:url value="/view_volunteers"/>" class="btn btn-primary" data-toggle="modal">Cancel</a>
						<input class="btn btn-primary" type="submit" value="Save Change" />
						<input id="volunteerId" name="volunteerId" type="hidden" value="${volunteer.volunteerId}"/>
					</fieldset>
				</form>
			</div>		
		</div>
	</div>
	
	</body>
</html>