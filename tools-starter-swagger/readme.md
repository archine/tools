# tools-starter-swagger
![](https://img.shields.io/badge/version-1.0.4-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
    快速集成Swagger，只需一个注解，即可开启默认配置并使用它,也可以自定义配置
> **推荐使用最新版本**  
     
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-starter-swagger/" title="swagger包">tools-starter-swagger</a>
---
### 注解
* @EnableSwagger: 启动类标注该注解既可开启Swagger文档.
```
可以使用默认配置 , 如果需要自定义配置 , 那么请根据下方 : 
> 配置如下(yml格式):
  cn:
    gjing:
      swagger:
        base-package: controller所在的包完整路径,例如: com.example.web (非必填,不填情况下默认寻找所有带有@ApiOpertaion注解的方法)
        title: api文档标题 (可以不配置,默认"")
        version: 版本号 (可以不配置,默认1.0)
        description: api文档描述 (可以不配置,默认"")
```
* @EnableSwaggerDoc: 启动类标注即可开启Swagger Api Doc文档管理 , 可增加任何与本身处于同一个eureka注册中心下的服务对应的Swagger文档,
                     一般用于网关使用,避免每个服务都需要去记住访问url.                                
```
可以使用默认配置 , 如果需要自定义配置 , 那么请根据下方 : 
> 配置如下 :
cn:
  gjing:
    swagger-doc:
      register-me: true (是否注册当前服务swagger文档, 默认true)
      doc-list:
          - demo(需要路由的服务名):
              location(地址,采用'/服务名/v2/api-docs'格式): /demo/v2/api-docs 
              version(版本号,默认1.0): 1.0
          - demo2(需要路由的服务名):
              location(地址,采用'/服务名/v2/api-docs'格式): /demo2/v2/api-docs
              version(版本号,默认1.0): 2.0              
```
## 注意: 
* @EnableSwaggerDoc注解最好在SpringCloud环境下使用,否则会导致整合失败,因为其他服务的swagger文档均通过服务名去路由 .     
