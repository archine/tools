package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.write.merge.ExcelOldCellModel;
import cn.gjing.tools.excel.write.merge.ExcelOldRowModel;
import cn.gjing.tools.excel.write.valid.OperatorType;
import cn.gjing.tools.excel.write.valid.Rank;
import cn.gjing.tools.excel.write.valid.ValidType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @author Gjing
 **/
public final class ExcelUtils {

    /**
     * Set cell value
     *
     * @param cell  Current cell
     * @param value Attribute values
     * @param field Current field
     */
    public static void setCellValue(Cell cell, Object value, Field field) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value instanceof Number) {
            cell.setCellValue(Double.parseDouble(value.toString()));
            return;
        }
        if (value instanceof Enum) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            return;
        }
        if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            return;
        }
        if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            return;
        }
        throw new IllegalArgumentException("Unsupported data type, you can use a data converter " + field.getName() + " " + value);
    }

    /**
     * Add a dropdown box when export
     *
     * @param combobox     Dropdown box content of 25 or less
     * @param showErrorBox Whether show error box
     * @param errorBoxRank Error box rank
     * @param errorTitle   Error box title
     * @param errorContent Error box value
     * @param workbook     Current workbook
     * @param sheet        Current sheet
     * @param firstRow     Start row
     * @param lastRow      End row
     * @param colIndex     Column index
     * @param values       The dropdown box can be large, but if it's version 07, it's limited by the window size in the Excel annotation{@link Excel}
     */
    public static void addDropdownBox(String[] combobox, boolean showErrorBox, Rank errorBoxRank, String errorTitle, String errorContent,
                                      Workbook workbook, Sheet sheet, int firstRow, int lastRow, int colIndex, String[] values) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint;
        if (values == null) {
            constraint = helper.createExplicitListConstraint(combobox);
        } else {
            Sheet explicitSheet = workbook.getSheet("explicitSheet");
            if (explicitSheet == null) {
                explicitSheet = workbook.createSheet("explicitSheet");
            }
            int valueLength = values.length;
            for (int i = 0; i < valueLength; i++) {
                Row explicitSheetRow = explicitSheet.getRow(i);
                if (explicitSheetRow == null) {
                    explicitSheetRow = explicitSheet.createRow(i);
                }
                explicitSheetRow.createCell(colIndex).setCellValue(values[i]);
            }
            char colOffset = (char) ('A' + colIndex);
            constraint = helper.createFormulaListConstraint(explicitSheet.getSheetName() + "!$" + colOffset + "$1:$" + colOffset + "$" + (valueLength == 0 ? 1 : valueLength));
            workbook.setSheetHidden(workbook.getSheetIndex("explicitSheet"), true);
        }
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.setShowErrorBox(showErrorBox);
        dataValidation.setErrorStyle(errorBoxRank.getRank());
        dataValidation.createErrorBox(errorTitle, errorContent);
        sheet.addValidationData(dataValidation);
    }

    /**
     * Add date validation when export
     *
     * @param operatorType operatorType
     * @param expr1        Date expression 1, such as: 2019-12-12
     * @param expr2        Date expression 2(Only operation types between and notBetween are required)，such as：2019-12-24
     * @param pattern      Date pattern
     * @param sheet        Current sheet
     * @param firstRow     Start row
     * @param lastRow      End row
     * @param colIndex     Column index
     * @param showErrorBox Whether show error box
     * @param errorBoxRank Error box rank
     * @param errorTitle   Error box title
     * @param errorContent Error box value
     * @param showTip      Whether show tip
     * @param tipContent   Tip content
     * @param tipTitle     Tip title
     */
    public static void addDateValid(OperatorType operatorType, String expr1, String expr2, String pattern, Sheet sheet, int firstRow, int lastRow,
                                    int colIndex, boolean showErrorBox, Rank errorBoxRank, String errorTitle, String errorContent, boolean showTip,
                                    String tipTitle, String tipContent) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint;
        if (sheet instanceof SXSSFSheet) {
            dvConstraint = helper.createDateConstraint(operatorType.getType(), "date(" + expr1.replaceAll("-", ",") + ")",
                    "date(" + expr2.replaceAll("-", ",") + ")", pattern);
        } else {
            dvConstraint = helper.createDateConstraint(operatorType.getType(), expr1, expr2, pattern);
        }
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(showErrorBox);
        dataValidation.setErrorStyle(errorBoxRank.getRank());
        dataValidation.createErrorBox(errorTitle, errorContent);
        if (showTip) {
            dataValidation.createPromptBox(tipTitle, tipContent);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * Add numeric validation when export,
     *
     * @param validType    validType
     * @param operatorType operatorType
     * @param expr1        Expression 1, such as: 1
     * @param expr2        Expression 2(Only operation types between and notBetween are required)，such as：2
     * @param sheet        Current sheet
     * @param firstRow     Start row
     * @param lastRow      End row
     * @param colIndex     Column index
     * @param showErrorBox Whether show error box
     * @param errorBoxRank Error box rank
     * @param errorTitle   Error box title
     * @param errorContent Error box value
     * @param showTip      Whether show tip
     * @param tipContent   Tip content
     * @param tipTitle     Tip title
     */
    public static void addNumericValid(ValidType validType, OperatorType operatorType, String expr1, String expr2, Sheet sheet, int firstRow, int lastRow,
                                       int colIndex, boolean showErrorBox, Rank errorBoxRank, String errorTitle, String errorContent, boolean showTip,
                                       String tipTitle, String tipContent) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint numericConstraint = helper.createNumericConstraint(validType.getType(),
                operatorType.getType(), expr1, "".equals(expr2) ? null : expr2);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation dataValidation = helper.createValidation(numericConstraint, regions);
        dataValidation.setShowErrorBox(showErrorBox);
        dataValidation.setErrorStyle(errorBoxRank.getRank());
        dataValidation.createErrorBox(errorTitle, errorContent);
        if (showTip) {
            dataValidation.createPromptBox(tipTitle, tipContent);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * Vertical merge
     * OldRowModelMap should be global, with minimum partitioning to each export
     *
     * @param oldRowModelMap  oldRowModelMap
     * @param sheet           Current sheet
     * @param row             Current row
     * @param index           Line index, index type according to isHead
     * @param dataSize        Excel head data size or body data size,
     * @param colIndex        Current col index
     * @param cellValue       Current cell value
     * @param allowMergeEmpty Whether is allow merge empty
     * @param toMerge         Whether is to merge
     */
    public static void mergeY(Map<Integer, ExcelOldRowModel> oldRowModelMap, Sheet sheet, Row row, boolean allowMergeEmpty, int index, int colIndex,
                              Object cellValue, int dataSize, boolean toMerge) {
        if (index == 0) {
            oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            return;
        }
        ExcelOldRowModel excelOldRowModel = oldRowModelMap.get(colIndex);
        if (toMerge) {
            if (ParamUtils.equals(cellValue, excelOldRowModel.getOldRowCellValue(), allowMergeEmpty)) {
                if (index == dataSize - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum(), colIndex, colIndex));
                }
                return;
            }
            if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
                sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            }
            if (index != dataSize - 1) {
                oldRowModelMap.put(colIndex, new ExcelOldRowModel(cellValue, row.getRowNum()));
            }
            return;
        }
        if (excelOldRowModel.getOldRowIndex() + 1 < row.getRowNum()) {
            sheet.addMergedRegion(new CellRangeAddress(excelOldRowModel.getOldRowIndex(), row.getRowNum() - 1, colIndex, colIndex));
            excelOldRowModel.setOldRowCellValue(cellValue);
            excelOldRowModel.setOldRowIndex(row.getRowNum());
            oldRowModelMap.put(colIndex, excelOldRowModel);
        }
    }

    /**
     * Horizontal merge
     * You need to make sure that the ExcelOldCellModel object is a singleton on each line, or is written out at once
     *
     * @param oldCellModel    oldCellModel
     * @param sheet           Current sheet
     * @param row             Current row
     * @param colIndex        Current col index
     * @param cellValue       Current cell value
     * @param colSize         Total col
     * @param allowMergeEmpty Whether is allow merge empty
     * @param toMerge         Whether is to merge
     */
    public static void mergeX(ExcelOldCellModel oldCellModel, Sheet sheet, Row row, boolean allowMergeEmpty, int colIndex, Object cellValue,
                              int colSize, boolean toMerge) {
        if (colIndex == 0) {
            oldCellModel.setLastCellValue(cellValue);
            oldCellModel.setLastCellIndex(colIndex);
            return;
        }
        if (toMerge) {
            if (ParamUtils.equals(cellValue, oldCellModel.getLastCellValue(), allowMergeEmpty)) {
                if (colIndex == colSize - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), oldCellModel.getLastCellIndex(), colIndex));
                }
                return;
            }
            if (oldCellModel.getLastCellIndex() + 1 < colIndex) {
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), oldCellModel.getLastCellIndex(), colIndex - 1));
            }
            if (colIndex != colSize - 1) {
                oldCellModel.setLastCellValue(cellValue);
                oldCellModel.setLastCellIndex(colIndex);
            }
            return;
        }
        if (oldCellModel.getLastCellIndex() + 1 < colIndex) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), oldCellModel.getLastCellIndex(), colIndex - 1));
            oldCellModel.setLastCellIndex(colIndex);
            oldCellModel.setLastCellValue(cellValue);
        }
    }

    /**
     * Create a sum expression
     *
     * @param firstColIndex Which column start
     * @param firstRowIndex Which row start
     * @param lastColIndex  Which column end
     * @param lastRowIndex  Which row end
     * @return expression
     */
    public static String createSumFormula(int firstColIndex, int firstRowIndex, int lastColIndex, int lastRowIndex) {
        return "SUM(" + (char) ('A' + firstColIndex) + firstRowIndex + ":" + (char) ('A' + lastColIndex) + lastRowIndex + ")";
    }
}
