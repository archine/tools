package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel校验器
 *
 * @author Gjing
 **/
public interface ExcelValidation {

    /**
     * 自定义时间校验规则
     * @param dateValid 时间注解
     * @param sheet sheet
     * @param firstRow 开始行
     * @param firstCol 开始列
     * @param lastCol 结束列
     */
    default void valid(DateValid dateValid, Sheet sheet, int firstRow, int firstCol, int lastCol) {

    }

    /**
     * 自定义数据校验规则
     * @param numericValid 数据注解
     * @param sheet sheet
     * @param firstRow 开始行
     * @param firstCol 开始列
     * @param lastCol 结束列
     */
    default void valid(NumericValid numericValid, Sheet sheet, int firstRow, int firstCol, int lastCol) {

    }

    /**
     * 下拉框校验规则
     * @param explicitValid 下拉框注解
     * @param workbook workbook
     * @param sheet 当前sheet
     * @param firstRow 开始列
     * @param firstCol 开始行
     * @param lastCol 结束行
     */
    default void valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int firstCol, int lastCol) {

    }
}
