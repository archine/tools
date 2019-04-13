# Gjing tools for java :smile:
![](https://img.shields.io/badge/version-1.1.4-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)   
tools是一个用于提供快速开发的工具包，整合了很多实际项目开发中所要用到的功能，杜绝每次新项目都要封装或者其他地方拷贝工具类,繁琐而费时.   
笔者技术有限,欢迎各位大佬指点,本人会及时优化.   
> **推荐使用最新版本**
     
项目宗旨:   
* 提高开发速度;   
* 提供用于项目开发的公共工具，而不必在每次编写项目时重新封装;   
* 解决新人刚去一家公司对已存在的工具类的反感.
* 简单、实用;     
     
**安装**
---
在您的项目中使用Java Gjing tools的推荐方法是从Maven中使用它。如下:
* <a href="https://mvnrepository.com/artifact/cn.gjing/all/" title="整合包">cn.gjing.all</a> （整合所有工具包）
* <a href="https://mvnrepository.com/artifact/cn.gjing/common/" title="公用组件包">cn.gjing.common</a> （参数处理，excel，时间，加密,线程池,验证码等）
* <a href="https://mvnrepository.com/artifact/cn.gjing/alitx/" title="腾讯和阿里工具包">cn.gjing.alitx</a> （阿里和腾讯的常用工具：短信，OSS）
* <a href="https://mvnrepository.com/artifact/cn.gjing/autoswagger/" title="swagger包">cn.gjing.autoswagger</a> （swagger）
* <a href="https://mvnrepository.com/artifact/cn.gjing/httpclient/" title="http工具包">cn.gjing.httpclient</a> （http请求工具)   
 :exclamation: 请注意，项目JDK版本支持1.8+
---
:heart_eyes: 有关以其他方式或者安装最新SDK的更多信息，请访问 <a href='https://mvnrepository.com/artifact/cn.gjing/'>中心库</a>   
---
快速举例(jar包区分):   
> **alitx**   
     
阿里OSS:   
1. 构建oss模板;   
2. 调用方法   
* 文件简单上传（最大文件不能超过5G）上传成功后返回图片地址,失败会返回错误信息,后续根据返回结果自行处理;
```
@RequestMapping("/testUpload")
@ApiOperation(value = "文件上传", httpMethod = "POST", response = ApiResponse.class)
public ResultVo testUpload(@RequestParam(name = "file") MultipartFile file) {
    OssModel ossModel = OssModel.builder().accessKeyId("您的accessKeyId").accessKeySecret("您的accessKey秘钥").bucketName("存储空间名字")
            .endPoint("oss访问域名").build();
    //文件上传后得到的文件url地址
    String fileOssUrl = AliOss.upload(file, ossModel);
    return ResultVo.success(upload);
}
```   
* 删除文件: 返回true为删除成功,支持删除多个文件,当前演示只删除指定文件,:exclamation: fileOssUrl为上传oss成功后返回的路径
```
@PostMapping("/deleteImg")
@ApiOperation(value = "文件删除", httpMethod = "POST", response = ApiResponse.class)
public ResultVo deleteImg(String fileOssUrl) {
    OssModel ossModel = OssModel.builder().accessKeyId("您的accessKeyId").accessKeySecret("您的accessKey秘钥").bucketName("存储空间名字")
            .endPoint("oss访问域名").build();
    //为ture表示执行成功
    boolean b = AliOss.delete(Arrays.asList("上传文件后返回的fileOssUrl,支持多个"), ossModel);
    if (b) {
        return ResultVo.success();
    }
    return ResultVo.error();
}
```   
下载文件: fileOssUrl为上传oss成功后返回的url,localFilePath为你要保存的本地目录(不存在会创建),返回true为成功;
```
@RequestMapping("/downloadImg")
@ApiOperation(value = "文件下载", httpMethod = "POST", response = ApiResponse.class)
public ResultVo downloadImg(String fileOssUrl,String localFilePath) {
    OssModel ossModel = OssModel.builder().accessKeyId("您的accessKeyId").accessKeySecret("您的accessKey秘钥").bucketName("存储空间名字")
            .endPoint("oss访问域名").build();
    //为ture表示执行成功,成功后会自动保存到你指定的目录
    boolean b = AliOss.downloadFile(ossModel, fileOssUrl, "本地目录");
    if (b) {
        return ResultVo.success();
    }
    return ResultVo.error();
}
```
流式下载文件:
```
@RequestMapping("/streamDown")
@ApiOperation(value = "文件下载", httpMethod = "POST", response = ApiResponse.class)
public ResultVo downloadImg(String fileOssUrl) {
    OssModel ossModel = OssModel.builder().accessKeyId("您的accessKeyId").accessKeySecret("您的accessKey秘钥").bucketName("存储空间名字")
            .endPoint("oss访问域名").build();
    //为ture表示执行成功,成功后会自动保存到你指定的目录
    boolean b = AliOss.downloadStream(ossModel, fileOssUrl);
    if (b) {
        return ResultVo.success();
    }
    return ResultVo.error();
}
```
阿里短信:
* 发送短信: 返回内容中含有ok表示发送成功 ,更多状态码请前往<a href="https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.14.633f56e06vZoyq">状态码</a>
```
public static void main(String[] args) {
    AliSmsModel model = new AliSmsModel("您的accessKeyId","您的accessKey秘钥");
    //参数,必须与对应的短信模板参数对应,如果没有参数,可以传null或者空map
    Map<String, String> param = new HashMap<>(16);
    //返回发送结果
    String result = AliSms.send(model,"接收的短信的手机号,支持多个,英文逗号隔开", "短信模板code", param, "短信签名");
    System.out.println(result);
}
```
* 查询短信, :exclamation: 日期必须为yyyyMMdd格式(20181207),仅支持最近30天;返回OK代表请求成功,更多状态码前往: <a href="https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.13.450fbc454bQfCJ">状态码</a>
```
public static void main(String[] args) {
    AliSmsModel model = new AliSmsModel("您的accessKeyId","您的accessKey秘钥");
    String result = AliSms.querySendDetails(model,"查询的手机号", "查询日期","每页显示的短信数量取值范围1-50",
    "指定发送记录要查看的页码(最小为1)");
    System.out.println(result);
}
```
腾讯短信:   
1. 构建sms模板;   
2. 调用方法 
* 指定模板ID单发短信, :exclamation: 手机号必须为单个,否则抛出号码长度异常,参数必须与你选择的模板参数对应和顺序对应;发送结果 0 表示成功(计费依据)，非 0 表示失败 ,
  更多错误码请前往: <a href="https://cloud.tencent.com/document/product/382/3771">错误码</a>
