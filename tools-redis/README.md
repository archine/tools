### SpringBoot分布式锁和缓存
![](https://img.shields.io/badge/version-1.2.1-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)      
## 分布式锁的使用
### 一. 导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-redis</artifactId>
    <version>1.2.1</version>
</dependency>
```
### 二. 启动类标注注解
```java
/**
 * @author Gjing
 */
@SpringBootApplication
@EnableToolsLock
public class TestRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestRedisApplication.class, args);
    }
}
```
### 三. 具体使用
#### 1、注解方式
在需要加锁的方法上使用``@Lock``注解即可
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    private static int num = 20;
    
    @GetMapping("/test1")
    @Lock(key = "test1")
    public void test1() {
        System.out.println("当前线程:" + Thread.currentThread().getName());
        if (num == 0) {
            System.out.println("卖完了");
            return;
        }
        num--;
        System.out.println("还剩余：" + num);
    }
}
```
**参数信息**    

|参数|描述|
|---|---|
|key|锁的key，每个方法最好``唯一``|
|expire|锁过期时间，单位``秒``，默认``5``|
|timeout|尝试获取锁超时时间，单位``毫秒``，默认``500``|
|retry|重新获取锁的间隔时间，单位``毫秒``，默认10|
#### 2、手动控制方式
在需要使用的方法的类中通过``@Resource``注入
##### a、lock(): 加锁
获取锁成功后会返回一个用于解锁的值，失败返回null   

``abstractLock.lock(key, expire, timeout, retry)``   
**参数说明**    

|参数|描述|
|---|---|
|key|锁的key，每个方法保证``唯一``|
|expire|锁过期时间，单位``秒``，默认``5``|
|timeout|尝试获取锁超时时间，单位``毫秒``，默认``500``|
|retry|重新获取锁的间隔时间，单位``毫秒``，默认10|
##### b、release()：解锁
释放锁成功返回当前被解锁的key，失败返回null    
``abstractLock.release(key, value)``   
**参数说明**     

|参数|描述|
|---|---|
|key|加锁时对应的key|
|value|获取锁成功后得到的值|       
##### 使用案例
```java
/**
 * @author Gjing
 **/
@RestController
public class LockController {
    @Resource
    private AbstractLock abstractLock;
    
    private static int num = 10;
    
    @GetMapping("/test2")
    public void test2() {
        String lock = null;
        try {
            lock = this.abstractLock.lock("testLock", 20, 10000, 50);
            System.out.println("当前线程:" + Thread.currentThread().getName());
            if (num == 0) {
                System.out.println("卖完了");
                return;
            }
            num--;
            System.out.println("还剩余：" + num);
        } finally {
            this.abstractLock.release("testLock", lock);
        }
    }
}
```
### 3. 重写异常处理
在获取锁时往往会出现长时间未获取锁，达到我们加锁设置的超时时间后会抛出超时异常，如果要走自己的逻辑，可以重写异常处理
```java
/**
 * @author Gjing
 **/
@Component
public class TimeoutHandler extends AbstractLockTimeoutHandler {
    @Override
    public void getLockTimeoutFallback(String s, int i, int i1, int i2) {
        // TODO: 2019/8/19 自定义逻辑 
    }
}
```
#### 4. 自定义实现锁
> 本项目使用Redis和lua脚本结合使用实现锁，如若想使用自己的锁，可以继承AbstartetLock类
```java
/**
 * @author Gjing
 **/
@Component
public class DemoLock extends AbstractLock {
    @Override
    public String lock(String s, String s1, int i, int i1, int i2) {
        return null;
    }
    @Override
    public String release(String s, String s1) {
        return null;
    }
}
```
## 分布式缓存的使用
### 一. 启动类标上注解
```java
/**
 * @author Gjing
 */
@SpringBootApplication
@EnableToolsCache
public class TestRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestRedisApplication.class, args);
    }
}
```
### 二. 配置
#### 1. 配置介绍
**以下为所有配置信息, 使用时可自定义设置, 皆以``tools.cache``开头**      
   
