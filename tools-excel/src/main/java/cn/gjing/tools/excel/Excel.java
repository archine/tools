package cn.gjing.tools.excel;

import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.write.resolver.ExcelBindWriter;

import java.lang.annotation.*;

import static cn.gjing.tools.excel.metadata.ExcelType.XLS;

/**
 * Excel mapped entity annotation
 * The entity class marked by the annotation will be declared as an Excel mapped entity
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
     * Excel file type to be exported
     *
     * @return Default XLS
     */
    ExcelType type() default XLS;

    /**
     * Window size, which is flushed to disk when exported
     * if the data that has been written out exceeds the specified size
     * only for xlsx
     *
     * @return windowSize
     */
    int windowSize() default 500;

    /**
     * Number of rows of data saved to memory when importing an Excel file
     * only for xlsx
     *
     * @return cacheRowSize
     */
    int cacheRowSize() default 100;

    /**
     * Buffer size to use when reading InputStream to file
     *
     * @return bufferSize
     */
    int bufferSize() default 2048;

    /**
     * Excel header row height
     *
     * @return headHeight
     */
    short headerHeight() default 400;

    /**
     * Excel body row height
     *
     * @return bodyHeight
     */
    short bodyHeight() default 370;

    /**
     * Set a unique ID for the exported Excel file,
     * which can be used to check whether it is the specified file at import time.
     * The default is to use the current mapping entity Class as the unique ID,
     * which you can also set by {@link ExcelBindWriter#bind(String)},The priority is higher than this value
     *
     *
     * @return key
     */
    String uniqueKey() default "";
}
