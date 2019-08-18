![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   

快速在项目中使用Feign, 无需定义``FeignClient``类即可使用
### 一、使用方法
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
                .execute(HttpMethod.POST, map, "/user")
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
                .execute(HttpMethod.POST, map, "/user")
                .getResult();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test3")
    public ResponseEntity test3() throws URISyntaxException {
        //使用服务名访问，带负载均衡功能，默认返回值类型（String）
        String result = FeignClientUtil.ofByName("demo")
                .execute(HttpMethod.GET, null, "/user/1")
                .getResult();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test4")
    public ResponseEntity test4() throws URISyntaxException {
        //使用URL访问，默认返回值类型（String）
        String result = FeignClientUtil.ofByUrl("127.0.0.1:8080")
                .execute(HttpMethod.GET, null, "/user")
                .getResult();
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/test5")
    public ResponseEntity test5() throws URISyntaxException {
        //发起参数为json类型
        PageResult result = FeignClientUtil.of(PageResult.class, RouteType.NAME, "demo")
                .executeByJsonEntity(PageResult.of("xxx", 1), "/user")
                .getResult();
        return ResponseEntity.ok(result.toString());
    }
}
```
**注意点**:
> URL请求时，可请求与当前服务不在同一个Eureka注册中心下的其他服务接口, 使用服务名路由，会带负载均衡功能，必须与当前服务在同一个Eureka注册中心下；
### 二. 内部方法介绍
#### 1、of: 生成FeignClientUtil实例
```java
FeignClientUtil.of(responseType, routeType, targetAddress);
```
**参数介绍**   

|参数|描述|
|---|---|
|responseType|返回值类型, ``必填``|
|routeType|路由类型, 总共有两种, 分别为``URL``和服务名路由, ``必填``|
|targetAddress|目标地址, ``服务名路由``模式直接传对应服务名, ``URL路由``需要协议+IP+端口, 如: http://127.0.0.1:8080, ``必填``|
#### 2、ofByName: 生成采用服务名路由的实例
**该实例发起请求后返回值类型为字符串类型**
```java
FeignClientUtil.ofByName("demo");
```
#### 3、ofByUrl: 生成采用Url路由的实例
**该实例发起请求后返回值为字符串类型**
```java
FeginClientUtil.ofByUrl("http://127.0.0.1:8080");
```
#### 4、execute: 发起请求
```java
FeignClientUtil.execute(method, queryMap, methodPath);
```
**参数介绍**    

|参数|描述|
|---|---|
|method|HttpMethod对象, 指定请求类型是GET还是POST等等|
|queryMap|请求参数, 无参可传null|
|methodPath|请求的接口路径, 如: /user_list|
#### 5、executeByJsonEntity: 发起Json请求
```java
FeignClientUtil.executeByJsonEntity(jsonEntity, methodPath);
```
**参数介绍**    

|参数|描述|
|---|---|
|jsonEntity|Json字符串、实体对象、Map皆可|
|methodPath|请求的接口路径, 如: /user_list|
#### 6、getResult: 获取请求结果
```java
FeignClientUtil.getResult();
```
---
**更详细教程请前往博客: [SpringCloud使用Feign](https://yq.aliyun.com/articles/703131?spm=a2c4e.11155435.0.0.c26c33125jAgU6)**