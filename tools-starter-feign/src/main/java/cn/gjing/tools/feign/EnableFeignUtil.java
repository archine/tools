package cn.gjing.tools.feign;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignSelector.class)
public @interface EnableFeignUtil {
}
