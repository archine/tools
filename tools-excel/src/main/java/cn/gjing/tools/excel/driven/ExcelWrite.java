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
     * Which table heads to be ignored when exporting, in the case of multiple table heads,
     * there are more than one child table heads under the ignored table head,
     * then the child table head will be ignored, if the ignored table head is from the table head
     * then it is ignored
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

    /**
     * Whether you need to add a file identifier when exporting an Excel file,
     * which can be used for template validation of the file at import time
     *
     * @return bind
     */
    boolean bind() default true;
}
