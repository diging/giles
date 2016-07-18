package edu.asu.giles.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestRestController {
	
	
	@RequestMapping(value = "/rest/test", method = RequestMethod.GET)
	public ResponseEntity<String> test(@RequestParam String accessToken) {
		GitHubTemplate template = new GitHubTemplate(accessToken);
		if (!template.isAuthorized()) {
			return new ResponseEntity<>("{ 'status': 'token not valid' }", HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>("{ 'status': 'success' }", HttpStatus.OK);
	}
}
