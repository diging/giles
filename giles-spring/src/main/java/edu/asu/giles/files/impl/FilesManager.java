package edu.asu.giles.files.impl;

import java.io.File;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.core.impl.Document;
import edu.asu.giles.core.impl.DocumentAccess;
import edu.asu.giles.core.impl.Upload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.files.IDocumentDatabaseClient;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.files.IUploadDatabaseClient;

@Service
public class FilesManager implements IFilesManager {
	
	Logger logger = LoggerFactory.getLogger(FilesManager.class);

	@Autowired
	private IFilesDatabaseClient databaseClient;
	
	@Autowired
	private IUploadDatabaseClient uploadDatabaseClient;
	
	@Autowired
	private IDocumentDatabaseClient documentDatabaseClient;
	
	@Autowired
	private IFileStorageManager storageManager;

	/* (non-Javadoc)
	 * @see edu.asu.giles.files.impl.IFilesManager#addFiles(java.util.List)
	 */
	@Override
	public List<StorageStatus> addFiles(Map<IFile, byte[]> files, String username) {
		
		String uploadId = null;
		while(true) {
			uploadId = "UP" + generateId();
			List<IFile> existingFiles = databaseClient.getFileByUploadId(uploadId);
			if (existingFiles == null || existingFiles.isEmpty()) {
				break;
			}
		}
		
		IUpload upload = new Upload(uploadId);
		OffsetDateTime uploadDate = OffsetDateTime.now(ZoneId.of("UTC"));
		upload.setCreatedDate(uploadDate);
		upload.setUsername(username);
		
		List<StorageStatus> statuses = new ArrayList<StorageStatus>();
		for (IFile file : files.keySet()) {
			byte[] content = files.get(file);
			
			if (content == null) {
				statuses.add(new StorageStatus(file, null, StorageStatus.FAILURE));
				continue;
			}
			
			String id = null;
			
			// generate unique id
			while(true) {
				id = "FILE" + generateId();
				IFile existingFile = databaseClient.getFileById(id);
				if (existingFile == null) {
					break;
				}
			}
			
			IDocument document = new Document();
			String docId = null;
			while(true) {
				docId = "DOC" + generateId();
				IDocument existingDoc = documentDatabaseClient.getDocumentById(docId);
				if (existingDoc == null) {
					break;
				}
			}
			
			if (file.getAccess() == null) {
				file.setAccess(DocumentAccess.PRIVATE);
			}
			
			document.setDocumentId(docId);
			document.setId(docId);
			document.setCreatedDate(uploadDate);
			document.setAccess(file.getAccess());
			document.setUploadId(uploadId);
			document.setFileIds(new ArrayList<>());
			document.getFileIds().add(id);
			
			file.setId(id);
			file.setDocumentId(docId);
			file.setUploadId(uploadId);
			file.setUploadDate(uploadDate);
			file.setUsername(username);
			file.setFilepath(getRelativePathOfFile(file));
			
			try {
				storageManager.saveFile(username, uploadId, id, file.getFilename(), content);
				databaseClient.saveFile(file);
				documentDatabaseClient.saveDocument(document);
				statuses.add(new StorageStatus(file, null, StorageStatus.SUCCESS));
			} catch (GilesFileStorageException e) {
				logger.error("Could not store uploaded files.", e);
				statuses.add(new StorageStatus(file, e, StorageStatus.FAILURE));
			}
		}
		
		boolean atLeastOneSuccess = statuses.stream().anyMatch(status -> status.getStatus() == StorageStatus.SUCCESS);
		if (atLeastOneSuccess) {
			uploadDatabaseClient.store(upload);
		}
		
		return statuses;
	}
	
	@Override
	public List<IFile> getFilesByUploadId(String uploadId) {
		return databaseClient.getFileByUploadId(uploadId);
	}
	
	@Override
	public List<IDocument> getDocumentsByUploadId(String uploadId) {
		List<IDocument> documents = documentDatabaseClient.getDocumentByUploadId(uploadId);
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
	public void saveFile(IFile file) {
		databaseClient.saveFile(file);
	}
	
	@Override
	public List<IUpload> getUploadsOfUser(String username) {
		return uploadDatabaseClient.getUploadsForUser(username);
	}
	
	@Override
	public IUpload getUpload(String id) {
		return uploadDatabaseClient.getUpload(id);
	}
	
	@Override
	public String getRelativePathOfFile(IFile file) {
		String directory = storageManager.getFileFolderPath(file.getUsername(), file.getUploadId(), file.getId());
		return directory + File.separator + file.getFilename();
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
	private String generateId() {
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
