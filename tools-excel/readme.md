![](https://img.shields.io/badge/version-1.0.0-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;     

**提供Excel导入导出功能**
## 一、安装依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-excel</artifactId>
    <version>1.0.0</version>
</dependency>
```
## 二、注解说明
### 1、@Excel
**使用在类上，表明这个类要绑定excel，注解参数如下**     

|参数|描述|
|---|---|
|value|Excel文件名，优先级低于方法传入|
|type|Excel文档类型，``默认XLS``|
### 2、@ExcelField
**使用在字段上，表明这是Excel的列表头，注解参数如下**     

|参数|描述|
|---|---|
|value|列表头名字|
|pattern|如果是时间需要转换指定格式，需要指定|
|width|这个列表头单元格的宽度|
### 3、@DateValid
**时间校验注解，使用在字段上，表明我这个列表头下的数据要进行校验，``XLSX``文档不支持，注解参数如下**     

|参数|描述|
|---|---|
|boxLastRow|数据校验最多校验多少行，默认是正文第一行|
|pattern|校验的时间格式，默认``yyyy-MM-dd``|
|operatorType|操作类型，默认``OperatorType.BETWEEN``|
|expr1|表达式1|
|expr2|表达式2|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|allowEmpty|是否允许空值，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容| 
### 4、@ExplicitValid
**明确范围内容校验，使用在注解上，注解参数如下**     

|参数|描述|
|-----|-----|
|combobox|范围值，数组类型|
|boxLastRow|数据校验最多校验多少行，默认为``列表头下一行``|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容|
#### 5、@NumericValid
**数据类型校验，使用在字段上，注解参数如下**     

|参数|描述|
|-----|-----|
|boxLastRow|数据校验最多校验多少行，默认为``列表头下一行``|
|operatorType|操作类型，默认``OperatorType.EQUAL``|
|validationType|校验类型，``必填``|
|expr1|表达式1，在表达式2前面，``必填``|
|expr2|表达式2，在操作类型为``BETWEEN``和``NOT_BETWEEN``情况下必填|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|allowEmpty|是否允许空值，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容| 
#### 6、@Convert
**枚举转换器，使用在枚举类型的字段上，注解参数如下**     

|参数|描述|
|---|---|
|convert|实现了``EnumConvert``接口的类Class|
## 三、使用说明
### 1、创建Excel对应的实体
```java
@Excel(value = "用户列表")
public class User {
    @ExcelField("id值")
    private Long id;

    @ExcelField("用户名")
    private String name;

    @ExcelField("用户地址")
    private String userAddress;

    @ExcelField("城市")
    private String userCity;
}
```
### 2、导出空的Excel
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {

    @GetMapping("/excel")
    @ApiOperation(value = "excel空文件导出")
    public void downExcel2(HttpServletResponse response) {
        ExcelFactory.createWriter(User.class, response).write();
    }
}
```
### 3、导出有数据的Excel
**这里只说明如何使用，不提供访问数据库层的代码**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/excel")
    @ApiOperation(value = "excel导出")
    public void downExcel(HttpServletResponse response) {
        List<User> userList = userService.listUser();
        ExcelFactory.createWriter(User.class, response, userList).write();
    }
}
```
### 4、导出有大标题的Excel
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/excel")
    @ApiOperation(value = "excel导出")
    public void downExcel(HttpServletResponse response) {
        List<User> userList = userService.listUser();
        ExcelFactory.createWriter(User.class, response, userList).bigTitle(BigTitle::new).write();
    }
}
```
### 5、导出自定义样式的Excel
**这里的``bigTitleStyle()``、``contentStyle()``、``headerStyle()``里面的参数需要传递实现了``ExcelStyle``接口的类**
#### a、实现自己的样式类
```java
/**
 * @author Gjing
 **/
public class MyStyle implements ExcelStyle {
    @Override
    public CellStyle style(CellStyle cellStyle) {
        // TODO: 2019/9/20 这里自定义配置 
        return cellStyle;
    }
}
```
#### b、使用
**大标题、正文、列表头的样式都可以分别定义，这里为了方便用了同样的**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/excel")
    @ApiOperation(value = "excel导出")
    public void downExcel(HttpServletResponse response) {
        ExcelFactory.createWriter(User.class, response)
                //设置大标题样式
                .bigTitleStyle(MyStyle::new)
                //设置正文样式
                .contentStyle(MyStyle::new)
                //列表头样式
                .headerStyle(MyStyle::new).write();
    }
}
```
### 6、导出有数据校验的Excel
#### a、改造实体
**只做介绍，具体的要根据业务场景来使用**
```java
@Excel(value = "用户列表")
public class User {
    @ExcelField("id值")
    private Long id;

