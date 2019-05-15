# http请求工具（支持http和https）
![](https://img.shields.io/badge/version-1.0.0-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)   
  提供http请求
> **推荐使用最新版本**   

**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-httpclient/" title="http工具包">tools-httpclient</a>  
---
> **使用方式**    
###### tip：不需要的参数可以传null
1. url: 请求参数 （必填）
2. queryMap: 参数
3. headers：请求头
4. connectTimeout：请求超时时间，默认5s
5. readTimeout：读超时时间，默认10秒
6. responseType：响应结果返回类型（必填）
7. body：对象， 一般用于目标方法使用@RequestBody接收参数
``` 
使用中可以使用 HttpClient.post(参数....)或者其他请求方式直接请求,返回结果为你指定的返回类型;
public static void main(String[] args) {
   Map<String, String> map = new HashMap<>(16);
   map.put("a", "参数a");
   map.put("b", "参数b");
   System.out.println(HttpClient.post("http://127.0.0.1:8090/web", map, String.class));
}
```
---
> UrlUtil(工具类) : 
* urlAppend：url和参数拼接,用于URL地址直接带参,结果为http://127.0.0.1/test/param1/param2
* paramUnicodeSort：将需要转化的参数构建成map后使用方法转成字符串,结果如: param1&param2&param3
* urlParamToMap：将url中的参数转成map