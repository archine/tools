package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.read.ExcelReaderContext;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gjing
 **/
@FunctionalInterface
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
     * @param cellValue Current cell value
     * @param field     Current field
     * @param rowIndex  Current row index
     * @param colIndex  Current col index
     * @param isHead    Whether is excel header
     * @return cellValue
     */
    default Object readCell(Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead){return cellValue;}

    /**
     * All data imported
     *
     * @param context Excel reader context
     */
    default void readFinish(ExcelReaderContext<R> context) {
    }
}
