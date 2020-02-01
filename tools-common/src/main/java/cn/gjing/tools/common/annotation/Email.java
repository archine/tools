package cn.gjing.tools.common.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Check the email address format，Use with @NotEmpty
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Email {
    /**
     * Exception information
     * @return String
     */
    String message() default "Invalid email format";
}
