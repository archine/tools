package cn.gjing.tools.excel.read.listener;

import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;

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
     * @param rowType     Current row type
     * @param otherValues Except for the content of the body
     * @return Whether to stop reading, true is stop
     */
    boolean readRow(R r, List<?> otherValues, int rowIndex, RowType rowType);

    /**
     * Read each cell successfully
     *
     * @param cellValue Current cell value
     * @param field     Current field
     * @param rowIndex  Current row index
     * @param colIndex  Current col index
     * @param rowType   Current row type
     * @return cellValue
     */
    default Object readCell(Object cellValue, Field field, int rowIndex, int colIndex, RowType rowType) {
        return cellValue;
    }

    /**
     * File data read finished
     */
    default void readFinish() {
    }

    /**
     * Before you start reading the data
     */
    default void readBefore() {
    }
}
