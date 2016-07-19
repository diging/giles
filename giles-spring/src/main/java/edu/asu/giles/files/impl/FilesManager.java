package edu.asu.giles.files.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IFile;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.files.IFilesManager;

@Service
public class FilesManager implements IFilesManager {

	@Autowired
	private IFilesDatabaseClient databaseClient;
	
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
		
		List<StorageStatus> statuses = new ArrayList<StorageStatus>();
		for (IFile file : files.keySet()) {
			byte[] content = files.get(file);
			String id = null;
			
			// generate unique id
			while(true) {
				id = "FILE" + generateId();
				IFile existingFile = databaseClient.getFileById(id);
				if (existingFile == null) {
					break;
				}
			}
			
			file.setId(id);
			file.setUploadId(uploadId);
			
			try {
				storageManager.saveFile(username, uploadId, id, file.getFilename(), content);
				databaseClient.addFile(file);
				statuses.add(new StorageStatus(file, null, StorageStatus.SUCCESS));
			} catch (GilesFileStorageException e) {
				statuses.add(new StorageStatus(file, e, StorageStatus.FAILURE));
			}
		}
		
		return statuses;
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
