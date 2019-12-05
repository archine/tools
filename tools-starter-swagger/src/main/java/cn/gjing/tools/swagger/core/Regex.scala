package cn.gjing.tools.swagger.core
import cn.gjing.tools.swagger.SwaggerBean
import com.google.common.base.Predicate
import springfox.documentation.builders.PathSelectors

/**
 * @author Gjing
 **/
private class Regex extends PathSelect {
  override def getPredicate(swaggerBean: SwaggerBean, pathPattern: String): Predicate[String] = {PathSelectors.regex(pathPattern);}
}
