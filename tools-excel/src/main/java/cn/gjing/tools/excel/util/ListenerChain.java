package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.ExcelField;
import cn.gjing.tools.excel.metadata.RowType;
import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import cn.gjing.tools.excel.read.listener.ExcelEmptyReadListener;
import cn.gjing.tools.excel.read.listener.ExcelRowReadListener;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.listener.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Excel listener link call tool
 *
 * @author Gjing
 **/
public final class ListenerChain {

    /**
     * Execute write cell listener
     *
     * @param listeners  Listener instances
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Data indexing, depending on the row type, starts at 0
     * @param colIndex   Current cell index
     * @param rowType    Current row type
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     */
    public static void doCompleteCell(List<ExcelListener> listeners, Sheet sheet, Row row, Cell cell,
                                      ExcelField excelField, Field field, int index, int colIndex, RowType rowType) {
        if (listeners != null) {
            for (ExcelListener cellListener : listeners) {
                if (cellListener instanceof ExcelCellWriteListener) {
                    ((ExcelCellWriteListener) cellListener).completeCell(sheet, row, cell, excelField, field, index, colIndex, rowType);
                }
            }
        }
    }

    /**
     * Execute write cell listener
     *
     * @param listeners  Listener instances
     * @param sheet      Current sheet
     * @param row        Current row
     * @param cell       Current cell
     * @param index      Data indexing, depending on the row type, starts at 0
     * @param colIndex   Current cell index
     * @param rowType    Current row type
     * @param excelField ExcelField annotation of current field
     * @param field      Current field
     * @param value      Cell value
     * @return Cell value
     */
    public static Object doAssignmentBefore(List<ExcelListener> listeners, Sheet sheet, Row row, Cell cell,
                                            ExcelField excelField, Field field, int index, int colIndex, RowType rowType, Object value) {
        if (listeners != null) {
            for (ExcelListener cellListener : listeners) {
                if (cellListener instanceof ExcelCellWriteListener) {
                    value = ((ExcelCellWriteListener) cellListener).assignmentBefore(sheet, row, cell, excelField, field, index, colIndex, rowType, value);
                }
            }
        }
        return value;
    }

    /**
     * Before you create a row
     *
     * @param sheet     Current sheet
     * @param index     Data indexing, depending on the row type, starts at 0
     * @param rowType   Current row type
     * @param listeners Listener instances
     */
    public static void doCreateRowBefore(List<ExcelListener> listeners, Sheet sheet, int index, RowType rowType) {
        if (listeners != null) {
            for (ExcelListener rowListener : listeners) {
                if (rowListener instanceof ExcelRowWriteListener) {
                    ((ExcelRowWriteListener) rowListener).createBefore(sheet, index, rowType);
                }
            }
        }
    }

    /**
     * Execute write row listener
     *
     * @param listeners Listener instances
     * @param sheet     Current sheet
     * @param row       Create the finished row
     * @param obj       Current java object
     * @param index     Data indexing, depending on the row type, starts at 0
     * @param rowType   Current row type
     */
    public static void doCompleteRow(List<ExcelListener> listeners, Sheet sheet, Row row, Object obj, int index, RowType rowType) {
        if (listeners != null) {
            for (ExcelListener rowListener : listeners) {
                if (rowListener instanceof ExcelRowWriteListener) {
                    ((ExcelRowWriteListener) rowListener).completeRow(sheet, row, obj, index, rowType);
                }
            }
        }
    }

    /**
     * Execute write sheet listener
     *
     * @param sheet     Current sheet
     * @param listeners Sheet listeners
     */
    public static void doCompleteSheet(List<ExcelListener> listeners, Sheet sheet) {
        if (listeners != null) {
            for (ExcelListener sheetListener : listeners) {
                if (sheetListener instanceof ExcelSheetWriteListener) {
                    ((ExcelSheetWriteListener) sheetListener).completeSheet(sheet);
                }
            }
        }
    }

    /**
     * Execute write workbook listener
     *
     * @param workbook          Current workbook
     * @param workbookListeners Workbook listeners
     * @return  If true, the download will start
     */
    public static boolean doWorkbookFlushBefore(List<ExcelListener> workbookListeners, Workbook workbook) {
        boolean continueDownload = true;
        if (workbookListeners != null) {
            for (ExcelListener workbookListener : workbookListeners) {
                if (workbookListener instanceof ExcelWorkbookWriteListener) {
                    continueDownload = ((ExcelWorkbookWriteListener) workbookListener).flushBefore(workbook);
                }
            }
        }
        return continueDownload;
    }

