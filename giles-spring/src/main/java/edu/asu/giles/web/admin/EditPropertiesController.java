package edu.asu.giles.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.rendering.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.web.admin.pages.SystemConfigPage;

@Controller
public class EditPropertiesController {
    
    @Autowired
    private IPropertiesManager propertyManager;

    @RequestMapping(value = "/admin/system/config")
    public String getConfigPage(Model model) {
        SystemConfigPage page = new SystemConfigPage();
        
        String clientId = propertyManager.getProperty(IPropertiesManager.GITHUB_CLIENT_ID);
        if (clientId != null && clientId.length() > 2) {
            clientId = clientId.substring(0,2) + (new String(new char[clientId.length()-2])).replace("\0", "*");
        }
        page.setGithubClientId(clientId);
        
        String githubSecret = propertyManager.getProperty(IPropertiesManager.GITHUB_SECRET);
        if (githubSecret != null && githubSecret.length() > 2) {
            githubSecret = githubSecret.substring(0,2) + new String(new char[githubSecret.length()-2]).replace("\0", "*");
        }
        page.setGithubSecret(githubSecret);
        
        page.setDigilibScalerUrl(propertyManager.getProperty(IPropertiesManager.DIGILIB_SCALER_URL));
        page.setGilesUrl(propertyManager.getProperty(IPropertiesManager.GILES_URL));
        page.setJarsFileUrl(propertyManager.getProperty(IPropertiesManager.JARS_FILE_URL));
        page.setJarsUrl(propertyManager.getProperty(IPropertiesManager.JAR_URL));
        String ocrImagesFromPdf = propertyManager.getProperty(IPropertiesManager.OCR_IMAGES_FROM_PDFS);
        if (ocrImagesFromPdf != null) {
            page.setOcrImagesFromPdfs(ocrImagesFromPdf.equals("true"));
        } else {
            page.setOcrImagesFromPdfs(false);
        }
        String extractText = propertyManager.getProperty(IPropertiesManager.PDF_EXTRACT_TEXT);
        if (extractText != null) {
            page.setPdfExtractText(extractText.equals("true"));
        } else {
            page.setPdfExtractText(false);
        }
        
        page.setPdfToImageDpi(propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_DPI));
        page.setPdfToImageType(propertyManager.getProperty(IPropertiesManager.PDF_TO_IMAGE_TYPE));
        page.setTesseractBinFolder(propertyManager.getProperty(IPropertiesManager.TESSERACT_BIN_FOLDER));
        page.setTesseractDataFolder(propertyManager.getProperty(IPropertiesManager.TESSERACT_DATA_FOLDER));    
        
        List<String> imageTypes = new ArrayList<String>();
        imageTypes.add(ImageType.ARGB.toString());
        imageTypes.add(ImageType.BINARY.toString());
        imageTypes.add(ImageType.GRAY.toString());
        imageTypes.add(ImageType.RGB.toString());
        model.addAttribute("imageTypeOptions", imageTypes);
        model.addAttribute("systemConfigPage", page);
        return "admin/system/config";
    }
}
