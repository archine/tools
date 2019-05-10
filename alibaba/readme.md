# 阿里巴巴工具包
![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp;
 ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
 ![](https://img.shields.io/badge/builder-success-green.svg)    
 提供阿里oss和短信功能
 > **推荐使用最新版本**
 **安装**
 ---
 * <a href="https://mvnrepository.com/artifact/cn.gjing/alibaba/" title="阿里巴巴工具包">cn.gjing.alibaba</a>
 > **使用方法**   
 --- 
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
     //为ture表示执行成功
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