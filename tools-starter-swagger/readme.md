# tools-starter-swagger
![](https://img.shields.io/badge/version-2.0.0-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
**快速在SpringBoot、SpringCloud项目中集成Swagger接口文档**
## 一、添加依赖
```xml
<dependency>
     <groupId>cn.gjing</groupId>
     <artifactId>tools-starter-swagger</artifactId>
     <version>2.0.0</version>
</dependency>
```

## 二、注解说明

### 1、@EnableSingleDoc

**在启动类上使用，开启SpringBoot项目接口文档**

### 2、@EnableGroupDoc

**在启动类上使用，开启SpringCloud项目文档聚合，``一般使用在网关项目``**

## 三、SpringBoot项目配置明细

**如下为全部配置参数，在项目中可选择自己需要的参数进行配置**

```yaml
kit:
  doc:
    contact:
      # 联系邮箱
      email:
      # 联系人昵称
      name:
      # 联系人地址
      url:
    # 是否开启文档，默认true，生产环境可以通过变量设置关闭
    enable: true
    # 标题
    title:
    # 描述
    description:
    # 接口所在包路径，如果未填写会默认找所有带了@ApiOperation注解的接口
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
    # 全局请求头，设置后每一个接口的参数列表都会加上请求头参数
    global-headers:
      # 请求头名称
      - name: token
        # 请求头描述
        desc: 登录的token
        # 是否必须, 默认为false
        required: true
```

**SpringBoot项目配置案例**

```yaml
# 接口文档
spring:
  application:
    name: doc-demo
# 如果不需要配置扫描包，那么可以不用配置其他的，直接全部走默认的即可
kit:
  doc:
    # 一般配置好扫描的controller包路径就好了，
    base-package: com.xxx.xxx.controller
    title: 文档API
```

**注意事项：**

* **全局响应信息，结果Bean的类名一定要在``Controller方法出现过``，否则文档无法展示其结构**
* **方法返回值为``void``时，设置``状态码为200``的schema才``有效``，否则会采用方法本身的返回值，如需自定义，必须采用方法上使用``@ApiResponses``进行设置的方式，其他的状态码就没啥关系**
* **全局配置的方式``优先级小于``方法或者类单独配置的方式**
* **全局配置方式无法更改``状态码为200``的提示信息，如需更改必须采用在方法或者类上配置``@ApiResponses``的方式**
* **配置全局会出现在``所有方法``里面，不与你在某个方法单独加个请求头冲突，``两者会合并``**

## 四、SpringCloud环境配置案例

### 1、Zuul网关

其他子服务均需要引入``kit-doc``依赖并开启了接口文档，且与``Zuul``在同一个注册中心下

```yaml
server:
  port: 8080
spring:
  application:
    name: zuul
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
zuul:
  routes:
    projectA:
      serviceId: demo
      path: /demo/**
# 文档配置
kit:
  doc:
    group:
      # 服务列表
      service-list:
        # 下拉选择时展示的名称
        - desc: 项目A
          # Zuul route中配置的serviceId
          target: demo
```

### 2、SpringCloud Gateway网关配置案例
与使用Zuul时配置的相同，其他子服务均需要引入``kit-doc``依赖并开启了接口文档，且与``GateWay``在同一个注册中心下
```yaml
server:
  port: 8080
spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        # 路线ID
        - id: demo
          # 目标URI，可以使用lb://服务名
          uri: lb://demo
          # 断言
          predicates:
            - Path=/demo/**
          # 过滤器集合
          filters:
            # 去除路由后的前缀
            - StripPrefix=1
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: gateway
# 文档配置
kit:
  doc:
    group:
      # 服务列表
      service-list:
        # 下拉选择时展示的名称
        - desc: 项目A
          # Zuul route中配置的serviceId
          target: demo
```
**``使用GateWay作为网关时，需要在每个子服务中eureka中增加如下配置，否则路由失败``**
```yaml
eureka:
  instance:
    # 不使用主机名来定义注册中心的地址，而使用IP地址的形式
    prefer-ip-address: true
    # 这个随便命名
    instance-id: xxx
```
**配置好后可以根据地址：http://ip:port/doc.html（bootstrap UI） 或者 http://ip:port/swagger-ui/index.html（原生UI） 进行访问**
## 五、效果图

### 1、全局响应信息以及全局请求头

![rh.png](https://upload-images.jianshu.io/upload_images/17866147-d3f7c4ce2fc5a95d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 2、SpringBoot项目开启接口文档效果
![image.png](https://upload-images.jianshu.io/upload_images/17866147-1982e7ac047a4931.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


### 3、SpringCloud项目网关项目开启聚合效果
**与SpringBoot开启文档一样，只不过可以在左上角的下拉框进行选择路由到其他服务**
![route.png](https://upload-images.jianshu.io/upload_images/17866147-7f9cb4c0105884d6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 4、原生UI效果
![image.png](https://upload-images.jianshu.io/upload_images/17866147-712ea9e60fbdd3d6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
**更多教程可前往查看博客: [SpringBoot使用swagger](https://yq.aliyun.com/articles/703133?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**