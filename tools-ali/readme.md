# tools-ali
![](https://img.shields.io/badge/version-1.1.0-green.svg) &nbsp;
 ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
 ![](https://img.shields.io/badge/builder-success-green.svg)    

提供阿里oss和短信功能   
### 一、导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-ali</artifactId>
    <version>1.1.0</version>
</dependency>
```
### 二、项目使用
#### 1、阿里OSS:   
文件简单上传（最大文件不能超过5G）上传成功后返回图片地址,失败会返回错误信息,后续根据返回结果自行处理;
 ```java
  @PostMapping("/upload")
  @ApiOperation(value = "上传图片", httpMethod = "POST")
  public ResponseEntity testUpload(@RequestParam(name = "file") MultipartFile file) {
      String fileOssUrl = AliOss.of("endPoint", "accessKeyId","accessKeySecret", "bucketName")
              .upload(file);
      return ResponseEntity.ok(fileOssUrl);
  }
```  
#### 2、删除文件
删除成功返回删除的文件名列表,支持删除多个文件,当前演示只删除指定文件, fileOssUrl为上传oss成功后返回的路径
 ```java
  @DeleteMapping("/file")
  @ApiOperation(value = "删除文件", httpMethod = "DELETE")
  public ResponseEntity deleteImg(@RequestParam("url") String url) {
      List<String> deleteObjectList = AliOss.of("endPoint", "accessKeyId","accessKeySecret", "bucketName")
              .delete(Collections.singletonList(url));
      return ResponseEntity.ok(deleteObjectList);
  }
 ```   
#### 3、下载文件
fileOssUrl为上传oss成功后返回的url,localFilePath为你要保存的本地目录(不存在会创建),返回true为成功;
```java
  @PostMapping("/img-native")
  @ApiOperation(value = "下载文件到本地", httpMethod = "POST")
  public ResponseEntity downImgToNative(String fileOssUrl, String path) {
      boolean down = AliOss.of("endPoint", "accessKeyId","accessKeySecret", "bucketName")
          .downloadFile(fileOssUrl, path);
      if (down) {
          return ResponseEntity.ok("下载成功");
      }
      return ResponseEntity.badRequest().body("下载失败");
  }
```
#### 4、流式下载文件:
```java
  @GetMapping("/img-stream")
  @ApiOperation(value = "流式下载", httpMethod = "GET")
  public ResponseEntity streamDown(String fileOssUrl, HttpServletResponse response) {
      boolean down = AliOss.of("endPoint", "accessKeyId","accessKeySecret", "bucketName")
          .downloadStream(fileOssUrl, response);
      if (down) {
          return ResponseEntity.ok("下载成功");
      }
     return ResponseEntity.badRequest().body("下载失败");
  }
```
#### 5、阿里短信:
 * 发送短信: 返回内容中Code为ok表示发送成功 ,更多状态码请前往[状态码](https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.14.633f56e06vZoyq)
 ```java
   @PostMapping("/send-ali")
   @ApiOperation(value = "阿里短信", httpMethod = "POST")
   public ResponseEntity sendAli() {
       //参数,必须与对应的短信模板参数对应,如果没有参数,可以传null或者空map
       Map<String, String> param = new HashMap<>(16);
       map.put("code", "123123");
       //接收的短信的手机号,支持多个,英文逗号隔开
       String send = AliSms.of("accessKeyId", "accessKeySecret")
               .send("157********", "短信模板Code", param, "短信签名");
    return ResponseEntity.ok(send);
   }
 ```
 * 查询短信, 日期必须为yyyyMMdd格式(20181207),仅支持最近30天;返回内容中Code为OK代表请求成功,每页显示的短信数量取值范围为1-50,指定发送记录要查看的页码(最小为1),
    更多状态码前往: [状态码](https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.13.450fbc454bQfCJ)
 ```java
   @GetMapping("query-ali")
   @ApiOperation(value = "查询阿里短信发送记录", httpMethod = "GET")
   public ResponseEntity queryAli() {
       String result = AliSms.of("您的accessKeyId", "您的accessKey秘钥")
               .querySendDetails("157********", "查询日期(年月日)", "每页数量", "当前页数");
       return ResponseEntity.ok(result);
   }
 ```
---
 **更多教程可前往博客查看: [JAVA使用阿里OSS和短信](https://yq.aliyun.com/articles/704400?spm=a2c4e.11155435.0.0.7a2f3312buXQzX)**