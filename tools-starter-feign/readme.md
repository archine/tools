# tools-starter-feign
![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
    快速使用Feign,不在需要每次调用目标服务的某个方法就得在当前服务写个一样的接口
> **推荐使用最新版本**  
     
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-starter-feign/" title="fast-feign">tools-starter-feign</a>
---
### 注解
* @EnableFeignUtil: 启动类标注该注解,即可启用Feign工具,使用SpringCloud环境，否则会出现加载失败

> 使用案例
* 使用服务名访问(同一个Eureka中心下的服务,带负载均衡功能),可以自定义返回值类型
```
@GetMapping("/test")
public ResponseEntity test() throws URISyntaxException {
    Map<String, String> map = new HashMap<>(16);
    map.put("a", "参数a");
    map.put("b", "参数b");
    String web = FeignClientUtil.of(String.class, RouteType.NAME, "web")
            .execute(HttpMethod.POST, map, null, "/web")
            .getResult();
    return ResponseEntity.ok(web);
}
```
* 使用服务名访问(同一个Eureka中心下的服务,带负载均衡功能),默认返回值类型(String)
```
@GetMapping("/test")
public ResponseEntity test() throws URISyntaxException {
    String result = FeignClientUtil.ofByName("web")
            .execute(HttpMethod.GET, null, null, "/method/123")
            .getResult();
    return ResponseEntity.ok(result);
}
```
* 使用URL访问(可以不在同一个注册中心下,不带负载均衡功能),可以自定义返回值类型
```
@GetMapping("/test")
public ResponseEntity test() throws URISyntaxException {
    Map<String, String> map = new HashMap<>(16);
    map.put("a", "参数a");
    String result = FeignClientUtil.of(String.class, RouteType.URL, "127.0.0.1:8080")
            .execute(HttpMethod.POST, map, "/method")
            .getResult();
    return ResponseEntity.ok(result);
}
```
* 使用URL访问(可以不在同一个注册中心下,不带负载均衡功能),默认返回值类型(String)
```
@GetMapping("/test")
public ResponseEntity test() throws URISyntaxException {
    String result = FeignClientUtil.ofByUrl("http://127.0.0.1:8080")
            .execute(HttpMethod.GET, null, "/test")
            .getResult();
    return ResponseEntity.ok(result);
}
```
* 发起参数为json类型
```
@GetMapping("/test")
public ResponseEntity test() throws URISyntaxException {
    Map<String, String> map = new HashMap<>(16);
    map.put("a", "参数a");
    PageResult result = FeignClientUtil.of(PageResult.class, RouteType.NAME, "demo")
            .executeByJsonEntity(map, "/method")
            .getResult();
    return ResponseEntity.ok(result.toString());
}
```

> 方法:
* 生成实例
    * of (responseType, routeType, targetAddress): 生成自定义返回类型和路由类型的FeignClientUtil实例
    * ofByName (targetAddress): 生成默认返回类型（String）的服务名路由FeignClientUtil实例
    * ofByUrl (targetAddress): 生成默认返回类型（String）的URL路由FeignClientUtil实例，可以访问非同一个注册中心下的其他非Cloud项目
* 发起请求    
    * execute (method, queryMap,methodPath): 发起请求
    * executeByJsonEntity(jsonEntity, methodPath): 发起POST携带JSON对象
* 获取请求结果
    * getResult (): 获取返回结果
> 参数:
* responseType: 返回值类型(必填),否则NPE
* routeType: 路由类型(必填),URL或者NAME
* queryMap: 参数,无参可传null
* targetAddress: 目标地址(必填),如果是NAME路由:则需要服务名: serve , 如果为URL路由,需要IP+端口: 127.0.0.1:8080
* methodPath: 接口路径(必填): /method/test
* jsonEntity: Json字符串、Json对应的实体对象、Map
---
**更详细教程请前往博客: [SpringCloud使用Feign](https://yq.aliyun.com/articles/703131?spm=a2c4e.11155435.0.0.c26c33125jAgU6)**