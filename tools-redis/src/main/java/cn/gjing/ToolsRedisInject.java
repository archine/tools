package cn.gjing;

import cn.gjing.lock.LockConfiguration;
import cn.gjing.lock.LockProcess;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Gjing
 **/
public class ToolsRedisInject implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{ LockConfiguration.class.getName(),LockProcess.class.getName()};
    }
}
