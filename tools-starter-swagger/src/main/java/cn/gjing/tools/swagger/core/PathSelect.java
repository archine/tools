package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.SwaggerBean;
import com.google.common.base.Predicate;

/**
 * @author Gjing
 **/
@SuppressWarnings("all")
interface PathSelect {
    Predicate<String> getPredicate(SwaggerBean swaggerBean,String pathPattern);
}
