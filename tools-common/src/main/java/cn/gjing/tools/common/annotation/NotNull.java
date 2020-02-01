package cn.gjing.tools.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 * Method parameters are not null
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface NotNull {
}
