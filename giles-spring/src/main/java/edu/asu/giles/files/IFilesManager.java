package edu.asu.giles.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.impl.StorageStatus;

public interface IFilesManager {

    /**
     * This method saves the given files to the database. It generates an id for
     * each file and an upload id that is the same for all files.
     * 
     * @param files
     *            The files to save.
     * @return The list of saved files with ids and upload id set.
     */
    public abstract List<StorageStatus> addFiles(Map<IFile, byte[]> files,
            String username, DocumentType docType, DocumentAccess access);

    public abstract List<IUpload> getUploadsOfUser(String username);

    public abstract IUpload getUpload(String id);

    public abstract List<IFile> getFilesByUploadId(String uploadId);

    public abstract List<IDocument> getDocumentsByUploadId(String uploadId);

    public abstract IDocument getDocument(String id);

    public abstract void saveDocument(IDocument document);

    public abstract List<IFile> getFilesOfDocument(IDocument doc);

    public abstract IFile getFile(String id);

    public abstract void saveFile(IFile file);

    public abstract IFile getFileByPath(String path);

    public abstract String getFileUrl(IFile file);

}