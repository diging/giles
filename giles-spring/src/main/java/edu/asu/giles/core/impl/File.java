package edu.asu.giles.core.impl;

import java.time.OffsetDateTime;

import edu.asu.giles.core.IFile;


public class File implements IFile {
	
	
	private String uploadId;
	private String filename;
	private String username;
	private String documentId;
	private String id;
	private OffsetDateTime uploadDate;
	private DocumentAccess access;
	private String contentType;
	private long size;
	private String filepath;
	
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
	public String getDocumentId() {
		if (documentId == null) {
			return id;
		}
		return documentId;
	}

	@Override
	public void setDocumentId(String zoteroDocumentId) {
		this.documentId = zoteroDocumentId;
	}

	@Override
	public OffsetDateTime getUploadDate() {
		return uploadDate;
	}

	@Override
	public void setUploadDate(OffsetDateTime uploadDate) {
		this.uploadDate = uploadDate;
	}

	@Override
	public DocumentAccess getAccess() {
		return access;
	}

	@Override
	public void setAccess(DocumentAccess access) {
		this.access = access;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String getFilepath() {
		return filepath;
	}

	@Override
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

}