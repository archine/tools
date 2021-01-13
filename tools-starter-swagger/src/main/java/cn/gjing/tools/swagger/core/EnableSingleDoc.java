package cn.gjing.tools.swagger.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Open a single project document for all SpringBoot projects
 * The SpringBoot version is recommended for 2.2.x +
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Import(SingleDocConfiguration.class)
public @interface EnableSingleDoc {
}
