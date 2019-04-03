# Gjing tools for java :smile:
![](https://img.shields.io/badge/version-1.1.0-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)   
tools是一个用于提供快速开发的工具包，整合了很多实际项目开发中所要用到的功能，从而摆脱每次构建项目都要重复封装，造成时间的浪费。    
     
项目宗旨:   
* 提高开发速度;   
* 提供用于项目开发的公共工具，而不必在每次编写项目时重新封装;   
* 简单、实用;     
     
**安装**
---
在您的项目中使用Java Gjing tools的推荐方法是从Maven中使用它。如下:
* <a href="https://mvnrepository.com/artifact/cn.gjing/all/" title="整合包">cn.gjing.all</a> （整合所有工具包）
* <a href="https://mvnrepository.com/artifact/cn.gjing/common/" title="公用组件包">cn.gjing.common</a> （参数处理，excel，时间，加密等）
* <a href="https://mvnrepository.com/artifact/cn.gjing/alitx/" title="腾讯和阿里工具包">cn.gjing.alitx</a> （阿里和腾讯的常用工具：短信，OSS）
* <a href="https://mvnrepository.com/artifact/cn.gjing/autoswagger/" title="swagger包">cn.gjing.autoswagger</a> （swagger）
* <a href="https://mvnrepository.com/artifact/cn.gjing/httpclient/" title="http工具包">cn.gjing.httpclient</a> （http请求工具)   
 :exclamation: 请注意，项目JDK版本支持1.8+
---
:heart_eyes: 有关以其他方式或者安装最新SDK的更多信息，请访问 <a href='https://mvnrepository.com/artifact/cn.gjing/'>中心库</a>   
---
快速举例:   
   
阿里OSS:   
1. 构建oss模板;   
2. 调用方法   
* 文件简单上传（最大文件不能超过5G）上传成功后返回图片地址,失败会返回错误信息,后续根据返回结果自行处理;
```
@RequestMapping("/testUpload")
@ApiOperation(value = "文件上传", httpMethod = "POST", response = ApiResponse.class)
public ResultVo testUpload(@RequestParam(name = "file") MultipartFile file) {
    OssModel ossModel = OssModel.builder().accessKeyId("您的accessKeyId").accessKeySecret("您的accessKey秘钥").bucketName("存储空间名字")
            .endPoint("oss访问域名").fileDir("存放文件到哪个目录,可以不传,最好传一下,不同类型文件放不同目录").build();
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
下载文件: fileOssUrl为上传oss成功后返回的url,localFilePath为下载到本地的什么位置,目录不存在会创建,文件后缀必须与要下载的文件后缀对应,返回true为成功;
```
@RequestMapping("/downloadImg")
@ApiOperation(value = "文件下载", httpMethod = "POST", response = ApiResponse.class)
public ResultVo downloadImg(String fileOssUrl,String localFilePath) {
    OssModel ossModel = OssModel.builder().accessKeyId("您的accessKeyId").accessKeySecret("您的accessKey秘钥").bucketName("存储空间名字")
            .endPoint("oss访问域名").build();
    //为ture表示执行成功,成功后会自动保存到你指定的目录
    boolean b = AliOss.downloadFile(ossModel, fileOssUrl, localFilePath);
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
REST请求(支持http/https,以及代理)   
1. 可以使用@Resourc和@AutoWired自动注入(使用于Spring项目),也可以使用new的方式(其他项目),本案例使用new的方式,执行成功后会返回请求结果,不需要的参数可以传null,   
    :exclamation: 请求地址不允许为空;
* POST
```
public static void main(String[] args) {
    HttpClient client = new HttpClient();
    Map<String, String> param = new HashMap<>(16);
    System.out.println(client.post("url", param));
}
```
* GET
```
public static void main(String[] args) {
    HttpClient client = new HttpClient();
    Map<String, String> param = new HashMap<>(16);
    System.out.println(client.get("url", param));
}
```
注解:    
```
* @NotNull: 只在web程序中使用,适用于方法,如若要排除方法中的某个参数不检验,可进行@NotNull(exclude={"参数名1","参数名2"}),:exclamation: 参数名必须与方法的参数名相同,   
            默认异常信息为参数不能为空,可以自定义异常信息@NotNull(message="您要使用的异常异常");
* @NotNull2: 可以在普通程序和web程序中使用,适用于方法参数校验,如若要排除方法中的某个参数,搭配使用@ExcludeParam注解到参数上;   
* @EnableSwagger: 启动swagger(如果您的项目需要用到swagger,可以直接在启动类上使用该注解,并且在您的配置文件中设置扫描路径等参数,
                  包路径不能为空,其他几个参数可以为空),配置文件前缀为'cn.gjing.swagger';
```  
返回结果模板:   
```
* ResultVo: 用于接口返回,包含code(状态码),msg(提示信息)和data(数据), 如若分页查询,可以搭配PageResult使用,设置进data,随同一起返回,pageResult里设置数据和总条数;   
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
1. ParamUtil: 主要提供参数校验、处理,字符串符号移除, 分割,匹配是否为邮件地址或者手机号等等;
2. TimeUtil: 主要用于操作时间和日期;
3. EncryptionUtil: 主要用于加密,目前含有MD5、sha256Hmac、sha1Hmac、base64
4. UrlUtil: 用于url的处理,目前含有unicode字符编码排序(字典序),url参数转map,restTemplate请求url拼接
#### **作者**
* Gjing:sunny:有任何疑问可以添加我QQ: 87096937
