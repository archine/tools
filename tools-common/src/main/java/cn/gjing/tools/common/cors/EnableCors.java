package cn.gjing.tools.common.cors;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启全局跨域
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CorsSelector.class)
public @interface EnableCors {
}
