package edu.asu.giles.aspects.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FileGitHubAccessCheck {
    String value() default "fileId";
    String github() default "accessToken";
}