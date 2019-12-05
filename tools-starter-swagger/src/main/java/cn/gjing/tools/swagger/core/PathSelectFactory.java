package cn.gjing.tools.swagger.core;

import cn.gjing.tools.swagger.PathType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
public enum PathSelectFactory {
    SELECT;
    private Map<PathType, PathSelect> pathMap = new HashMap<>();

    PathSelectFactory() {
        pathMap.put(PathType.ALL, new All());
        pathMap.put(PathType.REGEX, new Regex());
        pathMap.put(PathType.ANT, new Ant());
    }

    PathSelect create(PathType pathType) {
        return (PathSelect) pathMap.get(pathType);
    }
}
