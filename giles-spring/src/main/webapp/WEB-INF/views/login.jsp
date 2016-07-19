<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<h3>Welcome to Giles!</h3>

<sec:authorize access="isAuthenticated()">

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