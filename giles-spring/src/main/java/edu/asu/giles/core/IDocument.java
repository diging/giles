package edu.asu.giles.core;

import java.time.OffsetDateTime;
import java.util.List;

public interface IDocument {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getUploadId();

    public abstract void setUploadId(String uploadId);

    public abstract String getCreatedDate();

    public abstract void setCreatedDate(String createdDate);

    public abstract List<String> getFileIds();

    public abstract void setFileIds(List<String> fileIds);

    public abstract DocumentAccess getAccess();

    public abstract void setAccess(DocumentAccess access);

    public abstract void setDocumentId(String documentId);

    public abstract String getDocumentId();

    public abstract void setFiles(List<IFile> files);

    public abstract List<IFile> getFiles();

}