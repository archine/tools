package cn.gjing.tools.excel;

import cn.gjing.tools.excel.convert.DataConvert;
import cn.gjing.tools.excel.convert.DefaultDataConvert;
import cn.gjing.tools.excel.metadata.ExcelColor;
import cn.gjing.tools.excel.write.merge.Merge;

import java.lang.annotation.*;

/**
 * This annotation is used to declare that an attribute is an Excel header,
 * and if it is not annotated through this annotation,
 * it will not be exported or imported as a header
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {
    /**
     * Array of Excel header names.
     * If multiple headers appear in the array and you want to merge the same header when exporting,
     * you need to activate the multi-header mode before the export is executed.
     * The last one in the array belongs to the real header,
     * and if you are importing a multilevel header Excel file,
     * you need to specify the real header start subscript
     *
     * @return header names
     */
    String[] value() default {""};

    /**
     * Excel header width
     *
     * @return cell width
     */
    int width() default 5120;

    /**
     * List header sort, Please to use order
     *
     * @return sort index
     */
    @Deprecated
    int sort() default 0;

    /**
     * List header sort, if not set will default entity property order
     *
     * @return sort index
     */
    int order() default 0;

    /**
     * The format of the cells under the current table header when exported,
     * default to normal format
     *
     * @return format
     */
    String format() default "";

    /**
     * Whether to enable automatic vertical merge when export
     *
     * @return autoMerge
     */
    Merge autoMerge() default @Merge;

    /**
     * Whether null values are supported when imported,Please to use required
     *
     * @return boolean
     */
    @Deprecated
    boolean allowEmpty() default true;

    /**
     * Whether the contents in the cell below the current header are required when importing
     *
     * @return boolean
     */
    boolean required() default false;

    /**
     * Data convert, which you can use to change data during import and export,
     * before the cell are populated and converted to entity field
     *
     * @return DefaultDataConvert
     * @see DataConvert
     */
    Class<? extends DataConvert<?>> convert() default DefaultDataConvert.class;

    /**
     * Color index, can control the current header fill color,
     * default use style listener color configuration
     *
     * @return index
     * @see ExcelColor
     */
    ExcelColor color() default ExcelColor.LIME;

    /**
     * Font color index, can control the current header value color,
     * default use style listener color configuration
     *
     * @return index
     * @see ExcelColor
     */
    ExcelColor fontColor() default ExcelColor.WHITE;
}
