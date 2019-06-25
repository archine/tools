package cn.gjing.lock.core;

import cn.gjing.lock.AbstractLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author Gjing
 **/
@Slf4j
@Component
class RedisLock extends AbstractLock {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private DefaultRedisScript<String> lockScript;
    @Resource
    private DefaultRedisScript<String> releaseScript;
    @Resource
    private ToolsLockTimeoutHandler toolsLockTimeoutHandler;

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
        log.info(">>>>>>>>>>>>>>锁开始,{}", Thread.currentThread().getName());
        if (lockResult != null) {
            return lockResult;
        }
        while (lockResult == null) {
            log.warn("锁被占用，进入阻塞,线程：{}", Thread.currentThread().getName());
            //判断该请求是否超时
            if (System.currentTimeMillis() - lockTime > timeout) {
                this.toolsLockTimeoutHandler.timeoutAfter();
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
        log.info("阻塞结束，当前线程：{}", Thread.currentThread().getName());
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
