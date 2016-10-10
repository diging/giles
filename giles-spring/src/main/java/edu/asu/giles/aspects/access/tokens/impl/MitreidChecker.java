package edu.asu.giles.aspects.access.tokens.impl;

import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.tokens.INimbusTokenService;

@Service
public class MitreidChecker implements IChecker {
    
    public final static String ID = "MITREID";
    
    @Autowired
    private INimbusTokenService tokenService;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CheckerResult validateToken(String token) throws GeneralSecurityException,
            IOException, InvalidTokenException {
        try {
            tokenService.getOpenIdToken(token);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("The provided token is not a valid JWT token.", e);
        }
        return null;
    }

}
