package cn.gjing.tools.common.valid;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Method parameters are not empty ，Including map, array, collection
 **/
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmpty {
    /**
     * Exception information
     * @return String
     */
    String message() default "参数不能为空";
}
