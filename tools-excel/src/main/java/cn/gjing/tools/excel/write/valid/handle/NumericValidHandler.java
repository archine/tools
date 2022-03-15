package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ValidUtil;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelNumericValid;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Numeric valid handler
 *
 * @author Gjing
 **/
public class NumericValidHandler extends ExcelValidAnnotationHandler {
    public NumericValidHandler() {
        super(ExcelNumericValid.class);
    }

    @Override
    public void handle(Annotation validAnnotation, ExcelWriterContext writerContext, Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        ExcelNumericValid numericValid = (ExcelNumericValid) validAnnotation;
        int firstRow = row.getRowNum() + 1;
        int lastRow = numericValid.rows() == 0 ? firstRow : numericValid.rows() + firstRow - 1;
        DataValidationHelper helper = writerContext.getSheet().getDataValidationHelper();
        DataValidationConstraint numericConstraint = helper.createNumericConstraint(numericValid.validType().getType(),
                numericValid.operatorType().getType(), numericValid.expr1(), "".equals(numericValid.expr2()) ? null : numericValid.expr2());
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation dataValidation = helper.createValidation(numericConstraint, regions);
        ValidUtil.setErrorBox(dataValidation, numericValid.showErrorBox(), numericValid.rank(), numericValid.errorTitle(), numericValid.errorContent(),
                numericValid.showTip(), numericValid.tipTitle(), numericValid.tipContent());
        writerContext.getSheet().addValidationData(dataValidation);
    }
}
