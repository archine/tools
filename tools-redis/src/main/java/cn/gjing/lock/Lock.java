package cn.gjing.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Gjing
 **/
@Slf4j
public enum Lock implements AbstractLock {

    LOCK {
        private DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        private final StringRedisTemplate REDIS_TEMPLATE = new StringRedisTemplate();

        private final String LockText = "local key = KEYS[1]\n" +
                "local val = KEYS[2]\n" +
                "local expire = KEYS[3]\n" +
                "if redis.call(\"setnx\", key, val) == 1 then\n" +
                "    redis.call(\"expire\",key,expire)\n" +
                "    return key\n" +
                "end\n" +
                "return null";

        private final String releaseText = "local key = KEYS[1]\n" +
                "local val = redis.call(\"get\",key)\n" +
                "if val == null then\n" +
                "    return null\n" +
                "end \n" +
                "return val";

        @Override
        public String lock(DefaultRedisScript<String> redisScript,String key, String val, int expire) {
            String execute;
            redisScript.setScriptText(LockText);
            redisScript.setResultType(String.class);
            try {
                execute = REDIS_TEMPLATE.execute(redisScript, Arrays.asList(key, val, String.valueOf(expire)), "");
            } catch (Exception e) {
                e.printStackTrace();
                execute = null;
            }
            return execute;
        }

        @Override
        public String release(DefaultRedisScript<String> redisScript,String key) {
            redisScript.setScriptText(releaseText);
            redisScript.setResultType(String.class);
            String execute = null;
            try {
                execute = REDIS_TEMPLATE.execute(redisScript, Collections.singletonList(key), "");
            } catch (Exception e) {
                log.error("release error,key: {}", key);
                e.printStackTrace();
            }
            return execute;
        }
    }
}
