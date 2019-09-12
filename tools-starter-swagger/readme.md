# tools-starter-swagger
![](https://img.shields.io/badge/version-1.1.0-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
**快速在SpringBoot项目中集成Swagger**
### 使用方法
#### 1. 导入依赖
```xml
<dependency>
     <groupId>cn.gjing</groupId>
     <artifactId>tools-starter-swagger</artifactId>
     <version>1.1.0</version>
</dependency>
```
#### 2. 使用注解
**该注解可以用在``任何类``上, 案例中将其用在启动类上**
```java
@SpringBootApplication
@EnableSwagger
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
#### 3. 配置
**在进行了第二步之后, 已经可以正常使用Swagger, 各个属性都提供了默认值, 当然如果需要自己设置一些属性也可以, 所有属性如下:**
* yml格式
```yaml
swagger:
  contact:
    email: 联系邮箱
    name: 联系人昵称
    url: 联系人地址
  title: 标题
  description: 描述
  base-package: 接口所在包路径
  path-type: 接口选择规则类型, 共分为: ALL(所有接口), REGEX(符合正则), ANT(符合路径)三个类型, 默认为ALL类型
  path-pattern: 接口匹配规则,在path-type类型不为 "ALL" 的情况下必须设置,否则抛非法参数异常
  exclude-pattern: 排除路径，默认使用正则表达式方式，可在pathType设置为其他类型(pathType类型为ALL时默认走正则)
  terms-of-service-url: 服务条款
  license: 许可证
  license-url: 许可证地址
```
*  JavaBean方式
```java
@Configuration
public class DemoConfig {
    @Bean
    public SwaggerBean swaggerBean() {
        return SwaggerBean.builder()
                .basePackage("com.xxx.xxx")
                .pathType(PathType.ALL)
                .title("标题")
                .termsOfServiceUrl("http://127.0.0.1")
                .license("XXXX")
                .licenseUrl("http://xxx.xx.xx")
                .description("描述")
                .build();
    }
}     
```
#### 4. 聚合文档
**在实际工作中, 往往是多个服务的, 这样前端需要记住每个服务的地址, 显然太麻烦, 所以可以采用聚合文档模式, 将多个项目都聚合在一个服务里, 通常聚合在网关里, 毕竟
每个服务都是走网关过得, 该模式``限于SpringCloud环境``, 且每个服务都在``同一个注册中心下``, 参考配置如下:**
* yml格式
```yaml
server:
  port: 8080
spring:
  application:
    name: zuul-server
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
zuul:
  routes:
    projectA:
      serviceId: web1
      path: /demo/**
swagger:
  resources:
    enable: 是否开启聚合模式, 默认 False
    register-me: 当前项目的文档是否也要加入聚合, 默认 true
    # 服务列表
    service-list:
      - projectA: 这里可以随便定义
          view: 下拉选择时展示的名字, 一般用于标识对应文档的名字
          service: 跟随zuul网关路由的path而定，如上为：/demo/**，那么这里应该填demo
      - projectB:
          view: 项目b
          service: demo 
```
* JavaBean方式
```java
/**
 * @author Gjing
 **/
@Configuration
public class GatewayConfig {
    @Bean
    public SwaggerResources swaggerResources() {
        List<Map<String, SwaggerService>> serviceList = new ArrayList<>();
        Map<String, SwaggerService> service = new HashMap<>();
        service.put("projectA", SwaggerService.builder().view("项目A").service("demo").build());
        serviceList.add(service);
        return SwaggerResources.builder()
                .enable(true)
                .registerMe(true)
                .serviceList(serviceList)
                .build();
    }
}
```
---
**更多教程可前往查看博客: [SpringBoot使用swagger](https://yq.aliyun.com/articles/703133?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**