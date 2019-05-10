package cn.gjing.doc;

import cn.gjing.swagger.EnableSwagger;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableSwagger
@Import(SwaggerDocSelector.class)
public @interface EnableSwaggerDoc {

}
