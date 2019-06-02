package cn.gjing.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * parameter check,Only for methods,example：@NotNull2 or @NotNull2(exclude={“”,””})
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotNull2 {
    String[] exclude()default {};

    String message() default "";
}
