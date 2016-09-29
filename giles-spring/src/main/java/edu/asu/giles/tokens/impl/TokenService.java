package edu.asu.giles.tokens.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.tokens.ITokenContents;
import edu.asu.giles.tokens.ITokenService;

/**
 * Class to create new user tokens for access to the REST api.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class TokenService implements ITokenService {
    
    /**
     * 4 hours
     */
    private int timeTillExpiration = 14400000;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenService#generateToken(java.lang.String)
     */
    @Override
    public String generateToken(String username) {
        String compactJws = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date((new Date()).getTime() + timeTillExpiration))
                .signWith(SignatureAlgorithm.HS512, propertiesManager.getProperty(IPropertiesManager.SIGNING_KEY))
                .compact();
        
        return compactJws;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenService#getTokenContents(java.lang.String)
     */
    @Override
    public ITokenContents getTokenContents(String token) {
        try {
            ITokenContents contents = new TokenContents();
            Jws<Claims> jws = Jwts.parser().setSigningKey(propertiesManager.getProperty(IPropertiesManager.SIGNING_KEY)).parseClaimsJws(token);
            Claims claims = jws.getBody();
            contents.setUsername(claims.getSubject());
            Date expirationTime = claims.getExpiration();
            contents.setExpired(expirationTime.before(new Date()));
            return contents;
        } catch (SignatureException e) {
            return null;
        }
    }
    
}
