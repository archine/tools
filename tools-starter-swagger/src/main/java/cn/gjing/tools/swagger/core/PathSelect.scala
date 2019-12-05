package cn.gjing.tools.swagger.core

import cn.gjing.tools.swagger.SwaggerBean
import com.google.common.base.Predicate

/**
 * @author Gjing
 **/
trait PathSelect {
  def getPredicate(swaggerBean: SwaggerBean, pathPattern: String): Predicate[String]
}
