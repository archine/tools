package cn.gjing.core;

import cn.gjing.SwaggerBean;
import com.google.common.base.Predicate;
import lombok.NoArgsConstructor;
import springfox.documentation.builders.PathSelectors;

/**
 * @author Gjing
 **/
@SuppressWarnings("all")
@NoArgsConstructor
class Ant implements PathSelect {
    @Override
    public Predicate<String> getPredicate(SwaggerBean swaggerBean, String pathPattern) {
        return PathSelectors.ant(pathPattern);
    }
}
