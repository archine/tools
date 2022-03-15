package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ValidUtil;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelCustomValid;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;

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
        int lastRow = customValid.rows() == 0 ? firstRow : customValid.rows() + firstRow - 1;
        DataValidationHelper helper = writerContext.getSheet().getDataValidationHelper();
        DataValidationConstraint customConstraint = helper.createCustomConstraint(customValid.formula());
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation validation = helper.createValidation(customConstraint, regions);
        ValidUtil.setErrorBox(validation, customValid.showErrorBox(), customValid.rank(), customValid.errorTitle(), customValid.errorContent(),
                customValid.showTip(), customValid.tipTitle(), customValid.tipTitle());
        writerContext.getSheet().addValidationData(validation);
    }
}