```
public static void main(String[] args) {
    TxSmsModel smsModel = TxSmsModel.builder().appId("您的appid").appKey("您的appid对应的key").smsTemplateId("短信模板id")
            .smsSign("短信签名").params("短信模板中对应的参数").phoneNumbers("接收短信的手机号").build();
    String result = TxSms.send(smsModel);
    System.out.println("result: "+result);
}
```
* 指定模板ID群发短信, :exclamation: 群发一次请求最多支持200个号码。发送结果 0 表示成功(计费依据)，非 0 表示失败,   
  更多错误码请前往: <a href="https://cloud.tencent.com/document/product/382/3771">错误码</a>
```
public static void main(String[] args) {
    TxSmsModel smsModel = TxSmsModel.builder().appId("您的appid").appKey("您的appid对应的key").smsTemplateId("短信模板id")
           .smsSign("短信签名").params("短信模板中对应的参数").phoneNumbers("接收短信的手机号").build();
    String result = TxSms.multiSend(smsModel);
    System.out.println("result: "+result);
}
```
> **httpclient**

REST请求(支持http/https,以及代理)   
1. 执行成功后会返回请求结果,不需要的参数可以传null,   
    :exclamation: 请求地址不允许为空;
* POST
```
public static void main(String[] args) {
    //如果有参数，放map里
    Map<String, String> param = new HashMap<>(16);
    //get请求中参数根据实际情况填写
    String result = HttpClient.post("url", param ,null ,null ,null)
    System.out.println(result);
}
```
* GET
```
public static void main(String[] args) {
    Map<String, String> param = new HashMap<>(16);
    //get请求中参数根据实际情况填写
    String result = HttpClient.get("url", param ,null ,null ,null)
    System.out.println(result);
}
```
* UrlUtil: 用于url的处理,目前含有unicode字符编码排序(字典序),url参数转map,restTemplate请求url拼接;
```
里面包括(方法名:对应功能):
    1.urlAppend: url和参数拼接,结果为http://127.0.0.1:8080/test?param1={param1}; 2.unicodeSort:参数按照字段名的Unicode码从小到大排序（字典序);   
    3.urlParamToMap:将url中的参数转成map; 
```
> **autoswagger**
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
> **common**

