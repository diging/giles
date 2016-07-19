package edu.asu.giles.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.IFilesManager;

@Controller
public class FilesController {

	@Autowired
	private IFilesManager filesManager;
	
	@RequestMapping(value="/rest/files/upload/{uploadId}")
	public ResponseEntity<String> getFilePathsForUpload(@PathVariable("uploadId") String uploadId, @RequestParam String accessToken) {
		GitHubTemplate template = new GitHubTemplate(accessToken);
		if (!template.isAuthorized()) {
			return new ResponseEntity<>("{ 'error': 'Github token not valid' }", HttpStatus.FORBIDDEN);
		}
		
		String username = template.userOperations().getUserProfile().getUsername();
		IUpload upload = filesManager.getUpload(uploadId);
		if (upload == null) {
			return new ResponseEntity<String>("{'error': 'Upload does not exist.'", HttpStatus.NOT_FOUND);
		}
		if (!upload.getUsername().equals(username)) {
			return new ResponseEntity<String>("{'error': 'Upload id not valid for user.'", HttpStatus.BAD_REQUEST);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		ArrayNode filesNode = root.putArray("filePaths");
		
		List<IFile> files = filesManager.getFilesByUploadId(uploadId);
		for (IFile file : files) {
			filesNode.add(filesManager.getPathOfFile(file));
		}
		
		return new ResponseEntity<String>(root.toString(), HttpStatus.OK);
	}
}
