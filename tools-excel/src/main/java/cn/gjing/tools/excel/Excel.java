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
     * Excel head row height
     * @return 350
     */
    short headHeight() default 350;

    /**
     * The number of rows cached in memory
     * @return 1000
     */
    int maxSize() default 1000;

    /**
     * The byte size cached to memory
     * @return 10240
     */
    int bufferSize() default 10240;

    /**
     * Excel style
     *
     * @return ExcelStyle
     */
    Class<? extends ExcelStyle> style() default DefaultExcelStyle.class;
}
