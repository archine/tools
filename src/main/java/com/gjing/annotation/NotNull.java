package com.gjing.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * parameter check,Only for methods,example：@NotNull or @NotNull(exclude={“”,””})
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {
    String[] exclude()default {};

    String message() default "Parameter cannot be null";
}
