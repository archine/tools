package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.ValidUtil;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.valid.ExcelRepeatValid;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;

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
        int lastRow = repeatValid.rows() == 0 ? firstRow : repeatValid.rows() + firstRow - 1;
        int startRow;
        int startCol;
        if (writerContext.getSheet() instanceof HSSFSheet) {
            startRow = firstRow == 1 ? 1 : (firstRow - writerContext.getSheet().getLastRowNum());
            startCol = 0;
        } else {
            startRow = firstRow + 1;
            startCol = colIndex;
        }
        String index = ParamUtils.numberToEn(startCol);
        String formula;
        if (repeatValid.longTextNumber()) {
            formula = "COUNTIF(" + index + ":" + index + "," + index + startRow + "&\"*\")<2";
        } else {
            formula = "COUNTIF(" + index + ":" + index + "," + index + startRow + ")<2";
        }
        DataValidationHelper helper = writerContext.getSheet().getDataValidationHelper();
        DataValidationConstraint customConstraint = helper.createCustomConstraint(formula);
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation validation = helper.createValidation(customConstraint, regions);
        ValidUtil.setErrorBox(validation, repeatValid.showErrorBox(), repeatValid.rank(), repeatValid.errorTitle(), repeatValid.errorContent(),
                repeatValid.showTip(), repeatValid.tipTitle(), repeatValid.tipContent());
        writerContext.getSheet().addValidationData(validation);
    }
}
