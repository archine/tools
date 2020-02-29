package cn.gjing.tools.excel;

import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

/**
 * @author Gjing
 **/
public interface DataConvert<T> {
    /**
     * Convert to an entity field
     *
     * @param value      Excel cell value
     * @param excelField ExcelField
     * @param field      Current field
     * @return T
     */
    T toEntityAttribute(Object value, Field field, ExcelField excelField);

    /**
     * Convert to excel cell value
     * @param cell Excel cell
     * @param value The value of the current field
     * @param field Current field
     * @param excelField ExcelField
     */
    void toExcelAttribute(Cell cell, Object value, Field field, ExcelField excelField);
}
