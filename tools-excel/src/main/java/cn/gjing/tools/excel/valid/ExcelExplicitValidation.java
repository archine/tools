package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

/**
 * @author Gjing
 **/
public interface ExcelExplicitValidation {
    /**
     * Drop-down check rule
     *
     * @param explicitValid ExplicitValid
     * @param workbook      workbook
     * @param sheet         The current sheet
     * @param firstRow      First row
     * @param lastRow       Last row
     * @param firstCol      First col
     * @param lastCol       Last col
     * @param locked        Is lock?
     * @param values        Drop-down box contents, including field name/parent key and content
     * @param fieldName     Current field name
     * @return locked
     */
    default boolean valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, boolean locked, String fieldName, Map<String, String[]> values) {
        return locked;
    }
}
