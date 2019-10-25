package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * Default dropdown box verifier
 * @author Gjing
 **/
public class DefaultExplicitValidation implements ExcelValidation {
    @Override
    public void valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = helper.createExplicitListConstraint(explicitValid.combobox());
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, explicitValid.boxLastRow() == 0 ? firstRow : explicitValid.boxLastRow()+firstRow,
                firstCol, lastCol);
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(explicitValid.showErrorBox());
        dataValidation.setShowPromptBox(explicitValid.showPromptBox());
        dataValidation.setErrorStyle(explicitValid.rank().getRank());
        dataValidation.createErrorBox(explicitValid.errorTitle(),explicitValid.errorContent());
        sheet.addValidationData(dataValidation);
    }
}
