package cn.gjing.tools.excel.write.listener;

import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;

/**
 * Cascading dropdown box listener,
 * When {@link ExcelDropdownBox} is used in the header field of the mapped entity and a valid {@link ExcelDropdownBox#link()} parameter is set
 *
 * @author Gjing
 **/
public interface ExcelCascadingDropdownBoxListener extends ExcelWriteListener {
    /**
     * Add cascading dropdown box
     *
     * @param excelDropdownBox ExplicitValid
     * @param workbook         workbook
     * @param sheet            The current sheet
     * @param firstRow         First row
     * @param lastRow          Last row
     * @param colIndex         Col index
     * @param field            Current field
     */
    void addCascadingDropdownBox(ExcelDropdownBox excelDropdownBox, Workbook workbook, Sheet sheet, int firstRow, int lastRow, int colIndex,
                                 Field field);
}
