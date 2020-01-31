![](https://img.shields.io/badge/version-1.0.0-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)      
 
> 阿里云OSS和短信工具
## 一、导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-aliyun</artifactId>
    <version>1.0.0</version>
</dependency>
```
## 二、配置
```yaml
oss:
  # 节点, 前往阿里云查看
  end-point: xxxxxxxx
  # 存储空间, 不存在会创建
  bucket: xxxxxxx
  # 阿里云用户key
  access-key: xxxxxx
  # 秘钥
  access-key-secret: xxxxxx
  # 最大连接数, 默认1024
  max-connections: 1024
  # 最大空闲时间, 默认60000ms
  idle-time: 60000
  # socket超时时间, 默认50000ms
  socket-timeout: 50000
  # 连接超时时间
  connection-timeout: 60000
```
## 三、上传和删除
### 1、上传
**如果有目录那么可以指定其名称, 如果目录不存在会进行创建. 上传成功会返回``oss文件名``**
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
        return this.ossUpload.upload(file);
    }

    @PostMapping("/file2")
    public String upload(MultipartFile file) {
        return this.ossUpload.upload(file, "test");
    }

    @PostMapping("/file3")
    public String upload(MultipartFile file) throws IOException {
        return this.ossUpload.upload(file.getInputStream(), UUID.randomUUID().toString()+".jpg");
    }
}
```
### 2、删除
**``批量删除``执行完成后会返回删除成功的文件名列表**
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
        this.ossUpload.deleteFile(fileName);
    }

    @DeleteMapping("/test2")
    public String deleteFiles(String[] fileNames) {
        return this.ossUpload.deleteFiles(Arrays.asList(fileNames)).toString();
    }
}
```
## 四、下载
### 1、判断文件是否存在
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
### 2、下载
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
        // 下载到本地指定目录
        this.ossDownload.downByLocal("/Users/colin/Desktop/1/", fileName);
        // 通过流下载
        this.ossDownload.downByStream(fileName, response);
    }
}
```