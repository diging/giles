package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IFile;
import edu.asu.giles.db4o.IDatabaseClient;
import edu.asu.giles.exceptions.UnstorableObjectException;

public interface IFilesDatabaseClient extends IDatabaseClient<IFile> {

    public abstract IFile saveFile(IFile file) throws UnstorableObjectException;

    public abstract IFile getFile(String filename);

    public abstract List<IFile> getFilesByExample(IFile file);

    public abstract List<IFile> getFileByUploadId(String uploadId);

    public abstract IFile getFileById(String id);

}