package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.Excel;
import cn.gjing.tools.excel.write.valid.OperatorType;
import cn.gjing.tools.excel.write.valid.Rank;
import cn.gjing.tools.excel.write.valid.ValidType;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Gjing
 **/
public final class ExcelUtils {

    /**
     * Set cell value
     *
     * @param cell  Current cell
     * @param value Attribute values
     */
    public static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
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
        throw new IllegalArgumentException("Unsupported data type, you can use a data converter " + value);
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
     * Add repeat validation when export
     *
     * @param sheet          Current sheet
     * @param firstRow       Start row
     * @param lastRow        End row
     * @param colIndex       Column index
     * @param showErrorBox   Whether show error box
     * @param errorBoxRank   Error box rank
     * @param errorTitle     Error box title
     * @param errorContent   Error box value
     * @param longTextNumber Whether is long text number
     */
    public static void addRepeatValid(Sheet sheet, int firstRow, int lastRow, int colIndex, boolean showErrorBox, Rank errorBoxRank,
                                      String errorTitle, String errorContent, boolean longTextNumber) {

        int startRow;
        int startCol;
        if (sheet instanceof HSSFSheet) {
            startRow = firstRow == 1 ? 1 : (firstRow - sheet.getLastRowNum());
            startCol = 0;
        } else {
            startRow = firstRow + 1;
            startCol = colIndex;
        }
        String index = ParamUtils.numberToEn(startCol);
        if (longTextNumber) {
            addCustomValid("COUNTIF(" + index + ":" + index + "," + index + startRow + "&\"*\")<2", sheet, firstRow, lastRow, colIndex, showErrorBox,
                    errorBoxRank, errorTitle, errorContent);
        } else {
            addCustomValid("COUNTIF(" + index + ":" + index + "," + index + startRow + ")<2", sheet, firstRow, lastRow, colIndex, showErrorBox,
                    errorBoxRank, errorTitle, errorContent);
        }
    }

    /**
     * Add custom validation when export
     *
     * @param formula      Check formula
     * @param sheet        Current sheet
     * @param firstRow     Start row
     * @param lastRow      End row
     * @param colIndex     Column index
     * @param showErrorBox Whether show error box
     * @param errorBoxRank Error box rank
     * @param errorTitle   Error box title
     * @param errorContent Error box value
     */
    public static void addCustomValid(String formula, Sheet sheet, int firstRow, int lastRow, int colIndex, boolean showErrorBox, Rank errorBoxRank,
                                      String errorTitle, String errorContent) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint customConstraint = helper.createCustomConstraint(formula);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation validation = helper.createValidation(customConstraint, regions);
        validation.setShowErrorBox(showErrorBox);
        validation.setErrorStyle(errorBoxRank.getRank());
        validation.createErrorBox(errorTitle, errorContent);
        sheet.addValidationData(validation);
    }

    /**
     * Merge cells
     *
     * @param sheet    Current sheet
     * @param firstCol First column index
     * @param lastCol  last column index
     * @param firstRow First row index
     * @param LastRow  Last row index
     */
    public static void merge(Sheet sheet, int firstCol, int lastCol, int firstRow, int LastRow) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, LastRow, firstCol, lastCol));
    }

    /**
     * Get cell range address object
     *
     * @param sheet Current sheet
     * @param index address index, start of 0
     * @return CellRangeAddress
     */
    public static CellRangeAddress getCellRangeAddress(Sheet sheet, int index) {
        return sheet.getMergedRegion(index);
    }

    /**
     * Create a sum formula
     *
     * @param firstColIndex Which column start
     * @param firstRowIndex Which row start
     * @param lastColIndex  Which column end
     * @param lastRowIndex  Which row end
     * @return expression
     */
    public static String createSumFormula(int firstColIndex, int firstRowIndex, int lastColIndex, int lastRowIndex) {
        return createFormula("SUM", firstColIndex, firstRowIndex, lastColIndex, lastRowIndex);
    }

    /**
     * Create a formula
     *
     * @param suffix        Formula suffix
     * @param firstColIndex Which column start
     * @param firstRowIndex Which row start
     * @param lastColIndex  Which column end
     * @param lastRowIndex  Which row end
     * @return expression
     */
    public static String createFormula(String suffix, int firstColIndex, int firstRowIndex, int lastColIndex, int lastRowIndex) {
        if (firstRowIndex == lastRowIndex) {
            return suffix + "(" + ParamUtils.createFormulaX(firstColIndex, firstRowIndex, lastColIndex) + ")";
        }
        if (firstColIndex == lastColIndex) {
            return suffix + "(" + ParamUtils.createFormulaY(firstColIndex, firstRowIndex, lastRowIndex) + ")";
        }
        throw new IllegalArgumentException();
    }

    /**
     * Create a font
     *
     * @param workbook workbook
     * @return Font
     */
    public static Font createFont(Workbook workbook) {
        return workbook.createFont();
    }

    /**
     * Create rich text string
     *
     * @param workbook workbook
     * @param content  Rich text content
     * @return RichTextString
     */
    public static RichTextString createRichText(Workbook workbook, String content) {
        return workbook.getCreationHelper().createRichTextString(content);
    }

    /**
     * Create hyper link
     *
     * @param workbook workbook
     * @param type     link type
     * @return Hyperlink
     */
    public static Hyperlink createLink(Workbook workbook, HyperlinkType type) {
        return workbook.getCreationHelper().createHyperlink(type);
    }

    /**
     * Determines whether a cell has been merged
     *
     * @param sheet  Current sheet
     * @param row    Current row number
     * @param column current column number
     * @return True is merged
     */
    public static boolean isMerge(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }
}
