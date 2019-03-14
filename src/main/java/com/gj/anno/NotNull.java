package com.gj.anno;

import java.lang.annotation.*;

/**
 * @author Archine
 * parameter check
 **/
@Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

}
