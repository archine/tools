package cn.gjing.tools.excel

import org.apache.poi.ss.usermodel.CellStyle

import scala.beans.BeanProperty

/**
 * Style metadata
 * @author Gjing
 **/
class MetaStyle {
  @BeanProperty
  var headStyle:CellStyle = _
  @BeanProperty
  var bodyStyle:CellStyle = _
  @BeanProperty
  var titleStyle:CellStyle = _

  def this(headStyle:CellStyle,bodyStyle:CellStyle,titleStyle:CellStyle){
    this()
    this.headStyle = headStyle
    this.bodyStyle = bodyStyle
    this.titleStyle = titleStyle
  }
}
