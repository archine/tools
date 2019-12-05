package cn.gjing.tools.swagger.core

import cn.gjing.tools.swagger.{Contact, SwaggerBean, SwaggerResources}
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.`type`.AnnotationMetadata

/**
 * @author Gjing
 **/
class SwaggerSelector extends ImportSelector {
  override def selectImports(annotationMetadata: AnnotationMetadata): Array[String] = {
    Array[String](classOf[SwaggerBean].getName, classOf[SwaggerResources].getName, classOf[Contact].getName,
      classOf[SwaggerConfig].getName, classOf[ResourcesRegister].getName)
  }
}
