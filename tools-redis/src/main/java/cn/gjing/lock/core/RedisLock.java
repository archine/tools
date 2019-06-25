package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLock;
import cn.gjing.lock.TimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author Gjing
 **/
@Component
class RedisLock extends AbstractLock {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<String> lockScript;
    private DefaultRedisScript<String> releaseScript;

    RedisLock(DefaultRedisScript<String> lockScript, DefaultRedisScript<String> releaseScript) {
        this.lockScript = lockScript;
        this.releaseScript = releaseScript;
    }

    @Override
    public String lock(String key, String val, int expire, int timeout, int retry) {
        long lockTime = System.currentTimeMillis();
        String lockResult = null;
        List<String> keys = Arrays.asList(key, val, String.valueOf(expire));
        try {
            lockResult = stringRedisTemplate.execute(lockScript, keys,"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lockResult != null) {
            return lockResult;
        }
        while (lockResult == null) {
            //判断该请求是否超时
            if (System.currentTimeMillis() - lockTime > timeout) {
                throw new TimeoutException();
            }
            try {
                Thread.sleep(retry);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String value = stringRedisTemplate.opsForValue().get(key);
            //锁被释放
            if (value == null) {
                lockResult = stringRedisTemplate.execute(lockScript, keys,"");
            }
    }
        return lockResult;
    }

    @Override
    public String release(String key, String val) {
        String lockResult = null;
        List<String> keys = Arrays.asList(key, val);
        try {
            lockResult = stringRedisTemplate.execute(releaseScript, keys, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lockResult;
    }
}
