package cn.gjing.util.excel.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumericValidation {
    /**
     * 数据校验最多校验多少行
     */
    int boxLastRow() default 0;

    /**
     * 操作类型
     */
    OperatorType operatorType() default OperatorType.EQUAL;

    /**
     * 校验类型
     */
    ValidationType validationType();

    /**
     * 表达式1
     */
    String expr1();

    /**
     * 表达式2
     */
    String expr2() default "";

    /**
     * 是否弹出错误框
     */
    boolean showErrorBox() default true;

    /**
     * 提示框级别
     */
    Rank rank() default Rank.WARNING;

    /**
     * 错误框标题
     */
    String errorTitle() default "错误提示";

    /**
     * 详细错误内容
     */
    String errorContent() default "请填写正确的值";

    /**
     * 是否立即弹出
     */
    boolean showPromptBox() default true;

    /**
     * 是否允许空值
     */
    boolean allowEmpty() default true;
}
