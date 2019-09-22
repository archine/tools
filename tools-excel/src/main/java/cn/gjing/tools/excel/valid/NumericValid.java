package cn.gjing.tools.excel.valid;

import cn.gjing.tools.excel.write.ExcelNumberValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据类型校验注解
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NumericValid {

    /**
     * 校验器
     *
     * @return ExcelValidation
     */
    Class<? extends ExcelValidation> validClass() default ExcelNumberValidation.class;

    /**
     * 数据校验最多校验多少行
     * @return 校验行数
     */
    int boxLastRow() default 0;

    /**
     * 操作类型
     * @return 操作类型
     */
    OperatorType operatorType() default OperatorType.EQUAL;

    /**
     * 校验类型
     * @return 校验类型
     */
    ValidType validationType();

    /**
     * 表达式1
     * @return 表达式1
     */
    String expr1();

    /**
     * 表达式2
     * @return 表达式2
     */
    String expr2() default "";

    /**
     * 是否弹出错误框
     * @return boolean
     */
    boolean showErrorBox() default true;

    /**
     * 提示框级别
     * @return 级别
     */
    Rank rank() default Rank.WARNING;

    /**
     * 错误框标题
     * @return 标题
     */
    String errorTitle() default "错误提示";

    /**
     * 详细错误内容
     * @return 错误内容
     */
    String errorContent() default "请填写正确的值";

    /**
     * 是否立即弹出
     * @return boolean
     */
    boolean showPromptBox() default true;

    /**
     * 是否允许空值
     * @return boolean
     */
    boolean allowEmpty() default true;
}
