package edu.asu.giles.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.GilesGrantedAuthority;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public final class SimpleSignInAdapter implements SignInAdapter {

    private IUserManager userManager;

    public SimpleSignInAdapter(IUserManager userManager) {
        this.userManager = userManager;
    }

    public String signIn(String userId, Connection<?> connection,
            NativeWebRequest request) {
        UserProfile profile = connection.fetchUserProfile();
        List<GrantedAuthority> authorities = new ArrayList<>();

        User user = userManager.findUserByProviderUserId(connection.getKey().getProviderUserId(), connection.getKey().getProviderId());
        
        if (user == null) {
            authorities.add(new GilesGrantedAuthority(
                    GilesGrantedAuthority.ROLE_USER));
            user = new User();
            String username = profile.getUsername() + "_" + connection.getKey().getProviderId();
            
            // make sure someone else didn't change their username to this one
            User userWithUsername = userManager.findUser(username);
            if (userWithUsername == null) {
                user.setUsername(username);
            } else {
                user.setUsername(UUID.randomUUID().toString());
            }
            
            user.setFirstname(profile.getFirstName());
            user.setLastname(profile.getLastName());
            user.setEmail(profile.getEmail());
            user.setProvider(connection.getKey().getProviderId());
            user.setUserIdOfProvider(connection.getKey().getProviderUserId());
            user.setAccountStatus(AccountStatus.ADDED);

            userManager.addUser(user);
        } else {
            List<String> roles = user.getRoles();
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new GilesGrantedAuthority(role));
                }
            }
        }
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(user, null,
                                authorities));
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(
                request.getNativeRequest(HttpServletRequest.class),
                request.getNativeResponse(HttpServletResponse.class));

        if (savedRequest != null) {
            return savedRequest.getRedirectUrl();
        }
        return null;
    }

}