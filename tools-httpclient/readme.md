![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)     
  
**Http请求工具**
### 一、添加依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-httpclient</artifactId>
    <version>1.0.2</version>
</dependency>
```
### 二、项目使用
#### 1、HTTP请求（GET案例）
```java
public static void main(String[] args) {    
    HttpClient client = new HttpClient();
    Map<String, String> map = new HashMap<>(16);
    map.put("a", "参数a");
    map.put("b", "参数b");
    //发送http请求
    String result = client.get("http://127.0.0.1:8080/test", map, String.class);
    System.out.println(result);
}
```
#### 2、HTTPS请求（POST案例）
```java
public static void main(String[] args) {    
    HttpClient client = new HttpClient();
    Map<String, String> map = new HashMap<>(16);
    map.put("a", "参数a");
    map.put("b", "参数b");
    //发送https请求
    Map result = client.post("https://127.0.0.1:8080/test", map, Map.class);
    System.out.println(result.toString());
}
```
#### 3、DELETE案例
```java
public static void main(String[] args) {   
    HttpClient client = new HttpClient(); 
    ResultBean result = client.delete("http://127.0.0.1:8080/test/1", ResultBean.class);
    System.out.println(result.toString());
}
```
#### 4、PUT案例
```java
public static void main(String[] args) {   
    HttpClient client = new HttpClient(); 
    Integer result = client.put("https://127.0.0.1:8080/test/2", Integer.class);
    System.out.println(result);
}
```
---
### 三、HttpClient参数说明
**必填项参数除外的任意参数不需要时可直接传null**   

|参数|描述|
|-----|-----|
|**url**|请求地址, ``必填``|
|**queryMap**|请求参数|
|**headers**|请求头|
|**connectTimeout**|请求超时时间，``默认5s``|
|**readTimeout**|读超时时间，``默认10秒``|
|**responseType**|响应结果返回类型, ``必填``|
|**jsonEntity**|Json对象, Json字符串对应的对象或者map|
|**jsonStr**|Json字符串|    

响应结果``返回类型最好设置与目标方法一致``，否则可能会出现转换异常，如下：
* **目标方法**
```java
@GetMapping("/test")
public Integer test() {
    return 1111;
}
```
* **调用方**
```java
public static void main(String[] args) {
    HttpClient client = new HttpClient();
    Map result = client.get("http://127.0.0.1:8080/test", Map.class);
    System.out.println(result.toString());
}
```
由于目标方法返回的为Integer类型，不是key，value类型，故调用方使用Map接收会出现``转换异常``。
### 四、 UrlUtil工具类
#### 1、urlAppend
Url拼接, 返回结果格式如: http://xxx/param1/param2
```java
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8080/";
        Object[] param = {1, 2, 3, 4};
        UrlUtil.urlAppend(url, param);
    }
```
#### 2、paramUnicodeSort
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

#### 3、urlParamToMap
**将URL地址后带的参数转成map**
```java
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8080?a=2&b=2";
        UrlUtil.urlParamToMap(url);
    }
```
---
**更多教程可前往博客查看: [Java http请求工具](https://yq.aliyun.com/articles/703132?spm=a2c4e.11155435.0.0.73393312Egko4y)**