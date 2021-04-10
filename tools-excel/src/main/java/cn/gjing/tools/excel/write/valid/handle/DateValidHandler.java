package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelDateValid;
import org.apache.poi.ss.usermodel.Row;

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
        int firstRow = row.getRowNum() + 1;
        ExcelDateValid excelDateValid = (ExcelDateValid) validAnnotation;
        ExcelUtils.addDateValid(excelDateValid.operatorType(), excelDateValid.expr1(), excelDateValid.expr2(), excelDateValid.pattern(),
                writerContext.getSheet(), firstRow, excelDateValid.rows() == 0 ? firstRow : excelDateValid.rows() + firstRow - 1,
                colIndex, excelDateValid.showErrorBox(), excelDateValid.rank(), excelDateValid.errorTitle(), excelDateValid.errorContent(),
                excelDateValid.showTip(), excelDateValid.tipTitle(), excelDateValid.tipContent());
    }
}
