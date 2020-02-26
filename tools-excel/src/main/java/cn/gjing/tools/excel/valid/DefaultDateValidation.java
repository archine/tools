package cn.gjing.tools.excel.valid;


import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Default time verifier
 *
 * @author Gjing
 **/
public class DefaultDateValidation implements ExcelDateValidation {
    @Override
    public void valid(ExcelDateValid excelDateValid, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint;
        if (sheet instanceof XSSFSheet) {
            dvConstraint = helper.createDateConstraint(excelDateValid.operatorType().getType(), "date(" + excelDateValid.expr1().replaceAll("-", ",") + ")",
                    "date(" + excelDateValid.expr2().replaceAll("-", ",") + ")", excelDateValid.pattern());
        } else {
            dvConstraint = helper.createDateConstraint(excelDateValid.operatorType().getType(), excelDateValid.expr1(),
                    excelDateValid.expr2(), excelDateValid.pattern());
        }
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(excelDateValid.showErrorBox());
        dataValidation.setErrorStyle(excelDateValid.rank().getRank());
        dataValidation.createErrorBox(excelDateValid.errorTitle(), excelDateValid.errorContent());
        sheet.addValidationData(dataValidation);
    }
}
