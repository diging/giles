package edu.asu.giles.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.File;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.files.impl.StorageStatus;

@Service
public class FileUploadHelper {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private IFilesManager filesManager;
 
    public List<StorageStatus> processUpload(DocumentAccess access, DocumentType docType, MultipartFile[] files, String username) {
        Map<IFile, byte[]> uploadedFiles = new HashMap<>();
        
        if (access == null) {
            access = DocumentAccess.PRIVATE;
        }
        for (MultipartFile f : files) {
            IFile file = new File(f.getOriginalFilename());
            file.setContentType(f.getContentType());
            file.setSize(f.getSize());
            file.setAccess(access);
            try {
                uploadedFiles.put(file, f.getBytes());
            } catch (IOException e) {
                logger.error("Couldn't get file content.", e);
                uploadedFiles.put(file, null);
            }
        }

        return filesManager.addFiles(uploadedFiles, username, docType, access);
    }
}
