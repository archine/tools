package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.SwaggerBean;
import com.google.common.base.Predicate;

/**
 * @author Gjing
 **/
@SuppressWarnings("all")
final class PathSelectContext {
    public Predicate<String> getPredicate(SwaggerBean swaggerBean, String pathPattern) {
        return PathSelectFactory.SELECT.create(swaggerBean.getPathType()).getPredicate(swaggerBean,pathPattern);
    }
}
