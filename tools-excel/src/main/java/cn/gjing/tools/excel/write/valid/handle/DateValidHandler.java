package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ValidUtil;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelDateValid;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Date valid handler
 *
 * @author Gjing
 **/
public class DateValidHandler extends ExcelValidAnnotationHandler {
    public DateValidHandler() {
        super(ExcelDateValid.class);
    }

    @Override
    public void handle(Annotation validAnnotation, ExcelWriterContext writerContext, Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        ExcelDateValid excelDateValid = (ExcelDateValid) validAnnotation;
        DataValidationHelper helper = writerContext.getSheet().getDataValidationHelper();
        DataValidationConstraint dvConstraint;
        if (writerContext.getSheet() instanceof SXSSFSheet) {
            dvConstraint = helper.createDateConstraint(excelDateValid.operatorType().getType(), "date(" + excelDateValid.expr1().replaceAll("-", ",") + ")",
                    "date(" + excelDateValid.expr2().replaceAll("-", ",") + ")", excelDateValid.pattern());
        } else {
            dvConstraint = helper.createDateConstraint(excelDateValid.operatorType().getType(), excelDateValid.expr1(), excelDateValid.expr2(), excelDateValid.pattern());
        }
        int firstRow = row.getRowNum() + 1;
        int lastRow = excelDateValid.rows() == 0 ? firstRow : excelDateValid.rows() + firstRow - 1;
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation dataValidation = helper.createValidation(dvConstraint, regions);
        ValidUtil.setErrorBox(dataValidation, excelDateValid.showErrorBox(), excelDateValid.rank(), excelDateValid.errorTitle(), excelDateValid.errorContent(),
                excelDateValid.showTip(), excelDateValid.tipTitle(), excelDateValid.tipContent());
        writerContext.getSheet().addValidationData(dataValidation);
    }
}
