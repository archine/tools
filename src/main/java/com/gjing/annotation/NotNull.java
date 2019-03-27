package com.gjing.annotation;

import java.lang.annotation.*;

/**
 * @author Archine
 * parameter check,Only for methods,example：@NotNull or @NotNull(exclude={“”,””})
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {
    String[] exclude()default {};

    String message() default "";
}
