package cn.gjing.tools.common.valid;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Check the phone number format
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Mobile {
    /**
     * Exception information
     * @return String
     */
    String message() default "无效的手机号格式";
}
