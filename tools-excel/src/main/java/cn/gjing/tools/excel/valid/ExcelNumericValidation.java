package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author Gjing
 **/
public interface ExcelNumericValidation {
    /**
     * Custom data validation rules
     *
     * @param numericValid NumericValid
     * @param sheet        sheet
     * @param firstRow     First row
     * @param lastRow      Last row
     * @param firstCol     First col
     * @param lastCol      Last col
     */
    default void valid(NumericValid numericValid, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {

    }
}
