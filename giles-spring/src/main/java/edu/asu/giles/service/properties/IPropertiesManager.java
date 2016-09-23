package edu.asu.giles.service.properties;

import java.util.Map;

import edu.asu.giles.exceptions.GilesPropertiesStorageException;

public interface IPropertiesManager {
    
    public final static String GITHUB_CLIENT_ID = "github_client_id";
    public final static String GITHUB_SECRET = "github_secret";
    public final static String DIGILIB_SCALER_URL = "digilib_scaler_url";
    public final static String GILES_URL = "giles_url";
    public final static String PDF_TO_IMAGE_DPI = "pdf_to_image_dpi";
    public final static String PDF_TO_IMAGE_TYPE = "pdf_to_image_type";
    public final static String PDF_EXTRACT_TEXT = "pdf_extract_text";
    public final static String PDF_TO_IMAGE_FORMAT = "pdf_to_image_format";
    public final static String JARS_URL = "jars_url";
    public final static String JARS_FILE_URL = "jars_file_url";
    public final static String TESSERACT_BIN_FOLDER = "tesseract_bin_folder";
    public final static String TESSERACT_DATA_FOLDER = "tesseract_data_folder";
    public final static String DEFAULT_PAGE_SIZE = "default_page_size";
    public final static String OCR_IMAGES_FROM_PDFS = "ocr_images_from_pdfs";
    public final static String GILES_DIGILIB_ENDPOINT = "giles_digilib_endpoint";
    public final static String GILES_FILE_ENDPOINT = "giles_file_endpoint";
    public final static String GILES_FILE_CONTENT_SUFFIX = "giles_file_content_suffix";
    public final static String METADATA_SERVICE_DOC_ENDPOINT = "metadata_service_document_url";
    
    public final static String ALLOW_IFRAMING_FROM = "allow_iframing_from";
 

    public abstract void setProperty(String key, String value) throws GilesPropertiesStorageException;

    public abstract String getProperty(String key);

    public abstract void updateProperties(Map<String, String> props)
            throws GilesPropertiesStorageException;

}