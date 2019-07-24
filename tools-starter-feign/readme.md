# tools-starter-feign
![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   

快速使用Feign,不在需要每次调用目标服务的某个方法就得在当前服务写个一样的接口
### 使用方法
**1. 创建SpringCloud项目并加入依赖**
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-starter-feign</artifactId>
    <version>1.0.2</version>
</dependency>
```
**2. 启动类标注@EnableFeignUtil注解**
```java
@SpringBootApplication
@EnableEurekaClient
@EnableFeignUtil
public class FeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignApplication.class, args);
    }
}
```
**3. 使用案例**
```java
@RestController
public class TestController {

    @GetMapping("/test1")
    @ApiOperation(value = "测试1", httpMethod = "GET")
    public ResponseEntity test1() throws URISyntaxException {
        Map<String, String> map = new HashMap<>(16);
        map.put("a", "参数a");
        //使用 URL 访问，自定义返回值类型
        String result = FeignClientUtil.of(String.class, RouteType.URL, "http://127.0.0.1:8090/")
                .execute(HttpMethod.POST, map, "/method1")
                .getResult();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test2")
    public ResponseEntity test2() throws URISyntaxException {
        Map<String, String> map = new HashMap<>(16);
        map.put("a", "参数a");
        map.put("b", "参数b");
        //使用服务名访问，带负载均衡功能，自定义返回值类型
        String result = FeignClientUtil.of(String.class, RouteType.NAME, "demo")
                .execute(HttpMethod.POST, map, "/method2")
                .getResult();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test3")
    public ResponseEntity test3() throws URISyntaxException {
        //使用服务名访问，带负载均衡功能，默认返回值类型（String）
        String result = FeignClientUtil.ofByName("demo")
                .execute(HttpMethod.GET, null, "/method3/123")
                .getResult();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test4")
    public ResponseEntity test4() throws URISyntaxException {
        //使用URL访问，默认返回值类型（String）
        String result = FeignClientUtil.ofByUrl("127.0.0.1:8080")
                .execute(HttpMethod.GET, null, "/method")
                .getResult();
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/test5")
    public ResponseEntity test5() throws URISyntaxException {
        //发起参数为json类型
        PageResult result = FeignClientUtil.of(PageResult.class, RouteType.NAME, "demo")
                .executeByJsonEntity(PageResult.of("xxx", 1), "/method")
                .getResult();
        return ResponseEntity.ok(result.toString());
    }
}
```
#### tip：
* 以上案例中demo为注册到eureka的服务名，method为目标方法的请求路径，如果类上标有@RequestMapping，也需要和其路径一同拼接；
* 使用URL请求时，可请求与当前服务不在同一个Eureka注册中心下的其他服务接口；
* 使用Name请求时，会带负载均衡功能，必须与当前服务在同一个Eureka注册中心下；
#### FeignClientUtil中的方法 :
**1. 生成实例**
  >  * **of** (responseType, routeType, targetAddress): 生成自定义返回类型和路由类型的FeignClientUtil实例
   > * **ofByName** (targetAddress): 生成默认返回类型（String）的服务名路由FeignClientUtil实例
   > * **ofByUrl** (targetAddress): 生成默认返回类型（String）的URL路由FeignClientUtil实例，可以访问非同一个注册中心下的其他非Cloud项目

**2. 发起请求**
 >   * **execute** (method, queryMap,methodPath): 发起请求
 >  * **executeByJsonEntity**(jsonEntity, methodPath): 发起POST携带requestBody参数请求  

**3. 获取请求结果**
>    * **getResult** (): 获取返回结果
##### 方法中的参数:
>* **responseType**: 返回值类型( 必填 ),否则NPE；
>* **routeType**: 路由类型( 必填 ),URL或者NAME；
>* **targetAddress**: 目标地址( 必填 ),如果是NAME路由则需服务名，如：serve , 如果为URL路由,需要协议+IP+端口，如: http://127.0.0.1:8080
>* **methodPath**: 接口路径( 必填 )，如: /method/test；
>* **queryMap**: 参数,无参可传null；
>* **jsonEntity**: Json字符串、Json对应的实体对象、Map
---
**更详细教程请前往博客: [SpringCloud使用Feign](https://yq.aliyun.com/articles/703131?spm=a2c4e.11155435.0.0.c26c33125jAgU6)**