package edu.asu.giles.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.giles.aspects.access.GitHubAccessCheck;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.DocumentAccess;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.users.User;
import edu.asu.giles.util.DigilibConnector;

@Controller
public class DigilibPassthroughController {

    private static Logger logger = LoggerFactory
            .getLogger(DigilibPassthroughController.class);

    @Autowired
    private IFilesManager filesManager;

    @Autowired
    private DigilibConnector digilibConnector;

    @GitHubAccessCheck
    @RequestMapping(value = "/rest/digilib")
    public ResponseEntity<String> passthroughToDigilib(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(defaultValue = "") String accessToken, User user)
            throws UnsupportedEncodingException {

        Map<String, String[]> parameters = request.getParameterMap();
        // remove accessToken since Github doesn't care about

        String fn = null;
        StringBuffer parameterBuffer = new StringBuffer();
        for (String key : parameters.keySet()) {
            if (key.equals("accessToken")) {
                continue;
            }
            for (String value : parameters.get(key)) {
                parameterBuffer.append(key);
                parameterBuffer.append("=");

                parameterBuffer.append(URLEncoder.encode(value, "UTF-8"));
                parameterBuffer.append("&");

                if (key.equals("fn")) {
                    fn = value;
                }
            }
        }

        if (fn.startsWith(File.separator)) {
            fn = fn.substring(1);
        }
        IFile file = filesManager.getFileByPath(fn);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (file.getAccess() == DocumentAccess.PRIVATE
                && !file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        MultiValueMap<String, String> headers = new HttpHeaders();
        try {
            Map<String, List<String>> digilibHeaders = digilibConnector
                    .getDigilibImage(parameterBuffer.toString(),
                            response.getOutputStream());
            for (String key : digilibHeaders.keySet()) {
                if (key != null) {
                    headers.put(key, digilibHeaders.get(key));
                }
            }
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, String> simpleMap = headers.toSingleValueMap();
        response.setContentType(simpleMap.get(HttpHeaders.CONTENT_TYPE));
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

}
