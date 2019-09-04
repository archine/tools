# tools-common
![](https://img.shields.io/badge/version-1.1.8-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 

提供参数，Excel，时间，加密、验证码、邮件、跨域、随机数、Id生成等开发中常用到的工具。。。
## 一、导入依赖
```xml
<dependency>
  <groupId>cn.gjing</groupId>
  <artifactId>tools-common</artifactId>
  <version>1.1.8</version>
</dependency>
```
### 使用须知
> 如果是**Spring**环境，确保一些工具的可用, 需要手动在xml文件中进行如下配置，**SpringBoot**环境无需配置
```xml
<bean id="xxx" class="cn.gjing.handle.ToolsCommonAdapter"/>
```
## 二、常用注解:
### 1、@NotNull 
方法参数校验,如若要排除方法中的某个参数,搭配使用``@ExcludeParam``注解到指定参数上;
### 2、@NotNull2
方法参数校验, 可对null和空字符串进行校验, 如果要排除某些参数, 可将这些参数的名称设置到注解的参数``exclude``里, 如果需要自定义异常提示信息, 可设置``message``
### 3、@EnableCors
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
### 1、ResultVO
通用返回结果模板,包含``code(状态码)``和``message(提示信息)``以及``data(数据)``
```java
ResultVO resultVo = ResultVO.success();
```
### 2、PageResult
分页查询返回结果集, 包含``data(数据)``和``totalPage(总页数)``以及``CurrentPage(当前页数)``, 使用时可以直接使用builder构造, 也可以调用其中``of()``方法
```java
PageResult pageResult = PageResult.of("data", 1);
```
### 3、ErrorResult
错误返回模板, 里面包含``failure``(``状态码400``时使用,包含``code``和``message``, ``code``用于进一步确定错误), ``error``(服务器型异常,一般``用于500``等, 只包含``message``)
```java
ErrorResult.error(HttpStatus.BAD_REQUEST.getMsg());
```
## 四、参数校验工具类： 
**主要提供参数校验、处理,匹配等等， 使用时通过``ParamUtil.xxx()``使用，以下为该工具的所有方法介绍 :**
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
## 五、时间工具类： 
**对时间进行操作，使用时通过``TimeUtil.xxx()``调用，该工具的所有方法介绍如下 :**
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
**主要用于数据加密，使用时通过``EncryptionUtil.xxx()``调用，该工具包含的所有方法如下:**
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
## 七、随机数工具类
**用于随机生成数字或字符串，使用时通过``RandomUtil.xxx()``调用，该工具包含的所有方法如下 :**
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
**使用时通过``BeanUtil.xxx()``调用**
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
## 九、验证码工具类
**用于生成英文和数字混合的验证码，使用时通过构造``AuthCodeUtil``在进行调用其中的方法，构造时参数如下 :**    

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
**生成无符号的UUID和通过雪花算法生成一个唯一ID，在项目使用时，先通过``@Resource``注入，然后通过``idUtil.xxx()``生成，该工具包含的方法如下 :**
### 1、uuid 
获取去除``-``符号的uuid
```java
idUtil.uuid();
```
### 2、snowId
得到一个唯一的ID，在多服务需要操作同一个数据表的情况下, 需要保证每个服务的``centerId``和``machineId``唯一   
```java
idUtil.snowId();
```
**配置如下**    
```yaml
snow:
  center-id: 数据中心id, 范围0-31, 默认0
  machine-id: 机器id, 范围0-31, 默认0
```
## 十一、文件工具类
**对文件的一些操作，使用时通过``FileUtil.of()``生成实例之后再调用其中的方法，包含的所有方法如下 :**
### 1、downloadByUrl
从远程URL地址下载文件到本地
```java
FileUtil.of().downloadByUrl(fileUrl, fileName, savePath);
```
**参数说明**   

|参数|描述|
|---|---|
|fileUrl|要下载的文件URL地址|
|fileName|保存到本地的文件名, ``需要加后缀``|
|savePath|保存到本地的目录|    

### 2、downloadByStream
将本地的指定地址文件通过流下载
```java
FileUtil.of().downlocdByStream(response, file);
```
**参数说明**   

|参数|描述|
|---|---|
|response|HttpServletResponse|
|file|文件对象|   

### 3、getBytes
将指定路径下的文件转为byte数组
```java
byte[] data = FileUtil.of().getBytes(file);
```
### 4、writeFile
将字节数组写入到指定文件, 写入成功返回true
```java
boolean b = FileUtil.of().writeFile(bytes, file);
```
**参数说明**    

|参数|描述|
|---|---|
|bytes|byte数组|
|file|文件对象|   

### 5、readInputStream
从输入流读取内容并返回字节数组
```java
byte[] b = FileUtil.of().readInputStream(inputStream);
```
### 6、getExtension
获取文件扩展名
```java
String name = FileUtil.of().getExtension(fileName);
```
## 十二、邮件工具类
**用于发送邮件，支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功，使用时通过``EmailUtil.of()``生成实例之后在进行其中的方法，``of()``方法参数如下 :**     

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
public static void main(String[] args) {
    boolean b = EmailUtil.of("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
            .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
    if (b) {
        System.out.println("发送成功");
    }
}
```
## 十三、Excel工具类
### 1、ExcelWrite2(已废弃)
**Excel导出，使用时通过``ExcelWrite2.of().doWrite()``调用，调用时所有参数如下所示**

|参数|描述|
|-----|-----|
|response|HttpServletResponse|
|data|要导出数据|
|headers|excel表头|
|fileName|Excel文件名| 
|description|Excel描述|
|cellAddresses|描述区域的样式|  
 
**定义一个Service，模拟数据**
```java
/**
 * @author Gjing
 **/
@Service
public class UserService {

    @Resource
    private IdUtil idUtil;

    public List<User> listUser() {
        List<User> data = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            User u = User.builder()
                    .birthday(new Date())
                    .userAddress("中国厦门")
                    .userId(idUtil.snowId())
                    .userName("用户" + (i + 1))
                    .build();
            data.add(u);
        }
        return data;
    }
}
```
**使用示例如下 :**
```java
/**
 * @author Gjing
 **/
@RestController
public class ExcelController {
    @Resource
    private UserService userService;

    @GetMapping("/excel")
    public void downExcel(HttpServletResponse response) {
        List<User> users = userService.listUser();
        String[] headers = {"用户id", "用户名", "用户地址", "出生日期"};
        List<Object[]> data = new ArrayList<>();
        users.forEach(e->{
            Object[] objects = new Object[headers.length];
            objects[0] = e.getUserId();
            objects[1] = e.getUserName();
            objects[2] = e.getUserAddress();
            objects[3] = e.getBirthday();
            data.add(objects);
        });
        ExcelWrite2.of(data, headers, "用户列表").doWrite(response);
    }
}
```
### 2、ExcelWriter
**Excel导出，使用时通过``ExcelWriter.of().doWrite()``调用，调用时传递的所有参数如下**    

|参数|描述|
|-----|-----|
|excelClass|关联的实体Class|
|entityList|要导出的Excel关联实体集合|
|response|HttpServletResponse|
|ignores|忽略要导出的实体的字段名, 设置后该字段将不会导出到excel表格内|      
**示例**：
```java
    @GetMapping("/excel")
    @ApiOperation(value = "导出excel")
    public void downExcel2(HttpServletResponse response) {
        List<User> userList = userService.listUser();
        ExcelWriter.of(User.class, userList).doWrite(response);
    }
```
#### I、@Excel注解
在实体类上使用，表明这个类是与Excel关联的，里面的参数包括：    

|参数|描述|
|-----|-----|
|name|导出的Excel文件名称|
|type|文档类型：``XLS``或者``XLSX``，默认``XLS``|
|valueColor|单元格内容背景色，默认白色|
|headerColor|excel列表头背景色，默认蓝色|
|descriptionColor|excel大标题颜色，默认黄色|
|description|Excel文件描述，出现在Excel的列表头之前，``可空``|
|firstRow|Excel文件描述区域的开始行，默认``0``|
|lastRow|Excel文件描述区域的截止行，默认``2``|
|firstCell|Excel文件描述区域的开始单元格，默认``0``|
|lastCell|Excel文件描述区域的截止单元格，默认``列表头的长度``|
|autoWrap|单元格内容是否自动换行，默认``true``|    
#### II、@ExcelField注解
在Excel实体类或者其父类的字段上使用，表明这是一个列表头对应的字段，参数包括      

|参数|描述|
|-----|-----|
|name|列表头名称|
|pattern|如果是时间类型, 需要指定转换的时间格式, 如``yyyy-MM-dd``, 目前仅支持字段类型为``java.util.Date``|
|width|该列的宽度, 默认20*256|
|strategy|导入excel时自动生成ID, 具体说明请往下看|     
**示例**：
```java
@Excel(name = "用户列表", type = DocType.XLSX, description = "这是用户列表", lastRow = 4,firstCell = 1)
public class User {

    @ExcelField(name = "用户id", strategy = Generate.SNOW_ID)
    private Long userId;

    @ExcelField(name = "用户名")
    private String userName;

    @ExcelField(name = "用户年龄")
    private Integer age;

    @ExcelField(name = "用户地址")
    private String userAddress;

    @ExcelField(name = "出生日期", pattern = "yyyy-MM-dd")
    private Date birthday;
}
```
#### III、@DateValidation
时间格式校验，在字段使用后会在excel中对应的列表头下的内容进行时间格式校验，``XLXS类型文档不要使用，有未知BUG``，里面的参数包括       

|参数|描述|
|-----|-----|
|boxLastRow|数据校验最多校验多少行，默认为``列表头下一行``|
|operatorType|操作类型，默认``OperatorType.BETWEEN``|
|expr1|表达式1|
|expr2|表达式2|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|allowEmpty|是否允许空值，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容|       
**示例**：
表明时间必须输入2010-12-01至2020-12-01范围内
```java
@Excel(name = "用户列表")
public class User {

    @ExcelField(name = "出生日期", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateValidation(operatorType = BETWEEN,expr1 = "2010-12-01",expr2 = "2020-12-01")
    private Date birthday;
}
```
#### IV、@ExplicitValidation
给定范围校验，使用后excel对应的列表头下的单元格内容只能选择这个范围内的数据，参数包括     

|参数|描述|
|-----|-----|
|combobox|范围值，数组类型|
|boxLastRow|数据校验最多校验多少行，默认为``列表头下一行``|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容|      
**示例**：
只能选上海和厦门两个地址，同时这两个会出现在excel下拉框中
```java
@Excel(name = "用户列表")
public class User {
    @ExcelField(name = "用户地址")
    @ExplicitValidation(combobox = {"上海","厦门"})
    private String userAddress;
}
```
#### V、@NumericValidation
数值类型校验，使用后会在excel中进行指定规则校验，里面的参数包括     

|参数|描述|
|-----|-----|
|boxLastRow|数据校验最多校验多少行，默认为``列表头下一行``|
|operatorType|操作类型，默认``OperatorType.BETWEEN``|
|validationType|校验类型，``必填``|
|expr1|表达式1，在表达式2前面，``必填``|
|expr2|表达式2，在操作类型为``BETWEEN``和``NOT_BETWEEN``情况下必填|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|allowEmpty|是否允许空值，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容|      
**示例**：
填写的数据必须小于10，且是整数
```java
@Excel(name = "用户列表")
public class User {
    @ExcelField(name = "用户年龄")
    @NumericValidation(operatorType = LESS_THAN,validationType = INTEGER,expr1 = "10")
    private Integer age;
}
```
#### VI、实体类字段支持枚举类型
很多时候, 为了方便数据库和代码层的可读性, 实体类中常常会设置枚举类型的字段, 这时候只需要在您定义的枚举类中实现``EnumConvert<T extends Enum>``接口即可, 该接口中含有两个方法
值转枚举: ``T to(Object e)``和枚举转值: ``Object from(T t)``, 在这里, ``T``代表枚举类, 例子如下: 
```java
/**
 * @author Gjing
 **/
public enum Gender implements EnumConvert<Gender> {
    MAN("男"),WOMAN("女");

    private String type;

    Gender(String type) {
        this.type = type;
    }

    public static Gender of(String type) {
        for (Gender gender : Gender.values()) {
            if (gender.type.equals(type)) {
                return gender;
            }
        }
        throw new NullPointerException("没有你的枚举类型");
    }

    @Override
    public Gender to(Object type) {
        return of(type.toString());
    }

    @Override
    public Object from(Gender gender) {
        return gender.type;
    }
}
```
#### VII、Id生成策略
有时候在``导入Excel数据``时, 并不会让用户写每个数据的ID, 然后当碰上数据库ID不是自增的时候, 这时候就需要在导入时自动生成了, 目前支持UUID和SnowId(分布式唯一Id), 使用的话只需要在实体类中对应的字段设置下策略即可, 默认``none``, 一旦设置, 手动设置将无效, 会设置自动生成的值. 设置完策略之后, 在构造``ExcelReader``时传入``IdUtil``实例,
**使用例子如下**:     
 
**实体**
```java
@Excel(name = "用户列表", type = DocType.XLSX, description = "这是用户列表", lastRow = 4,firstCell = 1)
public class User {

    @Id
    @ExcelField(name = "用户id", strategy = Generate.SNOW_ID)
    private Long userId;
}
```
**构造ExcelReader**
```java
/**
 * @author Gjing
 **/
@Service
public class UserService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private IdUtil idUtil;

    /**
     * 导入用户并存入数据库
     *
     * @param file excel文件
     */
    public void saveUser(MultipartFile file) {
        try {
            List<User> users = ExcelReader.of(User.class, file.getInputStream(), idUtil).doRead();
            if (users.isEmpty()) {
                throw new NullPointerException("数据是空的,读失败了");
            }
            userRepository.saveAll(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
### 3、ExcelReader
**Excel导入, 使用时通过``ExcelReader.of().doRead()``调用, 涉及到的参数如下:**    

|参数|描述|
|-----|-----|
|excelClass|excel关联的实体class|
|inputStream|文件输入流|
|idUtil|id生成器, 如果有字段``选择了自动生成id的策略则必须传``|
#### I、@Excel注解、@ExcelField注解
与Excel导入相同, 这里不过多介绍

---
**详细教程可前往博客查看: [JAVA开发常用工具](https://yq.aliyun.com/articles/704350?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**