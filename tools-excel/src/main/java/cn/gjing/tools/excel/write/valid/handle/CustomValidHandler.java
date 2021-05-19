package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelCustomValid;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Custom check formula handler
 *
 * @author Gjing
 **/
public class CustomValidHandler extends ExcelValidAnnotationHandler {
    public CustomValidHandler() {
        super(ExcelCustomValid.class);
    }

    @Override
    public void handle(Annotation validAnnotation, ExcelWriterContext writerContext, Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        ExcelCustomValid customValid = (ExcelCustomValid) validAnnotation;
        int firstRow = row.getRowNum() + 1;
        ExcelUtils.addCustomValid(customValid.formula(), writerContext.getSheet(), firstRow, customValid.rows() == 0 ? firstRow : customValid.rows() + firstRow - 1,
                colIndex, customValid.showErrorBox(), customValid.rank(), customValid.errorTitle(), customValid.errorContent(),
                customValid.showTip(), customValid.tipTitle(), customValid.tipContent());
    }
}
