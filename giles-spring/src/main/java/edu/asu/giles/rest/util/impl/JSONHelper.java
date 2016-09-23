package edu.asu.giles.rest.util.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IPage;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.rest.util.IJSONHelper;

@Component
public class JSONHelper implements IJSONHelper {

    @Autowired
    private IFilesManager filesManager;

    /* (non-Javadoc)
     * @see edu.asu.giles.rest.util.IJSONHelper#createDocumentJson(edu.asu.giles.core.IDocument, com.fasterxml.jackson.databind.ObjectMapper, com.fasterxml.jackson.databind.node.ObjectNode)
     */
    @Override
    public void createDocumentJson(IDocument doc, ObjectMapper mapper, ObjectNode docNode) {
        docNode.put("documentId", doc.getDocumentId());
        docNode.put("uploadId", doc.getUploadId());
        docNode.put("uploadedDate", doc.getCreatedDate());
        docNode.put("access", (doc.getAccess() != null ? doc.getAccess()
                .toString() : DocumentAccess.PRIVATE.toString()));
        IFile uploadedFile = filesManager.getFile(doc.getUploadedFileId());
        
        if (uploadedFile != null) {
            ObjectNode uploadedFileNode = createFileJsonObject(mapper, uploadedFile);
            docNode.set("uploadedFile", uploadedFileNode);
        }
        
        if (doc.getExtractedTextFileId() != null) {
            IFile extractedTextFile = filesManager.getFile(doc.getExtractedTextFileId());
            docNode.set("extractedText", createFileJsonObject(mapper, extractedTextFile));
        }
        
        if (!doc.getPages().isEmpty()) {
            ArrayNode pagesArray = docNode.putArray("pages");
            for (IPage page : doc.getPages()) {
                ObjectNode pageNode = pagesArray.addObject();
                pageNode.put("nr", page.getPageNr());
                if (page.getImageFileId() != null) {
                    IFile imageFile = filesManager.getFile(page.getImageFileId());
                    pageNode.set("image", createFileJsonObject(mapper, imageFile));
                }
                if (page.getTextFileId() != null) {
                    IFile textFile = filesManager.getFile(page.getTextFileId());
                    pageNode.set("text", createFileJsonObject(mapper, textFile));
                }
            }
        }
    }

    private ObjectNode createFileJsonObject(ObjectMapper mapper, IFile file) {
        ObjectNode fileNode = mapper.createObjectNode();
        fileNode.put("filename", file.getFilename());
        fileNode.put("id", file.getId());
        fileNode.put("url", filesManager.getFileUrl(file));
        fileNode.put("path", filesManager.getRelativePathOfFile(file));
        fileNode.put("content-type", file.getContentType());
        fileNode.put("size", file.getSize());
        return fileNode;
    }

}
