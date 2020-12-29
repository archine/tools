![](https://img.shields.io/badge/version-1.0.9-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
![](https://img.shields.io/badge/builder-success-green.svg)

> 阿里云OSS和短信工具

# 导入依赖

```xml

<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-aliyun</artifactId>
    <version>1.0.9</version>
</dependency>
```

## 一、OSS

### 1、配置

**以下配置除了``后四个配置不必填``，其他都``必填``**

```yaml
tools:
  aliyun:
    # 用户key，在阿里云获取，此处为全局配置，短信和oss未单独设置则会默认调用此处配置
    access-key: xxxxxxx
    # 用户秘钥，在阿里云获取，此处为全局配置，短信和OSS都会默认调用此处的key
    access-key-secret: xxxxxxx
    oss:
      # 用户key，在阿里云获取，此处优先级高于全局配置
      access-key: xxxxxxx
      # 用户秘钥，此处优先级高于全局配置
      access-key-secret: xxxxxxx
      # 节点, 前往阿里云查看
      end-point: xxxxxxxx
      # 存储空间,不存在会创建
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
* **简单上传**    

适用于上传小文件，成功后会返回文件名，前端只需要将文件名与bucket域名拼接即可，bucket域名可在oss控制台，
点击bucket列表，找到你使用的bucket点进去后点击概览，就可以在右边界面看到了，或者自己拼接：``https://<bucket>.<endPoint>``。

```java
import cn.gjing.tools.aliyun.oss.SimpleOssUpload;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private SimpleOssUpload simpleOssUpload;

    @PostMapping("/file")
    public String upload(MultipartFile file) {
        // 直接上传，其他参数使用默认方式
        return this.simpleOssUpload.upload(file);
    }

    @PostMapping("/file2")
    public String upload(MultipartFile file) {
        // 手动设置文件名，默认为源文件的文件名，也可以指定包含文件后缀在内的完整路径
        return this.simpleOssUpload.upload(file, "目录1/123.jpg");
    }

    @PostMapping("/file3")
    public String upload(MultipartFile file) {
        // 可以手动设置bucket，否则读取yml文件中配置的bucket, bucket如果在阿里云oss不存在，会自动创建
        return this.simpleOssUpload.upload(file, "文件名", "bucket");
    }

    @DeleteMapping("/test1")
    public void deleteFile(String fileName) {
        // 删除指定oss文件，文件名为上传文件后返回的
        this.simpleOssUpload.deleteFile(fileName);
    }

    @DeleteMapping("/test2")
    public String deleteFiles(String[] fileNames) {
        // 批量删除, 会在执行完成后会返回删除成功的文件名列表，批量删除最多同时删除``1000``个
        return this.simpleOssUpload.deleteFiles(Arrays.asList(fileNames)).toString();
    }

    @PostMapping("/test3")
    public boolean existFile(String fileName) {
        // 判断文件是否存在，存在返回true
        return this.simpleOssUpload.existFile(fileName);
    }
}
```
* **分片上传**     
适用于大文件上传，通过分片，加快上传的速度，上传成功后返回一个``FileMeta``，里面包含``fileName``、``uploadId``、``bucket``。``默认分片切割大小为1M``

```java
import cn.gjing.tools.aliyun.oss.FileMeta;
import cn.gjing.tools.aliyun.oss.MultipartOssUpload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private MultipartOssUpload multipartOssUpload;

    @PostMapping("/file")
    public String upload(MultipartFile file) {
        // 直接上传，其他的参数使用默认
        return this.multipartOssUpload.upload(file).getFileName();
    }

    @PostMapping("/file2")
    public String upload(MultipartFile file) {
        // 手动设置文件名，默认获取源文件的文件名, 也可以指定包含文件后缀在内的完整路径
        return this.multipartOssUpload.upload(file, "目录1/123.jpg").getFileName();
    }

    @PostMapping("/file3")
    public String upload(MultipartFile file) {
        // 可以手动设置bucket，否则读取yml文件中配置的bucket，bucket如果在阿里云oss不存在，会创建
        return this.multipartOssUpload.upload(file, "文件名", "bucket").getFileName();
    }
}
```
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
      # 用户key，在阿里云获取，此处优先级高于全局配置
      access-key: xxxxxxx
      # 用户秘钥，此处优先级高于全局配置
      access-key-secret: xxxxxxx
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