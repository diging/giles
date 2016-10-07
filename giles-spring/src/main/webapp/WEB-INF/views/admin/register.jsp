<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<h1>Register new App</h1>

<c:url value="/admin/apps/register" var="actionUrl" />

<form:form modelAttribute="app" action="${actionUrl}" method="POST">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
	<spring:bind path="name">
	<div class="form-group ${status.error ? 'has-error' : ''}">
		<label for="name">App Name</label>
		<form:input type="text" class="form-control" id="name" path="name"></form:input>
		<small><form:errors path="name" cssClass="error"></form:errors></small>
	</div>
	</spring:bind>
	
	<spring:bind path="providerId">
	<div class="form-group ${status.error ? 'has-error' : ''}">
		<label for="name">Identity Provider</label>
		<form:select class="form-control" id="providerId" path="providerId" items="${providers}"></form:select>
		<small><form:errors path="providerId" cssClass="error"></form:errors></small>
	</div>
	</spring:bind>
	
	<button type="submit" class="btn btn-primary btn-sm">Register</button>

</form:form>