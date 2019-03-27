package com.gjing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Archine
 **/
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Get {

}
