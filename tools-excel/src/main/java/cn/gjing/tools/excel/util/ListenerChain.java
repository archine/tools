package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelReadListener;
import cn.gjing.tools.excel.metadata.listener.ExcelWriteListener;
import cn.gjing.tools.excel.read.ExcelReaderContext;
import cn.gjing.tools.excel.read.listener.ExcelEmptyReadListener;
import cn.gjing.tools.excel.read.listener.ExcelResultReadListener;
import cn.gjing.tools.excel.read.listener.ExcelRowReadListener;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Excel listener link call tool
 *
 * @author Gjing
 **/
public final class ListenerChain {

    /**
     * Execute write cell listener
     *
     * @param listenerCache Write listener cache
     * @param sheet         Current sheet
     * @param row           Current row
     * @param cell          Current cell
     * @param index         Data indexing, depending on the row type, starts at 0
     * @param colIndex      Current cell index
     * @param rowType       Current row type
     * @param excelField    ExcelField annotation of current field
     * @param field         Current field
     */
    public static void doCompleteCell(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> listenerCache, Sheet sheet, Row row, Cell cell,
                                      ExcelField excelField, Field field, int index, int colIndex, RowType rowType) {
        List<ExcelWriteListener> cellListeners = listenerCache.get(ExcelCellWriteListener.class);
        if (cellListeners != null) {
            cellListeners.forEach(e -> ((ExcelCellWriteListener) e).completeCell(sheet, row, cell, excelField, field, index, colIndex, rowType));
        }
    }

    /**
     * Execute write cell listener
     *
     * @param listenerCache Write listener cache
     * @param sheet         Current sheet
     * @param row           Current row
     * @param cell          Current cell
     * @param index         Data indexing, depending on the row type, starts at 0
     * @param colIndex      Current cell index
     * @param rowType       Current row type
     * @param excelField    ExcelField annotation of current field
     * @param field         Current field
     * @param value         Cell value
     * @return Cell value
     */
    public static Object doAssignmentBefore(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> listenerCache, Sheet sheet, Row row, Cell cell,
                                            ExcelField excelField, Field field, int index, int colIndex, RowType rowType, Object value) {
        List<ExcelWriteListener> cellListeners = listenerCache.get(ExcelCellWriteListener.class);
        if (cellListeners != null) {
            for (ExcelWriteListener cellListener : cellListeners) {
                value = ((ExcelCellWriteListener) cellListener).assignmentBefore(sheet, row, cell, excelField, field, index, colIndex, rowType, value);
            }
        }
        return value;
    }

    /**
     * Before you create a row
     *
     * @param sheet         Current sheet
     * @param index         Data indexing, depending on the row type, starts at 0
     * @param rowType       Current row type
     * @param listenerCache Write listener cache
     */
    public static void doCreateRowBefore(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> listenerCache, Sheet sheet, int index, RowType rowType) {
        List<ExcelWriteListener> rowListeners = listenerCache.get(ExcelRowWriteListener.class);
        if (rowListeners != null) {
            rowListeners.forEach(e -> ((ExcelRowWriteListener) e).createBefore(sheet, index, rowType));
        }
    }

    /**
     * Execute write row listener
     *
     * @param listenerCache Write listener cache
     * @param sheet         Current sheet
     * @param row           Create the finished row
     * @param obj           Current java object
     * @param index         Data indexing, depending on the row type, starts at 0
     * @param rowType       Current row type
     */
    public static void doCompleteRow(Map<Class<? extends ExcelWriteListener>, List<ExcelWriteListener>> listenerCache, Sheet sheet, Row row, Object obj, int index, RowType rowType) {
        List<ExcelWriteListener> rowListeners = listenerCache.get(ExcelRowWriteListener.class);
        if (rowListeners != null) {
            rowListeners.forEach(e -> ((ExcelRowWriteListener) e).completeRow(sheet, row, obj, index, rowType));
        }
    }

    /**
     * Execute write sheet listener
     *
     * @param sheet          Current sheet
     * @param sheetListeners Sheet listeners
     */
    public static void doCompleteSheet(List<ExcelWriteListener> sheetListeners, Sheet sheet) {
        if (sheetListeners != null) {
            sheetListeners.forEach(e -> ((ExcelSheetWriteListener) e).completeSheet(sheet));
        }
    }

    /**
     * Execute write workbook listener
     *
     * @param workbook          Current workbook
     * @param workbookListeners Workbook listeners
     */
    public static void doWorkbookFlushBefore(List<ExcelWriteListener> workbookListeners, Workbook workbook) {
        if (workbookListeners != null) {
            workbookListeners.forEach(e -> ((ExcelWorkbookWriteListener) e).flushBefore(workbook));
        }
    }

    /**
     * Workbook has created
     *
     * @param workbook          Current workbook
     * @param workbookListeners Workbook listeners
     */
    public static void doWorkbookCreated(List<ExcelWriteListener> workbookListeners, Workbook workbook) {
        if (workbookListeners != null) {
            workbookListeners.forEach(e -> ((ExcelWorkbookWriteListener) e).completeWorkbook(workbook));
        }
    }

