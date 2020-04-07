package cn.gjing.tools.excel;

import cn.gjing.tools.excel.write.callback.AutoColumnMergeCallback;
import cn.gjing.tools.excel.write.merge.DefaultColumnMergeCallback;

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
     * @return false
     */
    boolean open() default false;

    /**
     * Whether to merge empty
     * @return boolean
     */
    boolean empty() default false;

    /**
     * Custom Settings merge rules
     * @return DefaultMergeCallback
     */
    Class<? extends AutoColumnMergeCallback<?>> callback() default DefaultColumnMergeCallback.class;
}
