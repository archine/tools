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
     * @param r           Generated Java object , Null when not in the body
     * @param rowIndex    The index of the current row
     * @param isHead      Whether is excel header
     * @param isBody      Whether is excel body
     * @param otherValues Except for the content of the body
     * @return Whether to stop reading
     */
    boolean readRow(R r, List<Object> otherValues, int rowIndex, boolean isHead, boolean isBody);

    /**
     * Read each cell successfully
     *
     * @param cellValue Current cell value
     * @param field     Current field
     * @param rowIndex  Current row index
     * @param colIndex  Current col index
     * @param isHead    Whether is excel header
     * @param isBody    Whether is excel body
     * @return cellValue
     */
    default Object readCell(Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead, boolean isBody) {
        return cellValue;
    }

    /**
     * End of the import
     *
     * @param context Excel reader context
     */
    default void readFinish(ExcelReaderContext<R> context) {
    }
}
