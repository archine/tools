package cn.gjing.core;

import cn.gjing.PathType;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
@NoArgsConstructor
@SuppressWarnings("unchecked")
class PathSelectFactory {
    private static PathSelectFactory pathSelectFactory = new PathSelectFactory();
    private static Map pathMap = new HashMap();
    static {
        pathMap.put(PathType.ALL, new All());
        pathMap.put(PathType.REGEX, new Regex());
        pathMap.put(PathType.ANT, new Ant());
    }

    PathSelect create(PathType pathType) {
        return (PathSelect) pathMap.get(pathType);
    }

    static PathSelectFactory getInstance() {
        return pathSelectFactory;
    }
}
