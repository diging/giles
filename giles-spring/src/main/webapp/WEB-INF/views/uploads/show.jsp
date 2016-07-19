<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h3>Upload #${upload.id}</h3>

<p>The following files were uploaded as part of this upload on ${upload.createdDate}:</p>

<div class="list-group">
<c:forEach items="${files}" var="file">
  <a href="#" class="list-group-item">
    <h4 class="list-group-item-heading">File ${file.filename}</h4>
    <p class="list-group-item-text">Uploaded on ${file.uploadDate}.</p>
  </a>
 </c:forEach>
</div>