package cn.gjing.tools.excel.metadata.aware;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Retrieve workbook
 *
 * @author Gjing
 **/
public interface ExcelWorkbookAware extends ExcelAware {
    /**
     * Set workbook
     * @param workbook workbook
     */
    void setWorkbook(Workbook workbook);
}
