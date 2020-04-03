package cn.gjing.tools.excel;

import cn.gjing.tools.excel.listen.MergeCallback;
import cn.gjing.tools.excel.write.DefaultMergeCallback;

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
    Class<? extends MergeCallback<?>> callback() default DefaultMergeCallback.class;
}
