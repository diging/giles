package edu.asu.giles.tokens.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;

import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.tokens.INimbusTokenService;
import edu.asu.giles.tokens.IOpenIdToken;

@Service
public class NimbusTokenService implements INimbusTokenService {

    @Autowired
    private IPropertiesManager propertyManager;

    @Override
    public IOpenIdToken getOpenIdToken(String token) {
        Issuer iss = new Issuer("http://localhost:8081/openid-connect-server-webapp/");
        ClientID clientID = new ClientID(
                propertyManager.getProperty(IPropertiesManager.MITREID_CLIENT_ID));
        JWSAlgorithm jwsAlg = JWSAlgorithm.RS256;
        URL jwkSetURL;
        try {
            jwkSetURL = new URL(
                    "http://localhost:8081/openid-connect-server-webapp/jwk.json");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        IDTokenValidator validator = new IDTokenValidator(iss, clientID, jwsAlg,
                jwkSetURL);

        JWT idToken;
        try {
            idToken = JWTParser.parse(token);
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }

        // Set the expected nonce, leave null if not specified
        Nonce expectedNonce = null; // new Nonce("xyz..."); // or null

        IDTokenClaimsSet claims = null;

        try {
            claims = validator.validate(idToken, expectedNonce);
        } catch (BadJOSEException e) {
            // Invalid signature or claims (iss, aud, exp...)
            e.printStackTrace();
        } catch (JOSEException e) {
            // Internal processing exception
            e.printStackTrace();
        }

        if (claims != null)
            System.out.println("Logged in user " + claims.getSubject());
        
        return null;
    }
}
