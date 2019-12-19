package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * Default dropdown box verifier
 *
 * @author Gjing
 **/
public class DefaultExplicitValidation implements ExcelValidation {

    @Override
    public void valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int firstCol, int lastCol, int validIndex, String[] values) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint;
        if (values == null) {
            constraint = helper.createExplicitListConstraint(explicitValid.combobox());
        } else {
            Sheet explicitSheet = workbook.createSheet("explicitSheet" + validIndex);
            for (int i = 0; i < values.length; i++) {
                explicitSheet.createRow(i).createCell(0).setCellValue(values[i]);
            }
            constraint = helper.createFormulaListConstraint(explicitSheet.getSheetName() + "!$A$1:$A$" + values.length);
            workbook.setSheetHidden(validIndex + 1, true);
        }
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, explicitValid.boxLastRow() == 0 ? firstRow : explicitValid.boxLastRow() + firstRow,
                firstCol, lastCol);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.setShowErrorBox(explicitValid.showErrorBox());
        dataValidation.setShowPromptBox(explicitValid.showPromptBox());
        dataValidation.setErrorStyle(explicitValid.rank().getRank());
        dataValidation.createErrorBox(explicitValid.errorTitle(), explicitValid.errorContent());
        sheet.addValidationData(dataValidation);
    }
}
