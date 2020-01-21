package cn.gjing.tools.swagger.core;


import cn.gjing.tools.swagger.PathType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
public enum PathSelectFactory {
    /**
     * 路径选择工厂
     */
    SELECT;
    private Map<PathType, PathSelect> pathMap = new HashMap<>(16);

    PathSelectFactory() {
        pathMap.put(PathType.ALL, new AllType());
        pathMap.put(PathType.REGEX, new RegexType());
        pathMap.put(PathType.ANT, new AntType());
    }

    PathSelect create(PathType pathType) {
        return (PathSelect) pathMap.get(pathType);
    }
}
