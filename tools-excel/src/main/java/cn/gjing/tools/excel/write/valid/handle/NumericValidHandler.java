package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelNumericValid;
import org.apache.poi.ss.usermodel.Row;

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
        int firstRow = row.getRowNum() + 1;
        ExcelNumericValid excelNumericValid = (ExcelNumericValid) validAnnotation;
        ExcelUtils.addNumericValid(excelNumericValid.validType(), excelNumericValid.operatorType(), excelNumericValid.expr1(),
                excelNumericValid.expr2(), writerContext.getSheet(), firstRow, excelNumericValid.rows() == 0 ? firstRow : excelNumericValid.rows() + firstRow - 1,
                colIndex, excelNumericValid.showErrorBox(), excelNumericValid.rank(), excelNumericValid.errorTitle(), excelNumericValid.errorContent(),
                excelNumericValid.showTip(), excelNumericValid.tipTitle(), excelNumericValid.tipContent());
    }
}