|配置项|描述|
|------|------|
|cache-names|缓存key名|
|cache-value-nullable|是否存储控制，``默认true``，防止缓存穿透|
|dynamic|是否动态根据cacheName创建Cache实现, 默认``true``|
|cache-prefix|缓存key的前缀|
|caffeine.initial-capacity|初始化大小|
|caffeine.expire-after-access|访问后过期时间，单位毫秒|
|caffeine.expire-after-write|写入后过期时间，单位毫秒|
|caffeine.maximum-size|最大缓存对象个数，超过此数量时之前放入的缓存将失效|
|caffeine.refresh-after-write|写入后刷新时间，单位毫秒|
|redis.every-cache-expire|每个cacheName的过期时间，单位秒，优先级比``expire``高|
|redis.expire|全局过期时间，单位秒，默认不过期|
|redis.topic|缓存更新时通知其他节点的topic名称|
#### 2. 配置示例
* **yml方式**
```yml
tools:
  cache:
    cache-prefix: 锁的前缀
    redis:
      expire: 10
    caffeine:
      expire-after-write: 3000
```
* **JavaBean方式**
```java
/**
 * @author Gjing
 **/
@Configuration
public class CacheConfiguration {

    @Bean
    public ToolsCache toolsCache() {
        return ToolsCache.builder()
                .cachePrefix("锁的前缀")
                .dynamic(true)
                .build();
    }

    @Bean
    public RedisCache redisCache() {
        return RedisCache.builder()
                .expire(10)
                .build();
    }

    @Bean
    public CaffeineCache caffeineCache() {
        return CaffeineCache.builder()
                .expireAfterWrite(3000)
                .build();
    }
}
```
### 三. 简单使用
```java
/**
 * @author Gjing
 **/
@Service
@Slf4j
public class CustomService {

    @Resource
    private CustomRepository customRepository;

    /**
     * 获取一个用户
     * @param customId 用户id
     * @return Custom
     */
    @Cacheable(value = "user",key = "#customId")
    public Custom getCustom(Integer customId) {
        log.warn("查询数据库用户信息");
        return customRepository.findById(customId).orElseThrow(() -> new NullPointerException("User is not exist"));
    }

    /**
     * 删除一个用户
     * @param customId 用户id
     */
    @CacheEvict(value = "user", key = "#customId")
    public void deleteUser(Integer customId) {
        Custom custom = customRepository.findById(customId).orElseThrow(() -> new NullPointerException("User is not exist"));
        customRepository.delete(custom);
    }
}
```
### 四. 定义接口调用
```java
/**
 * @author Gjing
 **/
@RestController
public class CustomController {
    @Resource
    private CustomService customService;

    @GetMapping("/user/{custom-id}")
    @ApiOperation(value = "查询用户",httpMethod = "GET")
    public ResponseEntity getUser(@PathVariable("custom-id") Integer customId) {
        return ResponseEntity.ok(customService.getCustom(customId));
    }

    @DeleteMapping("/user")
    @ApiOperation(value = "删除用户", httpMethod = "DELETE")
    @ApiImplicitParam(name = "customId", value = "用户Id", dataType = "int", required = true, paramType = "Query")
    @NotNull
    public ResponseEntity deleteUser(Integer customId) {
        customService.deleteUser(customId);
        return ResponseEntity.ok("Successfully delete");
    }
}
```
---
**如果使用中发现BUG，欢迎提交Issue，或者邮箱联系：me@gjign.cn**      

**分布式锁博客地址**：[点击前往](https://yq.aliyun.com/articles/706641?spm=a2c4e.11155435.0.0.5542331293hetJ)      
**分布式缓存博客地址**：[点击前往](https://yq.aliyun.com/articles/706643?spm=a2c4e.11155435.0.0.11c533121nRGrc)