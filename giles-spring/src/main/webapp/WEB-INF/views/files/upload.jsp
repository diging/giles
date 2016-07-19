<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="<c:url value="/resources/jquery-file-upload/css/style.css" />">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet" href="<c:url value="/resources/jquery-file-upload/css/jquery.fileupload.css" />">

<h2>Select files to upload</h2>

	<div id="upload-field">
	<span class="btn btn-success fileinput-button">
        <i class="glyphicon glyphicon-plus"></i>
        <span>Add files...</span>
        <!-- The file input field used as target for the file upload widget -->
        <input id="fileupload" type="file" name="file" multiple>
    </span>
    </div>
    <br>
    <br>
    <!-- The global progress bar -->
    <div id="progress">
	    <div class="bar" style="width: 0%;"></div>
	</div>
    <!-- The container for the uploaded files -->
    <ul id="files" class="list-group" style="margin-top: 35px;">
    
    </ul>

<a href="#" id="jarsLink" class="btn btn-primary disabled">Add metadata in Jars</a>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="<c:url value="/resources/jquery-file-upload/js/vendor/jquery.ui.widget.js" />"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
<script src="//blueimp.github.io/JavaScript-Load-Image/js/load-image.all.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality -->
<script src="//blueimp.github.io/JavaScript-Canvas-to-Blob/js/canvas-to-blob.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.iframe-transport.js" />" ></script>
<!-- The basic File Upload plugin -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload.js" />" ></script>
<!-- The File Upload processing plugin -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-process.js" />"></script>
<!-- The File Upload image preview & resize plugin -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-image.js" />"></script>
<!-- The File Upload audio preview plugin -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-audio.js" />"></script>
<!-- The File Upload video preview plugin -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-video.js" />"></script>
<!-- The File Upload validation plugin -->
<script src="<c:url value="/resources/jquery-file-upload/js/jquery.fileupload-validate.js" />"></script>

<script>
//# sourceURL=upload.js
var uploadIds = [];
var jarsUrl = '${jars.url}';

$(function () {
    'use strict';

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
        url: '<c:url value="/files/upload" />',
        singleFileUploads: false,
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .bar').css(
                'width',
                progress + '%'
            );
        },
        done: function (e, data) {
        	$('#upload-field').prop("disabled",true);
        	var response = JSON.parse(data.result);
        	response.files.forEach(function(element, index) {
        		var newItem = '<li class="list-group-item">';
        		if (element.status == 0) {
        			newItem += '<span class="badge badge-success">Success</span>';
        		} else {
        			newItem += '<span class="badge badge-failure">Failure</span>';
        		}
        		newItem += element.name;
        		newItem += '</li>';
          		$('#files').append(newItem);
          		if (!uploadIds.includes(element.uploadId)) {
          			uploadIds.push(element.uploadId);
          		}
          	});
        	
        	var uploadIdsString = "";
        	uploadIds.forEach(function(element, index) {
        		uploadIdsString += element;
        		uploadIdsString += ",";
        	});
        	
        	$('#jarsLink').attr('href', jarsUrl + "?uploadIds=" + uploadIdsString);
        	$('#jarsLink').removeClass('disabled');
        },
        start: function(e, data) {
        	$('#progress .bar').css(
                    'width',
                    0 + '%'
                );
        }
    });
});
</script>