    @ExcelField("用户名")
    private String name;

    @ExcelField("用户地址")
    //表明这个字段长度要等于10
    @NumericValid(operatorType = OperatorType.EQUAL,validationType = ValidType.TEXT_LENGTH,expr1 = "10")
    private String userAddress;

    @ExcelField("城市")
    private String userCity;
}
```
#### b、导出
**按之前的导出示例导出即可**
### 7、导入Excel文件
**这里只说明如何使用，不提供数据接下来处理的示例代码**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/excel")
    @ApiOperation(value = "excel导入")
    public ResponseEntity excelImport(MultipartFile file) {
        try {
            ExcelFactory.createReader(file.getInputStream(), User.class).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("ok");
    }
}
```
## 三、复杂场景
### 1、自定义实现数据校验的逻辑
**自定义一些逻辑，在导出时在ExcelWrite通过``.setDateValidation(MyValid::new)``设置，其他几个校验也是一样**
```java
/**
 * @author Gjing
 **/
public class MyValid implements ExcelValidation {
    @Override
    public DataValidation valid(Sheet sheet) {
        return null;
    }
}
```
### 2、导入导出枚举转换
**如果字段的数据类型是枚举，必须指定转换器，该转换器实现``EnumConvert``接口，并重写其中的方法**
#### a、定义转换器
```java
/**
 * @author Gjing
 **/
public class MyConvert implements EnumConvert<Gender, Integer> {
    //转成实体字段
    @Override
    public Gender toEntityAttribute(String s) {
        return null;
    }
    //转成数据库字段
    @Overridevalue
    public Integer toDatabaseColumn(Gender gender) {
        return null;
    }
}
```
#### b、字段上指定转换器
```java
@Excel("用户列表")
public class User {
    @ExcelField("性别")
    @Convert(convert = MyConvert.class)
    private Gender userGender;
}
```
### 3、自定义Excel读写的解析器
#### a、导出解析器
**实现``ExcelWriterResolver``接口，并实现其中的方法。除了``builder()``、``write()``方法其他都可以根据自己需要决定是否要写**
```java
/**
 * @author Gjing
 **/
public class MyWriterResolver implements ExcelWriterResolver {
    @Override
    public ExcelWriterResolver builder(Workbook workbook, HttpServletResponse httpServletResponse, List<String> list, List<Field> list1, String s) {
        return null;
    }

    @Override
    public void write(List<?> list) {

    }

    @Override
    public void setBigTitle(BigTitle bigTitle) {

    }

    @Override
    public void setHeaderStyle(Supplier<? extends ExcelStyle> supplier) {

    }

    @Override
    public void setBigTitleStyle(Supplier<? extends ExcelStyle> supplier) {

    }

    @Override
    public void setContentStyle(Supplier<? extends ExcelStyle> supplier) {

    }

    @Override
    public void setDateValidation(Supplier<? extends ExcelValidation> supplier) {

    }

    @Override
    public void setExplicitValidation(Supplier<? extends ExcelValidation> supplier) {

    }

    @Override
    public void setNumberValidation(Supplier<? extends ExcelValidation> supplier) {

    }
}
```
#### b、导入解析器
**实现``ExcelReaderResovler``接口，并重写其中的方法**
```java
/**
 * @author Gjing
 **/
public class MyReadResolver implements ExcelReaderResolver {
    @Override
    public ExcelReaderResolver builder(InputStream inputStream) {
        return null;
    }

    @Override
    public void read(Class<?> aClass, Consumer<List<Object>> consumer) {

    }

    @Override
    public void setEnumConvert(Supplier<? extends EnumConvert> supplier) {

    }
}
```
#### c、项目使用
**在Excel工厂创建完之后调用读或者写的``changeResolver()``方法改变解析器即可，该方法要在``链式调用其他方法前使用``，否则改变前解析器的方法进行的操作会无效**
* **导入**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/excel")
    @ApiOperation(value = "excel使用自己的解析器导入")
    public ResponseEntity excelImport(MultipartFile file) {
        try {
            ExcelFactory.createReader(file.getInputStream(), User.class)
                    .changeResolver(MyReadResolver::new)
                    .read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("ok");
    }
}
```
**导出，需要同时指定``Workbook``**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/excel")
    @ApiOperation(value = "excel导出")
    public void downExcel(HttpServletResponse response) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        ExcelFactory.createWriter(User.class, response)
                .changeResolver(MyWriterResolver::new,workbook)
                .write();
    }
}
```