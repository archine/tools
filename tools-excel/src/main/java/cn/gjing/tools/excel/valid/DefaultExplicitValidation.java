package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * 弄人下拉框校验器
 * @author Gjing
 **/
public class DefaultExplicitValidation implements ExcelValidation {
    @Override
    public void valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = helper.createExplicitListConstraint(explicitValid.combobox());
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, explicitValid.boxLastRow() == 0 ? firstRow : explicitValid.boxLastRow()+firstRow,
                firstCol, lastCol);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(explicitValid.showErrorBox());
        dataValidation.setShowPromptBox(explicitValid.showPromptBox());
        dataValidation.setErrorStyle(explicitValid.rank().getRank());
        dataValidation.createErrorBox(explicitValid.errorTitle(),explicitValid.errorContent());
        sheet.addValidationData(dataValidation);
    }
}
