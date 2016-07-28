package edu.asu.giles.service.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
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

@PropertySource("classpath:/config.properties")
@Service
public class PdfFileHandler implements IFileTypeHandler {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${pdf_to_image_dpi}")
    private String dpi;
    
    @Value("${pdf_to_image_type}")
    private String type;
    
    @Value("${pdf_to_image_format}")
    private String format;

    @Autowired
    @Qualifier("fileStorageManager")
    private IFileStorageManager storageManager;
    
    @Autowired
    @Qualifier("pdfStorageManager")
    private IFileStorageManager pdfStorageManager;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(MediaType.APPLICATION_PDF_VALUE);
        return types;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document, IUpload upload, String id, byte[] content) throws GilesFileStorageException {
        PDDocument pdfDocument;
        try {
            pdfDocument = PDDocument.load(content);
        } catch (IOException e) {
            throw new GilesFileStorageException(e);
        }
        
        boolean success = true;
        int numPages = pdfDocument.getNumberOfPages();
        PDFRenderer renderer = new PDFRenderer(pdfDocument);
        String dirFolder = storageManager.getAndCreateStoragePath(username, file.getUploadId(), file.getId());
        
        for (int i = 0; i < numPages; i++) {
            try {
                BufferedImage image = renderer.renderImageWithDPI(i, Float.parseFloat(dpi), ImageType.valueOf(type));
                String fileName = file.getFilename() + "." + i + "." + format;
                String filePath = dirFolder + File.separator + fileName;
                File fileObject = new File(filePath);
                OutputStream output = new FileOutputStream(fileObject);
                success &= ImageIOUtil.writeImage(image, format, output, new Integer(dpi));
                
                IFile imageFile = file.clone();
                imageFile.setFilepath(dirFolder);
                imageFile.setFilename(fileName);
                imageFile.setId(filesDbClient.generateId());
                imageFile.setContentType("image/" + format);
                imageFile.setSize(fileObject.length());
                
                document.getFileIds().add(imageFile.getId());
                
                filesDbClient.saveFile(imageFile);                
            } catch (NumberFormatException | IOException e) {
                logger.error("Could not render image.", e);
                success = false;
            }          
        }
        
        pdfStorageManager.saveFile(file.getUsername(), file.getUploadId(), file.getId(), file.getFilename(), content);
        filesDbClient.saveFile(file);
        
        return success;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = pdfStorageManager.getFileFolderPath(file.getUsername(), file.getUploadId(), file.getId());
        return directory + File.separator + file.getFilename();
    }

}
