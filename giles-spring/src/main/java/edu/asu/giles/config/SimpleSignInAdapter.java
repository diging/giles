package edu.asu.giles.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import edu.asu.giles.users.GilesGrantedAuthority;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public final class SimpleSignInAdapter implements SignInAdapter {

	private IUserManager userManager;
	
	public SimpleSignInAdapter(IUserManager userManager) {
		this.userManager = userManager;
	}
	
	public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
		UserProfile profile = connection.fetchUserProfile();
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		User user = userManager.findUser(profile.getUsername());
		if (user == null) {
			authorities.add(new GilesGrantedAuthority(GilesGrantedAuthority.ROLE_USER));
	        user = new User();
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
		} else {
			List<String> roles = user.getRoles();
			for (String role : roles) {
				authorities.add(new GilesGrantedAuthority(role));
			}
		}
		SecurityContextHolder.getContext().setAuthentication(
	            new UsernamePasswordAuthenticationToken(user, null, authorities));
		return null;		
	}

}