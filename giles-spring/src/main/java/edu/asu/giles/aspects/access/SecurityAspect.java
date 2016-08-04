package edu.asu.giles.aspects.access;

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
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.stereotype.Component;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.IUpload;
import edu.asu.giles.exceptions.AspectMisconfigurationException;
import edu.asu.giles.files.IFilesManager;
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

    @Around("within(edu.asu.giles.web..*) && @annotation(noCheck)")
    public Object doNotCheckUserAccess(ProceedingJoinPoint joinPoint,
            NoAccessCheck noCheck) throws Throwable {

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.giles.web..*) && @annotation(uploadCheck)")
    public Object checkUpoadIdAccess(ProceedingJoinPoint joinPoint,
            UploadIdAccessCheck uploadCheck) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();

        String uploadId = null;
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals(uploadCheck.value())) {
                uploadId = (String) args[i];
            }
        }

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

    @Around("within(edu.asu.giles.web..*) && @annotation(fileCheck)")
    public Object checkFileAccess(ProceedingJoinPoint joinPoint,
            FileAccessCheck fileCheck) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();

        String fileId = null;
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals(fileCheck.value())) {
                fileId = (String) args[i];
            }
        }

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        User user = (User) auth.getPrincipal();

        IFile file = filesManager.getFile(fileId);
        if (file == null) {
            return "notFound";
        }

        if (!file.getUsername().equals(user.getUsername())) {
            return "forbidden";
        }

        return joinPoint.proceed();
    }

    @Around("within(edu.asu.giles.rest..*) && @annotation(github)")
    public Object checkUserAccess(ProceedingJoinPoint joinPoint,
            GitHubAccessCheck github) throws Throwable {
        logger.debug("Checking GitHub access token for REST endpoint.");
        Object[] args = joinPoint.getArgs();
        MethodSignature sig = (MethodSignature) joinPoint.getSignature();
        String[] argNames = sig.getParameterNames();
        Class<?>[] argTypes = sig.getParameterTypes();

        User user = null;
        String token = null;
        for (int i = 0; i < argNames.length; i++) {
            // check if GitHub token is passed as parameters
            if (argNames[i].equals(github.value())) {
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
        }

        if (user == null) {
            throw new AspectMisconfigurationException(
                    "User object is missing in method.");
        }

        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        GitHubTemplate template = new GitHubTemplate(token);
        GitHubUserProfile profile = template.userOperations().getUserProfile();
        User foundUser = userManager.findUser(profile.getUsername());
        logger.debug("Authorizing: " + profile.getUsername());

        if (foundUser == null) {
            return new ResponseEntity<>(
                    "{ \"error\": \"The user doesn't seem to have a Giles account.\" } ",
                    HttpStatus.FORBIDDEN);
        }

        fillUser(foundUser, user);

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
}
