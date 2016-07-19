package edu.asu.giles.core;

public interface IFile {

	public abstract String getUploadId();

	public abstract void setUploadId(String uploadId);

	public abstract String getFilename();

	public abstract void setFilename(String filename);

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract String getId();

	public abstract void setId(String id);

	public abstract void setZoteroDocumentId(String zoteroDocumentId);

	public abstract String getZoteroDocumentId();

}