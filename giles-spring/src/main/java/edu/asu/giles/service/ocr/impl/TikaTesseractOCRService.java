package edu.asu.giles.service.ocr.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import edu.asu.giles.service.ocr.IOCRService;
import edu.asu.giles.service.properties.IPropertiesManager;

@Service
public class TikaTesseractOCRService implements IOCRService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private ParseContext parseContext;
    
    private TesseractOCRParser ocrParser;
    
    @Autowired
    private IPropertiesManager propertyManager;
     
    /* (non-Javadoc)
     * @see edu.asu.giles.service.ocr.impl.IOCRService#ocrImage(java.lang.String)
     */
    @Override
    @Async("tesseractExecutor")
    public Future<String> ocrImage(String imageFile) {
        logger.info("(" + Thread.currentThread().getId() + ") OCR using Tesseract on: " + imageFile);
       
        String tesseractBin = propertyManager.getProperty(IPropertiesManager.TESSERACT_BIN_FOLDER);
        String tesseractData = propertyManager.getProperty(IPropertiesManager.TESSERACT_DATA_FOLDER);
        boolean createHocr = propertyManager.getProperty(IPropertiesManager.TESSERACT_CREATE_HOCR).equals("true");
        
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setTesseractPath(tesseractBin);
        config.setTessdataPath(tesseractData);
        parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        ocrParser = new TesseractOCRParser(createHocr);
        
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        
        try (InputStream stream = new FileInputStream(new File(imageFile))) {
            ocrParser.parse(stream, handler, metadata, parseContext);
            return new AsyncResult<String>(handler.toString());
        } catch (SAXException | TikaException | IOException e) {
            logger.error("Error during ocr.", e);
            return new AsyncResult<String>(null);
        }
    }
}
