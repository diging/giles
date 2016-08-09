package edu.asu.giles.service.ocr.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@PropertySource("classpath:/config.properties")
@Service
public class TikaTesseractOCRService implements IOCRService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${tesseract_bin_foler}")
    private String tesseractBin;
    
    @Value("${tesseract_data_folder}")
    private String tesseractData;
    
    private ParseContext parseContext;
    
    private TesseractOCRParser ocrParser;
    
    
    @PostConstruct
    public void init() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setTesseractPath(tesseractBin);
        config.setTessdataPath(tesseractData);
        parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        ocrParser = new TesseractOCRParser();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.ocr.impl.IOCRService#ocrImage(java.lang.String)
     */
    @Override
    public String ocrImage(String imageFile) {
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        
        try (InputStream stream = new FileInputStream(new File(imageFile))) {
            ocrParser.parse(stream, handler, metadata, parseContext);
            return handler.toString();
        } catch (SAXException | TikaException | IOException e) {
            logger.error("Error during ocr.", e);
            return null;
        }
    }
}
