# tools-starter-swagger
![](https://img.shields.io/badge/version-1.0.4-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
##### 快速集成Swagger，只需一个注解，即可开启默认配置并使用它,也可以自定义去配置它
> **推荐使用最新版本**  
     
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-starter-swagger/" title="swagger包">tools-starter-swagger</a>
---
### 注解
* @EnableSwagger: 启动类标注该注解既可开启Swagger文档或者创建Configuration类进行标注.
```
可以使用默认配置 , 如果需要自定义配置 , 那么请根据下方 : 
> 配置如下( SpringBoot下yml格式 ):
cn:
  gjing:
    swagger:
      base-package: controller所在的包完整路径,例如: com.example.web (非必填,不填情况下默认寻找所有带有@ApiOpertaion注解的方法)
      title: api文档标题 (可以不配置,默认"")
      version: 版本号 (可以不配置,默认1.0)
      description: api文档描述 (可以不配置,默认"")

> 使用java方式进行配置( Spring或者SpringBoot):

@EnableSwaggerDoc
@Configuration
public class SwaggerConfiguration {
    @Bean
    public SwaggerBean swaggerBean() {
        return SwaggerBean.builder()
                .basePackage("com.xxx.xxx")
                .version("1.0")
                .title("title")
                .description("lalalalall")
                .build();
    }
}        
```
* @EnableSwaggerDoc: 启动类标注或创建Configuration类标注,开启路由同Eureka注册中心下的其他服务的Swagger文档     
```
可以使用默认配置 , 如果需要自定义配置 , 那么请根据下方 : 
> 配置如下(SpringBoot下使用yml格式) :
cn:
  gjing:
    swagger-doc:
      register-me: true (是否注册当前服务swagger文档, 默认true)
      serve-list:
          - demo(服务名)
          - demo2(服务名)

> 使用java方式配置(Spring或者SpringBoot)

@Bean
public SwaggerDoc swaggerDoc() {
    return SwaggerDoc.builder()
            .serveList(Arrays.asList("服务名","服务名"))
            .registerMe(true)
            .build();
}          
```
## 注意: 
* @EnableSwaggerDoc注解需要在SpringCloud环境下使用,搭配路由工具(比如zuul,gateway)使用,凡是能提供通过服务名访问皆可,不然会导致访问其他服务文档加载失败
