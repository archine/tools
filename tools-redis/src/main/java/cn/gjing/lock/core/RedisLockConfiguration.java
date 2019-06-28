package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLock;
import cn.gjing.lock.AbstractLockTimeoutHandler;
import cn.gjing.lock.Script;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author Gjing
 **/
@Configuration
class RedisLockConfiguration {

    @Bean
    public DefaultRedisScript<String> lockScript() {
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.LOCK.getScript());
        defaultRedisScript.setResultType(String.class);
        return defaultRedisScript;
    }

    @Bean
    public DefaultRedisScript<String> releaseScript() {
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.RELEASE.getScript());
        defaultRedisScript.setResultType(String.class);
        return defaultRedisScript;
    }

    @Bean
    @ConditionalOnMissingBean(AbstractLockTimeoutHandler.class)
    public ToolsLockTimeoutHandler toolsLockTimeoutHandler() {
        return new ToolsLockTimeoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractLock.class)
    public RedisLock redisLock() {
        return new RedisLock(lockScript(), releaseScript());
    }

    @Bean
    public LockProcess lockProcess(AbstractLock abstractLock) {
        return new LockProcess(abstractLock);
    }

}
