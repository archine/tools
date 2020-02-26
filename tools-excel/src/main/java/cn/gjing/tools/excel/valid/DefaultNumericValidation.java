package cn.gjing.tools.excel.valid;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * Default value verifier
 *
 * @author Gjing
 **/
public class DefaultNumericValidation implements ExcelNumericValidation {
    @Override
    public void valid(ExcelNumericValid excelNumericValid, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint numericConstraint = helper.createNumericConstraint(excelNumericValid.validationType().getType(),
                excelNumericValid.operatorType().getType(), excelNumericValid.expr1(), "".equals(excelNumericValid.expr2()) ? null : excelNumericValid.expr2());
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow,firstCol, lastCol);
        DataValidation dataValidation = helper.createValidation(numericConstraint, regions);
        dataValidation.setShowErrorBox(excelNumericValid.showErrorBox());
        dataValidation.setErrorStyle(excelNumericValid.rank().getRank());
        dataValidation.createErrorBox(excelNumericValid.errorTitle(), excelNumericValid.errorContent());
        dataValidation.createErrorBox(excelNumericValid.errorTitle(), excelNumericValid.errorContent());
        sheet.addValidationData(dataValidation);
    }

}
