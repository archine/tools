![](https://img.shields.io/badge/version-1。0.0-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)      
**分布式缓存**
## 一. 导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-cache</artifactId>
    <version>1.0.0</version>
</dependency>
```
## 二、启动类使用注解
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
## 三、配置
### 1、配置介绍
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
### 2. 配置示例
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
## 四、简单使用
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
## 五、定义接口调用
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