package cn.gjing.core;

import cn.gjing.SwaggerBean;
import com.google.common.base.Predicate;

/**
 * @author Gjing
 **/
@SuppressWarnings("all")
interface PathSelect {
    Predicate<String> getPredicate(SwaggerBean swaggerBean,String pathPattern);
}
