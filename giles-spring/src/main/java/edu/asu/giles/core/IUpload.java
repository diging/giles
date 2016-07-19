package edu.asu.giles.core;

import java.util.Date;

public interface IUpload {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract Date getCreatedDate();

	public abstract void setCreatedDate(Date createdDate);

}