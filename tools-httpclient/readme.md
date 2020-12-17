![](https://img.shields.io/badge/version-1.2.8-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg)     
  
**Http请求工具**
## 一、添加依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-httpclient</artifactId>
    <version>1.2.8</version>
</dependency>
```
## 二、使用说明
**``返回值类型最好与目标方法一致``，否则可能会出现转换异常，在不确认返回类型时最好使用``String``去接收。在请求结束后，可以通过``get()``方法获取返回的内容，也可以通过``listener()``方法指定监听者去监听结果返回后的处理逻辑**
### 1、无参数请求
```java
public class Test{
    public static void main(String[] args) {
        String result = HttpClient.builder("http://127.0.0.1:8080/test", HttpMethod.GET, String.class)
                        .execute()
                        .get();
        System.out.println(result);
    }
}
```
### 2、带请求头请求
```java
public class Test{
    public static void main(String[] args) {
            Map<String, String> map = new HashMap<>(16);
            map.put("head", "111");
            HttpClient.builder("http://127.0.0.1:8080/test6", HttpMethod.GET, String.class)
                    .header(map)
                    .execute()
                    .listener(System.out::println);
    }
}
```
### 3、携带参数
```java
public class Test{
    public static void main(String[] args) {
        Map<String, Object> param = new HashMap<>();
        param.put("id","1");
        HttpClient.builder("http://127.0.0.1:8080/test6", HttpMethod.GET, String.class)
                .param(param)
                .execute()
                .listener(System.out::println);
    }
}
```
### 4、携带JSON
**可以是JSON字符串、Map、JSON对应实体对象，这里演示通过map传递**
```java
public class Test{
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("key", "code");
        map.put("val", "200");
        Map resultMap = HttpClient.builder("http://127.0.0.1:8080/test6", HttpMethod.POST, Map.class)
                .body(map)
                .execute()
                .get();
    }
}
```
### 5、设置超时时间
**可以分别设置读超时时间(``默认5000ms``)和连接超时时间(``默认2000ms``)**
```java
public class Test{
    public static void main(String[] args) {
        Map<String, Object> param = new HashMap<>();
        param.put("id","1");
        HttpClient.builder("http://127.0.0.1:8080/test6", HttpMethod.GET, String.class)
                .param(param)
                //读超时时间
                .readTimeout(5000)
                //连接超时时间
                .connectTimeout(2000)
                .execute()
                .listener(System.out::println);
    }
}
```
### 6、异常回退
**一旦发起请求出现了错误会进入该回退，默认抛出``HttpException``，你可以自行定义回退逻辑，该方法要在执行``execute()``请求方法之前调用，否则无效。该方法会将错误信息通知给你**
```java
public class Test{
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("key", "code");
        map.put("val", "200");
        Map resultMap = HttpClient.builder("http://127.0.0.1:8080/test6", HttpMethod.POST, Map.class)
                .body(map)
                .fallback(e -> {
                   throw new ServiceException("哎。回退把");
                })
                .execute()
                .get();
    }
}
```
## 三、 UrlUtils工具类
#### 1、url拼接
Url拼接, 返回结果格式如: http://xxx/param1/param2
```java
public class Test{
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8080/";
        Object[] param = {1, 2, 3, 4};
        UrlUtil.urlAppend(url, param);
    }
}
```
#### 2、参数排序
**参数按照字段名的Unicode码从小到大排序(字典序), 得到的结果格式如: a=参数1&b=参数2**
```java
public class Test{
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("a", "参数1");
        map.put("b", "参数2");
        UrlUtil.paramUnicodeSort(map, false, false);
    }
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
public class Test{
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8080?a=2&b=2";
        UrlUtil.toMap(url);
    }
}
```
---