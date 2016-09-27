package edu.asu.giles.aspects.access.openid.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public class CheckerResult {
    private ValidationResult result;
    private GoogleIdToken.Payload payload;
    
    public ValidationResult getResult() {
        return result;
    }
    public void setResult(ValidationResult result) {
        this.result = result;
    }
    public GoogleIdToken.Payload getPayload() {
        return payload;
    }
    public void setPayload(GoogleIdToken.Payload payload) {
        this.payload = payload;
    }
    
    
}