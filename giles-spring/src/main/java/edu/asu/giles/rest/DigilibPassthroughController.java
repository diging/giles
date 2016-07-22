package edu.asu.giles.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.giles.aspects.access.GitHubAccessCheck;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.DocumentAccess;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.users.User;
import edu.asu.giles.util.DigilibConnector;

@PropertySource("classpath:/config.properties")
@Controller
public class DigilibPassthroughController {
	
	private static Logger logger = LoggerFactory.getLogger(DigilibPassthroughController.class);
	
	@Autowired
	private IFilesManager filesManager;
	
	@Autowired
	private DigilibConnector digilibConnector;

	@GitHubAccessCheck
	@RequestMapping(value = "/rest/digilib")
	public ResponseEntity<String> passthroughToDigilib(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "") String accessToken, User user) {
		
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
				parameterBuffer.append(value);
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
		
		if (file.getAccess() == DocumentAccess.PRIVATE && !file.getUsername().equals(user.getUsername())) {
			return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
		}
		
		try {
			digilibConnector.getDigilibImage(parameterBuffer.toString(), response.getOutputStream());
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
}
