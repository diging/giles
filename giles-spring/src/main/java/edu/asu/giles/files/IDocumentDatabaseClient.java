package edu.asu.giles.files;

import java.util.List;

import edu.asu.giles.core.IDocument;

public interface IDocumentDatabaseClient extends IDatabaseClient {

    public abstract IDocument saveDocument(IDocument document);

    public abstract IDocument getDocumentById(String id);

    public abstract List<IDocument> getDocumentByUploadId(String uploadId);

    public abstract List<IDocument> getDocumentByExample(IDocument doc);

}