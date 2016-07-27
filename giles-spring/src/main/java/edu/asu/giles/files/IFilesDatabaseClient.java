package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.File;

public interface IFilesDatabaseClient {

    public abstract IFile saveFile(IFile file);

    public abstract IFile getFile(String filename);

    public abstract List<IFile> getFilesByExample(IFile file);

    public abstract List<IFile> getFileByUploadId(String uploadId);

    public abstract IFile getFileById(String id);

}