package cn.gjing.tools.excel

import scala.beans.BeanProperty

/**
 * Excel big title
 * @author Gjing
 **/
class BigTitle {
  /**
   * Title last row
   */
  @BeanProperty
  var lastRow:Int = _

  /**
   * Title content
   */
  @BeanProperty
  var content:String = _

  def this(lastRow:Int,content:String){
    this()
    this.content = content
    this.lastRow = lastRow
  }
}
