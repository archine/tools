package cn.gjing.tools.excel;


import java.lang.annotation.*;

import static cn.gjing.tools.excel.Type.XLS;

/**
 * Excel mapped entity annotation
 *
 * @author Gjing
 **/
@Documented
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

    /**
     * Lock sheet
     * @return false
     */
    boolean lock() default false;

    /**
     * Unlock password
     * @return ""
     */
    String secret() default "";
}
