package cn.gjing.core;

import cn.gjing.SwaggerBean;
import com.google.common.base.Predicate;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gjing
 **/
@SuppressWarnings("all")
@Getter
@Setter
class PathSelectContext {
    private PathSelect pathSelect;

    public Predicate<String> getPredicate(SwaggerBean swaggerBean,String pathPattern) {
        pathSelect = PathSelectFactory.getInstance().create(swaggerBean.getPathType());
        return pathSelect.getPredicate(swaggerBean,pathPattern);
    }

}