注解:
```
* @NotNull: 只在web程序中使用,适用于方法,如若要排除方法中的某个参数不检验,可进行@NotNull(exclude={"参数名1","参数名2"}),:exclamation: 参数名必须与方法的参数名相同,   
            默认异常信息为参数不能为空,可以自定义异常信息@NotNull(message="您要使用的异常异常");
* @NotNull2: 可以在普通程序和web程序中使用,适用于方法参数校验,如若要排除方法中的某个参数,搭配使用@ExcludeParam注解到参数上;   
* @EnableCors: 允许跨域,标注在启动类上
```  
返回结果模板:   
```
* ResultVo: 用于接口返回,包含code(状态码),msg(提示信息)和data(数据), 如若分页查询,可以搭配PageResult使用,设置进data,随同一起返回,pageResult里设置数据和总页数;   
```
Excel:   
* 导出: :exclamation: response, excel表头,excel文件名,不能为空 
```
@RequestMapping("/excel")
@ApiOperation(value = "excel导出", httpMethod = "GET", response = ApiResponse.class)
public void zsyProductTemplate(HttpServletResponse response) {
    ExcelUtil.excelExport(response, "要导出的数据(没有数据可以传null)", "excel表头", "excel文件名", "excel额外的内容(不需要可以传null,一般用于介绍或者总概括excel)");
}
```
实用工具类:   
* ParamUtil: 主要提供参数校验、处理,匹配等等;
```
里面主要包括(方法名:对应功能): 
    1.isEmpty: 判断给定参数是否为空; 2.isNotEmpty: 不为空; 3.listIncludeEmpty: 判断集合里是否含空; 4.requireNotNull: 不为空返回原值;   
    5.multiParamIncludeEmpty: 多参数判断是否含空; 6.equals: 判断两个参数是否相等; 7.trim: 去除参数或者集合里每个参数的多余的空;   
    8.toUpperCase/toLowerCase: 大小写; 9.removeSymbol: 移除字符串两边的符号,对应的startSymbol和endSymbol为首尾坐标; 10.removeSymbol2:移除参数中所有的符号;   
    11.split: 根据符号分割; 12.contains: 判断数组里是否含某值; 13.其他手机号,邮箱等匹配.
```
* TimeUtil: 主要用于操作时间和日期;
```
里面包括(方法名:对应功能):
    1.getDateAsString: 获取文本时间; 2.getDate:获取Date; 3.stringDateToCalendar: 文本时间转成日期; 4.calendarToDate:日期转Date; 5.calendarToStringDate: 日期转文本;   
    6.getAllDaysOfMonth: 获取某个日期的当月总天数; 7.getDays: 获取给定日期的天数; 8.getYears:获取给定日期的年份; 9:getMonth:获取给定日期的月份; 10.addMonth:给定日期增加月份.   
    11.addDay:日期上增加天数; 12.stringDateToStamp: 文本时间转为时间戳; 13.stampToStringDate:时间戳转为文本时间; 14: dateBetween:获取两个日期相差的天数,带include的为包括今天;   15.dateToLocalDateTime:Date转LocalDateTime; 16.dateToLocalDate:Date转LocalDate; 17. localDateToDate: LocalDate转Date; 18.localDateToString: localDate转String;   
    19: stringToLocalDate: String格式日期转LocalDate; 20: localDateTimeToStamp: localDateTime转时间戳; 21: getYearsByStartTime: 查询指定日期距离当前多少年;   
    22: stampToLocalDateTime: 时间戳转LocalDateTime.......
```
* EncryptionUtil: 主要用于加密,目前含有MD5、sha256Hmac、sha1Hmac、base64;
* ExecutorUtil: 线程池工具类，暂时含有无返回值调用和有返回值调用;
```
里面包括(方法名:对应功能):
    1. execute: 无返回值调用; 2.submit: 有返回值调用; 3.cancel:任务移除等待队列;
```
* AuthCodeUtil: 简单验证码工具类, 目前只支持英文和数字混合验证码,后期会加上拼图等类型验证码;
```
//第一种情况: 生成验证码到本地指定路径,以下为简单测试,具体逻辑根据业务需求自行设计
public static void main(String[] args) {
    AuthCodeUtil authCodeUtil = new AuthCodeUtil(160,40,5,150);
    try {
        String path="指定的路径/文件名/".png";
        System.out.println(vCode.getCode()+" >"+path);
        authCodeUtil.write(path);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
//第二种情况: 以流的方式返回给前端
@RequestMapping("/code")
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
@RequestMapping("/verifyCode")
@NotNull(exclude = {"request"})
public ResultVo verifyCode(String code,HttpServletRequest request) {
    HttpSession httpSession = request.getSession();
    String sessionCode = (String) httpSession.getAttribute("code");
    if (ParamUtil.paramIsEmpty(sessionCode)) {
        return ResultVo.error(HttpStatus.BAD_REQUEST.getMsg());
    }
    if (sessionCode.toLowerCase().equals(code.toLowerCase())) {
        return ResultVo.success();
    }
    return ResultVo.error("invalid code");
}
```
* EmailUtil: 邮件工具类,支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功,否则抛出GjingException.
```
里面参数主要包括: host(smtp服务器地址,比如qq邮箱:smtp.qq.com);password(发送者邮箱密码,有些邮箱需要用授权码代替密码);from(发送人邮箱地址);subject(邮件主题);body(邮件内容,支持html);   
files(要发送的附件物理地址,不要可以传null或者空数组);tos(收件人邮箱账号,多个逗号隔开);copyTo(抄送人邮箱地址,多个逗号隔开,不抄送可以传null或者空字符串"");
案例:
public static void main(String[] args) {
    boolean b = EmailUtil.getInstance("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
            .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
    if (b) {
        System.out.println("发送成功");
    }
}
```
#### **作者**
* Gjing:sunny:使用中出现任何BUG和有任何疑问可以添加我QQ: 87096937,我会及时回复并验证BUG并且发布更新.
