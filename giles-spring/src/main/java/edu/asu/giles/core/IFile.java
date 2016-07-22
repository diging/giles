package edu.asu.giles.core;

import java.util.Date;

import edu.asu.giles.core.impl.DocumentAccess;

public interface IFile {
	
	public abstract String getUploadId();

	public abstract void setUploadId(String uploadId);

	public abstract String getFilename();

	public abstract void setFilename(String filename);

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract String getId();

	public abstract void setId(String id);

	public abstract void setDocumentId(String zoteroDocumentId);

	public abstract String getDocumentId();

	public abstract void setUploadDate(Date uploadDate);

	public abstract Date getUploadDate();

	public abstract void setAccess(DocumentAccess access);

	public abstract DocumentAccess getAccess();

}