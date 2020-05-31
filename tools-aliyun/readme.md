![](https://img.shields.io/badge/version-1.0.4-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)      
 
> 阿里云OSS和短信工具
# 导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-aliyun</artifactId>
    <version>1.0.4</version>
</dependency>
```
## 一、OSS
### 1、配置
**以下配置除了``后四个配置不必填``，其他都``必填``**
```yaml
tools:
  aliyun:
    # 用户key，在阿里云获取
    access-key: xxxxxxx
    # 用户秘钥，在阿里云获取
    access-key-secret: xxxxxxx
    oss:
     # 节点, 前往阿里云查看
     end-point: xxxxxxxx
     # 存储空间, 不存在会创建
     bucket: xxxxxxx
     # 最大连接数，默认1024
     max-connections: 1024
     # 最大空闲时间(毫秒)，默认60000
     idle-time: 60000
     # socket超时时间(毫秒)，默认50000
     socket-timeout: 50000
     # 连接超时时间(毫秒)，默认50000
     connection-timeout: 50000
```
### 2、文件上传
**如果有目录那么可以指定目录名称, 如果目录不存在会进行创建. 上传成功会返回``oss文件名``，获取到文件名后前端只需将bucket域名与文件名拼接即可**
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private OssUpload ossUpload;

    @PostMapping("/file")
    public String upload(MultipartFile file) {
        // 通过文件上传
        return this.ossUpload.upload(file);
    }

    @PostMapping("/file2")
    public String upload(MultipartFile file) {
        // 上传到test目录
        return this.ossUpload.upload(file, "test");
    }

    @PostMapping("/file3")
    public String upload(MultipartFile file) throws IOException {
        // 通过流或者byte[]进行上传, 需要指定文件名，前面不能出现 /
        return this.ossUpload.upload(file.getInputStream(), UUID.randomUUID().toString()+".jpg");
    }
}
```
**``upload()``方法参数如下**       

|参数|描述|
|---|---|
|fileName|上传到oss后的文件名, 重复会覆盖掉之前的，如果要上传到指定目录的话将目录一并作为文件名即可，如：files/1.jpg|
|file|文件、文件流、byte数组|
|dir|保存到bucket那个目录对应的目录名称|
### 3、文件删除
**使用``批量删除``方法会在执行完成后会返回删除成功的文件名列表，批量删除最多同时删除``1000``个**
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private OssUpload ossUpload;

    @DeleteMapping("/test1")
    public void deleteFile(String fileName) {
        // 删除指定oss文件
        this.ossUpload.deleteFile(fileName);
    }

    @DeleteMapping("/test2")
    public String deleteFiles(String[] fileNames) {
        // 批量删除
        return this.ossUpload.deleteFiles(Arrays.asList(fileNames)).toString();
    }
}
```
**``deleteFile()``、``deleteFiles()``方法参数如下**       

|参数|描述|
|---|---|
|fileName|oss文件名，上传成功后返回给您的|
|fileNames|oss文件名集合，最多同时删除``1000``个|
### 4、判断文件是否存在
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private OssDownload ossDownload;
    
    @PostMapping("/test")
    public boolean test(String fileName) {
        return this.ossDownload.isExist(fileName);
    }
}
```
**``isExist()``方法参数如下**       

|参数|描述|
|---|---|
|fileName|oss文件名，上传成功后返回给您的|
### 5、文件下载
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private OssDownload ossDownload;

    @GetMapping("/test")
    public void downLocal(String fileName, HttpServletResponse response) {
        // 1、下载到本地指定目录
        this.ossDownload.downByLocal("/Users/colin/Desktop/1/", fileName);
        // 2、通过流下载
        this.ossDownload.downByStream(fileName, response);
    }
}
```
**``downByLocal()``、``downByStream()``方法参数如下**       

|参数|描述|
|---|---|
|fileName|oss文件名，上传成功后返回给您的|
|dir|本地文件目录地址，不存在会创建|
## 二、短信
### 1、配置
**以下配置除了``region``其他都``必填``**
```yaml
tools:
  aliyun:
    # 用户key，在阿里云获取
    access-key: xxxxxxx
    # 用户秘钥，在阿里云获取
    access-key-secret: xxxxxx
    sms:
      # 短信模板ID，必须是已添加并审核通过的
      template-code: xxxxxxx
      # 短信签名名称，必须是已添加并审核通过的
      sign-name: xxxxxx
      # 区域，默认default
      region: default
```
### 2、发送短信
**短信模板变量对应的实际值**
```java
/**
 * @author Gjing
 **/
@RestController
public class SmsController {
    @Resource
    private SmsHelper smsHelper;

    @PostMapping("/sms")
    public String send(String phones) {
        Map<String, Integer> param = new HashMap<>();
        param.put("code", 12345);
        return this.smsHelper.send(phones, param);
    }
}
```
**``send()``方法参数如下**       

|参数|描述|
|---|---|
|phones|11位手机号,多个用英文逗号隔开,上限为``1000``个|
|templateCode|短信模板code，必须是已存在且审核通过|
|signName|短信签名名称，必须是已存在且审核通过|
|param|短信模板变量对应的实际值|
### 3、查询指定号码发送记录
****
```java
/**
 * @author Gjing
 **/
@RestController
public class SmsController {
    @Resource
    private SmsHelper smsHelper;

    @GetMapping("/sms_record")
    public String findSmsRecord(String phone) {
        return this.smsHelper.findSendDetail(phone, "2020-02-01", 1, 5);
    }
}
```
**``findSendDetail()``方法参数如下**       

|参数|描述|
|---|---|
|phone|11位手机号|
|sendDate|发送日期，格式：yyyy-MM-dd|
|page|页数|
|row|每页条数，最大``50``|
---