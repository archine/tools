package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.SwaggerBean;
import com.google.common.base.Predicate;
import springfox.documentation.builders.PathSelectors;

/**
 * @author Gjing
 **/
@SuppressWarnings("all")
class RegexType implements PathSelect {
    @Override
    public Predicate<String> getPredicate(SwaggerBean swaggerBean, String pathPattern) {
        return PathSelectors.regex(pathPattern);
    }
}
