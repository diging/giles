package edu.asu.giles.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.aspects.access.FileAccessCheck;
import edu.asu.giles.core.IFile;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.util.DigilibConnector;

@Controller
public class ViewImageController {

    private Logger logger = LoggerFactory.getLogger(ViewImageController.class);

    @Autowired
    private DigilibConnector digilibConnector;

    @Autowired
    private IFilesManager filesManager;

    @FileAccessCheck
    @RequestMapping(value = "/files/{fileId}/img")
    public ResponseEntity<String> viewImage(
            @PathVariable("fileId") String fileId,
            HttpServletResponse response, HttpServletRequest request) {

        Map<String, String[]> parameters = request.getParameterMap();
        // remove accessToken since Github doesn't care about

        StringBuffer parameterBuffer = new StringBuffer();
        for (String key : parameters.keySet()) {
            if (key.equals("accessToken")) {
                continue;
            }
            for (String value : parameters.get(key)) {
                parameterBuffer.append(key);
                parameterBuffer.append("=");
                parameterBuffer.append(value);
                parameterBuffer.append("&");
            }
        }

        IFile file = filesManager.getFile(fileId);
        parameterBuffer.append("fn=");
        parameterBuffer.append(filesManager.getRelativePathOfFile(file));

        try {
            digilibConnector.getDigilibImage(parameterBuffer.toString(),
                    response.getOutputStream());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @FileAccessCheck
    @RequestMapping(value = "/files/{fileId}")
    public String showImagePage(Model model,
            @PathVariable("fileId") String fileId) {
        IFile file = filesManager.getFile(fileId);
        model.addAttribute("file", file);

        return "files/file";
    }
}