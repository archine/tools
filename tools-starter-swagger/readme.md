# swagger-starter
![](https://img.shields.io/badge/version-1.0.0-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
提供swagger  
> **推荐使用最新版本**  
     
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-starter-swagger/" title="swagger包">cn.gjing.tools-starter-swagger</a>
> **autoswagger工具包**
---
```
* @EnableSwagger: 启动swagger(如果您的项目需要用到swagger,可以直接在启动类上使用该注解,并且在您的配置文件中设置扫描路径等参数,
                  包路径不能为空,其他几个参数可以为空),配置文件前缀为'cn.gjing.swagger';
> 配置如下(yml格式):
  cn:
    gjing:
      swagger:
        base-package: com.example.demo(controller层完整包路径,不可以为空)
        title: 我是标题(可以为空)
        version: 1.0(默认1.0)
        description: 我是描述(可以为空)
```  
