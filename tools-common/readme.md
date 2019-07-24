# tools-common
![](https://img.shields.io/badge/version-1.0.5-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 提供参数校验与处理，excel导出，时间转换，数据加密,线程池,验证码,发送邮件,开启跨域等功能...
 > **推荐使用最新版本，由于项目使用了lombok，请确保本地开发工具安装了lombok插件**      
### **安装**
```xml
<dependency>
  <groupId>cn.gjing</groupId>
  <artifactId>tools-common</artifactId>
  <version>1.0.5</version>
</dependency>
```
---
> **工具介绍**
### 一. 注解:
#### **1. @NotNull**: 
可以在普通程序和web程序中使用,适用于方法参数校验,如若要排除方法中的某个参数,搭配使用@ExcludeParam注解到指定参数上;
#### **2. @NotNull2**: 
只在web程序中使用,适用于方法,如若要排除方法中的某个参数不检验,可进行@NotNull2(exclude={"参数名1","参数名2"}),**参数名必须与方法的参数名相同**,   默认异常信息为参数不能为空,可以自定义异常信息@NotNull2(message="您要使用的异常异常"); 
**如果是Spring环境，需要手动在xml文件中进行如下配置，如果此处配置了，使用SpringBeanUtil时无需再配置，SpringBoot环境无需配置**
```xml
<bean id="toolsCommon" class="cn.gjing.handle.ToolsCommonAdapter"/>
```
#### **3. @EnableCors**: 
全局允许跨域,标注在启动类或者配置类上,默认允许开启所有跨域，也可以自定义配置
 * **yml文件**
```yaml
cors:
  # 支持的方法类型
  allowed-methods: POST,GET,DELETE,PUT
  # 支持的请求头
  allowed-headers:
  # 支持的域名
  allowed-origins:
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

### 二.  返回结果模板:   
* **ResultVo**: 通用返回结果模板,包含code(状态码),message(提示信息),data(数据)三个参数
* **PageResult**: 分页查询返回结果模板,包含data(数据)和totalPage(总页数)以及CurrentPage(当前页数),使用时可以直接使用builder构造,也可以调用其中of方法.
* **ErrorResult**: 错误返回模板, 里面包含failure(状态码400时使用,里包含code和message,code用于进一步确定错误),error(服务器型异常,一般用于500等,只包含message)
### 三. Excel:   
* **导出: response, headers, title不能为空 **
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
### 四. 实用工具类:   
#### ParamUtil： 
主要提供参数校验、处理,匹配等等;
  * **isEmpty**：判断给定参数是否为空，可以是字符串、包装类型、数组、集合。。。
      > boolean isEmpty(T str)
  * **isNotEmpty**：判断给定是否不为空，可以是字符串、包装类型、数组、集合等。。
      > boolean isNotEmpty(T str)  
  * **requireNotNull**：该参数不能为空，为空抛出NPE，否则返回原值
      > T requireNotNull(T str)
  * **ListHasEmpty**：判断集合里是否含有空值
      > boolean ListHasEmpty(Collection<? extends T> list)
  * **multiEmpty**：检查多参数里面是否有空值
      > boolean multiEmpty(Object...params)
  * **equals**：判断两个参数是否相等
      > boolean equals(Object t, Object u)
  * **trim**：去除字符串的空格
      > String trim(String str)
  * **trim**：去除集合中的空元素
      > List<String> trim(List<String> list)
  * **removeSymbol**：移除字符串两边的符号
      > String removeSymbol(String str, String symbol)
  * **removeStartSymbol**：移除字符串开始的符号
      > String removeStartSymbol(String str, String symbol)
  * **removeEndSymbol**：移除字符串末尾的符号
      > String removeEndSymbol(String str, String symbol)
  * **split**：根据符号截取
      > String[] split(String str, String symbol)
  * **removeAllSymbol**：移除字符串里的符号
      > String removeAllSymbol(String str, String symbol)
  * **contains**：判断数组里是否包含指定的值
      > boolean contains(Object[] t, Object u)
  * **isEmail**：判断是否为email
      > boolean isEmail(String email)
  * **isMobileNumber**：判断是否是手机号码
      > boolean isMobileNumber(String phone)
  * **isTelPhone**：判断是不是电话号码
      > boolean isTelPhone(String tel)
  * **判断是否为邮编**
      > boolean isPostCode(String postCode)
#### TimeUtil： 
主要用于操作时间和日期;
  * **dateToString**：获取文本格式时间
      > String dateToString(Date date)
  * **dateToLocalDateTime**：date转localDateTime
      > LocalDateTime dateToLocalDateTime(Date date)
  * **dateToLocalDate**：date转localDate
      > LocalDate dateToLocalDate(Date date)
  * **localDateToDate**：localDate转Date
      > Date localDateToDate(LocalDate localDate)
  * **LocalDateToString**：LocalDate转指定格式字符串
      > String localDateToString(LocalDate localDate)
  * **LocalDateTimeToString**：LocalDateTime转指定格式字符串
      > localDateTimeToString(LocalDateTime localDateTime)
  * **localTimeToString**：LocalTime转指定格式字符串
      > localTimeToString(LocalTime localTime)
  * **stringToLocalDate**：字符串日期转指定格式LocalDate
      > LocalDate stringToLocalDate(String time)
  * **stringToLocalDateTime**：字符串日期转指定格式LocalDateTime
      > LocalDate stringToLocalDateTime(String time)
  * **localDateTimeToDate**：LocalDateTime转Date
      > Date localDateTimeToDate(LocalDateTime dateTime)
  * **localDateTimeToStamp**：localDateTime转时间戳
      > long localDateTimeToStamp(LocalDateTime localDateTime)
  * **stampToLocalDateTime**：时间戳转LocalDateTime
      > LocalDateTime stampToLocalDateTime(Long stamp)
  * **getYearsByStartTime**：查询一个日期（年月日）到现在过了多少年
      > Integer getYearsByStartTime(String startTime)
  * **dateToString**：Date转字符串
      > String dateToString(Date date, String format)
  * **stringToDate**：字符串转Date
      > Date stringToDate(String date)
  * **getDate**：字符串日期转指定格式Date
      > Date stringToDate(String date, string format)
  * **stringDateToCalendar**：字符串时间转日期
      > Calendar stringDateToCalendar(String str)
  * **calendarToDate**：日期转Date
      > Date calendarToDate(Calendar calendar, String format)
  * **calendarToStringDate**：日期转字符串
      > String calendarToStringDate(Calendar calendar, String format)
  * **getAllDaysOfMonth**：获取某个时间所在月份的天数
      > int getAllDaysOfMonth(Date date)
  * **getDays**：获取时间的天数，如2017-12-13，返回13
      > int getDays(Date date)
  * **getYears**：获取时间所在的年份
      > int getYears(Date date)    
  * **getMonth**：获取时间所在月份
      > int getMonth(Date date)
  * **addMonth**：增加月份
      > Date addMonth(Date date, int n)
  * **addDay**：增加天数
      > Date addDay(Date date, int n)
  * **stringDateToStamp**：字符串日期转时间戳
      > Long stringDateToStamp(String stringDate)
  * **stampToStringDate**：时间戳转字符串
      > String stampToStringDate(Long timeStamp)
  * **dateBetween**：计算两个日期相差的天数（不包括今天）
      > int dateBetween(String startDate, String endDate)
  * **dateBetweenIncludeToday**：计算两个日期相差的天数（包括今天）
      > int dateBetween(String startDate, String endDate)
#### EncryptionUtil：
主要用于加密,目前含有MD5、sha256Hmac、sha1Hmac、base64;
  * **of**：生成加密实例
  * **encodeMd5**：MD5加密
  * **encodeBase64**：Base64加密
  * **decodeBase64**：Base64解密
  * **encodeSha256Hmac**：Sha356Hmac加密
  * **sha1Hmac**：sha1Hmac加密
  * **encodeAes**：AES加密
  * **decodeAes**：AES解密
  * **byteArrayToHexString**：二进制数组转16进制
#### **RandomUtil**：
随机数工具类
  * **randomInt**：获取随机整数，可设置最大值和最小值
  * **getRandom**：获取一个Random实例
  * **generateMixString**：生成混合指定长度字符串（数字、字母大小写）
  * **generateString**：获取指定长度纯字符串（字母大小写）
  * **generateNumber**：获取指定长度数字字符串
  * **uuid**：获取uuid随机字符串（去除里面的-）
#### SpringBeanUtil：
SpringBean工具类，如果是Spring环境，需要在XML文件作如下配置，如果@NotNull2注解那块进行了配置，则无需再配置,SpringBoot环境无需配置
  ```xml
  <bean id="toolsCommon" class="cn.gjing.handle.toolsCommonAdapter"/>
  ```
  * **getApplicationContext**：获取ApplicationContext实例
  * **getBean**：通过bean名称获取bean类对象获取bean
#### AuthCodeUtil: 
简单验证码工具类, 目前只支持英文和数字混合验证码,后期会加上拼图等类型验证码;
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
#### EmailUtil： 
邮件工具类,支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功
```java
里面参数主要包括: host(smtp服务器地址,比如qq邮箱:smtp.qq.com);password(发送者邮箱密码,有些邮箱需要用授权码代替密码);from(发送人邮箱地址);subject(邮件主题);body(邮件内容,支持html);   
files(要发送的附件物理地址,不要可以传null或者空数组);tos(收件人邮箱账号,多个逗号隔开);copyTo(抄送人邮箱地址,多个逗号隔开,不抄送可以传null或者空字符串"");
案例:
public static void main(String[] args) {
    boolean b = EmailUtil.of("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
            .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
    if (b) {
        System.out.println("发送成功");
    }
}
```
---
**详细教程可前往博客查看: [JAVA开发常用工具](https://yq.aliyun.com/articles/704350?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**