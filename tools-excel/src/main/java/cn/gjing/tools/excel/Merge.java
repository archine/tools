package cn.gjing.tools.excel;

import cn.gjing.tools.excel.listen.MergeCallback;
import cn.gjing.tools.excel.write.DefaultMergeCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Merge {
    /**
     * Whether to open merge or not
     * @return boolean
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
