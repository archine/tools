package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.valid.ExcelValidation;
import cn.gjing.tools.excel.valid.ExplicitValid;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * @author Gjing
 **/
class ExcelExplicitValidation implements ExcelValidation {
    private ExplicitValid explicitValid;
    private int firstRow;
    private int cellNum;

    ExcelExplicitValidation(ExplicitValid explicitValid, int firstRow, int cellNum) {
        this.explicitValid = explicitValid;
        this.firstRow = firstRow;
        this.cellNum = cellNum;
    }

    @Override
    public DataValidation valid(Sheet sheet) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = helper.createExplicitListConstraint(explicitValid.combobox());
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, explicitValid.boxLastRow() == 0 ? firstRow : explicitValid.boxLastRow(),
                cellNum, cellNum);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(explicitValid.showErrorBox());
        dataValidation.setShowPromptBox(explicitValid.showPromptBox());
        dataValidation.setErrorStyle(explicitValid.rank().getRank());
        dataValidation.createErrorBox(explicitValid.errorTitle(),explicitValid.errorContent());
        return dataValidation;
    }
}
