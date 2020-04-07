package cn.gjing.tools.excel;


import cn.gjing.tools.excel.write.style.DefaultExcelStyle;

import java.lang.annotation.*;

import static cn.gjing.tools.excel.ExcelType.XLS;

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
    ExcelType type() default XLS;

    /**
     * Excel head row height
     * @return 350
     */
    short headHeight() default 350;

    /**
     * Window size, which is flushed to disk if more data is written to the export than the specified size
     * @return 500
     */
    int windowSize() default 500;

    /**
     * number of rows to keep in memory
     * @return 500
     */
    int cacheRowSize() default 100;

    /**
     * buffer size to use when reading InputStream to file
     * @return 10240
     */
    int bufferSize() default 2048;

    /**
     * Excel style
     *
     * @return ExcelStyle
     */
    Class<?> style() default DefaultExcelStyle.class;

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
