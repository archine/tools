package cn.gjing.cache.core;

import cn.gjing.cache.Script;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author Gjing
 **/
@Configuration
class SecondCacheScriptConfiguration {

    @Bean
    public DefaultRedisScript<Boolean> setNxScript() {
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.SET_NX.getScript());
        defaultRedisScript.setResultType(Boolean.class);
        return defaultRedisScript;
    }

    @Bean
    public DefaultRedisScript<Boolean> setScript() {
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptText(Script.SET.getScript());
        defaultRedisScript.setResultType(Boolean.class);
        return defaultRedisScript;
    }
}
