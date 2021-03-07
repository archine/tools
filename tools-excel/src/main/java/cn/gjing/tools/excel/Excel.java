package cn.gjing.tools.excel;


import cn.gjing.tools.excel.metadata.ExcelType;
import cn.gjing.tools.excel.read.resolver.ExcelBindReader;

import java.lang.annotation.*;

import static cn.gjing.tools.excel.metadata.ExcelType.XLS;

/**
 * Excel mapped entity annotation
 * The entity class marked by the annotation will be declared as a excel mapped entity
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
     * Window size, which is flushed to disk when exported
     * if the data that has been written out exceeds the specified size
     * only for xlsx
     *
     * @return windowSize
     */
    int windowSize() default 500;

    /**
     * How many lines of data in the Excel file need to be saved when imported,
     * only for xlsx
     *
     * @return cacheRowSize
     */
    int cacheRowSize() default 100;

    /**
     * buffer size to use when reading InputStream to file
     *
     * @return bufferSize
     */
    int bufferSize() default 2048;

    /**
     * Excel head row height
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
     * The unique key of the mapped entity is used to detect whether the imported Excel file matches the mapped entity.
     * The default key is the mapping entity class name.
     *
     * @see ExcelBindReader#check(boolean)
     * @return key
     */
    String uniqueKey() default "";
}
