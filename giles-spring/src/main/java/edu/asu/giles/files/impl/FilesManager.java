package edu.asu.giles.files.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.core.impl.Document;
import edu.asu.giles.core.impl.Upload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.files.IDocumentDatabaseClient;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.files.IUploadDatabaseClient;
import edu.asu.giles.service.IFileHandlerRegistry;
import edu.asu.giles.service.IFileTypeHandler;

@PropertySource("classpath:/config.properties")
@Service
public class FilesManager implements IFilesManager {

    private Logger logger = LoggerFactory.getLogger(FilesManager.class);

    @Value("${default_page_size}")
    private Integer defaultPageSize;

    @Autowired
    private IFilesDatabaseClient databaseClient;

    @Autowired
    private IUploadDatabaseClient uploadDatabaseClient;

    @Autowired
    private IDocumentDatabaseClient documentDatabaseClient;

    @Autowired
    private IFileHandlerRegistry fileHandlerRegistry;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.impl.IFilesManager#addFiles(java.util.List)
     */
    @Override
    public List<StorageStatus> addFiles(Map<IFile, byte[]> files,
            String username, DocumentType docType, DocumentAccess access) {

        String uploadId = uploadDatabaseClient.generateId();
        String uploadDate = OffsetDateTime.now(ZoneId.of("UTC")).toString();

        IUpload upload = createUpload(username, uploadId, uploadDate);

        List<StorageStatus> statuses = new ArrayList<StorageStatus>();
        IDocument document = null;
        if (docType == DocumentType.MULTI_PAGE) {
            document = createDocument(uploadId, uploadDate, access, docType);
        }
        for (IFile file : files.keySet()) {
            byte[] content = files.get(file);

            if (content == null) {
                statuses.add(new StorageStatus(file, null,
                        StorageStatus.FAILURE));
                continue;
            }

            String id = databaseClient.generateId();

            if (docType == DocumentType.SINGLE_PAGE) {
                document = createDocument(uploadId, uploadDate,
                        file.getAccess(), docType);
            }

            file.setId(id);
            file.setDocumentId(document.getId());
            file.setUploadId(uploadId);
            file.setUploadDate(uploadDate);
            file.setUsername(username);
            file.setFilepath(getRelativePathOfFile(file));

            document.getFileIds().add(id);

            IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                    .getContentType());

            try {
                boolean success = handler.processFile(username, file, document,
                        upload, content);
                documentDatabaseClient.saveDocument(document);
                statuses.add(new StorageStatus(file, null,
                        (success ? StorageStatus.SUCCESS
                                : StorageStatus.FAILURE)));
            } catch (GilesFileStorageException e) {
                logger.error("Could not store uploaded files.", e);
                statuses.add(new StorageStatus(file, e, StorageStatus.FAILURE));
            } catch (Exception e) {
                // this is meant to be Exception to make sure we give the
                // user appropriate feedback
                logger.error("An unexpected exception was thrown.", e);
                statuses.add(new StorageStatus(file,
                        new GilesFileStorageException(e), StorageStatus.FAILURE));
            }
        }

        boolean atLeastOneSuccess = statuses.stream().anyMatch(
                status -> status.getStatus() == StorageStatus.SUCCESS);
        if (atLeastOneSuccess) {
            uploadDatabaseClient.store(upload);
        }

        return statuses;
    }

    private IUpload createUpload(String username, String uploadId,
            String uploadDate) {
        IUpload upload = new Upload(uploadId);
        upload.setCreatedDate(uploadDate);
        upload.setUsername(username);
        return upload;
    }

    private IDocument createDocument(String uploadId, String uploadDate,
            DocumentAccess access, DocumentType docType) {
        IDocument document = new Document();
        String docId = documentDatabaseClient.generateId();
        document.setDocumentId(docId);
        document.setId(docId);
        document.setCreatedDate(uploadDate);
        document.setAccess(access);
        document.setUploadId(uploadId);
        document.setFileIds(new ArrayList<>());
        document.setTextFileIds(new ArrayList<>());
        document.setDocumentType(docType);

        return document;
    }

    @Override
    public List<IFile> getFilesByUploadId(String uploadId) {
        return databaseClient.getFileByUploadId(uploadId);
    }

    @Override
    public List<IDocument> getDocumentsByUploadId(String uploadId) {
        List<IDocument> documents = documentDatabaseClient
                .getDocumentByUploadId(uploadId);
        for (IDocument doc : documents) {
            doc.setFiles(new ArrayList<>());
            for (String fileId : doc.getFileIds()) {
                doc.getFiles().add(databaseClient.getFileById(fileId));
            }
        }
        return documents;
    }

    @Override
    public IFile getFile(String id) {
        return databaseClient.getFileById(id);
    }

    @Override
    public IFile getFileByPath(String path) {
        IFile file = new edu.asu.giles.core.impl.File();
        file.setFilepath(path);

        List<IFile> files = databaseClient.getFilesByExample(file);
        if (files == null || files.isEmpty()) {
            return null;
        }

        return files.get(0);
    }

    @Override
    public byte[] getFileContent(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getFileContent(file);
    }

    @Override
    public void saveFile(IFile file) {
        databaseClient.saveFile(file);
    }

    @Override
    public List<IUpload> getUploadsOfUser(String username, int page, int pageSize, String sortBy, int sortDirection) {
        if (pageSize == -1) {
            pageSize = defaultPageSize;
        }
        if (page < 1) {
            page = 1;
        }
        int pageCount = getUploadsOfUserPageCount(username);
        if (page > pageCount) {
            page = pageCount;
        }
        return uploadDatabaseClient.getUploadsForUser(username, page, pageSize, sortBy, sortDirection);
    }
    
    @Override
    public int getUploadsOfUserCount(String username) {
        List<IUpload> uploads = uploadDatabaseClient.getUploadsForUser(username);
        return uploads.size();
    }
    
    @Override
    public int getUploadsOfUserPageCount(String username) {
        int totalUploads = getUploadsOfUserCount(username);
        return (int) Math.ceil(new Double(totalUploads) / new Double(defaultPageSize));
    }

    @Override
    public IUpload getUpload(String id) {
        return uploadDatabaseClient.getUpload(id);
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getRelativePathOfFile(file);
    }

    @Override
    public String getFileUrl(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getFileUrl(file);
    }

    @Override
    public IDocument getDocument(String id) {
        return documentDatabaseClient.getDocumentById(id);
    }

    @Override
    public void saveDocument(IDocument document) {
        documentDatabaseClient.saveDocument(document);
    }

    @Override
    public List<IFile> getFilesOfDocument(IDocument doc) {
        List<String> fileIds = doc.getFileIds();

        List<IFile> files = new ArrayList<>();
        fileIds.forEach(id -> files.add(getFile(id)));

        return files;
    }
    
    @Override
    public List<IFile> getTextFilesOfDocument(IDocument doc) {
        List<String> fileIds = doc.getTextFileIds();
        
        List<IFile> files = new ArrayList<>();
        fileIds.forEach(id -> files.add(getFile(id)));
        return files;
    }

}
