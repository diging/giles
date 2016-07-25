package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IUpload;
import edu.asu.giles.core.impl.Upload;

public interface IUploadDatabaseClient {

    public abstract IUpload store(IUpload upload);

    public abstract IUpload getUpload(String id);

    public abstract List<IUpload> getUploadsForUser(String username);

}