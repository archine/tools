package cn.gjing.tools.common.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * parameter check,Only for methods,example：@NotNull2 or @NotNull2(exclude={“”,””})
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmpty {
    String message() default "";
}
