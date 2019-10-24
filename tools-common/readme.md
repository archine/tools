# tools-common
![](https://img.shields.io/badge/version-1.2.6-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 

提供参数，时间，加密、验证码、邮件、跨域、随机数、Id生成等开发中常用到的工具。。。
## 一、导入依赖
```xml
<dependency>
  <groupId>cn.gjing</groupId>
  <artifactId>tools-common</artifactId>
  <version>1.2.6</version>
</dependency>
```
## 二、常用注解:
### 1、@NotNull 
方法参数校验，如若要排除方法中的某个参数,搭配使用``@Exclude``注解到指定参数上;
### 2、@NotEmpty
方法参数校验, 可对null和空字符串进行校验,如若要排除方法中的某个参数,搭配使用``@Exclude2``注解到指定参数上，如果需要自定义异常提示信息, 可设置``message``
> 如果是**Spring**环境, 需要手动在xml文件中进行如下配置，**SpringBoot**环境无需配置
```xml
<bean id="xxx" class="cn.gjing.tools.common.handle.ToolsCommonNotEmptyAdapter"/>
```
### 3、@EnableCors
开启全局跨域，在启动类或者任意类使用该注解即可，会走默认配置，也可以自行配置，配置示例如下：
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
## 三、返回结果模板
### 1、ResultVO
通用返回结果模板,包含``code(状态码)``和``message(提示信息)``以及``data(数据)``
### 2、PageResult
分页查询返回结果集, 包含``data(数据)``、``totalPages(总页数)``、``CurrentPage(当前页数)``、``totalRows(总条数)``、``pageRows(每页的条数)``
### 3、ErrorResult
错误返回模板, 里面包含``failure``(``状态码400``时使用,包含``code``和``message``, ``code``用于进一步确定错误), ``error``(服务器型异常,一般``用于500``等, 只包含``message``)
## 四、参数校验工具类： 
**主要提供参数校验、处理,匹配等等， 使用时通过``ParamUtils.xxx()``使用，以下为该工具的所有方法介绍 :**
* **isEmpty**：判断给定参数是否为空，可以是字符串、包装类型、数组、集合等
```java
boolean isEmpty(T str)
```
* **isNotEmpty**：判断给定是否不为空，可以是字符串、包装类型、数组、集合等。。
```java
boolean isNotEmpty(T str)
```
* **requireNotNull**：该参数不能为空，为空抛出NPE，否则返回原值
```java
T requireNotNull(T str)
```
* **ListHasEmpty**：判断集合里是否含有空值
```java
boolean ListHasEmpty(Collection<? extends T> list)
```
* **multiEmpty**：检查多参数里面是否有空值
```java
boolean multiEmpty(Object...params)
```
* **equals**：判断两个参数是否相等
```java
boolean equals(Object t, Object u)
```
* **trim**：去除字符串的空格
```java
String trim(String str)
```
* **trim**：去除集合中的空元素
```java
List<String> trim(List<String> list)
```
* **removeSymbol**：移除字符串两边的符号
```java
String removeSymbol(String str, String symbol)
```
* **removeStartSymbol**：移除字符串开始的符号
```java
String removeStartSymbol(String str, String symbol)
```
* **removeEndSymbol**：移除字符串末尾的符号
```java
String removeEndSymbol(String str, String symbol)
```
* **split**：根据符号截取
```java
String[] split(String str, String symbol)
```
* **removeAllSymbol**：移除字符串里的符号
```java
String removeAllSymbol(String str, String symbol)
```
* **contains**：判断数组里是否包含指定的值
```java
boolean contains(String[] arr, String val)
```
* **isEmail**：判断是否为email
```java
boolean isEmail(String email)
```
* **isMobileNumber**：判断是否是手机号码
```java
boolean isMobileNumber(String phone)
```
* **isTelPhone**：判断是不是电话号码
```java
boolean isTelPhone(String tel)
```
* **判断是否为邮编**
```java
boolean isPostCode(String postCode)
```
## 五、时间工具类： 
**对时间进行操作，使用时通过``TimeUtils.xxx()``调用，该工具的所有方法介绍如下 :**
* **dateToString**：获取文本格式时间
```java
String dateToString(Date date)
```
* **dateToLocalDateTime**：date转localDateTime
```java
LocalDateTime dateToLocalDateTime(Date date)
```
* **dateToLocalDate**：date转localDate
```java
LocalDate dateToLocalDate(Date date)
```
* **localDateToDate**：localDate转Date
```java
Date localDateToDate(LocalDate localDate)
```
* **LocalDateToString**：LocalDate转指定格式字符串
```java
String localDateToString(LocalDate localDate)
```
* **LocalDateTimeToString**：LocalDateTime转指定格式字符串
```java
localDateTimeToString(LocalDateTime localDateTime)
```
* **localTimeToString**：LocalTime转指定格式字符串
```java
localTimeToString(LocalTime localTime)
```
* **stringToLocalDate**：字符串日期转指定格式
```java
LocalDate stringToLocalDate(String time)
```
* **stringToLocalDateTime**：字符串日期转指定格式
```java
LocalDate stringToLocalDateTime(String time)
```
* **localDateTimeToDate**：LocalDateTime转
```java
Date localDateTimeToDate(LocalDateTime dateTime)
```
* **localDateTimeToStamp**：localDateTime转时间
```java
long localDateTimeToStamp(LocalDateTime localDateTime)
```
* **stampToLocalDateTime**：时间戳转
```java
LocalDateTime stampToLocalDateTime(Long stamp)
```
* **getYearsByStartTime**：查询一个日期（年月日）到现在过了多少
```java
Integer getYearsByStartTime(String startTime)
```
* **dateToString**：Date转字符串
```java
String dateToString(Date date, String format)
```
* **stringToDate**：字符串转Date
```java
Date stringToDate(String date)
```
* **getDate**：字符串日期转指定格式Date
```java
Date stringToDate(String date, string format)
```
* **stringDateToCalendar**：字符串时间转日期
```java
Calendar stringDateToCalendar(String str)
```
* **calendarToDate**：日期转Date
```java
Date calendarToDate(Calendar calendar, String format)
```
* **calendarToStringDate**：日期转字符串
```java
String calendarToStringDate(Calendar calendar, String format)
```
* **getAllDaysOfMonth**：获取某个时间所在月份的天数
```java
int getAllDaysOfMonth(Date date)
```
* **getDays**：获取时间的天数，如2017-12-13，返回13
```java
int getDays(Date date)
```
* **getYears**：获取时间所在的年份
```java
int getYears(Date date)    
```
* **getMonth**：获取时间所在月份
```java
int getMonth(Date date)
```
* **addMonth**：增加月份
```java
Date addMonth(Date date, int n)
```
* **addDay**：增加天数
```java
Date addDay(Date date, int n)
```
* **stringDateToStamp**：字符串日期转时间戳
```java
Long stringDateToStamp(String stringDate)
```
* **stampToStringDate**：时间戳转字符串
```java
String stampToStringDate(Long timeStamp)
````
* **dateBetween**：计算两个日期相差的天数（不包括今天）
```java
int dateBetween(String startDate, String endDate)
```
* **dateBetweenIncludeToday**：计算两个日期相差的天数（包括今天）
```java
int dateBetween(String startDate, String endDate)
```
## 六、加密工具类
**主要用于数据加密, 该工具包含的所有方法如下:**
* **encodeMd5**：MD5加密
```java
String encodeMd5(String body)
```
* **encodeBase64**：Base64加密
```java
encodeBase64(String content)
```
* **decodeBase64**：Base64解密
```java
String decodeBase64(String content)
```
* **encodeSha256Hmac**：sha566 hmac加密
```java
String encodeSha256Hmac(String str, String secret)
```
* **sha1Hmac**：sha1Hmac加密
```java
String sha1Hmac(String str, String secret)
```
* **encodeAes**：AES加密
```java
String encodeAes(String content, String password)
```
* **decodeAes**：AES解密
```java
String decodeAes(String content, String password)
```
## 七、随机数工具类
**用于随机生成数字或字符串，使用时通过``RandomUtils.xxx()``调用，该工具包含的所有方法如下 :**
* **randomInt**：获取随机整数，可设置最大值和最小值
```java
int randomInt(int min, int max)
```
* **getRandom**：获取一个Random实例
```java
Random getRandom()
```
* **generateMixString**：生成混合指定长度字符串（数字、字母大小写）
```java
String generateMixString(int length)
```
* **generateString**：获取指定长度纯字符串（字母大小写）
```java
String generateString(int length)
```
* **generateNumber**：获取指定长度数字字符串
```java
String generateNumber(int length)
```
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
```
### 2、getCode
获取生成的验证码字符
```java
String code = authCodeUtil.writeToLocal(xxx).getCode;
```
### 3、write
以流的方式返回给前端，案例如下
```java
@GetMapping("/code")
public void getCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
    AuthCodeUtil authCodeUtil = new AuthCodeUtil(100, 50, 4, 50);
    response.setContentType("image/jpeg");
    //禁止图像缓存
    response.setHeader("param", "no-cache");
    response.setDateHeader("Expires", 0);
    authCodeUtil.write(response.getOutputStream());
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