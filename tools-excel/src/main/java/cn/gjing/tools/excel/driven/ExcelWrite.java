package cn.gjing.tools.excel.driven;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcelWrite {
    /**
     * Excel file name
     *
     * @return value
     */
    String value() default "";

    /**
     * Excel mapping entity
     *
     * @return class
     */
    Class<?> mapping();

    /**
     * The exported field is to be ignored
     *
     * @return ignores
     */
    String[] ignores() default {};

    /**
     * Sheet name
     *
     * @return Default sheet1
     */
    String sheet() default "Sheet1";

    /**
     * Whether to add validation when exporting Excel templates
     *
     * @return boolean
     */
    boolean needValid() default false;

    /**
     * Whether is need excel head
     *
     * @return true
     */
    boolean needHead() default true;

    /**
     * Whether to open multistage Excel headers
     *
     * @return boolean
     */
    boolean multiHead() default false;

    /**
     * Init default excel style
     *
     * @return boolean
     */
    boolean initDefaultStyle() default true;

}
