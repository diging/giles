package edu.asu.giles.files;

import java.io.File;

import edu.asu.giles.exceptions.GilesFileStorageException;

public interface IFileStorageManager {

	public abstract void saveFile(String username, String uploadId,
			String fileId, String filename, byte[] bytes)
			throws GilesFileStorageException;

	public abstract String getFileFolderPath(String username, String uploadId,
			String fileId);

}