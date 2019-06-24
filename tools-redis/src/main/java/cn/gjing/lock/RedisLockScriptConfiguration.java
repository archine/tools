package cn.gjing.lock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author Gjing
 **/
@Configuration
class RedisLockScriptConfiguration {

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    public DefaultRedisScript<String> lockScript() {
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.LOCK.getScript());
        defaultRedisScript.setResultType(String.class);
        return defaultRedisScript;
    }

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    public DefaultRedisScript<String> releaseScript() {
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.RELEASE.getScript());
        defaultRedisScript.setResultType(String.class);
        return defaultRedisScript;
    }
}
