package edu.asu.giles.rest;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PropertySource("classpath:/config.properties")
@Controller
public class DigilibPassthroughController {
	
	private static Logger logger = LoggerFactory.getLogger(DigilibPassthroughController.class);
	
	@Value("${digilib_scaler_url}")
	private String digilibUrl;

	@RequestMapping(value = "/rest/digilib")
	public ResponseEntity<String> passthroughToDigilib(HttpServletRequest request, HttpServletResponse response, @RequestParam String accessToken) {
		GitHubTemplate template = new GitHubTemplate(accessToken);
		if (!template.isAuthorized()) {
			return new ResponseEntity<>("{ 'error': 'Github token not valid' }", HttpStatus.FORBIDDEN);
		}
		
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
		
		URL url;
		try {
			url = new URL(digilibUrl + "?" + parameterBuffer.toString());
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// optional default is GET
		try {
			con.setRequestMethod("GET");
		} catch (ProtocolException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		OutputStream output;
		try {
			output = response.getOutputStream();
			InputStream input = con.getInputStream();
			
			byte[] buffer = new byte[4096];
			int n = - 1;

			while ( (n = input.read(buffer)) != -1) 
			{
			    output.write(buffer, 0, n);
			}
			input.close();
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
}
