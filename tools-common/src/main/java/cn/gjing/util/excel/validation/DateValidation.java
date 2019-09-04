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
public @interface DateValidation {
    /**
     * 数据校验最多校验多少行
     */
    int boxLastRow() default 0;

    /**
     * 时间格式
     */
    String pattern() default "yyyy-MM-dd";

    /**
     * 操作类型
     */
    OperatorType operatorType() default OperatorType.BETWEEN;

    /**
     * 表达式1
     */
    String expr1() default "1970-01-01";

    /**
     * 表达式2
     */
    String expr2() default "2999-01-01";

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
    String errorContent() default "请填写正确的时间范围：1970-01-01至2999-01-01";

    /**
     * 是否立即弹出
     */
    boolean showPromptBox() default true;

    /**
     * 是否允许空值
     */
    boolean allowEmpty() default true;
}
