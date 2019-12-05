package cn.gjing.tools.swagger.core

import cn.gjing.tools.swagger.SwaggerBean
import com.google.common.base.Predicate

import scala.beans.BeanProperty

/**
 * @author Gjing
 **/
class PathSelectContext {
  def getPredicate(swaggerBean: SwaggerBean, pathPattern: String): Predicate[String] = {
    PathSelectFactory.SELECT.create(swaggerBean.getPathType).getPredicate(swaggerBean, pathPattern)
  }
}
