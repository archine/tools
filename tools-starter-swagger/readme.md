# swagger-starter
![](https://img.shields.io/badge/version-1.0.1-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
提供swagger  
> **推荐使用最新版本**  
     
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-starter-swagger/" title="swagger包">cn.gjing.tools-starter-swagger</a>
> **autoswagger工具包**
---
### 注解
* @EnableSwagger: 启动类标注该注解既可开启Swagger Api Doc文档.
```
可以使用默认配置 , 如果需要自定义配置 , 那么请根据下方 : 
> 配置如下(yml格式):
  cn:
    gjing:
      swagger:
        base-package: controller所在的包完整路径,例如: com.example.web (可以不配置,默认所有路径,不过还是建议指定自己项目的controller所在的包)
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
    swagger:
      doc:
        doc-list:
          - demo:
              location: /demo/v2/api-docs 
              version: 1.0
          - demo2:
              location: /demo2/v2/api-docs
              version: 2.0              
```
### 以上配置中 demo 和 demo2 为注册eureka上的服务名 , location为swagger文档地址 , 必须使用: /服务名/v2/api-doc , version为目标文档的版本号,默认1.0                     
