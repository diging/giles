package edu.asu.giles.web.users;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.giles.users.IUserManager;

@Controller
public class ApproveAccountController {
    
    @Autowired
    private IUserManager userManager;

    @RequestMapping(value = "/users/user/{username}/approve", method = RequestMethod.POST)
    public String approveUser(@PathVariable String username, Model model, Principal principal) {
        userManager.approveUser(username);
        return "redirect:/users";
    }
}
