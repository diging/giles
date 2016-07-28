package edu.asu.giles.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.core.impl.Upload;
import edu.asu.giles.db4o.DatabaseManager;
import edu.asu.giles.files.IUploadDatabaseClient;

@Service
public class UploadDatabaseClient extends DatabaseClient implements IUploadDatabaseClient {

	private ObjectContainer client;

	@Autowired
	@Qualifier("uploadDatabaseManager")
	private DatabaseManager userDatabase;

	@PostConstruct
	public void init() {
		client = userDatabase.getClient();
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.giles.files.impl.IUploadDatabaseClient#store(edu.asu.giles.core.impl.Upload)
	 */
	@Override
	public IUpload store(IUpload upload) {
		client.store(upload);
		client.commit();
		return upload;
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.giles.files.impl.IUploadDatabaseClient#getUpload(java.lang.String)
	 */
	@Override
	public IUpload getUpload(String id) {
		IUpload upload = new Upload(id);
		return queryByExample(upload);
	}
	
	/* (non-Javadoc)
	 * @see edu.asu.giles.files.impl.IUploadDatabaseClient#getUploadsForUser(java.lang.String)
	 */
	@Override
	public List<IUpload> getUploadsForUser(String username) {
		IUpload upload = new Upload();
		upload.setUsername(username);
		return getFilesByExample(upload);
	}
	
	private IUpload queryByExample(IUpload upload) {
		ObjectSet<Upload> uploads = client.queryByExample(upload);
		if (uploads != null && uploads.size() > 0) {
			return uploads.get(0);
		}
		return null;
	}
	
	private List<IUpload> getFilesByExample(IUpload upload) {
		ObjectSet<Upload> uploads = client.queryByExample(upload);
		List<IUpload> results = new ArrayList<IUpload>();
		for (IUpload u : uploads) {
			results.add(u);
		}
		return results;
	}

    @Override
    protected String getIdPrefix() {
        return "UP";
    }

    @Override
    protected Object getById(String id) {
        return getUpload(id);
    }
	
}
