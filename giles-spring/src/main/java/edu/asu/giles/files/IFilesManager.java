package edu.asu.giles.files;

import java.util.List;
import java.util.Map;

import edu.asu.giles.core.IFile;
import edu.asu.giles.files.impl.StorageStatus;

public interface IFilesManager {

	/**
	 * This method saves the given files to the database. It generates
	 * an id for each file and an upload id that is the same for all
	 * files.
	 * 
	 * @param files The files to save.
	 * @return The list of saved files with ids and upload id set.
	 */
	public abstract List<StorageStatus> addFiles(Map<IFile, byte[]> files, String username);

}