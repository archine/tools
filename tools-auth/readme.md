![](https://img.shields.io/badge/version-1.0.1-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;   
**简单快速的在项目中进行权限验证**
## 一、导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-auth</artifactId>
    <version>1.0.1</version>
</dependency>
```
## 二、权限注解
**该注解使用在API上，用于对用户请求方法时进行身份认证**
### 1、@RequiredPermissions
**权限认证，请求的用户需要存在该注解中设置的权限，否则抛出``PermissionAuthorizationException``**     

|参数|描述|
|---|---|
|value|需要的权限|
### 2、@RequiredRoles
**角色认证，请求的用户需要存在该注解中设置的角色，否则抛出``RoleAuthorizationException``**      

|参数|描述|
|---|---|
|value|需要的角色|
## 三、token生成器
**主要用来生成Token和解析Token，使用时可以通过``@Resource``注解进行依赖注入**
```java
public class TestController {
    @Resource
    private TokenAssistant tokenAssistant;
    
    @GetMapping("/token")
    public void getToken() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("user", "张三");
        String token = this.tokenAssistant.createToken(map);
        System.out.println("生成的token：" + map);
        System.out.println("解析token：" + this.tokenAssistant.parseToken(token));
    }
}
```
## 四、启动类增加注解
**在项目启动类增加``@EnableAuthorization``注解用于开启项目权限校验**
## 五、设置权限认证监听器
**通过监听器可以对用户拥有的权限进行提供以及进行自己的认证校验**
```java
/**
 * @author Gjing
 **/
@Component
public class MyListener implements AuthorizationListener {
    /**
     * 增加用户的访问权限用于对增加了权限注解的方法进行身份认证，如果返回null会抛出NoAccountException
     * 这里模拟权限数据，给当前请求的用户增加admin角色和add权限。实际使用时
     * 用户权限你可以保存在任何地方
     *
     * @param token 用户Token
     * @return AuthorizationMetaData
     */
    @Override
    public AuthorizationMetaData supplyAccess(String token) {
        SimpleAuthorizationMetaData metaData = new SimpleAuthorizationMetaData();
        metaData.addRole("admin");
        metaData.addPermission("add");
        return metaData;
    }

    /**
     * 权限注解验证通过后会触发该方法，你可以在这里做一些项目中自己的权限认证逻辑
     *
     * @param token 用户Token
     */
    @Override
    public void authentication(String token) {

    }

    /**
     * 验证全部通过后且方法执行完毕
     *
     * @param request HttpServletRequest
     * @param method  请求的方法
     */
    @Override
    public void authenticationSuccess(HttpServletRequest request, Method method) {
        System.out.println();
    }
}
```
## 六、额外配置
**可以通过这些配置控制拦截路径和过滤路径，以及token存在的请求头名称等等...全部配置如下：**
```yaml
tools:
  auth:
    # 拦截的路径
    path: /**
    # 排除的路径
    filter: 
    # 生成token的加密方式
    type: hs256
    # token存放的请求头名称
    header: Authorization
    # 加密的盐
    salt: 
```
---
