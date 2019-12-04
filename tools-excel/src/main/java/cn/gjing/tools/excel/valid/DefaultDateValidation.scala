package cn.gjing.tools.excel.valid

import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddressList

/**
 * Default time verifier
 * @author Gjing
 **/
class DefaultDateValidation extends ExcelValidation{
  /**
   * Custom time validation rules
   *
   * @param dateValid DateValid
   * @param sheet     sheet
   * @param firstRow  First row
   * @param firstCol  First col
   * @param lastCol   Last col
   */
  override def valid(dateValid: DateValid, sheet: Sheet, firstRow: Int, firstCol: Int, lastCol: Int): Unit = {
    val helper = sheet.getDataValidationHelper
    val dvConstraint = helper.createDateConstraint(dateValid.operatorType.getType, dateValid.expr1, dateValid.expr2, dateValid.pattern)
    val regions = new CellRangeAddressList(firstRow, if (dateValid.boxLastRow == 0) firstRow
    else dateValid.boxLastRow + firstRow, firstCol, lastCol)
    val dataValidation = helper.createValidation(dvConstraint, regions)
    dataValidation.setShowErrorBox(dateValid.showErrorBox)
    dataValidation.setShowPromptBox(dateValid.showPromptBox)
    dataValidation.setErrorStyle(dateValid.rank.getRank)
    dataValidation.createErrorBox(dateValid.errorTitle, dateValid.errorContent)
    sheet.addValidationData(dataValidation)
  }
}
