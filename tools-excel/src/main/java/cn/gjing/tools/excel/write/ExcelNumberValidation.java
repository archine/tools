package cn.gjing.tools.excel.write;

import cn.gjing.tools.excel.valid.ExcelValidation;
import cn.gjing.tools.excel.valid.NumericValid;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * @author Gjing
 **/
class ExcelNumberValidation implements ExcelValidation {
    private NumericValid numericValid;
    private int firstRow;
    private int cellNum;

    ExcelNumberValidation(NumericValid numericValid, int firstRow, int cellNum) {
        this.numericValid = numericValid;
        this.firstRow = firstRow;
        this.cellNum = cellNum;
    }

    @Override
    public DataValidation valid(Sheet sheet) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint numericConstraint = helper.createNumericConstraint(numericValid.validationType().getType(),
                numericValid.operatorType().getType(),
                numericValid.expr1(),
                numericValid.expr2().equals("") ? null : numericValid.expr2());
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, numericValid.boxLastRow() == 0 ? firstRow : numericValid.boxLastRow(),
                cellNum, cellNum);
        DataValidation dataValidation = helper.createValidation(numericConstraint, regions);
        dataValidation.setShowErrorBox(numericValid.showErrorBox());
        dataValidation.setShowPromptBox(numericValid.showPromptBox());
        dataValidation.setEmptyCellAllowed(numericValid.allowEmpty());
        dataValidation.setErrorStyle(numericValid.rank().getRank());
        dataValidation.createErrorBox(numericValid.errorTitle(),numericValid.errorContent());
        dataValidation.createErrorBox(numericValid.errorTitle(),numericValid.errorContent());
        return dataValidation;
    }
}
