package com.gj.anno;

import java.lang.annotation.*;

/**
 * @author Archine
 * parameter check,Only for methods,example：@NotNull or @NotNull(exclude={“”,””})
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull {
    String[] exclude()default {};
}
