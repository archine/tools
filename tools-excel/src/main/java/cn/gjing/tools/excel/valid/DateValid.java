package cn.gjing.tools.excel.valid;

import cn.gjing.tools.excel.write.ExcelDateValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间校验注解
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateValid {

    /**
     * 校验器
     *
     * @return ExcelValidation
     */
    Class<? extends ExcelValidation> validClass() default ExcelDateValidation.class;

    /**
     * 数据校验最多校验多少行
     * @return 校验行数
     */
    int boxLastRow() default 0;

    /**
     * 时间格式
     * @return 时间转换格式
     */
    String pattern() default "yyyy-MM-dd";

    /**
     * 操作类型
     * @return 操作类型,默认between
     */
    OperatorType operatorType() default OperatorType.BETWEEN;

    /**
     * 表达式1
     * @return 表达式1
     */
    String expr1() default "1970-01-01";

    /**
     * 表达式2
     * @return 表达式2
     */
    String expr2() default "2999-01-01";

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
     * @return 错误框标题
     */
    String errorTitle() default "错误提示";

    /**
     * 详细错误内容
     * @return 错误内容
     */
    String errorContent() default "请填写正确的时间范围：1970-01-01至2999-01-01";

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
