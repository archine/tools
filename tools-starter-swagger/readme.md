# tools-starter-swagger
![](https://img.shields.io/badge/version-1.6.0-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
**快速在SpringBoot项目中集成Swagger**
## 一、添加依赖
```xml
<dependency>
     <groupId>cn.gjing</groupId>
     <artifactId>tools-starter-swagger</artifactId>
     <version>1.6.0</version>
</dependency>
```
## 二、注解说明
### 1、@EnableSingleDoc
**使用在启动类上，开启单项目的接口文档**
### 2、@EnableGroupDoc
**使用在启动类上，开启多项目文档聚合**    
## 三、自定义配置
**在启动类指定了第二块介绍的注解后，会采用默认的配置。本段落介绍如何自定义设置一些属性**
### 1、配置说明
```yaml
tools:
  doc:
    contact:
      # 联系邮箱
      email:
      # 联系人昵称
      name:
      # 联系人地址
      url:
    # 是否开启文档，默认true
    enable: true
    # 标题
    title: 
    # 描述
    description: 
    # 接口所在包路径，如果未填写会默认找所有带@ApiOperation注解的接口
    base-package:
    # 接口选择规则类型, 共分为: REGEX(正则匹配), ANT(路径匹配), 默认ANT
    path-type:
    # 接口匹配规则表达式
    path-pattern:
    # 接口排除匹配表达式
    exclude-pattern:
    # 服务条款
    terms-of-service-url:
    # 许可证
    license:
    # 许可证地址
    license-url:
    # 全局响应信息
    global-response-schemas:
       # 状态码
      - code: 200
        # 响应信息
        message: 正常
        # 结果Bean的类名
        schema: ResultVO
      - code: 400
        message: 错误
    # 请求头
    global-headers:
        # 请求头名称
      - name: token
        # 请求头描述
        desc: 登录的token
        # 是否必须, 默认为false
        required: true
      - name: token2
        desc: 登录的token2
        required: false
```
### 2、注意点
#### 全局响应结果信息
* **全局响应信息，结果Bean的类名一定要在``Controller方法出现过``，否则文档无法展示其结构**
* **方法返回值为``void``时，设置``状态码为200``的schema才``有效``，否则会采用方法本身的返回值，如需自定义，必须采用方法上使用``@ApiResponses``进行设置的方式，其他的状态码就没啥关系**
* **全局配置的方式``优先级小于``方法或者类单独配置的方式**
* **全局配置方式无法更改``状态码为200``的提示信息，如需更改必须采用在方法或者类上配置``@ApiResponses``的方式**
#### 全局请求头
* **配置全局会出现在``所有方法``里面，不与你在某个方法单独加个请求头冲突，``两者会合并``**
## 四 聚合文档
### 1、配置
**SpringCloud环境下在网关``zuul``服务进行多服务的文档聚合,使用时只要登录网关服务的``swagger``文档即可,通过``tab``标签进行选择. 可参考如下配置**
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
tools:
  doc:
    group:
      # 是否开启聚合模式, 默认 False
      enable: false
      # 聚合类型，可用值有url(文档地址)、name(服务名，一般用在zuul网关等)
      type: name
      # 服务列表
      service-list:
          # 下拉选择时展示的名称
          - desc: 项目A
          # 跟随zuul网关路由的path而定，如上为：/demo/**，那么这里应该填demo
            url: demo
          # 下拉选择时展示的名称
          - desc: 项目B
          # 跟随zuul网关路由的path而定，如上为：/demo/**，那么这里应该填demo
            url: demo
```
### 2、自定义聚合逻辑
**实现``DocGroupHandler``接口，并重写其方法，最后将其交给Spring管理**
```java
/**
 * @author Gjing
 **/
public class MyHandler implements DocGroupHandler {
  
    @Override
    public List<SwaggerResource> get() {
        return null;
    }
}
```
**这里需要注意的是要使用``@Primary``注解**
```java
@Configuration
public class MyConfiguration {
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DocGroupHandler myGroupHandler() {
       return new MyHandler();
    }
}
```
## 五、效果图
### 1、全局响应信息以及全局请求头
![rh.png](https://upload-images.jianshu.io/upload_images/17866147-d3f7c4ce2fc5a95d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2、聚合文档
![route.png](https://upload-images.jianshu.io/upload_images/17866147-7f9cb4c0105884d6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
**更多教程可前往查看博客: [SpringBoot使用swagger](https://yq.aliyun.com/articles/703133?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**