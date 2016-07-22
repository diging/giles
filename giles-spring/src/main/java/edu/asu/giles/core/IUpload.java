package edu.asu.giles.core;

import java.time.OffsetDateTime;

public interface IUpload {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract OffsetDateTime getCreatedDate();

	public abstract void setCreatedDate(OffsetDateTime createdDate);

}