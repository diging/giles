package edu.asu.giles.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.giles.core.IUpload;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.users.User;

@Controller
public class LoginController {
	
	@Autowired
	private IFilesManager filesManager;

	@RequestMapping(value ="/")
	public String login(Principal principal, Model model) {
		
		String username = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			username = ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUsername();
		}
		
		if (username != null) {
			List<IUpload> uploads = filesManager.getUploadsOfUser(username);
			model.addAttribute("uploads", uploads);
		}
		
		
		return "login";
	}
	
}
