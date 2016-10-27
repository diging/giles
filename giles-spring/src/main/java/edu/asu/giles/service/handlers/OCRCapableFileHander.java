package edu.asu.giles.service.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IPage;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.service.ocr.IOCRService;
import edu.asu.giles.service.properties.IPropertiesManager;

public abstract class OCRCapableFileHander extends AbstractFileHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final String TRUE = "true";
    
    @Autowired
    protected IPropertiesManager propertyManager;
    
    @Autowired
    @Qualifier("fileStorageManager") IFileStorageManager storageManager;
    
    @Autowired
    @Qualifier("textStorageManager")
    private IFileStorageManager textStorageManager;

    @Autowired
    private IFilesDatabaseClient filesDbClient;

    @Autowired IOCRService ocrService;
    

    public OCRCapableFileHander() {
        super();
    }

    protected IFile doOcr(IPage page, IFile imageFile, String username, IDocument document) {
        String imageFolderPath = storageManager.getAndCreateStoragePath(
                username, imageFile.getUploadId(), imageFile.getDocumentId());
        Future<String> ocrResult = ocrService.ocrImage(imageFolderPath
                + File.separator + imageFile.getFilename());
    
        String extractedText = null;
        while (true) {
            if (ocrResult.isDone()) {
                try {
                    extractedText = ocrResult.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Exception getting result.", e);
                }
                break;
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("Thread couldn't sleep.", e);
                }
            }
        }
    
        if (extractedText == null || extractedText.isEmpty()) {
            return null;
        }
        
        boolean createHOCR = propertyManager.getProperty(IPropertiesManager.TESSERACT_CREATE_HOCR).equalsIgnoreCase("true");
    
        String fileExtension = ".txt";
        if (createHOCR) {
            fileExtension = ".hocr";
        }
        IFile textFile = saveTextToFile(imageFile, -1, username, document, extractedText, fileExtension);
    
        if (textFile != null) {
            document.getTextFileIds().add(textFile.getId());
            if (page != null) {
                page.setTextFileId(textFile.getId());
            }
        }
    
        return textFile;
    }
    
    protected IFile saveTextToFile(IFile mainFile, int pageNr, String username,
            IDocument document, String pageText, String fileExtentions) {
        String docFolder = textStorageManager.getAndCreateStoragePath(username,
                document.getUploadId(), document.getDocumentId());
        
        String filename = mainFile.getFilename();
        if (pageNr > -1) {
            filename = filename +  "." + pageNr;
        }
        
        if (!fileExtentions.startsWith(".")) {
            fileExtentions = "." + fileExtentions;
        }
        filename = filename + fileExtentions;
        
        String filePath = docFolder + File.separator + filename;
        File fileObject = new File(filePath);
        try {
            fileObject.createNewFile();
        } catch (IOException e) {
            logger.error("Could not create file.", e);
            return null;
        }

        try {
            FileWriter writer = new FileWriter(fileObject);
            BufferedWriter bfWriter = new BufferedWriter(writer);
            bfWriter.write(pageText);
            bfWriter.close();
            writer.close();
        } catch (IOException e) {
            logger.error("Could not write text to file.", e);
            return null;
        }

        IFile textFile = mainFile.clone();
        textFile.setFilepath(docFolder + File.separator + filename);
        textFile.setFilename(filename);
        textFile.setId(filesDbClient.generateId());
        textFile.setContentType(MediaType.TEXT_PLAIN_VALUE);
        textFile.setSize(fileObject.length());
        textFile.setDerivedFrom(mainFile.getId());
        try {
            filesDbClient.saveFile(textFile);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file,", e);
            return null;
        }
        return textFile;
    }

}