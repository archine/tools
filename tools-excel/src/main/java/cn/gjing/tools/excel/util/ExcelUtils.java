package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.write.valid.ExcelDateValid;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import cn.gjing.tools.excel.write.valid.ExcelNumericValid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * @author Gjing
 **/
public class ExcelUtils {

    public static void addDropdownBox(ExcelDropdownBox excelDropdownBox, Workbook workbook, Sheet sheet, int firstRow, int lastRow, int index,
                                      String[] values) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint;
        if ("".equals(excelDropdownBox.link())) {
            if (values == null) {
                constraint = helper.createExplicitListConstraint(excelDropdownBox.combobox());
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
                    explicitSheetRow.createCell(index).setCellValue(values[i]);
                }
                char colOffset = (char) ('A' + index);
                constraint = helper.createFormulaListConstraint(explicitSheet.getSheetName() + "!$" + colOffset + "$1:$" + colOffset + "$" + (valueLength == 0 ? 1 : valueLength));
                workbook.setSheetHidden(workbook.getSheetIndex("explicitSheet"), true);
            }
            CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, index, index);
            DataValidation dataValidation = helper.createValidation(constraint, regions);
            dataValidation.setShowErrorBox(excelDropdownBox.showErrorBox());
            dataValidation.setErrorStyle(excelDropdownBox.rank().getRank());
            dataValidation.createErrorBox(excelDropdownBox.errorTitle(), excelDropdownBox.errorContent());
            sheet.addValidationData(dataValidation);
        }
    }

    public static void addDateValid(ExcelDateValid excelDateValid, Sheet sheet, int firstRow, int lastRow, int index) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint;
        if (sheet instanceof SXSSFSheet) {
            dvConstraint = helper.createDateConstraint(excelDateValid.operatorType().getType(), "date(" + excelDateValid.expr1().replaceAll("-", ",") + ")",
                    "date(" + excelDateValid.expr2().replaceAll("-", ",") + ")", excelDateValid.pattern());
        } else {
            dvConstraint = helper.createDateConstraint(excelDateValid.operatorType().getType(), excelDateValid.expr1(),
                    excelDateValid.expr2(), excelDateValid.pattern());
        }
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, index, index);
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(excelDateValid.showErrorBox());
        dataValidation.setErrorStyle(excelDateValid.rank().getRank());
        dataValidation.createErrorBox(excelDateValid.errorTitle(), excelDateValid.errorContent());
        sheet.addValidationData(dataValidation);
    }

    public static void addNumericValid(ExcelNumericValid excelNumericValid, Sheet sheet, int firstRow, int lastRow, int index) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint numericConstraint = helper.createNumericConstraint(excelNumericValid.validationType().getType(),
                excelNumericValid.operatorType().getType(), excelNumericValid.expr1(), "".equals(excelNumericValid.expr2()) ? null : excelNumericValid.expr2());
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, index, index);
        DataValidation dataValidation = helper.createValidation(numericConstraint, regions);
        dataValidation.setErrorStyle(excelNumericValid.rank().getRank());
        dataValidation.createErrorBox(excelNumericValid.errorTitle(), excelNumericValid.errorContent());
        dataValidation.createErrorBox(excelNumericValid.errorTitle(), excelNumericValid.errorContent());
        dataValidation.setShowErrorBox(excelNumericValid.showErrorBox());
        sheet.addValidationData(dataValidation);
    }
}
