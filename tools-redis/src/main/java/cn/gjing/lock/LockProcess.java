package cn.gjing.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gjing
 **/
@Aspect
@Component
@Slf4j
public class LockProcess {

    @Resource
    private DefaultRedisScript<String> redisScript;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 重试次数
     */
    private static AtomicInteger retry = new AtomicInteger(1);

    @Pointcut("@annotation(cn.gjing.lock.RedisLock))")
    public void cut() {

    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        if (StringUtils.isEmpty(redisLock.key())) {
            throw new NullPointerException("Key cannot be null");
        }
        String lock = this.lock(redisLock);
        String key = redisLock.key();
        BackType backType = redisLock.BACK_TYPE();
        if (lock != null) {
            return joinPoint.proceed();
        }
        switch (backType) {
            case BLOCK:
                while (StringUtils.isEmpty(lock)) {
                    Thread.sleep(10);
                    String val = stringRedisTemplate.opsForValue().get(redisLock.key());
                    //锁被释放
                    if (val == null) {
                        lock = this.lock(redisLock);
                        continue;
                    }
                    break;
                }
                return joinPoint.proceed();
            case RETRY:
                if (retry.get() == redisLock.retry()) {
                    throw new TimeoutException("The request timeout");
                }
                while (StringUtils.isEmpty(lock)) {
                    retry.getAndIncrement();
                    Thread.sleep(10);
                    if (stringRedisTemplate.opsForValue().get(key) == null) {
                        lock = this.lock(redisLock);
                        continue;
                    }
                    retry.set(1);
                    break;
                }
                return joinPoint.proceed();
            default:
                throw new NullPointerException("No you checked back type");
        }
    }

    private String lock(RedisLock redisLock) {
        return Lock.LOCK.lock(redisScript,redisLock.key(), String.valueOf(System.currentTimeMillis()), redisLock.expire());
    }
}

