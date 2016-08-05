package edu.asu.giles.config;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public class GilesConnectionSignUp implements ConnectionSignUp {

    private IUserManager userManager;

    public GilesConnectionSignUp(IUserManager userManager) {
        this.userManager = userManager;
    }
 
    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        
        User user = new User();
        user.setUsername(profile.getUsername());
        user.setFirstname(profile.getFirstName());
        user.setLastname(profile.getLastName());
        user.setEmail(profile.getEmail());
        user.setProvider(connection.getKey().getProviderId());
        user.setUserIdOfProvider(connection.getKey().getProviderUserId());
        user.setAccountStatus(AccountStatus.ADDED);
        
        userManager.addUser(user);
        return user.getUsername();
    }

}