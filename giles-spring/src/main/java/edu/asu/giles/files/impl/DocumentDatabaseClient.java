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
import edu.asu.giles.db4o.DatabaseManager;
import edu.asu.giles.files.IDocumentDatabaseClient;

@Component
public class DocumentDatabaseClient extends DatabaseClient implements
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
    public IDocument saveDocument(IDocument document) {
        client.store(document);
        client.commit();
        return document;
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
        return queryByExample(doc);
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

    private IDocument queryByExample(IDocument doc) {
        ObjectSet<IDocument> docs = client.queryByExample(doc);
        if (docs != null && docs.size() > 0) {
            return docs.get(0);
        }
        return null;
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
