package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLock;
import cn.gjing.lock.AbstractLockTimeoutHandler;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Gjing
 * redis锁
 **/
@Component
class RedisLock extends AbstractLock {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private AbstractLockTimeoutHandler abstractLockTimeoutHandler;
    private DefaultRedisScript<String> lockScript;
    private DefaultRedisScript<String> releaseScript;

    RedisLock(DefaultRedisScript<String> lockScript, DefaultRedisScript<String> releaseScript) {
        this.lockScript = lockScript;
        this.releaseScript = releaseScript;
    }

    @Override
    public String lock(String key, int expire, int timeout, int retry) {
        List<String> keys = Arrays.asList(key, UUID.randomUUID().toString().replaceAll("-",""), String.valueOf(expire));
        long lockTime = System.currentTimeMillis();
        String lockResult = stringRedisTemplate.execute(lockScript, keys, "");
        if (lockResult != null) {
            return lockResult;
        }
        while (lockResult == null) {
            //判断该请求是否超时
            if (System.currentTimeMillis() - lockTime > timeout) {
                this.abstractLockTimeoutHandler.getLockTimeoutFallback(key, expire, timeout, retry);
                break;
            }
            try {
                Thread.sleep(retry);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String val = stringRedisTemplate.opsForValue().get(key);
            //锁被释放
            if (val == null) {
                lockResult = stringRedisTemplate.execute(lockScript, keys, "");
            }
        }
        return lockResult;
    }

    @Override
    public String release(String key, String value) {
        List<String> keys = Arrays.asList(key, value);
        return stringRedisTemplate.execute(releaseScript, keys, "");
    }
}
