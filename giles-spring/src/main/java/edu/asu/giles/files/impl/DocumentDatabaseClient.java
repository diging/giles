package edu.asu.giles.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.impl.Document;
import edu.asu.giles.db4o.impl.DatabaseClient;
import edu.asu.giles.db4o.impl.DatabaseManager;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IDocumentDatabaseClient;

@Component
public class DocumentDatabaseClient extends DatabaseClient<IDocument> implements
        IDocumentDatabaseClient {

    private ObjectContainer client;

    @Autowired
    @Qualifier("documentDatabaseManager")
    private DatabaseManager userDatabase;

    @PostConstruct
    public void init() {
        client = userDatabase.getClient();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#addFile(edu.asu.giles
     * .core.IDocument)
     */
    @Override
    public IDocument saveDocument(IDocument document) throws UnstorableObjectException {
        return store(document);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#getFileById(java.lang
     * .String)
     */
    @Override
    public IDocument getDocumentById(String id) {
        IDocument doc = new Document();
        doc.setId(id);
        return queryByExampleGetFirst(doc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#getFileByUploadId(java
     * .lang.String)
     */
    @Override
    public List<IDocument> getDocumentByUploadId(String uploadId) {
        IDocument doc = new Document();
        doc.setUploadId(uploadId);
        return getDocumentByExample(doc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#getFilesByExample(edu
     * .asu.giles.core.IDocument)
     */
    @Override
    public List<IDocument> getDocumentByExample(IDocument doc) {
        ObjectSet<IDocument> docs = client.queryByExample(doc);
        List<IDocument> results = new ArrayList<IDocument>();
        for (IDocument f : docs) {
            results.add(f);
        }
        return results;
    }

    protected ObjectContainer getClient() {
        return client;
    }

    @Override
    protected String getIdPrefix() {
        return "DOC";
    }

    @Override
    protected Object getById(String id) {
        return getDocumentById(id);
    }
}
