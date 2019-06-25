package cn.gjing.lock.core;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
class ToolsRedisLockSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                LockProcess.class.getName(),
                RedisLockScriptConfiguration.class.getName(),
                RedisLock.class.getName(),
                ToolsLockTimeoutHandler.class.getName(),
                LockTimeoutHandler.class.getName()};
    }
}
