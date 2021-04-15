package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import cn.gjing.tools.excel.util.ExcelUtils;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.ExcelCascadingDropdownBoxListener;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Dropdown box valid handler
 *
 * @author Gjing
 **/
public class DropdownBoxValidHandler extends ExcelValidAnnotationHandler {
    public DropdownBoxValidHandler() {
        super(ExcelDropdownBox.class);
    }

    @Override
    public void handle(Annotation validAnnotation, ExcelWriterContext writerContext, Field field, Row row, int colIndex, Map<String, String[]> boxValues) {
        ExcelDropdownBox excelDropdownBox = (ExcelDropdownBox) validAnnotation;
        int firstRow = row.getRowNum() + 1;
        if ("".equals(excelDropdownBox.link())) {
            ExcelUtils.addDropdownBox(excelDropdownBox.combobox(), excelDropdownBox.showErrorBox(), excelDropdownBox.rank(), excelDropdownBox.errorTitle(),
                    excelDropdownBox.errorContent(), writerContext.getWorkbook(), writerContext.getSheet(),
                    firstRow, excelDropdownBox.rows() == 0 ? firstRow : excelDropdownBox.rows() + firstRow - 1,
                    colIndex, boxValues == null ? null : boxValues.get(field.getName()));
        } else {
            List<ExcelListener> dropdownListeners = writerContext.getListenerCache();
            if (dropdownListeners == null) {
                return;
            }
            for (ExcelListener listener : dropdownListeners) {
                if (listener instanceof ExcelCascadingDropdownBoxListener) {
                    ((ExcelCascadingDropdownBoxListener) listener)
                            .addCascadingDropdownBox(excelDropdownBox, writerContext.getWorkbook(), writerContext.getSheet(), firstRow,
                                    excelDropdownBox.rows() == 0 ? firstRow : excelDropdownBox.rows() + firstRow - 1, colIndex, field);
                }
            }
        }
    }
}
