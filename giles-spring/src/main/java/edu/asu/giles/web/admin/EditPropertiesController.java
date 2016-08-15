package edu.asu.giles.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.rendering.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.giles.exceptions.GilesPropertiesStorageException;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.validators.SystemConfigValidator;
import edu.asu.giles.web.admin.pages.SystemConfigPage;

@Controller
public class EditPropertiesController {
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder, WebDataBinder validateBinder) {
        validateBinder.addValidators(new SystemConfigValidator());
    }

    @RequestMapping(value = "/admin/system/config", method = RequestMethod.GET)
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
        page.setJarsUrl(propertyManager.getProperty(IPropertiesManager.JARS_URL));
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
    
    @RequestMapping(value = "/admin/system/config", method = RequestMethod.POST)
    public String storeSystemConfig(@Validated @ModelAttribute SystemConfigPage systemConfigPage, BindingResult results, Model model, RedirectAttributes redirectAttrs) {
        model.addAttribute("systemConfigPage", systemConfigPage);
        
        if (results.hasErrors()) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "System Configuration could not be saved. Please check the error messages below.");
            return "admin/system/config";
        }
        
        Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put(IPropertiesManager.GITHUB_CLIENT_ID, systemConfigPage.getGithubClientId());
        propertiesMap.put(IPropertiesManager.GITHUB_SECRET, systemConfigPage.getGithubSecret());
        propertiesMap.put(IPropertiesManager.DIGILIB_SCALER_URL, systemConfigPage.getDigilibScalerUrl());
        propertiesMap.put(IPropertiesManager.GILES_URL, systemConfigPage.getGilesUrl());
        propertiesMap.put(IPropertiesManager.PDF_TO_IMAGE_DPI, systemConfigPage.getPdfToImageDpi());
        propertiesMap.put(IPropertiesManager.PDF_TO_IMAGE_TYPE, systemConfigPage.getPdfToImageType());
        propertiesMap.put(IPropertiesManager.PDF_EXTRACT_TEXT, new Boolean(systemConfigPage.isPdfExtractText()).toString()); 
        propertiesMap.put(IPropertiesManager.TESSERACT_BIN_FOLDER, systemConfigPage.getTesseractBinFolder());
        propertiesMap.put(IPropertiesManager.TESSERACT_DATA_FOLDER, systemConfigPage.getTesseractDataFolder());
        propertiesMap.put(IPropertiesManager.OCR_IMAGES_FROM_PDFS, new Boolean(systemConfigPage.isOcrImagesFromPdfs()).toString());
        propertiesMap.put(IPropertiesManager.JARS_URL, systemConfigPage.getJarsUrl());
        propertiesMap.put(IPropertiesManager.JARS_FILE_URL, systemConfigPage.getJarsFileUrl());
        
        try {
            propertyManager.updateProperties(propertiesMap);
        } catch (GilesPropertiesStorageException e) {
            model.addAttribute("show_alert", true);
            model.addAttribute("alert_type", "danger");
            model.addAttribute("alert_msg", "An unexpected error occurred. System Configuration could not be saved.");
            return "admin/system/config";
        }
        
        redirectAttrs.addFlashAttribute("show_alert", true);
        redirectAttrs.addFlashAttribute("alert_type", "success");
        redirectAttrs.addFlashAttribute("alert_msg", "System Configuration was successfully saved.");
        
        return "redirect:/admin/system/config";
    }
}
