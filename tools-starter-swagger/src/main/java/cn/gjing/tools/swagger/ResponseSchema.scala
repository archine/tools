package cn.gjing.tools.swagger

import scala.beans.BeanProperty

/**
 * @author Gjing
 **/
class ResponseSchema {
  /**
   * 状态码
   */
  @BeanProperty
  var code: Int = _
  /**
   * 响应信息
   */
  @BeanProperty
  var message: String = _
  /**
   * 结果Bean名称
   */
  @BeanProperty
  var schema: String = _

  def this(code: Integer, message: String, schema: String) {
    this()
    this.code = code
    this.message = message
    this.schema = schema
  }
}
