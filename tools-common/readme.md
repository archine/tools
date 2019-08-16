# tools-common
![](https://img.shields.io/badge/version-1.1.1-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 
提供参数校验与处理，excel导出，时间转换，数据加密、验证码、发送邮件、开启跨域、随机数、Id生成等工具... 
## 一、导入依赖
```xml
<dependency>
  <groupId>cn.gjing</groupId>
  <artifactId>tools-common</artifactId>
  <version>1.1.1</version>
</dependency>
```     
## 二、注解:
#### 1、@NotNull 
方法参数校验,如若要排除方法中的某个参数,搭配使用``@ExcludeParam``注解到指定参数上;
#### 2、@NotNull2
方法参数校验, 可对null和空字符串进行校验, 如果要排除某些参数, 可将这些参数的名称设置到注解的参数``exclude``里, 如果需要自定义异常提示信息, 可设置``message`` 
, ``如果是Spring环境，需要手动在xml文件中进行如下配置，如果此处配置了，使用BeanUtil时无需再配置，SpringBoot环境无需配置``
```xml
<bean id="toolsCommon" class="cn.gjing.handle.ToolsCommonAdapter"/>
```
#### 3、@EnableCors
开启允许跨域，在启动类或者任意类使用该注解即可，会走默认配置，也可以自行配置，配置示例如下：
* **yml方式**
```yaml
cors:
  # 支持的方法类型
  allowed-methods: POST,GET,DELETE,PUT...
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
    public Cors cors() {
        return Cors.builder()
                .allowCredentials(Boolean.TRUE)
                .maxAge(1800L)
                .path("/**")
                .build();
    }
}
```
## 三、返回结果模板
#### 1、ResultVO
通用返回结果模板,包含``code(状态码)``和``message(提示信息)``以及``data(数据)``
```java
ResultVO resultVo = ResultVO.success();
```
#### 2、PageResult
分页查询返回结果集, 包含``data(数据)``和``totalPage(总页数)``以及``CurrentPage(当前页数)``, 使用时可以直接使用builder构造, 也可以调用其中``of()``方法
```java
PageResult pageResult = PageResult.of("data", 1);
```
#### 3、ErrorResult
错误返回模板, 里面包含``failure``(``状态码400``时使用,包含``code``和``message``, ``code``用于进一步确定错误), ``error``(服务器型异常,一般``用于500``等, 只包含message)
```java
ErrorResult.error(HttpStatus.BAD_REQUEST.getMsg());
```
## 四、Excel导出
**Excel文件导出**
```java
@RequestMapping("/excel")
public void excel(HttpServletResponse response) {
    String[] headers = {"标题1", "标题2"};
    ExcelUtil.excelExport(response, null, headers, "测试无内容excel", null);
}

@RequestMapping("/excel2")
public void excelContainsValue(HttpServletResponse response) {
    String[] headers = {"标题1", "标题2"};
    List<Object[]> data = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        Object[] objects = new Object[headers.length];
        objects[0] = i;
        objects[1] = i+1;
        data.add(objects);
    }
    ExcelUtil.excelExport(response, data, headers, "测试含有内容excel", null);
}

@RequestMapping("/excel3")
public void excelContainsInfo(HttpServletResponse response) {
    String[] headers = {"标题1", "标题2"};
    List<Object[]> data = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        Object[] objects = new Object[headers.length];
        objects[0] = i;
        objects[1] = i+1;
        data.add(objects);
    }
    ExcelUtil.excelExport(response, data, headers, "测试含有详情的excel", "详情");
}
```
## 五、实用工具类:   
### 1、ParamUtil： 
**主要提供参数校验、处理,匹配等等**
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
boolean contains(Object[] t, Object u)
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
### 2、TimeUtil： 
**操作时间和日期**
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
### 3、EncryptionUtil
**主要用于加密,目前含有MD5、sha256Hmac、sha1Hmac、base64**
* **of**：生成加密实例
```java
EncryptionUtil of()
```
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
### 4、RandomUtil
**随机数工具类**
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
### 5、BeanUtil：
**SpringBean工具类，如果是Spring环境，需要在XML文件作如下配置，如果@NotNull2注解那块进行了配置，则无需再配置,SpringBoot环境无需配置**
```xml
<bean id="toolsCommon" class="cn.gjing.handle.toolsCommonAdapter"/>
```
#### a. getApplicationContext
获取ApplicationContext实例       
```java
BeanUtil.getApplicationContext()
```
#### b. getBean
通过bean名称获取bean类对象获取bean     
```java
BeanUtil.getBean(Class<T> beanClass)
```
#### c. copyProperties
复制属性值,用于将一个对象的属性值复制到另一个对象,``两个对象间属性的数据类型和属性名要相同``     
```java
BeanUtil.copyProperties(Object source, Class<T> target, String... ignores)
```    
**参数说明**      
    
|参数|描述|
|-----|-----|
|source|原对象|
|target|目标对象|
|ignores|字段名|     

#### d. toMap
将bean转为map     
```java
BeanUtil.toMap(Object bean)
```
#### e. toBean
将Map转为bean     
```java
BeanUtil.toBean(Map<String, ?> map, Class<T> bean)
```
### 5、AuthCodeUtil
**简单验证码工具类, 支持英文和数字混合验证码**
```java
//第一种情况: 生成验证码到本地指定路径,以下为简单测试,具体逻辑根据业务需求自行设计
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
//第二种情况: 以流的方式返回给前端
@GetMapping("/code")
public void getCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
    AuthCodeUtil authCodeUtil = new AuthCodeUtil(100, 50, 4, 50);
    response.setContentType("image/jpeg");
    //禁止图像缓存
    response.setHeader("param", "no-cache");
    response.setDateHeader("Expires", 0);
    //该案例将验证码存在session中,具体业务中根据场景自行设计
    HttpSession session = request.getSession();
    session.setAttribute("code", authCodeUtil.getCode());
    authCodeUtil.write(response.getOutputStream());
}

/**
 * 验证码验证,由于示例为服务端session存储,所以下面为session方式验证,具体根据个人存储条件相应更改
 * @param code 前端传来的验证码
 * @param request request
 * @return .
 */
@PostMapping("/verifyCode")
@NotNull2(exclude = {"request"})
public ResultVo verifyCode(String code, HttpServletRequest request) {
    HttpSession httpSession = request.getSession();
    String sessionCode = (String) httpSession.getAttribute("code");
    if (ParamUtil.isEmpty(sessionCode)) {
        return ResultVo.error(null, "code不存在");
    }
    if (sessionCode.toLowerCase().equals(code.toLowerCase())) {
        return ResultVo.success();
    }
    return ResultVo.error(null,"invalid code");
}
```
### 6、IdUtil
**Id生成工具类,提供生成UUID和分布式ID, 项目中使用通过``@Resource``注入**
#### a. uuid
获取去除``-``符号的uuid
```java
idUtil.uuid();
```
#### b. snowId
**获取分布式Id, 通过雪花算法生成, 在多服务需要操作同一个数据表的情况下, 需要保证每个服务的``centerId``和``machineId``唯一**   
```java
idUtil.snowId();
```
**配置如下**    
```yaml
snow:
  center-id: 数据中心id, 范围0-30, 默认0
  machine-id: 机器id, 范围0-30, 默认0
```   
### 7、EmailUtil
**邮件工具类,支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功**
```java
public static void main(String[] args) {
    boolean b = EmailUtil.of("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
            .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
    if (b) {
        System.out.println("发送成功");
    }
}
```
**参数说明**    

|参数|描述|
|-----|-----|
|host|smtp服务器地址,比如qq邮箱:smtp.qq.com|
|password|发送者邮箱密码,有些邮箱需要用授权码代替密码|
|from|发送人邮箱|
|subject|主题|
|body|邮件内容，支持HTML|
|files|要发送的附件物理地址,不要可以传null或者空数组|
|tos|收件人邮箱账号，多个使用逗号隔开|
|copyTo|抄送人地址，多个用逗号隔开，不抄送可以传null或者空字符串|
---
**详细教程可前往博客查看: [JAVA开发常用工具](https://yq.aliyun.com/articles/704350?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**