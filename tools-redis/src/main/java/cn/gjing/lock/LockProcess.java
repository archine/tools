package cn.gjing.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Gjing
 **/
@Aspect
@Component
@Slf4j
class LockProcess {
    @Resource
    private AbstractLock abstractLock;

    @Pointcut("@annotation(cn.gjing.lock.Lock))")
    public void cut() {

    }

    @Around("cut()")
    public Object doLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Lock lock = method.getAnnotation(Lock.class);
        if (StringUtils.isEmpty(lock.key())) {
            throw new NullPointerException("Key cannot be null");
        }
        return this.proceed(joinPoint, lock.key(), this.lock(lock));
    }

    /**
     * 方法执行
     *
     * @param joinPoint joinPoint
     * @param key       锁key
     * @param val       获取锁成功返回的val
     * @return Object
     * @throws Throwable Throwable
     */
    private Object proceed(ProceedingJoinPoint joinPoint, String key, String val) throws Throwable {
        String release = this.release(key, val);
        if (release != null) {
            log.info("锁被释放，当前线程：{}", Thread.currentThread().getName());
        } else {
            log.error("锁释放失败，{}", val);
        }
        return joinPoint.proceed();
    }

    /**
     * 获取锁
     *
     * @param lock lock注解
     * @return 锁结果
     * @throws TimeoutException 超时异常
     */
    private String lock(Lock lock) throws TimeoutException {
        return this.abstractLock.lock(lock.key(), UUID.randomUUID().toString().replaceAll("-", ""),
                lock.expire(), lock.timeout(), lock.retry());
    }

    /**
     * 释放锁
     *
     * @param key 锁key
     * @param val 获取锁得到得val
     * @return 释放结果
     */
    private String release(String key, String val) {
        return this.abstractLock.release(key, val);
    }
}

