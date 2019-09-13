![](https://img.shields.io/badge/version-1.1.0-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)     
  
**Http请求工具**
## 一、添加依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-httpclient</artifactId>
    <version>1.1.0</version>
</dependency>
```
## 二、使用说明
### 1、创建httpClient实例
**请求地址支持``https``**
```java
public static void main(String[] args) {
    HttpClient httpClient = HttpClientFactory.builder("http://127.0.0.1:8080/test", HttpMethod.GET)
}
```
### 2、携带请求头
```java
public static void main(String[] args) {
    Map<String, String> header = new HashMap<>();
    header.put("token", "xxxx");
    HttpClient httpClient = HttpClientFactory.builder("http://127.0.0.1:8080/test", HttpMethod.GET)
            .withHeader(header);
}
```
### 3、携带参数
```java
public static void main(String[] args) {
    Map<String, Object> param = new HashMap<>();
    param.put("id","1")
    HttpClient httpClient = HttpClientFactory.builder("http://127.0.0.1:8080/test", HttpMethod.GET)
            .withParam(param);
}
```
### 4、携带JSON
**``withBody()``方法内参数可以是JSON字符串、Map、JSON对应实体对象**
```java
public static void main(String[] args) {
    Map<String, String> map = new HashMap<>();
    map.put("key", "code");
    map.put("val", "200");
    HttpClient httpClient = HttpClientFactory.builder("http://127.0.0.1:8080/test_body", HttpMethod.POST)
            .withBody(map);
}
```
### 5、发起请求
**需要传入响应类型, ``最好与目标方法一致``, 否则可能出现转换异常, 此方法为``最终操作``**
```java
public static void main(String[] args) {
    Map<String, String> map = new HashMap<>();
    map.put("key", "code");
    map.put("val", "200");
    HttpClient httpClient = HttpClientFactory.builder("http://127.0.0.1:8080/test_body", HttpMethod.POST)
            .withBody(map);
    HttpVo httpVo = httpClient.execute(HttpVo.class);
    System.out.println(httpVo.toString());
}
```
## 三、 UrlUtil工具类
#### 1、url拼接
Url拼接, 返回结果格式如: http://xxx/param1/param2
```java
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8080/";
        Object[] param = {1, 2, 3, 4};
        UrlUtil.urlAppend(url, param);
    }
```
#### 2、参数排序
**参数按照字段名的Unicode码从小到大排序(字典序), 得到的结果格式如: a=参数1&b=参数2**
```java
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("a", "参数1");
        map.put("b", "参数2");
        UrlUtil.paramUnicodeSort(map, false, false);
    }
```
**参数说明**     

|参数|描述|
|----|----|
|paramMap|参数|
|urlEncode|是否进行URL编码|
|keyToLower|转换后的参数的key值是否要转为小写|   

#### 3、url参数转map
**将URL地址后带的参数转成map**
```java
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8080?a=2&b=2";
        UrlUtil.toMap(url);
    }
```
---
**更多教程可前往博客查看: [Java http请求工具](https://yq.aliyun.com/articles/703132?spm=a2c4e.11155435.0.0.73393312Egko4y)**