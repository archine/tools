package cn.gjing.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * @author Gjing
 **/
@Configuration
public class LockConfiguration {

    private static final String LockText = "local key = KEYS[1]\n" +
            "local val = KEYS[2]\n" +
            "local expire = KEYS[3]\n" +
            "if redis.call(\"setnx\", key, val) == 1 then\n" +
            "    redis.call(\"expire\",key,expire)\n" +
            "    return key\n" +
            "end\n" +
            "return null";

    @Bean
    public DefaultRedisScript<String> redisScript() {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LockText);
        redisScript.setResultType(String.class);
        return redisScript;
    }
}
