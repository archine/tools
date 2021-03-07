package cn.gjing.tools.excel.write.merge;

import cn.gjing.tools.excel.write.callback.DefaultExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;

import java.lang.annotation.*;

/**
 * Custom merge cells that are adjacent to the same column
 *
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Merge {
    /**
     * Enable automatic merge
     *
     * @return false
     */
    boolean enable() default false;

    /**
     * Whether to merge when null value is encountered
     *
     * @return boolean
     */
    boolean empty() default false;

    /**
     * Callback policy, you can control the merge rules by callback policy,
     * the default policy is to merge as long as the values are the same
     *
     * @return DefaultMergeCallback
     * @see ExcelAutoMergeCallback
     */
    Class<? extends ExcelAutoMergeCallback<?>> callback() default DefaultExcelAutoMergeCallback.class;
}
