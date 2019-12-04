package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel validator
 *
 * @author Gjing
 **/
public interface ExcelValidation {

    /**
     * Custom time validation rules
     *
     * @param dateValid DateValid
     * @param sheet     sheet
     * @param firstRow  First row
     * @param firstCol  First col
     * @param lastCol   Last col
     */
    default void valid(DateValid dateValid, Sheet sheet, int firstRow, int firstCol, int lastCol) {
    }

    /**
     * Custom data validation rules
     *
     * @param numericValid NumericValid
     * @param sheet        sheet
     * @param firstRow     First row
     * @param firstCol     First col
     * @param lastCol      Last col
     */
    default void valid(NumericValid numericValid, Sheet sheet, int firstRow, int firstCol, int lastCol) {
    }

    /**
     * Drop-down check rule
     *
     * @param explicitValid ExplicitValid
     * @param workbook      workbook
     * @param sheet         The current sheet
     * @param firstRow      First row
     * @param firstCol      First col
     * @param lastCol       Last col
     */
    default void valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int firstCol, int lastCol) {
    }
}
