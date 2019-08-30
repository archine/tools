package cn.gjing.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记实体类上，说明这是excel的文件名
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {
    /**
     * 文件名
     */
    String name();

    /**
     * 文档类型
     */
    DocType type() default DocType.XLS;

    /**
     * excel描述
     */
    String description() default "";

    /**
     * 描述起始行
     */
    int firstRow() default 0;

    /**
     * 描述截至行
     */
    int lastRow() default 2;

    /**
     * 起始单元格
     */
    int firstCell() default 0;

    /**
     * 截止单元格
     */
    int lastCell() default 0;

    /**
     * 是否自动换行
     */
    boolean autoWrap() default false;
}
