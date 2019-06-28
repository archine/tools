# http请求工具（支持http和https）
![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)   
  提供http请求
> **推荐使用最新版本**   

**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-httpclient/" title="http工具包">tools-httpclient</a>  
---
> **使用方式**    
###### tip：不需要的参数可以传null, 请求成功会返回数据，否则会抛出异常信息
1. url: 请求参数 （必填）
2. queryMap: 参数
3. headers：请求头
4. connectTimeout：请求超时时间，默认5s
5. readTimeout：读超时时间，默认10秒
6. responseType：响应结果返回类型（必填）
7. jsonEntity：Json对象,可以是json字符串对应的对象也可使用map
8. jsonStr: Json字符串
``` 
@GetMapping("/test")
public ResponseEntity http() {
    //创建httpClient实例
    HttpClient client = new HttpClient();
    ResultBean resultBean = new ResultBean();
    resultBean.setMessage("ok");
    resultBean.setData("data");
    Map result = client.postByJsonEntity("http://127.0.0.1:8080/test",resultBean, Map.class);
    return ResponseEntity.ok(result);
}
@GetMapping("/demo")
public ResponseEntity demo4() {
    HttpClient client = new HttpClient();
    Map<String, Object> map = new HashMap<>(16);
    map.put("a", "a");
    String result = client.get("http://127.0.0.1:8080/demo", null, 3000,5000,String.class);
    return ResponseEntity.ok(result);
}
```
---
> UrlUtil(工具类) : 
* urlAppend：url和参数拼接,用于URL地址直接带参,结果为http://127.0.0.1/test/param1/param2
* paramUnicodeSort：将需要转化的参数构建成map后使用方法转成字符串,结果如: 参数a=a&参数b=a
* urlParamToMap：将url中的参数转成map
---
**更多教程可前往博客查看: [Java http请求工具](https://yq.aliyun.com/articles/703132?spm=a2c4e.11155435.0.0.73393312Egko4y)**