package cn.gjing.tools.swagger

import scala.beans.BeanProperty

/**
 * @author Gjing
 **/
class SwaggerService {
  /**
   * Tab标签名
   */
  @BeanProperty
  var view: String = _

  /**
   * 目标服务path
   */
  @BeanProperty
  var service: String = _

  def this(view: String, service: String) {
    this
    this.view = view
    this.service = service
  }

}