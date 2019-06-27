### SpringBoot分布式锁和缓存
![](https://img.shields.io/badge/version-1.0.0-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)      
### 分布式锁的使用
### 一. 导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 二. 启动类标注注解
```java
/**
 * @author Gjing
 */
@SpringBootApplication
@EnableRedisLock
public class TestRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestRedisApplication.class, args);
    }
}
```
### 三. 具体使用
#### 参数信息
* **key：锁对应的key**
* **value：随机字符串**
* **expire：锁过期时间，单位秒**
* **timeout：获取锁超时时间，单位毫秒**
* **retry：重新获取锁间隔时间，单位毫秒**
#### 1. 注解方式
  * **@Lock(String key, int expire , int timeout, int retry)**
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
#### 2. 手动控制方式
* **注入AbstractLock依赖**
```
@Resource
private AbstractLock abstractLock;
```
* **在要锁住的地方加入abstractLock.lock()，获取锁成功返回一个解锁的值, 失败返回null**
> **String lock(String key, String value, int expire, int timeout, int retry)**
* **需要释放的地方使用abstractLock.release(), 释放锁成功返回当前被解锁的key，失败返回null**
> **String release(String key, String value)**
* **使用案例**
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
            lock = this.abstractLock.lock("testLock", RandomUtil.generateMixString(5), 20, 10000, 50);
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
#### 注意！！！
**锁对应的key最好唯一，否则会造成多个方法在同时共享一个锁，造成不好的结果，解锁时传入的value，一定要是获取锁得到的value，否则会解锁失败，避免造成解锁其他人的锁**
#### 3. 重写异常处理
> 某个请求获取锁超时后，默认会返回超时异常信息，如果要自定义返回，可以继承AbstractLockTimeoutHandler超时异常处理类
```java
/**
 * @author Gjing
 **/
@Component
public class LockExceptionHandler extends AbstractLockTimeoutHandler {
    @Override
    public ResponseEntity timeoutAfter(TimeoutException e) {
        // TODO: 实现自定义处理的逻辑  
    }
}
```
#### 4. 自定义实现锁
> 本项目使用Redis和lua脚本结合使用实现锁，如若想使用自己的锁，可以继承AbstartetLock类
```java
/**
 * @author Gjing
 **/
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
### 分布式缓存的使用
#### 一. 启动类标上注解
```java
/**
 * @author Gjing
 */
@SpringBootApplication
@EnableSecondCache
public class TestRedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestRedisApplication.class, args);
    }
}
```
#### 二. 配置
* **配置参数**
    * **Set<String> cacheNames**：缓存key名称
    * **boolean cacheValueNullable**：是否存储控制，默认true，防止缓存穿透
    * **boolean dynamic**：是否动态根据cacheName创建Cache实现
    * **String cachePrefix**：默认无
    * **Integer expire**：redis缓存过期时间，单位秒
    * **Map<String, Integer> everyCacheExpire**：每个cacheName的过期时间，单位秒，优先级比defaultExpiration高
    * **String topic**：缓存更新时通知其他节点的topic名称
    * **Integer expireAfterAccess**：访问后过期时间，默认不过期，单位毫秒
    * **Integer expireAfterWrite**：写入后过期时间，默认不过期，单位毫秒
    * **Integer refreshAfterWrite**：写入后刷新时间，默认不过期，单位毫秒
    * **Integer initialCapacity**：初始化大小
    * **Integer maximumSize**：最大缓存对象个数，超过此数量时之前放入的缓存将失效
* **yml方式**
```yml
second:
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
    public SecondCache secondCache() {
        return SecondCache.builder()
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
#### 三. 简单使用
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
#### 四. 定义接口调用
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