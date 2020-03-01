package cn.gjing.tools.common.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Regex {
    /**
     * Regular expression
     * @return expression
     */
    String expr();

    /**
     * Exception information
     * @return message
     */
    String message() default "请输入符合正则格式的内容";
}
