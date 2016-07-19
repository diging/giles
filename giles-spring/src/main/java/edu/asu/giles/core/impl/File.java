package edu.asu.giles.core.impl;

import java.util.Date;

import edu.asu.giles.core.IFile;


public class File implements IFile {
	
	
	private String uploadId;
	private String filename;
	private String username;
	private String zoteroDocumentId;
	private String id;
	private Date uploadDate;
	private String access;
	
	public File() {}
	
	public File(String filename) {
		this.filename = filename;
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#getUploadId()
	 */
	@Override
	public String getUploadId() {
		return uploadId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#setUploadId(java.lang.String)
	 */
	@Override
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#getFilename()
	 */
	@Override
	public String getFilename() {
		return filename;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#setFilename(java.lang.String)
	 */
	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#setUsername(java.lang.String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IFile#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getZoteroDocumentId() {
		return zoteroDocumentId;
	}

	@Override
	public void setZoteroDocumentId(String zoteroDocumentId) {
		this.zoteroDocumentId = zoteroDocumentId;
	}

	@Override
	public Date getUploadDate() {
		return uploadDate;
	}

	@Override
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	@Override
	public String getAccess() {
		return access;
	}

	@Override
	public void setAccess(String access) {
		this.access = access;
	}

}
