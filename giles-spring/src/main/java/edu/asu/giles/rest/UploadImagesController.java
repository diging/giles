package edu.asu.giles.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.aspects.access.GitHubAccessCheck;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.DocumentAccess;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.files.impl.StorageStatus;
import edu.asu.giles.users.User;
import edu.asu.giles.util.FileUploadHelper;

@Controller
public class UploadImagesController {
    
    @Autowired
    private FileUploadHelper uploadHelper;
    
    @Autowired
    private IFilesManager filesManager;

    @GitHubAccessCheck
    @RequestMapping(value = "/rest/files/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImages(
            @RequestParam String accessToken, @RequestParam(value = "access", defaultValue = "PRIVATE") String access, @RequestParam("files") MultipartFile[] files, User user) {

        DocumentAccess docAccess = DocumentAccess.valueOf(access);
        if (docAccess == null) {
            return new ResponseEntity<String>("Access type: " + access
                    + " does not exist.", HttpStatus.BAD_REQUEST);
        }
        
        List<StorageStatus> statuses = uploadHelper.processUpload(docAccess, files, user.getUsername());
        
        Set<String> docIds = new HashSet<String>();
        statuses.forEach(status -> docIds.add(status.getFile().getDocumentId()));
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode root = mapper.createArrayNode();
        
        for (String docId : docIds) {
            
            IDocument doc = filesManager.getDocument(docId);
            
            ObjectNode docNode = mapper.createObjectNode();
            root.add(docNode);

            docNode.put("documentId", doc.getDocumentId());
            docNode.put("uploadId", doc.getUploadId());
            docNode.put("uploadedDate", doc.getCreatedDate());
            docNode.put("access", doc.getAccess().toString());

            ArrayNode paths = docNode.putArray("files");
            
            Stream<StorageStatus> docFileStatues = statuses.stream().filter(status -> status.getFile().getDocumentId().equals(docId));
            
            for (StorageStatus status : docFileStatues.collect(Collectors.toList())) {
                ObjectNode fileNode = mapper.createObjectNode();
                IFile file = status.getFile();
                fileNode.put("filename", file.getFilename());
                fileNode.put("path", filesManager.getRelativePathOfFile(file));
                fileNode.put("content-type", file.getContentType());
                fileNode.put("size", file.getSize());
                fileNode.put("success", status.getStatus());
                paths.add(fileNode);
            }
        }
        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            return new ResponseEntity<String>(
                    "{\"error\": \"Could not write json result.\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<String>(sw.toString(), HttpStatus.OK);
    }
}
