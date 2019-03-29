package com.gjing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Archine
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface NonNull {
}
