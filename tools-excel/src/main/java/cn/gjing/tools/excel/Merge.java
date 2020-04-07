package cn.gjing.tools.excel;

import cn.gjing.tools.excel.write.callback.AutoMergeCallback;
import cn.gjing.tools.excel.write.callback.DefaultAutoMergeCallback;

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
    Class<? extends AutoMergeCallback<?>> callback() default DefaultAutoMergeCallback.class;
}
