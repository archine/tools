package cn.gjing.tools.excel.read.listener;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gjing
 **/
public interface ExcelRowReadListener<R> extends ExcelReadListener {
    /**
     * Read each row successfully
     *
     * @param r         Generated Java object
     * @param rowIndex  The index of the current row
     * @param isHead    Whether is excel head
     * @param hasNext   Whether has next row
     * @param headNames All the table headers of the current row
     * @return Whether to stop reading
     */
    boolean readRow(R r, List<String> headNames, int rowIndex, boolean isHead, boolean hasNext);

    /**
     * Read each cell successfully
     *
     * @param r         Current Java object
     * @param cellValue Current cell value
     * @param field     Current field
     * @param rowIndex  Current row index
     * @param colIndex  Current col index
     * @param isHead    Whether is excel header
     */
    void readCell(R r, Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead);
}
