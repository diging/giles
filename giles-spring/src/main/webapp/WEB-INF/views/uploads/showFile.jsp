<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<h1>${file.filename}</h1>

<p>
<c:if test="${file.access == 'PUBLIC'}">
	<span class="label label-info">Public</span>
</c:if>
<c:if test="${file.access == 'PRIVATE'}">
   <span class="label label-danger">Private</span>
</c:if>
&nbsp; &nbsp;
<a href="${file.metadataLink}"><i class="fa fa-globe" aria-hidden="true"></i> View/edit metadata</a>

</p>

<p>
Uploaded on ${file.uploadDate}.
</p>

<p>
<img src="<c:url value="/files/${file.id}/img?dw=600" /> ">
</p>