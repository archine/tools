package cn.gjing.tools.excel.write.listener;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Merge listener
 * The end of each line is triggered
 *
 * @author Gjing
 **/
@FunctionalInterface
public interface BaseMergeWriteListener<T> extends WriteListener {
    /**
     * merge
     *
     * @param sheet      Current sheet
     * @param currentObj The Java object corresponding to the current row
     * @param row        Current row
     * @param rowIndex   Current row index
     */
    void merge(Sheet sheet, Row row, int rowIndex, T currentObj);
}
