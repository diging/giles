package edu.asu.giles.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.File;
import edu.asu.giles.db4o.DatabaseManager;
import edu.asu.giles.files.IFilesDatabaseClient;

@Component
public class FilesDatabaseClient extends DatabaseClient implements
        IFilesDatabaseClient {

    private ObjectContainer client;

    @Autowired
    @Qualifier("filesDatabaseManager")
    private DatabaseManager userDatabase;

    @PostConstruct
    public void init() {
        client = userDatabase.getClient();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.IFilesDatabaseClient#addFile(edu.asu.giles.core.impl
     * .File)
     */
    @Override
    public IFile saveFile(IFile file) {
        client.store(file);
        client.commit();
        return file;
    }

    @Override
    public IFile getFileById(String id) {
        IFile file = new File();
        file.setId(id);
        return queryByExample(file);
    }

    @Override
    public List<IFile> getFileByUploadId(String uploadId) {
        IFile file = new File();
        file.setUploadId(uploadId);
        return getFilesByExample(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.IFilesDatabaseClient#getFile(java.lang.String)
     */
    @Override
    public IFile getFile(String filename) {
        IFile file = new File(filename);
        return queryByExample(file);
    }

    private IFile queryByExample(IFile file) {
        ObjectSet<File> files = client.queryByExample(file);
        if (files != null && files.size() > 0) {
            return files.get(0);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.IFilesDatabaseClient#getFilesByExample(edu.asu.giles
     * .core.impl.File)
     */
    @Override
    public List<IFile> getFilesByExample(IFile file) {
        ObjectSet<File> files = client.queryByExample(file);
        List<IFile> results = new ArrayList<IFile>();
        for (IFile f : files) {
            results.add(f);
        }
        return results;
    }

    @Override
    protected String getIdPrefix() {
        return "FILE";
    }

    @Override
    protected Object getById(String id) {
        return getFileById(id);
    }

}
