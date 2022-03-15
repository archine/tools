package cn.gjing.tools.excel.write.valid.handle;

import cn.gjing.tools.excel.metadata.listener.ExcelListener;
import cn.gjing.tools.excel.util.ValidUtil;
import cn.gjing.tools.excel.write.ExcelWriterContext;
import cn.gjing.tools.excel.write.listener.ExcelCascadingDropdownBoxListener;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

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
        ExcelDropdownBox dropdownBox = (ExcelDropdownBox) validAnnotation;
        int firstRow = row.getRowNum() + 1;
        if (!"".equals(dropdownBox.link())) {
            List<ExcelListener> dropdownListeners = writerContext.getListenerCache();
            if (dropdownListeners == null) {
                return;
            }
            for (ExcelListener listener : dropdownListeners) {
                if (listener instanceof ExcelCascadingDropdownBoxListener) {
                    ((ExcelCascadingDropdownBoxListener) listener)
                            .addCascadingDropdownBox(dropdownBox, writerContext.getWorkbook(), writerContext.getSheet(), firstRow,
                                    dropdownBox.rows() == 0 ? firstRow : dropdownBox.rows() + firstRow - 1, colIndex, field);
                }
            }
            return;
        }
        DataValidationHelper helper = writerContext.getSheet().getDataValidationHelper();
        DataValidationConstraint constraint;
        String[] values = boxValues == null ? null : boxValues.get(field.getName());
        if (values == null || values.length == 0) {
            constraint = helper.createExplicitListConstraint(dropdownBox.combobox());
        } else {
            Sheet explicitSheet = writerContext.getWorkbook().getSheet("explicitSheet");
            if (explicitSheet == null) {
                explicitSheet = writerContext.getWorkbook().createSheet("explicitSheet");
            }
            int valueLength = values.length;
            Row explicitSheetRow = getRow(explicitSheet, 0);
            int lastCol = explicitSheetRow.getPhysicalNumberOfCells();
            explicitSheetRow.createCell(lastCol).setCellValue(values[0]);
            for (int i = 1; i < valueLength; i++) {
                explicitSheetRow = getRow(explicitSheet, i);
                explicitSheetRow.createCell(lastCol).setCellValue(values[i]);
            }
            char colOffset = (char) ('A' + lastCol);
            constraint = helper.createFormulaListConstraint(explicitSheet.getSheetName() + "!$" + colOffset + "$1:$" + colOffset + "$" + valueLength);
            writerContext.getWorkbook().setSheetHidden(writerContext.getWorkbook().getSheetIndex("explicitSheet"), true);
        }
        int lastRow = dropdownBox.rows() == 0 ? firstRow : dropdownBox.rows() + firstRow - 1;
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        ValidUtil.setErrorBox(dataValidation, dropdownBox.showErrorBox(), dropdownBox.rank(), dropdownBox.errorTitle(), dropdownBox.errorContent(),
                dropdownBox.showTip(), dropdownBox.tipTitle(), dropdownBox.tipContent());
        writerContext.getSheet().addValidationData(dataValidation);
    }

    private Row getRow(Sheet explicitSheet, int i) {
        Row row = explicitSheet.getRow(i);
        if (row == null) {
            row = explicitSheet.createRow(i);
        }
        return row;
    }
}
