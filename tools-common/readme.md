![](https://img.shields.io/badge/version-1.3.8-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 

Java常用工具类整合
## 一、导入依赖
```xml
<dependency>
  <groupId>cn.gjing</groupId>
  <artifactId>tools-common</artifactId>
  <version>1.3.8</version>
</dependency>
```
## 一、Rest接口参数校验注解
项目为``Spring``环境需要手动在xml文件中对参数校验处理器bean进行配置
```xml
<bean id="xxx" class="cn.gjing.tools.common.valid.ParamValidationAdapter"/>
```
**支持自定义配置需要校验的接口路径和要排除的接口路径，yml格式如下:**
```yaml
valid:
  # 需要校验的接口路径
  path: /**
  # 需要排除的接口路径
  exclude-path: /**
```
### 1、@Not
**排除某个参数不需要非空校验**
```java
@RestController
public class TestController{
    @PostMapping("/test")
    @NotEmpty
    public void test(String param1, @Not String param2) {
        System.out.println(param1);
    }
}
```
### 2、@Json
**如果需要校验的参数为``对象``，通过该注解对其进行注释**
```java
@RestController
public class TestController {
    @PostMapping("/test")
    public void test(@Json @RequestBody User user) {
        System.out.println("ok");
    }
}
```
### 3、@NotNull 
对方法参数非null校验，可以作用在方法和参数以及对象属性上     
* **使用在方法上默认会对该方法的``所有参数``进行校验，如果需要排除某个参数则在对应参数加上``@Not``注解。使用在方法上无法自定义错误信息，默认抛出具体哪个参数不能为空**
```java
@RestController
public class TestController{
    @PostMapping("/test")
    @NotNull
    public void test(String param1，@Not String param2) {
        System.out.println(param1);
    }
}
```
* **方法参数**
```java
@RestController
public class TestController{
    @PostMapping("/test")
    public void test(String param1，@NotNull(message = "参数不能为空") String param2) {
        System.out.println(param1);
    }
}
```
* **对象属性**
```java
@Data
public class User {
    @NotNull(message = "邮箱不能为Null")
    private String userEmail;
}
```
### 4、@NotEmpty
对方法参数非空校验（包括数组、集合、map、字符串、null），可以作用在方法和参数以及对象属性上     
* **使用在方法上默认会对该方法的``所有参数``进行校验，如果需要排除某个参数则在对应参数加上``@Not``注解。使用在方法上无法自定义错误信息，默认抛出具体哪个参数不能为空**
```java
@RestController
public class TestController{
    @PostMapping("/test")
    @NotEmpty
    public void test(String param1,String param2) {
        System.out.println(param1);
    }
}
```
* **方法参数**
```java
@RestController
public class TestController{
    @PostMapping("/test")
    public void test(@NotEmpty(message = "参数不能为空") String param1) {
        System.out.println(param1);
    }
}
```
* **对象属性**
```java
@Data
public class User {
    @NotEmpty(message = "邮箱不能为空")
    private String userEmail;
}
```
### 5、@Email
**作用在对象的属性和方法参数, 对邮箱格式进行校验，错误信息可自定义**      
* **对象属性**
```java
@Data
public class User {
    @Email
    private String userEmail;
}
```
* **方法参数**
```java
@RestController
public class TestController {
    @PostMapping("/test")
    public void test(@Email String userEmail) {
        System.out.println("ok");
    }
}
```
### 6、@Length
**作用在方法参数和对象属性，可设置最小长度和最大长度，当``最小长度大于0``的时候会进行非null判断。可自定义错误信息**       
* **对象属性**
```java
@Data
public class User {
    @Length(max = 3, message = "用户名长度不能大于3")
    private String name;
}
```
* **方法参数**
```java
@RestController
public class TestController {
    @PostMapping("/test")
    public void test(@Length(max = 3,message = "长度不能超过3") String userName) {
        System.out.println("ok");
    }
}
```
### 7、@Mobile
**作用在对象属性和方法参数，对手机号格式进行校验，错误信息可自定义**      
* **对象属性**
```java
@Data
public class User {
    @Mobile(message = "手机号格式错误")
    private String name;
}
```
* **方法参数**
```java
@RestController
public class TestController {
    @PostMapping("/test")
    public void test(@Mobile String userPhone) {
        System.out.println("ok");
    }
}
```
## 三、跨域
开启全局跨域，在启动类或者任意类使用``@EnableCors``注解即可，会走默认配置，也可以自行配置，配置示例如下：
* **yml方式**
```yaml
cors:
  # 支持的方法类型
  allowed-methods: POST,GET,DELETE,PUT,OPTIONS
  # 支持的请求头
  allowed-headers: xxx
  # 支持的域名
  allowed-origins: xxx
  # 方法路径
  path: /**
  max-age: 1800
```
* **javaBean方式**
```java
/**
 * @author Gjing
 **/
@Configuration
public class CorsConfiguration {
    @Bean
    public CommonCors cors() {
        return CommonCors.builder()
                .allowCredentials(Boolean.TRUE)
                .maxAge(1800L)
                .path("/**")
                .build();
    }
}
```
## 四、返回结果模板
### 1、ResultVO
通用返回结果模板     

|参数|描述|
|---|---|
|code|状态码|
|message|提示信息|
|data|数据|
### 2、PageResult
分页查询返回结果集    

|参数|描述|
|---|---|
|totalPages|总页数|
|CurrentPage|当前页数|
|totalRows|总条数|
|pageRows|每页的条数|
|data|数据|
### 3、ErrorResult
错误返回模板      

|参数|描述|
|---|---|
|code|状态码|
|message|提示信息|
## 四、参数校验工具类： 
**主要提供参数校验、处理,匹配等等， 使用时通过``ParamUtils.xxx()``使用，以下为该工具的所有方法介绍 :**
### 1、isEmpty
判断给定参数是否为空，可以是字符串、包装类型、数组、集合等    

|参数|描述|
|---|---|
|str|参数|
### 2、isNotEmpty
判断给定是否不为空，可以是字符串、包装类型、数组、集合等     

|参数|描述|
|---|---|
|str|参数|
### 3、requireNotNull
该参数不能为空，为空抛出NPE，否则返回原值     

|参数|描述|
|---|---|
|str|参数|
### 4、ListHasEmpty
判断集合里是否含有空值   

|参数|描述|
|---|---|
|list|集合|
### 5、multiEmpty
检查多个参数里面是否有空值     

|参数|描述|
|---|---|
|params|多个参数|
### 6、equals
判断两个参数是否相等    

|参数|描述|
|---|---|
|t|参数1|
|u|参数2|
### 7、trim
去除字符串的空格     

|参数|描述|
|---|---|
|str|字符串|
### 8、trim
去除集合中的空元素     

|参数|描述|
|---|---|
|list|集合|
### 9、removeSymbol
移除字符串两边的指定符号      

|参数|描述|
|---|---|
|str|字符串|
|symbol|符号|
### 10、removeStartSymbol
移除字符串开始的指定符号     

|参数|描述|
|---|---|
|str|字符串|
|symbol|符号|
### 11、removeEndSymbol
移除字符串末尾的指定符号    

|参数|描述|
|---|---|
|str|字符串|
|symbol|符号|
### 12、split
根据符号截取    

|参数|描述|
|---|---|
|str|字符串|
|symbol|符号|
### 13、removeAllSymbol
移除字符串里的所有指定符号    

|参数|描述|
|---|---|
|str|字符串|
|symbol|符号|
### 14、contains
判断数组里是否包含指定的值     

|参数|描述|
|---|---|
|arr|数组|
|val|值|
### 15、isEmail
判断是否为email       

|参数|描述|
|---|---|
|email|字符串|
### 16、isMobileNumber
判断是否是手机号码    

|参数|描述|
|---|---|
|phone|字符串|
### 17、isTelPhone
判断是不是电话号码     

|参数|描述|
|---|---|
|tel|字符串|
### 18、isPostCode
判断是否为邮编    

|参数|描述|
|---|---|
|postCode|字符串|
## 五、时间工具类： 
**对时间进行操作，使用时通过``TimeUtils.xxx()``调用，该工具的所有方法介绍如下 :**
### 1、dateToString
获取文本格式时间     

|参数|描述|
|---|---|
|date|Date对象|
### 2、dateToLocalDateTime
date转localDateTime     

|参数|描述|
|---|---|
|date|Date对象|
### 3、dateToLocalDate
date转localDate    

|参数|描述|
|---|---|
|date|Date对象|
### 4、localDateToDate
localDate转Date    

|参数|描述|
|---|---|
|localDate|LocalDate对象|
### 5、LocalDateToString
LocalDate转指定格式字符串     

|参数|描述|
|---|---|
|localDate|LocalDate对象|
### 6、LocalDateTimeToString
LocalDateTime转指定格式字符串     

|参数|描述|
|---|---|
|localDateTime|LocalDateTime对象|
### 7、localTimeToString
LocalTime转指定格式字符串     

|参数|描述|
|---|---|
|localTime|LocalTime对象|
### 8、stringToLocalDate
字符串日期转指定格式      

|参数|描述|
|---|---|
|time|字符串日期|
### 9、stringToLocalDateTime
字符串日期转指定格式     

|参数|描述|
|---|---|
|time|字符串日期|
### 10、localDateTimeToDate
LocalDateTime转Date     

|参数|描述|
|---|---|
|dateTime|LocalDateTime对象|
### 11、localDateTimeToStamp
localDateTime转时间戳     

|参数|描述|
|---|---|
|dateTime|LocalDateTime对象|
### 12、stampToLocalDateTime
时间戳转LocalDateTime     

|参数|描述|
|---|---|
|stamp|时间戳|
### 13、getYearsByStartTime
查询一个日期(年月日格式)到目前过了多少年    

|参数|描述|
|---|---|
|startTime|开始的时间(字符串)|
### 14、dateToString
Date转字符串      

|参数|描述|
|---|---|
|date|Date对象|
|format|格式，如:yyyy-MM-dd|
### 15、stringToDate
字符串转Date      

|参数|描述|
|---|---|
|date|时间字符串|
### 16、getDate
字符串日期转指定格式Date     

|参数|描述|
|---|---|
|date|时间字符串|
|format|格式，如:yyyy-MM-dd|
### 17、stringDateToCalendar
字符串时间转日期   

|参数|描述|
|---|---|
|str|字符串时间|
### 18、calendarToDate
日期转Date     

|参数|描述|
|---|---|
|calendar|日期|
|format|格式，如:yyyy-MM-dd|
### 19、calendarToStringDate
日期转字符串      

|参数|描述|
|---|---|
|calendar|日期|
|format|格式，如:yyyy-MM-dd|
### 20、getAllDaysOfMonth
获取某个时间所在月份的天数     

|参数|描述|
|---|---|
|date|时间对象|
### 21、getDays
获取时间的天数，如2017-12-13，返回13    

|参数|描述|
|---|---|
|date|时间对象|
### 22、getYears
获取时间所在的年份    

|参数|描述|
|---|---|
|date|时间对象|
### 23、getMonth
获取时间所在月份    

|参数|描述|
|---|---|
|date|时间对象|
### 24、addMonth
增加月份    

|参数|描述|
|---|---|
|date|时间对象|
|n|月数|
### 25、addDay
增加天数    

|参数|描述|
|---|---|
|date|时间对象|
|n|天数|
### 26、stringDateToStamp
字符串日期转时间戳      

|参数|描述|
|---|---|
|stringDate|字符串时间|
### 27、stampToStringDate
时间戳转字符串时间     

|参数|描述|
|---|---|
|timeStamp|时间戳|
### 28、dateBetween
计算两个日期相差的天数（不包括今天）     

|参数|描述|
|---|---|
|startDate|开始时间|
|endDate|结束时间|
### 29、dateBetweenIncludeToday
计算两个日期相差的天数（包括今天）     

|参数|描述|
|---|---|
|startDate|开始时间|
|endDate|结束时间|
## 六、加密工具类
**主要用于数据加密, 使用时通过``EncryptionUtils.xxx()``调用，该工具包含的所有方法如下:**
### 1、encodeMd5
MD5加密     

|参数|描述|
|---|---|
|body|要加密的内容|
### 2、encodeBase64
Base64加密     

|参数|描述|
|---|---|
|content|要加密的内容|
### 3、decodeBase64
Base64解密     

|参数|描述|
|---|---|
|content|要加密的内容|
### 4、encodeSha256Hmac
sha566 hmac加密     

|参数|描述|
|---|---|
|str|要加密的内容|
|secret|密钥|
### 5、sha1Hmac
sha1Hmac加密      

|参数|描述|
|---|---|
|str|要加密的内容|
|secret|密钥|
### 6、encodeAes
AES加密     

|参数|描述|
|---|---|
|content|要加密的内容|
|password|加密需要的密码|
### 7、decodeAes
AES解密     

|参数|描述|
|---|---|
|content|要加密的内容|
|password|加密需要的密码|
## 七、随机数工具类
**用于随机生成数字或字符串，使用时通过``RandomUtils.xxx()``调用，该工具包含的所有方法如下 :**
### 1、randomInt
获取随机整数，可设置最大值和最小值    

|参数|描述|
|---|---|
|min|最小值|
|max|最大值|
### 2、getRandom
获取一个Random实例
### 3、generateMixString
生成混合指定长度字符串（数字、字母大小写）      

|参数|描述|
|---|---|
|length|生成的字符串长度|
### 4、generateString
获取指定长度纯字符串（字母大小写）         

|参数|描述|
|---|---|
|length|生成的字符串长度|
### 5、generateNumber
获取指定长度数字字符串    

|参数|描述|
|---|---|
|length|生成的字符串长度|
## 八、Bean工具类：
**使用时通过``BeanUtils.xxx()``调用**
### 1、copyProperties
属性复制，用于将一个对象的属性赋值到另一个对象，两个对象间的``参数名和数据类型必须相同``      
**参数说明**     

|参数|描述|
|-----|-----|
|source|源对象|
|target|目标对象|
|ignores|忽略的字段，设置后不会进行复制|     

### 2、toBean
将map转为bean对象     
**参数说明**      

|参数|描述|
|-----|-----|
|map|需要转为Bean的map|
|beanClass|目标Bean的class|
### 3、findMethod
查找类中的方法       
**参数说明**     

|参数|描述|
|-----|-----|
|clazz|目标类class|
|methodName|方法名|
|paramTypes|方法参数类型|
### 4、findDeclaredMethod
查找类中声明的方法       
**参数说明**     

|参数|描述|
|---|---|
|clazz|目标类class|
|methodName|方法名|
|paramTypes|方法参数类型|
### 5、setFieldValue
给类中的某个字段设置值     
**参数说明**     

|参数|描述|
|-----|-----|
|o|字段所在的对象|
|field|字段|
|value|值|
### 6、getFieldValue
获取某个字段的值    
**参数说明**    

|参数|描述|
|-----|-----|
|o|字段所在的对象|
|field|字段|
### 7、toMap
将bean对象转为map         
**参数说明**     

|参数|描述|
|-----|-----|
|bean|需要转为map的bean对象|
### 8、getInterfaceType
获取泛型接口中某个泛型的真实对象类型     
**参数说明**     

|参数|描述|
|---|---|
|source|实现泛型接口的类|
|typeInterface|泛型接口|
|paramIndex|泛型参数下标，0开始|
## 九、验证码工具类
**用于生成英文和数字混合的验证码，使用时通过构造``AuthCodeUtils``在进行调用其中的方法，构造时参数如下 :**    

|参数|描述|
|----|----|
|width|验证码图片宽度|
|height|验证码图片高度|
|codeCount|验证码字符个数|
|lineCount|验证码干扰线数|     

**该工具下的所有方法如下:**
### 1、writeToLocal
生成验证码到本地，案例如下
```java
public class Test{
    public static void main(String[] args) {
        AuthCodeUtil authCodeUtil = new AuthCodeUtil(160,40,5,150);
        try {
            String path="/文件夹路径/code.png";
            //写入到本地时可以通过getCode()方法获取生成的验证码
            String code = authCodeUtil.writeToLocal(path).getCode();
            System.out.println(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
### 2、getCode
获取生成的验证码字符
```java
public class Test{
    public static void main(String[] args){
      String code = authCodeUtil.writeToLocal(xxx).getCode;
      System.out.println(code);
    }
}
```
### 3、write
以流的方式返回给前端，案例如下
```java
public class Test{
    @GetMapping("/code")
    public void getCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        AuthCodeUtil authCodeUtil = new AuthCodeUtil(100, 50, 4, 50);
        response.setContentType("image/jpeg");
        //禁止图像缓存
        response.setHeader("param", "no-cache");
        response.setDateHeader("Expires", 0);
        authCodeUtil.write(response.getOutputStream());
    }
}
```
## 十、Id生成工具类
**项目中使用可以将``IdUtils``注册为一个bean以在其他地方进行依赖注入, 或者将其设置为``静态的``, 不要出现重复的``IdUtils``对象, 否则并发情况下会出现重复,
如果要调用``snowId``, 必须通过带参数的构造方法进行创建实例**
### 1、uuid 
获取去除``-``符号的uuid
```java
public class Test{
    public static void main(String[] args){
      IdUtils idUtils = new IdUtils();
      System.out.println(idUtils.uuid());
    }
}
```
### 2、snowId
得到一个唯一的ID，在多服务需要操作同一个数据表的情况下, 需要保证每个服务的``centerId``和``machineId``唯一   
```java
public class Test{
    public static void main(String[] args){
      IdUtils idUtils = new IdUtils(1,2);
      for(int i = 0; i < 100; i++) {
        System.out.println(idUtils.snowId());
      }
    }
}
```
## 十一、文件工具类
**对文件的一些操作，包含的所有方法如下 :**
### 1、downloadByUrl
从远程URL地址下载文件到本地

**参数说明**   

|参数|描述|
|---|---|
|fileUrl|要下载的文件URL地址|
|fileName|保存到本地的文件名, ``需要加后缀``|
|savePath|保存到本地的目录|    

### 2、downloadByStream
将本地的指定地址文件通过流下载    

**参数说明**   

|参数|描述|
|---|---|
|response|HttpServletResponse|
|file|文件对象|   

### 3、getBytes
将文件转为byte数组   
  
**参数说明**    

|参数|描述|
|---|---|
|file|文件|
### 4、writeFile
将字节数组写入到指定文件, 写入成功返回true

**参数说明**    

|参数|描述|
|---|---|
|bytes|byte数组|
|file|文件对象|   

### 5、readInputStream
从输入流读取内容并返回字节数组  
  
**参数说明**     
   
|参数|描述|
|---|---|
|InputStream|输入流|
### 6、getExtension
获取文件扩展名    
    
**参数说明**    

|参数|描述|
|---|---|
|fileName|文件名|
## 十二、邮件工具类
**用于发送邮件，支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功，使用时通过``EmailUtils.of()``生成实例之后在进行其中的方法，``of()``方法参数如下 :**     

|参数|描述|
|-----|-----|
|host|smtp服务器地址,比如qq邮箱:smtp.qq.com|
|password|发送者邮箱密码,有些邮箱需要用授权码代替密码|
|from|发送人邮箱|    

**该工具包含的所有方法如下 :**
### 1、sendEmail
发送邮件，参数如下：   

|参数|描述|
|-----|-----|
|subject|主题|
|body|邮件内容，支持HTML|
|files|要发送的附件物理地址,不要可以传null或者空数组|
|tos|收件人邮箱账号，多个使用逗号隔开|
|copyTo|抄送人地址，多个用逗号隔开，不抄送可以传null或者空字符串|    

**完整示例如下 :**
```java
public class Test{
    public static void main(String[] args) {
        boolean b = EmailUtils.of("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
                .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
        if (b) {
            System.out.println("发送成功");
        }
    }
}
```
---
**详细教程可前往博客查看: [JAVA开发常用工具](https://yq.aliyun.com/articles/704350?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**