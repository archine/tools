package cn.gjing.tools.excel.metadata.listener;

import cn.gjing.tools.excel.util.ParamUtils;
import cn.gjing.tools.excel.util.ValidUtil;
import cn.gjing.tools.excel.write.listener.ExcelCascadingDropdownBoxListener;
import cn.gjing.tools.excel.write.valid.ExcelDropdownBox;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Default add cascading dropdown box,
 *
 * @author Gjing
 **/
public final class DefaultCascadingDropdownBoxListener implements ExcelCascadingDropdownBoxListener {
    /**
     * Cascading dropdown box values
     */
    private final Map<String, String[]> boxValues;
    /**
     * Whether to initialize the create name manager
     */
    private boolean init = true;

    public DefaultCascadingDropdownBoxListener(Map<String, String[]> boxValues) {
        this.boxValues = boxValues;
    }

    @Override
    public void addCascadingDropdownBox(ExcelDropdownBox excelDropdownBox, Workbook workbook, Sheet sheet, int firstRow, int lastRow,
                                        int colIndex, Field field) {
        if (this.init) {
            Sheet explicitSheet = workbook.getSheet("subsetSheet");
            if (explicitSheet == null) {
                explicitSheet = workbook.createSheet("subsetSheet");
                workbook.setSheetHidden(workbook.getSheetIndex("subsetSheet"), true);
            }
            for (Map.Entry<String, String[]> valueMap : this.boxValues.entrySet()) {
                Name name = workbook.getName(valueMap.getKey());
                if (name == null) {
                    int rowIndex = explicitSheet.getPhysicalNumberOfRows();
                    Row subsetSheetRow = explicitSheet.createRow(rowIndex);
                    subsetSheetRow.createCell(0).setCellValue(valueMap.getKey());
                    for (int i = 0, length = valueMap.getValue().length; i < length; i++) {
                        subsetSheetRow.createCell(i + 1).setCellValue(valueMap.getValue()[i]);
                    }
                    String formula = ParamUtils.createFormulaX(1, rowIndex + 1, valueMap.getValue().length);
                    name = workbook.createName();
                    name.setNameName(valueMap.getKey());
                    name.setRefersToFormula("subsetSheet!" + formula);
                }
            }
            this.init = false;
        }
        char parentIndex = (char) ('A' + Integer.parseInt(excelDropdownBox.link()));
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint;
        CellRangeAddressList regions;
        DataValidation dataValidation;
        for (int i = firstRow; i <= lastRow; i++) {
            String forMuaString = "INDIRECT($" + parentIndex + "$" + (i + 1) + ")";
            constraint = helper.createFormulaListConstraint(forMuaString);
            regions = new CellRangeAddressList(i, i, colIndex, colIndex);
            dataValidation = helper.createValidation(constraint, regions);
            ValidUtil.setErrorBox(dataValidation, excelDropdownBox.showErrorBox(), excelDropdownBox.rank(), excelDropdownBox.errorTitle(), excelDropdownBox.errorContent(),
                    excelDropdownBox.showTip(), excelDropdownBox.tipTitle(), excelDropdownBox.tipContent());
            sheet.addValidationData(dataValidation);
        }
    }
}
