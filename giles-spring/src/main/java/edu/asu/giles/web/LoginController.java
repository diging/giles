package edu.asu.giles.web;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping(value ="/")
	public String login(Principal principal) {
		return "login";
	}
}
