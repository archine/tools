package cn.gjing.tools.excel.valid;

import cn.gjing.tools.excel.write.ExcelExplicitValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel明确范围数据校验注解
 *
 * @author Gjing
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExplicitValid {

    /**
     * 校验器
     *
     * @return ExcelValidation
     */
    Class<? extends ExcelValidation> validClass() default ExcelExplicitValidation.class;

    /**
     * 范围值
     * @return 范围值
     */
    String[] combobox() default {};

    /**
     * 数据校验最多校验多少行
     * @return 校验多少行
     */
    int boxLastRow() default 0;

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
    String errorContent() default "请填写下拉框内的值";

    /**
     * 是否立即弹出
     * @return boolean
     */
    boolean showPromptBox() default true;
}
