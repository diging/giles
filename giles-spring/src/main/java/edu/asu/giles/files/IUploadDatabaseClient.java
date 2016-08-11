package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IUpload;

public interface IUploadDatabaseClient extends IDatabaseClient {
    
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;

    public abstract IUpload store(IUpload upload);

    public abstract IUpload getUpload(String id);

    public abstract List<IUpload> getUploadsForUser(String username);

    public abstract List<IUpload> getUploadsForUser(String username, int page,
            int pageSize, String sortBy, int sortDirection);

}