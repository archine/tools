package com.gj.anno;

import java.lang.annotation.*;

/**
 * @author Archine
 * @date 2019-03-12
 * parameter check
 **/
@Target({ElementType.PARAMETER,ElementType.METHOD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {

}