    /**
     * Set excel big title style
     *
     * @param cell           Current cell
     * @param bigTitle       Bit title
     * @param styleListeners Style listeners
     */
    public static void doSetTitleStyle(List<ExcelWriteListener> styleListeners, BigTitle bigTitle, Cell cell) {
        if (styleListeners != null) {
            styleListeners.forEach(e -> ((ExcelStyleWriteListener) e).setTitleStyle(bigTitle, cell));
        }
    }

    /**
     * Set excel head style
     *
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead
     * @param colIndex   cell index
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     * @param styleListeners Style listeners
     */
    public static void doSetHeadStyle(List<ExcelWriteListener> styleListeners, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        if (styleListeners != null) {
            styleListeners.forEach(e -> ((ExcelStyleWriteListener) e).setHeadStyle(row, cell, excelField, field, index, colIndex));
        }
    }

    /**
     * Set excel head style
     *
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Line index, index type according to isHead
     * @param colIndex   cell index
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     * @param styleListeners Style listeners
     */
    public static void doSetBodyStyle(List<ExcelWriteListener> styleListeners, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        if (styleListeners != null) {
            styleListeners.forEach(e -> ((ExcelStyleWriteListener) e).setBodyStyle(row, cell, excelField, field, index, colIndex));
        }
    }


    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param r                Generated Java object
     * @param rowIndex         The index of the current row
     * @param otherValues      Except for the content of the body
     * @param rowType          Current row type
     * @param <R>              R
     * @return Whether to stop reading
     */
    @SuppressWarnings("unchecked")
    public static <R> boolean doReadRow(List<ExcelReadListener> rowReadListeners, R r, List<?> otherValues, int rowIndex, RowType rowType) {
        boolean stop = false;
        if (rowReadListeners != null) {
            for (ExcelReadListener rowReadListener : rowReadListeners) {
                stop = ((ExcelRowReadListener<R>) rowReadListener).readRow(r, otherValues, rowIndex, rowType);
            }
        }
        return stop;
    }

    /**
     * Before you start reading the data
     *
     * @param listeners Excel read listeners
     * @param context   Excel reader context
     * @param <R>       The generic
     */
    @SuppressWarnings("unchecked")
    public static <R> void doReadBefore(List<ExcelReadListener> listeners, ExcelReaderContext<R> context) {
        if (listeners != null) {
            listeners.forEach(e -> ((ExcelRowReadListener<R>) e).readBefore(context));
        }
    }

    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param cellValue        Current cell value
     * @param field            Current field
     * @param rowIndex         Current row index
     * @param colIndex         Current col index
     * @param rowType          Current row type
     * @return cellValue
     */
    @SuppressWarnings("rawtypes")
    public static Object doReadCell(List<ExcelReadListener> rowReadListeners, Object cellValue, Field field, int rowIndex, int colIndex, RowType rowType) {
        if (rowReadListeners != null) {
            for (ExcelReadListener rowReadListener : rowReadListeners) {
                cellValue = ((ExcelRowReadListener) rowReadListener).readCell(cellValue, field, rowIndex, colIndex, rowType);
            }
        }
        return cellValue;
    }

    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param context          Excel reader context
     * @param <R>              R
     */
    @SuppressWarnings("unchecked")
    public static <R> void doReadFinish(List<ExcelReadListener> rowReadListeners, ExcelReaderContext<R> context) {
        if (rowReadListeners != null) {
            rowReadListeners.forEach(e -> ((ExcelRowReadListener<R>) e).readFinish(context));
        }
    }

    /**
     * Execute read empty listener
     *
     * @param emptyReadListeners emptyReadListeners
     * @param r                  Current Java object
     * @param field              Current field
     * @param excelField         ExcelField annotation on that field
     * @param rowIndex           The index of the current row
     * @param colIndex           The index of the current col
     * @param <R>                R
     * @return Whether to save this data
     */
    @SuppressWarnings("unchecked")
    public static <R> boolean doReadEmpty(List<ExcelReadListener> emptyReadListeners, R r, Field field, ExcelField excelField, int rowIndex, int colIndex) {
        boolean isSave = false;
        if (emptyReadListeners != null) {
            for (ExcelReadListener emptyReadListener : emptyReadListeners) {
                isSave = ((ExcelEmptyReadListener<R>) emptyReadListener).readEmpty(r, field, excelField, rowIndex, colIndex);
            }
        }
        return isSave;
    }

    /**
     * Execute result notify listener
     *
     * @param resultReadListener resultReadListener
     * @param data               Import all the Java objects generated after success
     * @param <R>                R
     */
    public static <R> void doResultNotify(ExcelResultReadListener<R> resultReadListener, List<R> data) {
        if (data != null) {
            resultReadListener.notify(data);
        }
    }
}
