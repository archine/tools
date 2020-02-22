package cn.gjing.tools.excel;

import cn.gjing.tools.excel.read.DefaultReadCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
    Type type() default Type.XLS;

    /**
     * Excel style
     *
     * @return ExcelStyle
     */
    Class<? extends ExcelStyle> style() default DefaultExcelStyle.class;

    /**
     * Read each line for a callback
     * @return CallBacker
     */
    Class<? extends ReadCallback> readCallback() default DefaultReadCallback.class;
}
