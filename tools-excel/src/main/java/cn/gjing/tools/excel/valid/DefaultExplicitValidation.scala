package cn.gjing.tools.excel.valid

import org.apache.poi.ss.usermodel.{Sheet, Workbook}
import org.apache.poi.ss.util.CellRangeAddressList

/**
 * @author Gjing
 **/
class DefaultExplicitValidation extends ExcelValidation{
  /**
   * Drop-down check rule
   *
   * @param explicitValid ExplicitValid
   * @param workbook      workbook
   * @param sheet         The current sheet
   * @param firstRow      First row
   * @param firstCol      First col
   * @param lastCol       Last col
   */
  override def valid(explicitValid: ExplicitValid, workbook: Workbook, sheet: Sheet, firstRow: Int, firstCol: Int, lastCol: Int): Unit = {
    val helper = sheet.getDataValidationHelper
    val dvConstraint = helper.createExplicitListConstraint(explicitValid.combobox)
    val regions = new CellRangeAddressList(firstRow, if (explicitValid.boxLastRow == 0) firstRow else explicitValid.boxLastRow + firstRow, firstCol, lastCol)
    val dataValidation = helper.createValidation(dvConstraint, regions)
    dataValidation.setShowErrorBox(explicitValid.showErrorBox)
    dataValidation.setShowPromptBox(explicitValid.showPromptBox)
    dataValidation.setErrorStyle(explicitValid.rank.getRank)
    dataValidation.createErrorBox(explicitValid.errorTitle, explicitValid.errorContent)
    sheet.addValidationData(dataValidation)
  }
}