    /**
     * Set excel big title style
     *
     * @param cell           Current cell
     * @param bigTitle       Bit title
     * @param styleListeners Style listeners
     */
    public static void doSetTitleStyle(List<ExcelListener> styleListeners, BigTitle bigTitle, Cell cell) {
        if (styleListeners != null) {
            for (ExcelListener styleListener : styleListeners) {
                if (styleListener instanceof ExcelStyleWriteListener) {
                    ((ExcelStyleWriteListener) styleListener).setTitleStyle(bigTitle, cell);
                }
            }
        }
    }

    /**
     * Set excel head style
     *
     * @param row            Current row
     * @param cell           Current cell
     * @param index          Line index, index type according to isHead
     * @param colIndex       cell index
     * @param excelField     ExcelField annotation of current field
     * @param field          Current field
     * @param styleListeners Style listeners
     */
    public static void doSetHeadStyle(List<ExcelListener> styleListeners, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        if (styleListeners != null) {
            for (ExcelListener styleListener : styleListeners) {
                if (styleListener instanceof ExcelStyleWriteListener) {
                    ((ExcelStyleWriteListener) styleListener).setHeadStyle(row, cell, excelField, field, index, colIndex);
                }
            }
        }
    }

    /**
     * Set excel head style
     *
     * @param row            Current row
     * @param cell           Current cell
     * @param index          Line index, index type according to isHead
     * @param colIndex       cell index
     * @param excelField     ExcelField annotation of current field
     * @param field          Current field
     * @param styleListeners Style listeners
     */
    public static void doSetBodyStyle(List<ExcelListener> styleListeners, Row row, Cell cell, ExcelField excelField, Field field, int index, int colIndex) {
        if (styleListeners != null) {
            for (ExcelListener styleListener : styleListeners) {
                if (styleListener instanceof ExcelStyleWriteListener) {
                    ((ExcelStyleWriteListener) styleListener).setBodyStyle(row, cell, excelField, field, index, colIndex);
                }
            }
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
    public static <R> boolean doReadRow(List<ExcelListener> rowReadListeners, R r, List<?> otherValues, int rowIndex, RowType rowType) {
        boolean stop = false;
        if (rowReadListeners != null) {
            for (ExcelListener rowReadListener : rowReadListeners) {
                if (rowReadListener instanceof ExcelRowReadListener) {
                    stop = ((ExcelRowReadListener<R>) rowReadListener).readRow(r, otherValues, rowIndex, rowType);
                }
            }
        }
        return stop;
    }

    /**
     * Before you start reading the data
     *
     * @param listeners Excel read listeners
     * @param <R>       The generic
     */
    @SuppressWarnings("unchecked")
    public static <R> void doReadBefore(List<ExcelListener> listeners) {
        if (listeners != null) {
            for (ExcelListener listener : listeners) {
                if (listener instanceof ExcelRowReadListener) {
                    ((ExcelRowReadListener<R>) listener).readBefore();
                }
            }
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
    public static Object doReadCell(List<ExcelListener> rowReadListeners, Object cellValue, Field field, int rowIndex, int colIndex, RowType rowType) {
        if (rowReadListeners != null) {
            for (ExcelListener rowReadListener : rowReadListeners) {
                if (rowReadListener instanceof ExcelRowReadListener) {
                    cellValue = ((ExcelRowReadListener) rowReadListener).readCell(cellValue, field, rowIndex, colIndex, rowType);
                }
            }
        }
        return cellValue;
    }

    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param <R>              R
     */
    @SuppressWarnings("unchecked")
    public static <R> void doReadFinish(List<ExcelListener> rowReadListeners) {
        if (rowReadListeners != null) {
            for (ExcelListener rowReadListener : rowReadListeners) {
                if (rowReadListener instanceof ExcelRowReadListener) {
                    ((ExcelRowReadListener<R>) rowReadListener).readFinish();
                }
            }
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
    public static <R> boolean doReadEmpty(List<ExcelListener> emptyReadListeners, R r, Field field, ExcelField excelField, int rowIndex, int colIndex) {
        boolean isSave = false;
        if (emptyReadListeners != null) {
            for (ExcelListener emptyReadListener : emptyReadListeners) {
                if (emptyReadListener instanceof ExcelEmptyReadListener) {
                    isSave = ((ExcelEmptyReadListener<R>) emptyReadListener).readEmpty(r, field, excelField, rowIndex, colIndex);
                }
            }
        }
        return isSave;
    }
}
