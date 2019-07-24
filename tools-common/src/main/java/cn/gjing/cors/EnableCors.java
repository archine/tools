package cn.gjing.cors;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CorsSelector.class)
public @interface EnableCors {
}
