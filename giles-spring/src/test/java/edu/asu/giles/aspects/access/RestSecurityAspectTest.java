package edu.asu.giles.aspects.access;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.google.common.net.HttpHeaders;

import edu.asu.giles.aspects.access.annotations.OpenIdTokenCheck;
import edu.asu.giles.aspects.access.annotations.TokenCheck;
import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.aspects.access.tokens.impl.GilesChecker;
import edu.asu.giles.aspects.access.tokens.impl.GoogleChecker;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.files.IFilesManager;
import edu.asu.giles.tokens.ITokenContents;
import edu.asu.giles.tokens.impl.TokenContents;
import edu.asu.giles.users.AccountStatus;
import edu.asu.giles.users.IUserManager;
import edu.asu.giles.users.User;

public class RestSecurityAspectTest {

    private final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private final String INVALID_ACCESS_TOKEN = "INVALID_ACCESS_TOKEN";

    @Mock
    private IUserManager userManager;

    @Mock
    private IFilesManager filesManager;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature sig;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private GoogleChecker googleChecker;
    
    @Mock
    private GilesChecker gilesChecker;
    
    @Spy
    private List<IChecker> checkers = new ArrayList<IChecker>();

    @InjectMocks
    private RestSecurityAspect aspectToTest = new RestSecurityAspect();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        checkers.add(googleChecker);
        checkers.add(gilesChecker);
        
        Mockito.when(googleChecker.getId()).thenReturn(GoogleChecker.ID);
        Mockito.when(gilesChecker.getId()).thenReturn(GilesChecker.ID);
        
        aspectToTest.init();   
        
        User user = new User();
        user.setUsername("test");
        user.setAccountStatus(AccountStatus.APPROVED);
        Authentication auth = new UsernamePasswordAuthenticationToken(user,null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        User addedAccount = new User();
        addedAccount.setUsername("test2");
        addedAccount.setAccountStatus(AccountStatus.ADDED);
        
        User revokedAccount = new User();
        revokedAccount.setUsername("test3");
        revokedAccount.setAccountStatus(AccountStatus.REVOKED);
        
        Mockito.when(userManager.findUser("test")).thenReturn(user);
        Mockito.when(userManager.findUserByProviderUserId("test")).thenReturn(user);
        Mockito.when(userManager.findUser("test2")).thenReturn(addedAccount);
        Mockito.when(userManager.findUser("test3")).thenReturn(revokedAccount);
    }

    @Test
    public void test_checkUserAccess_success() throws Throwable {
        setUpTokenMocking("test");

        User user = new User();
        prepareMethodCalls(ACCESS_TOKEN, "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);
        Assert.assertEquals("proceed", returnObj);
        Assert.assertEquals("test", user.getUsername());
    }

    @Test
    public void test_checkUserAccess_tokenInHeader_success() throws Throwable {
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(
                "token " + ACCESS_TOKEN);
        setUpTokenMocking("test");

        User user = new User();
        prepareMethodCalls("", "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);
        Assert.assertEquals("proceed", returnObj);
        Assert.assertEquals("test", user.getUsername());
    }

    @Test
    public void test_checkUserAccess_invalidToken() throws Throwable {
        setUpTokenMocking("test");

        User user = new User();
        prepareMethodCalls(INVALID_ACCESS_TOKEN, "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);
        
        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test()
    public void test_checkUserAccess_tokenInHeader_invalidToken() throws Throwable {
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(
                "token " + INVALID_ACCESS_TOKEN);
        setUpTokenMocking("test");

        User user = new User();
        prepareMethodCalls("", "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");

        Mockito.when(joinPoint.proceed()).thenReturn("proceed");

        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);
        
        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkUserAccess_added() throws Throwable {
        setUpTokenMocking("test2");

        User user = new User();
        prepareMethodCalls(ACCESS_TOKEN, "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");
        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.FORBIDDEN,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkUserAccess_revoked() throws Throwable {
        setUpTokenMocking("test3");

        User user = new User();
        prepareMethodCalls(ACCESS_TOKEN, "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");
        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.FORBIDDEN,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkUserAccess_noAccount() throws Throwable {
        setUpTokenMocking("test4");

        User user = new User();
        prepareMethodCalls(ACCESS_TOKEN, "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");

        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.FORBIDDEN,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    @Test
    public void test_checkUserAccess_noToken() throws Throwable {
        setUpTokenMocking("test");

        User user = new User();
        prepareMethodCalls(null, "token", user);
        OpenIdTokenCheck check = createOpenIdAccessCheckAnnotation("token");

        Object returnObj = aspectToTest.checkOpenIdUserAccess(joinPoint, check);

        Assert.assertEquals(ResponseEntity.class, returnObj.getClass());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED,
                ((ResponseEntity) returnObj).getStatusCode());
    }

    private void prepareMethodCalls(String paraValue, String paraName, User user) {

        Object[] args = new Object[3];
        args[0] = paraValue;
        args[1] = user;
        args[2] = request;

        Mockito.when(joinPoint.getArgs()).thenReturn(args);
        Mockito.when(joinPoint.getSignature()).thenReturn(sig);

        String[] argNames = new String[3];
        argNames[0] = paraName;
        argNames[1] = "user";
        argNames[2] = "request";
        Mockito.when(sig.getParameterNames()).thenReturn(argNames);

        Class<?>[] paraTypes = new Class<?>[3];
        paraTypes[0] = String.class;
        paraTypes[1] = User.class;
        paraTypes[2] = StandardMultipartHttpServletRequest.class;
        Mockito.when(sig.getParameterTypes()).thenReturn(paraTypes);
    }

    private void setUpTokenMocking(String username) throws GeneralSecurityException, IOException, InvalidTokenException {
        CheckerResult validResult = new CheckerResult();
        validResult.setResult(ValidationResult.VALID);
        
        ITokenContents tokenContents = new TokenContents();
        tokenContents.setUsername(username);
        tokenContents.setExpired(false);
        validResult.setPayload(tokenContents);
        Mockito.when(googleChecker.validateToken(ACCESS_TOKEN)).thenReturn(validResult);
        
        CheckerResult invalidResult = new CheckerResult();
        invalidResult.setResult(ValidationResult.INVALID);
        
        invalidResult.setPayload(null);
        Mockito.when(googleChecker.validateToken(INVALID_ACCESS_TOKEN)).thenReturn(invalidResult);
    }

    private TokenCheck createTokenAccessCheckAnnotation(String parameterName) {
        TokenCheck check = new TokenCheck() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TokenCheck.class;
            }

            @Override
            public String value() {
                return parameterName;
            }
        };

        return check;
    }
    
    private OpenIdTokenCheck createOpenIdAccessCheckAnnotation(String parameterName) {
        OpenIdTokenCheck check = new OpenIdTokenCheck() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TokenCheck.class;
            }

            @Override
            public String value() {
                return parameterName;
            }
        };

        return check;
    }
}
