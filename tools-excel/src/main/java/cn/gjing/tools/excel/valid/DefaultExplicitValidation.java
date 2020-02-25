package cn.gjing.tools.excel.valid;

import cn.gjing.tools.excel.util.ParamUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.Map;

/**
 * Default dropdown box verifier
 *
 * @author Gjing
 **/
public class DefaultExplicitValidation implements ExcelExplicitValidation {

    @Override
    public boolean valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, boolean locked, String fieldName, Map<String, String[]> values) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint;
        CellRangeAddressList regions;
        Sheet explicitSheet;
        if (ParamUtils.equals("", explicitValid.link())) {
            String[] explicitValues = values.get(fieldName);
            if (explicitValues == null) {
                constraint = helper.createExplicitListConstraint(explicitValid.combobox());
            } else {
                explicitSheet = workbook.getSheet("explicitSheet");
                if (explicitSheet == null) {
                    explicitSheet = workbook.createSheet("explicitSheet");
                }
                for (int i = 0; i < explicitValues.length; i++) {
                    Row explicitSheetRow = explicitSheet.getRow(i);
                    if (explicitSheetRow == null) {
                        explicitSheetRow = explicitSheet.createRow(i);
                    }
                    explicitSheetRow.createCell(firstCol).setCellValue(explicitValues[i]);
                }
                char colOffset = (char) ('A' + firstCol);
                constraint = helper.createFormulaListConstraint(explicitSheet.getSheetName() + "!$" + colOffset + "$1:$" + colOffset + "$" + explicitValues.length);
                workbook.setSheetHidden(workbook.getSheetIndex("explicitSheet"), true);
            }
            regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
            this.setValid(explicitValid, sheet, helper, constraint, regions);
            values.remove(fieldName);
        } else {
            if (!locked) {
                explicitSheet = workbook.getSheet("subsetSheet");
                if (explicitSheet == null) {
                    explicitSheet = workbook.createSheet("subsetSheet");
                }
                for (Map.Entry<String, String[]> valueMap : values.entrySet()) {
                    int rowIndex = explicitSheet.getPhysicalNumberOfRows();
                    Row subsetSheetRow = explicitSheet.createRow(rowIndex);
                    subsetSheetRow.createCell(0).setCellValue(valueMap.getKey());
                    for (int i = 0, length = valueMap.getValue().length; i < length; i++) {
                        subsetSheetRow.createCell(i + 1).setCellValue(valueMap.getValue()[i]);
                    }
                    String formula = ParamUtils.createFormula(1, rowIndex + 1, valueMap.getValue().length);
                    Name name = workbook.getName(valueMap.getKey());
                    if (name == null) {
                        name = workbook.createName();
                        name.setNameName(valueMap.getKey());
                        name.setRefersToFormula("subsetSheet!" + formula);
                    }
                }
                locked = true;
                workbook.setSheetHidden(workbook.getSheetIndex("subsetSheet"), true);
            }
            char parentIndex = (char) ('A' + Integer.parseInt(explicitValid.link()));
            for (int i = firstRow; i <= lastRow; i++) {
                String forMuaString = "INDIRECT($" + parentIndex + "$" + (i + 1) + ")";
                constraint = helper.createFormulaListConstraint(forMuaString);
                regions = new CellRangeAddressList(i, i, firstCol, lastCol);
                this.setValid(explicitValid, sheet, helper, constraint, regions);
            }
        }
        return locked;
    }

    private void setValid(ExplicitValid explicitValid, Sheet sheet, DataValidationHelper helper, DataValidationConstraint constraint, CellRangeAddressList regions) {
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.setShowErrorBox(explicitValid.showErrorBox());
        dataValidation.setErrorStyle(explicitValid.rank().getRank());
        dataValidation.createErrorBox(explicitValid.errorTitle(), explicitValid.errorContent());
        sheet.addValidationData(dataValidation);
    }
}
