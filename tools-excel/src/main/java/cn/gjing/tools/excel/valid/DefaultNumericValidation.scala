package cn.gjing.tools.excel.valid

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddressList

/**
 * @author Gjing
 **/
class DefaultNumericValidation extends ExcelValidation{
  /**
   * Custom data validation rules
   *
   * @param numericValid NumericValid
   * @param sheet        sheet
   * @param firstRow     First row
   * @param firstCol     First col
   * @param lastCol      Last col
   */
  override def valid(numericValid: NumericValid, sheet: Sheet, firstRow: Int, firstCol: Int, lastCol: Int): Unit = {
    val helper = sheet.getDataValidationHelper
    val numericConstraint = helper.createNumericConstraint(numericValid.validationType.getType, numericValid.operatorType.getType, numericValid.expr1,
      if ("" == numericValid.expr2) null else numericValid.expr2)
    val regions = new CellRangeAddressList(firstRow, if (numericValid.boxLastRow == 0) firstRow
    else numericValid.boxLastRow + firstRow, firstCol, lastCol)
    val dataValidation = helper.createValidation(numericConstraint, regions)
    dataValidation.setShowErrorBox(numericValid.showErrorBox)
    dataValidation.setShowPromptBox(numericValid.showPromptBox)
    dataValidation.setErrorStyle(numericValid.rank.getRank)
    dataValidation.createErrorBox(numericValid.errorTitle, numericValid.errorContent)
    dataValidation.createErrorBox(numericValid.errorTitle, numericValid.errorContent)
    sheet.addValidationData(dataValidation)
  }
}
