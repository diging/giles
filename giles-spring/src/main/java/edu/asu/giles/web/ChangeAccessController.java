package edu.asu.giles.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.giles.aspects.access.AccountCheck;
import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.files.IFilesManager;

@Controller
public class ChangeAccessController {

    @Autowired
    private IFilesManager filesManager;

    @AccountCheck
    @RequestMapping(value = "/documents/{documentId}/access/change", method = RequestMethod.POST)
    public String changeAccess(@PathVariable("documentId") String documentId,
            @RequestParam("access") String access,
            @RequestParam("uploadId") String uploadId,
            RedirectAttributes redirectAttrs) {

        if (documentId == null || documentId.isEmpty() || access == null
                || access.isEmpty()) {
            // soemthing is wrong here
            // let's just silently ignore it...
            return "redirect:/files/upload";
        }

        IDocument document = filesManager.getDocument(documentId);
        if (document == null) {
            // and again, something weird going on
            // let's ignore it
            return "redirect:/files/upload";
        }

        DocumentAccess docAccess = DocumentAccess.valueOf(access);
        if (docAccess == null) {
            // and again, something weird going on
            // let's ignore it
            return "redirect:/files/upload";
        }

        document.setAccess(docAccess);
        filesManager.saveDocument(document);

        List<IFile> files = filesManager.getFilesOfDocument(document);
        for (IFile file : files) {
            file.setAccess(docAccess);
            filesManager.saveFile(file);
        }
        files = filesManager.getTextFilesOfDocument(document);
        for (IFile file : files) {
            file.setAccess(docAccess);
            filesManager.saveFile(file);
        }

        redirectAttrs.addAttribute("show_alert", true);
        redirectAttrs.addAttribute("alert_type", "success");
        redirectAttrs.addAttribute("alert_msg",
                "Access type successfully updated.");

        return "redirect:/uploads/" + uploadId;
    }
}
