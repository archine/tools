# tools-starter-swagger
![](https://img.shields.io/badge/version-1.0.8-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
##### 快速集成Swagger，只需一个注解，即可开启默认配置并使用它, 也可以自定义去配置它.
> **推荐使用最新版本,由于该项目使用了lombok插件，请确保本地安装了lombok**  
     
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-starter-swagger/" title="swagger包">tools-starter-swagger</a>
---
### 注解
* @EnableSwagger: 标注在启动类或其他配置类即可.
### 自定义配置(皆非必填)
```
> yml文件格式: 

swagger:
  contact:
    email: (联系邮箱)
    name: (联系人昵称)
    url: (联系人地址)
  title: (标题)
  description: (描述)
  base-package: (接口所在包路径)
  path-type: (接口选择规则类型, 共分为: ALL(所有接口), REGEX(符合正则), ANT(符合路径)三个类型, 默认为ALL类型)
  path-pattern: (接口匹配规则,在path-type类型不为 "ALL" 的情况下必须设置,否则抛非法参数异常)
  exclude-pattern: (排除路径，默认使用正则表达式方式，可在pathType设置为其他类型（pathType类型为ALL时默认走正则）)
  terms-of-service-url: (服务条款)
  license: (许可证)
  license-url: (许可证地址)

> JavaBean方式:

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
### 如若要开启多资源模式,可在基础配置上增加以下配置, 需搭配zuul使用, 并且在同一个Eureka注册中心下
:exclamation: 如果register-me设置为false,并且serve-list为空,则会抛出无效参数异常
```
> yml格式:

swagger:
  resources:
    serve-list:
      - demo: (此处demo为目标项目的服务名)
          name: (对应资源文档展示昵称,默认为对应服务的服务名)
          location: (目标项目文档路径, 可以传目标项目的服务名或者完整路径 服务名+ /v2/api-docs , 如: /demo/v2/api-docs)
      - demo2:
          name: 服务2
          location: demo2   
    enable: (是否开启多资源模式,默认false)
    register-me: (是否需要把当前项目的swagger文档也加入,默认为true)
    
> JavaBean格式

@Configuration
@EnableSwagger
public class GatewayConfig {

    @Bean
    public Resources resources() {
        Map<String, Serve> map = new HashMap<>();
        map.put("demo", Serve.builder().name("xxx").location("demo").build());
        return Resources.builder()
                .registerMe(true)
                .enable(true)
                .serveList(Collections.singletonList(map)).build();
    }
}
    
```