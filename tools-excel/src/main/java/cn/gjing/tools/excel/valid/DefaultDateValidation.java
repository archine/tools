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
    public void valid(DateValid dateValid, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint;
        if (sheet instanceof XSSFSheet) {
            dvConstraint = helper.createDateConstraint(dateValid.operatorType().getType(), "date(" + dateValid.expr1().replaceAll("-", ",") + ")",
                    "date(" + dateValid.expr2().replaceAll("-", ",") + ")", dateValid.pattern());
        } else {
            dvConstraint = helper.createDateConstraint(dateValid.operatorType().getType(), dateValid.expr1(),
                    dateValid.expr2(), dateValid.pattern());
        }
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        dataValidation.setShowErrorBox(dateValid.showErrorBox());
        dataValidation.setErrorStyle(dateValid.rank().getRank());
        dataValidation.createErrorBox(dateValid.errorTitle(), dateValid.errorContent());
        sheet.addValidationData(dataValidation);
    }
}
