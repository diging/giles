package edu.asu.giles.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import edu.asu.giles.users.GilesGrantedAuthority;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public class GilesConnectionSignUp implements ConnectionSignUp {

    private IUserManager userManager;

    public GilesConnectionSignUp(IUserManager userManager) {
        this.userManager = userManager;
    }
 
    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GilesGrantedAuthority(
                GilesGrantedAuthority.ROLE_USER));

        User user = new User();
        user.setUsername(profile.getUsername());
        user.setFirstname(profile.getFirstName());
        user.setLastname(profile.getLastName());
        user.setEmail(profile.getEmail());
        user.setProvider(connection.getKey().getProviderId());
        user.setUserIdOfProvider(connection.getKey().getProviderUserId());

        List<String> roles = new ArrayList<>();
        roles.add(GilesGrantedAuthority.ROLE_USER);
        user.setRoles(roles);

        userManager.addUser(user);
        return user.getUsername();
    }

}