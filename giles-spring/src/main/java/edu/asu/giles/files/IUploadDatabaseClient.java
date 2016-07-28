package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IUpload;

public interface IUploadDatabaseClient extends IDatabaseClient {

    public abstract IUpload store(IUpload upload);

    public abstract IUpload getUpload(String id);

    public abstract List<IUpload> getUploadsForUser(String username);

}