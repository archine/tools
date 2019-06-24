package cn.gjing.lock;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
public class ToolsRedisLockSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{
                LockProcess.class.getName(),
                ToolsRedisLockConfiguration.class.getName(),
                RedisLock.class.getName()};
    }
}
