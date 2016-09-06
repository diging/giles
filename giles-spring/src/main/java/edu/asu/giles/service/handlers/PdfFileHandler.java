package edu.asu.giles.service.handlers;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.GilesFileStorageException;
import edu.asu.giles.files.IFileStorageManager;
import edu.asu.giles.files.IFilesDatabaseClient;
import edu.asu.giles.service.IFileTypeHandler;
import edu.asu.giles.service.ocr.IOCRService;
import edu.asu.giles.service.properties.IPropertiesManager;

@PropertySource("classpath:/config.properties")
@Service
public class PdfFileHandler extends AbstractFileHandler implements
        IFileTypeHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String TRUE = "true";

    @Autowired
    @Qualifier("fileStorageManager")
    private IFileStorageManager storageManager;

    @Autowired
    @Qualifier("pdfStorageManager")
    private IFileStorageManager pdfStorageManager;

    @Autowired
    @Qualifier("textStorageManager")
    private IFileStorageManager textStorageManager;

    @Autowired
    private IFilesDatabaseClient filesDbClient;

    @Autowired
    private IOCRService ocrService;
    
    @Autowired
    private IPropertiesManager propertyManager;

    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(MediaType.APPLICATION_PDF_VALUE);
        return types;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException {
        String dpi = propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_DPI).trim();
        String type = propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_TYPE).trim();
        String format = propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_FORMAT).trim();
        String doOcrOnImages = propertyManager.getProperty(IPropertiesManager.OCR_IMAGES_FROM_PDFS).trim();
        String extractText = propertyManager.getProperty(IPropertiesManager.PDF_EXTRACT_TEXT).trim();
        
        PDDocument pdfDocument;
        try {
            pdfDocument = PDDocument.load(content);
        } catch (IOException e) {
            throw new GilesFileStorageException(e);
        }

        boolean success = true;

        int numPages = pdfDocument.getNumberOfPages();
        PDFRenderer renderer = new PDFRenderer(pdfDocument);
        String dirFolder = storageManager.getAndCreateStoragePath(username,
                file.getUploadId(), file.getDocumentId());

        document.setPageCount(numPages);
        for (int i = 0; i < numPages; i++) {
            try {
                BufferedImage image = renderer.renderImageWithDPI(i,
                        Float.parseFloat(dpi), ImageType.valueOf(type));
                String fileName = file.getFilename() + "." + i + "." + format;
                IFile imageFile = saveImage(username, file, document,
                        dirFolder, image, fileName);
                success &= imageFile != null;

                if (imageFile != null && doOcrOnImages.equalsIgnoreCase(TRUE)) {
                    doOcr(imageFile, username, document);
                }
            } catch (NumberFormatException | IOException e) {
                logger.error("Could not render image.", e);
                success = false;
            }
        }

        if (extractText.equalsIgnoreCase(TRUE)) {
            String fileName = file.getFilename() + ".txt";
            extractText(pdfDocument, username, file, document, fileName);
        }

        try {
            pdfDocument.close();
        } catch (IOException e) {
            logger.error("Error closing document.", e);
        }

        pdfStorageManager.saveFile(file.getUsername(), file.getUploadId(),
                document.getDocumentId(), file.getFilename(), content);
        filesDbClient.saveFile(file);

        return success;
    }

    private IFile extractText(PDDocument pdfDocument, String username,
            IFile file, IDocument document, String filename) {
        String docFolder = textStorageManager.getAndCreateStoragePath(username,
                document.getUploadId(), document.getDocumentId());
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
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(pdfDocument, bfWriter);
            bfWriter.close();
            writer.close();
        } catch (IOException e) {
            logger.error("Could not extract text.", e);
            return null;
        }
        
        String contents = null;
        try {
            contents = FileUtils.readFileToString(fileObject);
        } catch (IOException e) {
            logger.error("Could not get contents.", e);
        }

        if (contents == null || contents.trim().isEmpty()) {
            fileObject.delete();
            return null;
        }

        IFile textFile = file.clone();
        textFile.setFilepath(docFolder + File.separator + filename);
        textFile.setFilename(filename);
        textFile.setId(filesDbClient.generateId());
        textFile.setContentType(MediaType.TEXT_PLAIN_VALUE);
        textFile.setDerivedFrom(file.getDerivedFrom());

        textFile.setSize(fileObject.length());
        filesDbClient.saveFile(textFile);

        document.getTextFileIds().add(textFile.getId());
        return textFile;
    }

    private IFile doOcr(IFile imageFile, String username, IDocument document) {
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

        String docFolder = textStorageManager.getAndCreateStoragePath(username,
                document.getUploadId(), document.getDocumentId());
        String filename = imageFile.getFilename() + ".txt";
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
            bfWriter.write(extractedText);
            bfWriter.close();
            writer.close();
        } catch (IOException e) {
            logger.error("Could not write text to file.", e);
            return null;
        }

        IFile textFile = imageFile.clone();
        textFile.setFilepath(docFolder + File.separator + filename);
        textFile.setFilename(filename);
        textFile.setId(filesDbClient.generateId());
        textFile.setContentType(MediaType.TEXT_PLAIN_VALUE);
        textFile.setSize(fileObject.length());
        textFile.setDerivedFrom(imageFile.getId());
        filesDbClient.saveFile(textFile);

        document.getTextFileIds().add(textFile.getId());

        return textFile;
    }

    private IFile saveImage(String username, IFile file, IDocument document,
            String dirFolder, BufferedImage image, String fileName)
            throws IOException, FileNotFoundException {
        String dpi = propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_DPI).trim();
        String type = propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_TYPE).trim();
        String format = propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_FORMAT).trim();
        
        String filePath = dirFolder + File.separator + fileName;
        File fileObject = new File(filePath);
        OutputStream output = new FileOutputStream(fileObject);
        boolean success = ImageIOUtil.writeImage(image, format, output,
                new Integer(dpi));
        if (!success) {
            return null;
        }

        IFile imageFile = file.clone();
        String docFoler = storageManager.getFileFolderPath(username,
                file.getUploadId(), file.getDocumentId());
        imageFile.setFilepath(docFoler + File.separator + fileName);
        imageFile.setFilename(fileName);
        imageFile.setId(filesDbClient.generateId());
        imageFile.setContentType("image/" + format);
        imageFile.setSize(fileObject.length());
        imageFile.setDerivedFrom(file.getId());

        document.getFileIds().add(imageFile.getId());

        filesDbClient.saveFile(imageFile);
        return imageFile;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = pdfStorageManager.getFileFolderPath(
                file.getUsername(), file.getUploadId(), file.getDocumentId());
        return directory + File.separator + file.getFilename();
    }

    @Override
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL).trim();
        String pdfEndpoint = propertyManager.getProperty(IPropertiesManager.GILES_FILE_ENDPOINT).trim();
        String contentSuffix = propertyManager.getProperty(IPropertiesManager.GILES_FILE_CONTENT_SUFFIX).trim();
        
        return gilesUrl + pdfEndpoint + file.getId() + contentSuffix;
    }

    @Override
    protected IFileStorageManager getStorageManager() {
        return pdfStorageManager;
    }

}
