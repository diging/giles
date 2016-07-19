package edu.asu.giles.core;

import java.util.Date;

public interface IFile {
	
	public final static String PUBLIC = "PUBLIC";
	public final static String PRIVATE = "PRIVATE";
	public final static String AUTHORIZED = "AUTHORIZED";


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

	public abstract void setUploadDate(Date uploadDate);

	public abstract Date getUploadDate();

	public abstract void setAccess(String access);

	public abstract String getAccess();

}