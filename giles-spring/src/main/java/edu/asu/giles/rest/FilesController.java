package edu.asu.giles.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.IFilesManager;

@Controller
public class FilesController {

	@Autowired
	private IFilesManager filesManager;

	@RequestMapping(value = "/rest/files/upload/{uploadId}")
	public ResponseEntity<String> getFilePathsForUpload(
			@PathVariable("uploadId") String uploadId,
			@RequestParam String accessToken) {
		GitHubTemplate template = new GitHubTemplate(accessToken);
		if (!template.isAuthorized()) {
			return new ResponseEntity<>(
					"{ 'error': 'Github token not valid' }",
					HttpStatus.FORBIDDEN);
		}

		String username = template.userOperations().getUserProfile()
				.getUsername();
		IUpload upload = filesManager.getUpload(uploadId);
		if (upload == null) {
			return new ResponseEntity<String>(
					"{'error': 'Upload does not exist.'", HttpStatus.NOT_FOUND);
		}
		if (!upload.getUsername().equals(username)) {
			return new ResponseEntity<String>(
					"{'error': 'Upload id not valid for user.'",
					HttpStatus.BAD_REQUEST);
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		ArrayNode root = mapper.createArrayNode();
		
		List<IFile> files = filesManager.getFilesByUploadId(uploadId);
		Map<String, List<String>> filesByDoc = new HashMap<String, List<String>>();

		for (IFile file : files) {
			if (filesByDoc.get(file.getDocumentId()) == null) {
				filesByDoc.put(file.getDocumentId(), new ArrayList<>());
			}
			filesByDoc.get(file.getDocumentId()).add(filesManager.getPathOfFile(file));
		}
		
		for (String key : filesByDoc.keySet()) {
			ObjectNode doc = mapper.createObjectNode();
			root.add(doc);
			ArrayNode paths = doc.putArray(key);
			for (String path : filesByDoc.get(key)) {
				paths.add(path);
			}
		}

		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, root);
		} catch (IOException e) {
			return new ResponseEntity<String>("{\"error\": \"Could not write json result.\" }", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
	}
}
