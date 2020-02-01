package cn.gjing.tools.common.annotation;

import java.lang.annotation.*;

/**
 * @author Gjing
 * Marks the parameter as a Json object，Use with @NotEmpty
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Json {
}
