package edu.asu.giles.core.impl;

import java.time.OffsetDateTime;
import java.util.List;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;

public class Document implements IDocument {

	private String id;
	private String documentId;
	private String uploadId;
	private String createdDate;
	private List<String> fileIds;
	private DocumentAccess access;
	private transient List<IFile> files;
	
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#getUploadId()
	 */
	@Override
	public String getUploadId() {
		return uploadId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#setUploadId(java.lang.String)
	 */
	@Override
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#getCreatedDate()
	 */
	@Override
	public String getCreatedDate() {
		return createdDate;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#setCreatedDate(java.util.Date)
	 */
	@Override
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#getFileIds()
	 */
	@Override
	public List<String> getFileIds() {
		return fileIds;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#setFileIds(java.util.List)
	 */
	@Override
	public void setFileIds(List<String> fileIds) {
		this.fileIds = fileIds;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#getAccess()
	 */
	@Override
	public DocumentAccess getAccess() {
		return access;
	}
	/* (non-Javadoc)
	 * @see edu.asu.giles.core.impl.IDocument#setAccess(edu.asu.giles.core.impl.DocumentAccess)
	 */
	@Override
	public void setAccess(DocumentAccess access) {
		this.access = access;
	}
	@Override
	public String getDocumentId() {
		return documentId;
	}
	@Override
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	@Override
	public List<IFile> getFiles() {
		return files;
	}
	@Override
	public void setFiles(List<IFile> files) {
		this.files = files;
	}
}
