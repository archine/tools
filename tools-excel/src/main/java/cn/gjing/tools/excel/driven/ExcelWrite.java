package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.write.BigTitle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel export annotation, which export Excel when used on methods.
 * The return value of this method needs to specify a List of data,
 * which can also be wrapped in a {@link ExcelWriteWrapper}. Of course,
 * if you are exporting the template, you can set the return value of the method to void,
 * or you can use the wrapper to wrap the empty data,
 * the template if there is a big title you can specify the return value is {@link BigTitle}
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcelWrite {
    /**
     * Excel file name,If it is not set and the mapping entity
     * is not set at the same time, the current date is default
     *
     * @return value
     */
    String value() default "";

    /**
     * Excel mapping entity,You can refer to {@link Excel}
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
     * Whether enable excel valid
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
     * Whether init default excel style
     *
     * @return boolean
     */
    boolean initDefaultStyle() default true;
}
