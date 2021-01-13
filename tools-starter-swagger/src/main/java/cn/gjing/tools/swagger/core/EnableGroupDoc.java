package cn.gjing.tools.swagger.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable the document aggregation pattern,
 * which applies to the SpringCloud pattern
 * and applies to the gateway service
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GroupDocConfiguration.class)
@EnableSingleDoc
public @interface EnableGroupDoc {
}
