package cn.gjing.tools.swagger

import scala.beans.BeanProperty

/**
 * @author Gjing
 **/
class RequestHeader {
  /**
   * 请求头名称
   */
  @BeanProperty
  var name: String = _
  /**
   * 描述
   */
  @BeanProperty
  var description: String = _
  /**
   * 是否必须, 默认true
   */
  @BeanProperty
  var required: Boolean = true

  def this(name: String, description: String, required: Boolean) {
    this()
    this.name = name
    this.description = description
    this.required = required
  }
}
