package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelRepeatValid;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Repeat valid handler
 *
 * @author Gjing
 **/
public class RepeatValidHandler extends ExcelValidAnnotationHandler {
    public RepeatValidHandler() {
        super(ExcelRepeatValid.class);
    }

    @Override
    public void handle(Annotation validAnnotation, ExcelWriterContext writerContext, Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        int firstRow = row.getRowNum() + 1;
        ExcelRepeatValid repeatValid = (ExcelRepeatValid) validAnnotation;
        ExcelUtils.addRepeatValid(writerContext.getSheet(), firstRow, repeatValid.rows() == 0 ? firstRow : repeatValid.rows() + firstRow - 1, colIndex,
                repeatValid.showErrorBox(), repeatValid.rank(), repeatValid.errorTitle(), repeatValid.errorContent(), repeatValid.longTextNumber());
    }
}
