package cn.gjing.tools.common.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 * Method parameters are not null
 **/
@Target({ElementType.METHOD,ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    /**
     * Exception information
     * @return String
     */
    String message() default "参数不能为Null";
}
