package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.metadata.ExcelFieldProperty;
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
     * @param listeners Listener instances
     * @param sheet     Current sheet
     * @param row       Current row
     * @param cell      Current cell
     * @param index     Data indexing, depending on the row type, starts at 0
     * @param colIndex  Current cell index
     * @param rowType   Current row type
     * @param property  ExcelField property
     */
    public static void doCompleteCell(List<ExcelListener> listeners, Sheet sheet, Row row, Cell cell,
                                      ExcelFieldProperty property, int index, int colIndex, RowType rowType) {
        if (listeners != null) {
            for (ExcelListener cellListener : listeners) {
                if (cellListener instanceof ExcelCellWriteListener) {
                    ((ExcelCellWriteListener) cellListener).completeCell(sheet, row, cell, property, index, colIndex, rowType);
                }
            }
        }
    }

    /**
     * Execute write cell listener
     *
     * @param listeners Listener instances
     * @param sheet     Current sheet
     * @param row       Current row
     * @param cell      Current cell
     * @param index     Data indexing, depending on the row type, starts at 0
     * @param colIndex  Current cell index
     * @param rowType   Current row type
     * @param property  ExcelField property
     * @param value     Cell value
     * @return Cell value
     */
    public static Object doAssignmentBefore(List<ExcelListener> listeners, Sheet sheet, Row row, Cell cell,
                                            ExcelFieldProperty property, int index, int colIndex, RowType rowType, Object value) {
        if (listeners != null) {
            for (ExcelListener cellListener : listeners) {
                if (cellListener instanceof ExcelCellWriteListener) {
                    value = ((ExcelCellWriteListener) cellListener).assignmentBefore(sheet, row, cell, property, index, colIndex, rowType, value);
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
     * @return If true, the download will start
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
     * @param property       ExcelField property
     * @param styleListeners Style listeners
     */
    public static void doSetHeadStyle(List<ExcelListener> styleListeners, Row row, Cell cell, ExcelFieldProperty property, int index, int colIndex) {
        if (styleListeners != null) {
            for (ExcelListener styleListener : styleListeners) {
                if (styleListener instanceof ExcelStyleWriteListener) {
                    ((ExcelStyleWriteListener) styleListener).setHeadStyle(row, cell, property, index, colIndex);
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
     * @param property       ExcelField property
     * @param styleListeners Style listeners
     */
    public static void doSetBodyStyle(List<ExcelListener> styleListeners, Row row, Cell cell, ExcelFieldProperty property, int index, int colIndex) {
        if (styleListeners != null) {
            for (ExcelListener styleListener : styleListeners) {
                if (styleListener instanceof ExcelStyleWriteListener) {
                    ((ExcelStyleWriteListener) styleListener).setBodyStyle(row, cell, property, index, colIndex);
                }
            }
        }
    }


    /**
     * Execute read row listener
     *
     * @param rowReadListeners rowReadListeners
     * @param r                Generated Java object
     * @param row              The current read completed row
     * @param rowType          Current row type
     * @param <R>              R
     * @return Continue read next row
     */
    @SuppressWarnings("unchecked")
    public static <R> boolean doReadRow(List<ExcelListener> rowReadListeners, R r, Row row, RowType rowType) {
        boolean continueRead = true;
        if (rowReadListeners != null) {
            for (ExcelListener rowReadListener : rowReadListeners) {
                if (rowReadListener instanceof ExcelRowReadListener) {
                    ((ExcelRowReadListener<R>) rowReadListener).readRow(r, row, row.getRowNum(), rowType);
                    continueRead = ((ExcelRowReadListener<R>) rowReadListener).continueRead();
                }
            }
        }
        return continueRead;
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
     * @param cell             Current cell
     * @param rowIndex         Current row index
     * @param colIndex         Current col index
     * @param rowType          Current row type
     * @return cellValue
     */
    @SuppressWarnings("rawtypes")
    public static Object doReadCell(List<ExcelListener> rowReadListeners, Object cellValue, Cell cell, int rowIndex, int colIndex, RowType rowType) {
        if (rowReadListeners != null) {
            for (ExcelListener rowReadListener : rowReadListeners) {
                if (rowReadListener instanceof ExcelRowReadListener) {
                    cellValue = ((ExcelRowReadListener) rowReadListener).readCell(cellValue, cell, rowIndex, colIndex, rowType);
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
     * @param header             Current header
     * @param cell               Current cell
     * @param <R>                R
     * @return Return true to continue reading the cells of that row and retain the object generated by the current row,
     *         Returning false immediately stops reading the current row and starts the next row,
     *         and deletes the objects generated by the current row
     */
    @SuppressWarnings("unchecked")
    public static <R> boolean doReadEmpty(List<ExcelListener> emptyReadListeners, R r, String header, Cell cell) {
        boolean isSave = false;
        if (emptyReadListeners != null) {
            for (ExcelListener emptyReadListener : emptyReadListeners) {
                if (emptyReadListener instanceof ExcelEmptyReadListener) {
                    isSave = ((ExcelEmptyReadListener<R>) emptyReadListener).readEmpty(r, header, cell);
                }
            }
        }
        return isSave;
    }
}
