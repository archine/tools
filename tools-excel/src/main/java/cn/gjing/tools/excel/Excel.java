package cn.gjing.tools.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static cn.gjing.tools.excel.Type.XLS;

/**
 * Excel mapped entity annotation
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {
    /**
     * Excel file name
     *
     * @return name
     */
    String value() default "";

    /**
     * Excel file type
     *
     * @return Type
     */
    Type type() default XLS;

    /**
     * Excel style
     *
     * @return ExcelStyle
     */
    Class<? extends ExcelStyle> style() default DefaultExcelStyle.class;
}
