package edu.asu.giles.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.core.IDocument;

public interface IJSONHelper {

    public abstract void createDocumentJson(IDocument doc, ObjectMapper mapper,
            ObjectNode docNode);

}