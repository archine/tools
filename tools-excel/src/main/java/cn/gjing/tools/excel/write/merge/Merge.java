package cn.gjing.tools.excel.write.merge;

import cn.gjing.tools.excel.write.callback.ExcelAutoMergeCallback;
import cn.gjing.tools.excel.write.callback.DefaultExcelAutoMergeCallback;

import java.lang.annotation.*;

/**
 * @author Gjing
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Merge {
    /**
     * Whether to enable row merge
     *
     * @return false
     */
    boolean open() default false;

    /**
     * Whether to merge empty
     *
     * @return boolean
     */
    boolean empty() default false;

    /**
     * Callback strategy
     *
     * @return DefaultMergeCallback
     */
    Class<? extends ExcelAutoMergeCallback<?>> callback() default DefaultExcelAutoMergeCallback.class;
}
