package cn.gjing.tools.common.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Check the phone number format，Use with @NotEmpty
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Mobile {
    /**
     * Exception information
     * @return String
     */
    String message() default "Invalid mobile number format";
}
