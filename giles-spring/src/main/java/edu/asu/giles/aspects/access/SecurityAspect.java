package edu.asu.giles.aspects.access;

import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.giles.aspects.access.annotations.AccountCheck;
import edu.asu.giles.aspects.access.annotations.DocumentAccessCheck;
import edu.asu.giles.aspects.access.annotations.DocumentIdAccessCheck;
import edu.asu.giles.aspects.access.annotations.FileAccessCheck;
import edu.asu.giles.aspects.access.annotations.FileTokenAccessCheck;
import edu.asu.giles.aspects.access.annotations.NoAccessCheck;
import edu.asu.giles.aspects.access.annotations.TokenCheck;
import edu.asu.giles.aspects.access.annotations.UploadIdAccessCheck;
import edu.asu.giles.aspects.access.openid.google.Checker;
import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.AspectMisconfigurationException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

@Aspect
@Component
public class SecurityAspect {

    private Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Autowired
    private IUserManager userManager;

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
//    @Autowired
//    private GitHubTemplateFactory templateFactory;
    

    @Around("within(edu.asu.giles.web..*) && @annotation(noCheck)")
    public Object doNotCheckUserAccess(ProceedingJoinPoint joinPoint,
            NoAccessCheck noCheck) throws Throwable {

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.giles.web..*) && @annotation(uploadCheck)")
    public Object checkUpoadIdAccess(ProceedingJoinPoint joinPoint,
            UploadIdAccessCheck uploadCheck) throws Throwable {
        
        String uploadId = getRequestParameter(joinPoint, uploadCheck.value());

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IUpload upload = filesManager.getUpload(uploadId);
        if (upload == null) {
            return "notFound";
        }

        if (!upload.getUsername().equals(user.getUsername())) {
            return "forbidden";
        }

        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.web..*) && @annotation(docCheck)")
    public Object checkDocumentIdAccess(ProceedingJoinPoint joinPoint, DocumentIdAccessCheck docCheck) throws Throwable {
        String docId = getRequestParameter(joinPoint, docCheck.value());
        
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IDocument doc = filesManager.getDocument(docId);
        if (doc == null) {
            return "notFound";
        }

        if (doc.getUsername() != null) {
            if (!doc.getUsername().equals(user.getUsername())) {
                return "forbidden";
            }
        }

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.giles.web..*) && @annotation(fileCheck)")
    public Object checkFileAccess(ProceedingJoinPoint joinPoint,
            FileAccessCheck fileCheck) throws Throwable {
        String fileId = getRequestParameter(joinPoint, fileCheck.value());
        
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (!file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        return joinPoint.proceed();
    }
    
    private String getRequestParameter(ProceedingJoinPoint joinPoint,
            String parameterName) {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();

        String value = null;
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals(parameterName)) {
                value = (String) args[i];
            }
        }
        return value;
    }

    @Around("within(edu.asu.giles.rest..*) && @annotation(github)")
    public Object checkUserAccess(ProceedingJoinPoint joinPoint,
            TokenCheck github) throws Throwable {
        logger.debug("Checking GitHub access token for REST endpoint.");
        
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, github.value(), null);
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;

        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }

        ResponseEntity<String> authResult = checkAuthorization(user, token);
        if (authResult != null) {
            return authResult;
        }

        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.rest..*) && @annotation(check)")
    public Object checkDocument(ProceedingJoinPoint joinPoint, DocumentAccessCheck check) throws Throwable {
                
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, check.github(), check.value());
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;
        String docId = userTokenObj.elementId;
        
        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }
        
        IDocument doc = filesManager.getDocument(docId);
        if (doc == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        if (doc.getAccess() == DocumentAccess.PUBLIC) {
            return joinPoint.proceed();
        }
        
        ResponseEntity<String> authResult = checkAuthorization(user, token);
        if (authResult != null) {
            return authResult;
        }

        if (!doc.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        return joinPoint.proceed();
    }
    
    @Around("within(edu.asu.giles.rest..*) && @annotation(check)")
    public Object checkFileGitHubAccess(ProceedingJoinPoint joinPoint, FileTokenAccessCheck check) throws Throwable {
        
        UserTokenObject userTokenObj = extractUserTokenInfo(joinPoint, check.github(), check.value());
        
        User user = userTokenObj.user;
        String token = userTokenObj.token;
        String fileId = userTokenObj.elementId;
        
        
        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }
        
        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        if (file.getAccess() == DocumentAccess.PUBLIC) {
            return joinPoint.proceed();
        }
        
        ResponseEntity<String> authResult = checkAuthorization(user, token);
        if (authResult != null) {
            return authResult;
        }

        if (!file.getUsername().equals(user.getUsername())) {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
        
        return joinPoint.proceed();
    }
    
    private UserTokenObject extractUserTokenInfo(ProceedingJoinPoint joinPoint, String tokenParameter, String parameterName) {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();
        Class<?>[] argTypes = sig.getParameterTypes();

        User user = null;
        String token = null;
        String elementId = null;
        for (int i = 0; i < argNames.length; i++) {
            // check if GitHub token is passed as parameters
            if (argNames[i].equals(tokenParameter)) {
                token = (String) args[i];
            }
            // check if there is a request header with github token
            if (HttpServletRequest.class.isAssignableFrom(argTypes[i])) {
                String tokenHeader = ((HttpServletRequest)args[i]).getHeader(HttpHeaders.AUTHORIZATION);
                if (tokenHeader != null) {
                    token = tokenHeader.substring(6);
                }
            }
            if (argTypes[i].equals(User.class)) {
                user = (User) args[i];
            }
            
            if (parameterName != null) {
                if (argNames[i].equals(parameterName)) {
                    elementId = (String) args[i];
                }
            }
        }
        
        return new UserTokenObject(user, token, elementId);
    }
    
    private ResponseEntity<String> checkAuthorization(User user, String token) {
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        CheckerResult validationResult = null;
        
        try {
            validationResult = validateToken(token);
        } catch (GeneralSecurityException e) {
            logger.error("Security issue with token.", e);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", e.getLocalizedMessage());
            
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            logger.error("Network issue.", e);
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", e.getLocalizedMessage());
            
            return generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (validationResult == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        if (validationResult.getResult() != ValidationResult.VALID) {
            Map<String, String> msgs = new HashMap<String, String>();
            msgs.put("errorMsg", validationResult.getResult().name());
            return generateResponse(msgs, HttpStatus.UNAUTHORIZED);
        }
        
        User foundUser = userManager.findUser(validationResult.getPayload().getSubject());
        logger.debug("Authorizing: " + validationResult.getPayload().getSubject());

        if (foundUser == null) {
            return new ResponseEntity<>(
                    "{ \"error\": \"The user doesn't seem to have a Giles account.\" } ",
                    HttpStatus.FORBIDDEN);
        }
        if (foundUser.getAccountStatus() != AccountStatus.APPROVED) {
            return new ResponseEntity<>(
                    "{ \"error\": \"The user account you are using has not been approved. Please contact a Giles administrator.\" } ",
                    HttpStatus.FORBIDDEN);
        }

        fillUser(foundUser, user);
        
        return null;
    }
    
    private CheckerResult validateToken(String token) throws GeneralSecurityException, IOException {
        String clientIds = propertiesManager.getProperty(IPropertiesManager.REGISTERED_APPS_CLIENT_IDS);
        String[] clientIdsList = clientIds.split(",");
        Checker checker = new Checker(clientIdsList, clientIdsList);
        
        return checker.check(token);
    }
    
    
    @Around("within(edu.asu.giles.web..*) && @annotation(check)")
    public Object checkAccount(ProceedingJoinPoint joinPoint, AccountCheck check) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();
        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            return "forbidden";
        }
        return joinPoint.proceed();
    }

    private void fillUser(User filled, User toBeFilled) {
        toBeFilled.setAdmin(filled.getIsAdmin());
        toBeFilled.setEmail(filled.getEmail());
        toBeFilled.setFirstname(filled.getFirstname());
        toBeFilled.setLastname(filled.getLastname());
        toBeFilled.setProvider(filled.getProvider());
        toBeFilled.setRoles(filled.getRoles());
        toBeFilled.setUserIdOfProvider(filled.getUserIdOfProvider());
        toBeFilled.setUsername(filled.getUsername());
    }
    
    private ResponseEntity<String> generateResponse(Map<String, String> msgs, HttpStatus status) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        for (String key : msgs.keySet()) {
            root.put(key, msgs.get(key));
        }
        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            logger.error("Could not write json.", e);
            return new ResponseEntity<String>(
                    "{\"errorMsg\": \"Could not write json result.\", \"errorCode\": \"errorCode\": \"500\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<String>(sw.toString(), status);
    }
    
    class UserTokenObject {
        
        public User user;
        public String token;
        public String elementId;
        
        public UserTokenObject(User user, String token, String elementId) {
            super();
            this.user = user;
            this.token = token;
            this.elementId = elementId;
        }
    }
}
