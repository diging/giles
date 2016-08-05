<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>




<div class="jumbotron">

<sec:authorize access="isAnonymous()">
<h1>Welcome to Giles!</h1>
<p>
Giles is an app that work in tandem with <a href="http://digilib.sourceforge.net/" target="_blank">Digilib</a> and
<a href="https://github.com/diging/jars" target="_blank">Jars</a>. Giles provides upload functionality for images
that are then accessible through Digilib. Metadata of uploaded images  managed with Jars.
</p>

<p>
To use Giles, you need to log in with your GitHub account. Don't have one? You can create one <a href="http://github.com">here</a>.
</p>


<form action="<c:url value="/signin/github" />" method="POST">
	<button class="btn btn-primary btn-lg" type="submit">
		<i class="fa fa-github" aria-hidden="true"></i> Login with GitHub
	</button>
</form>
</sec:authorize>

<sec:authorize access="isAuthenticated()">
<div class="hidden"><c:catch var="exception"><sec:authentication
							property="principal.fullname" /></c:catch></div>
<c:if test="${empty exception}">
<h2>Welcome, <sec:authentication
							property="principal.fullname" />!</h2>
</c:if>
<c:if test="${not empty exception}">
<h2>Welcome, <sec:authentication
							property="principal.username" />!</h2>
</c:if>

<sec:authorize access="hasRole('ROLE_USER')">
<p>
What do you want to do? To upload new files, head over to the upload page. You can find your previous uploads below.
</p>							
<p>						
<a href="<c:url value="/files/upload" />" class="btn btn-primary btn-md">Upload new files</a>
</p>
</sec:authorize>

<c:if test="${not empty user and user.accountStatus == 'ADDED' }">
<p>
Your account has not be approved yet. Please contact a Giles administrator.
</p>
</c:if>

<c:if test="${not empty user and user.accountStatus == 'REVOKED' }">
<p>
Your account has been revoked. Please contact a Giles administrator.
</p>
</c:if>

</sec:authorize>

</div>

<sec:authorize access="hasRole('ROLE_USER')">

<h4>Your latest uploads:</h4>
<div class="list-group">
<c:forEach items="${uploads}" var="upload">
  <a href="<c:url value="/uploads/${upload.id}" />" class="list-group-item">
    <h4 class="list-group-item-heading">Upload #${upload.id}</h4>
    <p class="list-group-item-text">Uploaded on ${upload.createdDate}.</p>
  </a>
 </c:forEach>
</div>

</sec:authorize>

