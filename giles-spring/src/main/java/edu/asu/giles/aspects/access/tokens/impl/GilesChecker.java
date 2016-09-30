package edu.asu.giles.aspects.access.tokens.impl;

import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.tokens.ITokenContents;
import edu.asu.giles.tokens.ITokenService;

@Service
public class GilesChecker implements IChecker {

    public final static String ID = "GILES";
    
    @Autowired
    private ITokenService tokenService;
    
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CheckerResult validateToken(String token) throws GeneralSecurityException,
            IOException, InvalidTokenException {
        ITokenContents contents;
        
        try {
            contents = tokenService.getTokenContents(token);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("The provided token is not a valid JWT token.", e);
        }
        
        CheckerResult result = new CheckerResult();
        result.setPayload(contents);
        if (contents == null) {
            result.setResult(ValidationResult.INVALID);
        } else if (!contents.isExpired()) {
            result.setResult(ValidationResult.VALID);
        } else {
            result.setResult(ValidationResult.EXPIRED);
        }
        
        return result;
    }

}
