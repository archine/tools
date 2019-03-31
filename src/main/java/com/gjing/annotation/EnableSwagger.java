package com.gjing.annotation;

import com.gjing.config.SwaggerSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SwaggerSelector.class)
public @interface EnableSwagger {
}
