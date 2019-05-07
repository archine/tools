# http请求工具
![](https://img.shields.io/badge/version-1.1.6-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)   
  提供http请求
> **推荐使用最新版本**   

**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/httpclient/" title="http工具包">cn.gjing.httpclient</a>  
---
> **使用方式**    

a. ***HttpClient*** (支持http/https,以及代理)   
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
b. ***HttpClient2*** (推荐使用,支持https和http)
```
tips:   url为请求参数; params为参数构建的map; headers为请求头构建的map; connectTimeout为连接超时时间,默认3000毫秒;    
        readTimeout为读超时时间,默认5000毫秒; responseType为返回类型;
例子:    
        使用中可以使用 HttpClient2.post(参数....)或者其他请求方式直接请求,返回结果为你指定的返回类型;
        public static void main(String[] args) {
            Map<String, String> param = new HashMap<>(16);
            //post请求中参数根据实际情况填写
            String result = HttpClient2.post("url", param ,String.class)
            System.out.println(result);
        }
```
* UrlUtil: 用于url的处理,目前含有unicode字符编码排序(字典序),url参数转map,restTemplate请求url拼接;
```
里面包括(方法名:对应功能):
    1.urlAppend: url和参数拼接,用于url直接带参,结果为http://127.0.0.1/test/param1/param2; 2.paramUnicodeSort: 将需要转化的参数构建成map后使用方法转成字符串,结果如: param1&param2&param3,   
    方法中urlEncode参数为true时表示将参数进行url编码,默认false; keyToLower参数为true时表示全部转小写,默认false;
    3.urlParamToMap:将url中的参数转成map; 
```