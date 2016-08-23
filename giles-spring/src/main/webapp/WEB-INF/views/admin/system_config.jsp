<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h1>System Configuration</h1>

<form:form modelAttribute="systemConfigPage" action="${pageContext.request.contextPath}/admin/system/config" method="POST">

<div class="page-header">
	<h3>GitHub Integration</h3>
</div>
	<div class="form-group">
		<label for="githubSecret">GitHub Secret</label> 
		<form:input
			type="text" class="form-control" id="githubSecret"
			placeholder="GitHub Secret" path="githubSecret" value="${githubSecret}" ></form:input>
		<small><form:errors class="error" path="githubSecret"></form:errors></small>
	</div>
	<div class="form-group">
		<label for="githubClientId">GitHub Client Id</label> 
		<form:input
			type="text" class="form-control" id="githubClientId"
			placeholder="GitHub Client Id" path="githubClientId" value="${githubClientId}"></form:input>
		<small><form:errors class="error" path="githubClientId"></form:errors></small>
	</div>

<div class="page-header">
	<h3>Application Integration</h3>
</div>

	<div class="form-group">
		<label for="gilesUrl">Giles Base URL</label> 
		<form:input
			type="text" class="form-control" id="gilesUrl"
			placeholder="Giles Base URL" path="gilesUrl" value="${gilesUrl}" ></form:input>
		<small><form:errors class="error" path="gilesUrl"></form:errors></small>
	</div>
	<div class="form-group">
		<label for="digilibScalerUrl">Digilib Scaler URL</label> 
		<form:input
			type="text" class="form-control" id="digilibScalerUrl"
			placeholder="Digilib Scaler URL" path="digilibScalerUrl" value="${digilibScalerUrl}" ></form:input>
		<small><form:errors class="error" path="digilibScalerUrl"></form:errors></small>
	</div>
	<div class="form-group">
		<label for="jarsUrl">Jars URL</label> 
		<form:input
			type="text" class="form-control" id="jarsUrl"
			placeholder="Jars URL" path="jarsUrl" value="${jarsUrl}" ></form:input>
		<small><form:errors class="error" path="jarsUrl"></form:errors></small>
	</div>
	<div class="form-group">
		<label for="jarsFileUrl">Jars Callback URL for Files</label> 
		<form:input
			type="text" class="form-control" id="jarsFileUrl"
			placeholder="Jars Callback URL" path="jarsFileUrl" value="${jarsFileUrl}" ></form:input>
		<p>
		<small>You can use <code>{giles}</code> as a placeholder for Giles' base URL and <code>{fileId}</code> 
		to indicate that the id of the file a callback is created for should be inserted.</small>
		</p>
		<small><form:errors class="error" path="jarsFileUrl"></form:errors></small>
	</div>

<div class="page-header">
	<h3>PDF Processing</h3>
</div>

	<div class="form-group">
		<label for="pdfExtractText">Extract Images from PDFs</label>
		<form:select class="form-control" id="pdfExtractText" path="pdfExtractText">
		   <form:option value="true" label="Yes"/>
		   <form:option value="false" label="No"/>
		</form:select>
		<small><form:errors class="error" path="pdfExtractText"></form:errors></small>
	</div>

	<div class="form-group">
		<label for="pdfToImageDpi">DPI of Extracted Images from PDF</label> 
		<form:input
			type="text" class="form-control" id="pdfToImageDpi"
			placeholder="DPI of extracted images" path="pdfToImageDpi" value="${pdfToImageDpi}" ></form:input>
		<small><form:errors class="error" path="pdfToImageDpi"></form:errors></small>
	</div>
	
	<div class="form-group">
		<label for="pdfToImageType">Type of Extracted Images from PDF</label> 
		<form:select
			class="form-control" id="pdfToImageType" items="${imageTypeOptions}"
			path="pdfToImageType" >
		</form:select>
		<small><form:errors class="error" path="pdfToImageType"></form:errors></small>
		<p></p>
	</div>
	
	<div class="form-group">
		<label for="pdfToImageFormat">Image Format of extracted Images</label>
		<form:input
			type="text" class="form-control" id="pdfToImageFormat"
			placehodler="Image format" path="pdfToImageFormat" value="${pdfToImageFormat}"></form:input>
		<small><form:errors class="error" path="pdfToImageFormat"></form:errors></small>
	</div>
	

<div class="page-header">
	<h3>Tesseract Integration</h3>
</div>

	<div class="form-group">
		<label for="ocrImagesFromPdfs">Run OCR on Extracted Images</label>
		<form:select class="form-control" id="ocrImagesFromPdfs" path="ocrImagesFromPdfs">
		   <form:option value="true" label="Yes"/>
		   <form:option value="false" label="No"/>
		</form:select>
		<small><form:errors class="error" path="ocrImagesFromPdfs"></form:errors></small>
	</div>
	
	<div class="form-group">
		<label for="tesseractBinFolder">Path to Tesseract <code>bin</code> Folder</label> 
		<form:input
			type="text" class="form-control" id="tesseractBinFolder"
			placeholder="e.g. /usr/bin/" path="tesseractBinFolder" value="${tesseractBinFolder}" ></form:input>
		<small><form:errors class="error" path="tesseractBinFolder"></form:errors></small>
	</div>
	
	<div class="form-group">
		<label for="tesseractDataFolder">Path to Tesseract <code>data</code> Folder</label> 
		<form:input
			type="text" class="form-control" id="tesseractDataFolder"
			placeholder="e.g. /usr/share/tesseract/" path="tesseractDataFolder" value="${tesseractDataFolder}" ></form:input>
		<small><form:errors class="error" path="tesseractDataFolder"></form:errors></small>
	</div>

<button class="btn btn-primary btn-md pull-right" type="submit">Save Changes!</button>
</form:form>