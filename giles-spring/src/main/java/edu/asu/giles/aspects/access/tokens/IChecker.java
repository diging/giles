package edu.asu.giles.aspects.access.tokens;

import java.io.IOException;
import java.security.GeneralSecurityException;

import edu.asu.giles.aspects.access.openid.google.CheckerResult;

public interface IChecker {
    
    public String getId();

    public CheckerResult validateToken(String token)
            throws GeneralSecurityException, IOException;

}