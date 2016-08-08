package edu.asu.giles.web.pages;

import java.util.List;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.core.IFile;

public class DocumentPageBean {
    private String id;
    private String documentId;
    private String uploadId;
    private String createdDate;
    private List<String> fileIds;
    private DocumentAccess access;
    private transient List<IFile> files;
    private DocumentType documentType;
    private int pageCount;
    private IFile firstImage;
    private List<String> textFileIds;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getUploadId() {
        return uploadId;
    }
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public List<String> getFileIds() {
        return fileIds;
    }
    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }
    public DocumentAccess getAccess() {
        return access;
    }
    public void setAccess(DocumentAccess access) {
        this.access = access;
    }
    public List<IFile> getFiles() {
        return files;
    }
    public void setFiles(List<IFile> files) {
        this.files = files;
    }
    public DocumentType getDocumentType() {
        return documentType;
    }
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    public int getPageCount() {
        return pageCount;
    }
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    public IFile getFirstImage() {
        for (IFile file : files) {
            if (file.getContentType().startsWith("image/")) {
                return file;
            }
        }
        return null;
    }
    public void setFirstImage(IFile firstImage) {
        this.firstImage = firstImage;
    }
    public List<String> getTextFileIds() {
        return textFileIds;
    }
    public void setTextFileIds(List<String> textFileIds) {
        this.textFileIds = textFileIds;
    }
    
    
}
