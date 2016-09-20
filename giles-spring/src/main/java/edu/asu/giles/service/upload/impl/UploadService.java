package edu.asu.giles.service.upload.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.files.impl.StorageStatus;
import edu.asu.giles.service.upload.IUploadService;

@Service
public class UploadService implements IUploadService {
    
    private final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;

    private Map<String, Future<List<StorageStatus>>> currentUploads;
    private Map<String, Date> expirationList;
    
    @Autowired
    private UploadThread uploadThread;
    
    @PostConstruct
    public void init() {
        currentUploads = new HashMap<String, Future<List<StorageStatus>>>();
        expirationList = new HashMap<>();
    }

    /* (non-Javadoc)
     * @see edu.asu.giles.service.upload.impl.IUploadService#startUpload(edu.asu.giles.core.DocumentAccess, edu.asu.giles.core.DocumentType, org.springframework.web.multipart.MultipartFile[], java.lang.String)
     */
    @Override
    public String startUpload(DocumentAccess access, DocumentType type, MultipartFile[] files, String username) {
        // clean out old uploads before adding new
        cleanUp();
        
        String uploadProgressId = generateId();
        currentUploads.put(uploadProgressId, uploadThread.runUpload(access, type, files, username));
        expirationList.put(uploadProgressId, new Date());
        return uploadProgressId;
    }
    
    public void cleanUp() {
        List<String> remove = new ArrayList<String>();
        for (String id : expirationList.keySet()) {
            Date expirationDate = expirationList.get(id);
            boolean expired = Math.abs(expirationDate.getTime() - new Date().getTime()) > MILLIS_PER_DAY;

            if (expired) {
                currentUploads.remove(expirationList.get(expirationDate));
                remove.add(id);
            }
        }
        
        remove.forEach(id -> expirationList.remove(id));
    }
    
    @Override
    public Future<List<StorageStatus>> getUpload(String id) {
        return currentUploads.get(id);
    }
    
    @Override
    public void removeUpload(String id) {
        currentUploads.remove(id);
    }
    
    protected String generateId() {
        String id = null;
        while (true) {
            id = "PROG" + generateUniqueId();
            Object existingFile = currentUploads.get(id);
            if (existingFile == null) {
                break;
            }
        }
        return id;
    }
    
    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 6 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
}
