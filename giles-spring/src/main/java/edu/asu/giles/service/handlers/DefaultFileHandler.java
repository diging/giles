package edu.asu.giles.service.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.service.IFileTypeHandler;

@Service
public class DefaultFileHandler implements IFileTypeHandler {

    @Autowired
    @Qualifier("fileStorageManager")
    private IFileStorageManager storageManager;
    
    @Autowired
    private IFilesDatabaseClient databaseClient;
    
    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DEFAULT_HANDLER);
        return types;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document, IUpload upload, String id, byte[] content) throws GilesFileStorageException {
        storageManager.saveFile(username, upload.getId(), id, file.getFilename(), content);
        databaseClient.saveFile(file);
        return true;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = storageManager.getFileFolderPath(file.getUsername(), file.getUploadId(), file.getId());
        return directory + File.separator + file.getFilename();
    }

}
