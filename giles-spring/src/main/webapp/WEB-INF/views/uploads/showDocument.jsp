<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<ol class="breadcrumb">
  <li>Upload</li>
  <li><a href="<c:url value="/uploads/${document.uploadId}" />">${document.uploadId}</a></li>
</ol>

<h1>${document.documentId}</h1>

<div class="row">
<div class="col-md-9">
<p>
	<c:if test="${document.access == 'PUBLIC'}">
		<span class="label label-info">Public</span>
	</c:if>
	<c:if test="${document.access == 'PRIVATE'}">
		<span class="label label-danger">Private</span>
	</c:if>

	&nbsp; &nbsp; <a href="${document.metadataUrl}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a>

</p>


<p>Uploaded on <span class="date">${document.createdDate}</span>.</p>


<dl class="dl-horizontal">
  <dt>Uploaded file: </dt><dd><a href="<c:url value="/files/${document.uploadedFile.id}" />" >${document.uploadedFile.filename}</a>&nbsp; &nbsp; <a href="${page.uploadedFile.metadataLink}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a></dd>
  <dt>Embedded text: </dt><dd><c:if test="${not empty document.extractedTextFile}"><a href="<c:url value="/files/${document.extractedTextFile.id}" />" >${document.extractedTextFile.filename}</a> &nbsp; &nbsp; <a href="${page.extractedTextFile.metadataLink}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a></c:if></dd>
</dl>

<h4>Pages</h4>

<c:if test="${not empty document.pages}">
<c:forEach items="${document.pages}" var="page">

<dl class="dl-horizontal clearfix">
	<dt><span class="label label-default">Page ${page.pageNr}</span>&nbsp; &nbsp; Image: </dt><dd><a href="<c:url value="/files/${page.imageFile.id}" />" >${page.imageFile.filename}</a>&nbsp; &nbsp; <a href="${page.imageFile.metadataLink}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a></dd>
	<dt> Text: </dt><dd><a href="<c:url value="/files/${page.textFile.id}" />" >${page.textFile.filename}</a>&nbsp; &nbsp; <a href="${page.textFile.metadataLink}"><i class="fa fa-globe" aria-hidden="true"></i> view metadata</a></dd>
</dl>
</c:forEach>
</c:if>
</div>

<div class="col-md-3">
<c:if test="${not empty document.firstImage}">
<div class="pull-right">
    <a href="<c:url value="/files/${document.firstImage.id}" />" >
    <img src="<c:url value="/files/${document.firstImage.id}/img?dw=200" />" >
    </a> 
</div>
</c:if>
</div>
</div>