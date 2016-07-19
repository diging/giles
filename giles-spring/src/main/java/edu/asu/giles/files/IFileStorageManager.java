package edu.asu.giles.files;

import edu.asu.giles.exceptions.GilesFileStorageException;

public interface IFileStorageManager {

	public abstract void saveFile(String username, String uploadId,
			String fileId, String filename, byte[] bytes)
			throws GilesFileStorageException;

}