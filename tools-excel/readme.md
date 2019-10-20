![](https://img.shields.io/badge/version-1.1.0-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;     

**提供Excel导入导出功能**
## 一、安装依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-excel</artifactId>
    <version>1.1.0</version>
</dependency>
```
## 二、注解说明
### 1、@Excel
**使用在类上，表明这个类要绑定excel，注解参数如下**     

|参数|描述|
|---|---|
|value|Excel文件名，优先级``低``于方法传入|
|type|Excel文档类型，默认``XLS``|
|style|导出的Excel样式|
### 2、@ExcelField
**使用在字段上，表明这是Excel的列表头，注解参数如下**     

|参数|描述|
|---|---|
|value|列表头名字|
|pattern|时间类型字段需要转换指定格式，需要指定|
|width|这个列表头单元格的宽度|
### 3、@DateValid
**时间校验注解，使用在字段上，表明我这个列表头下指定行数的单元格要进行数据校验，``XLSX``类型文档不支持，注解参数如下**     

|参数|描述|
|---|---|
|validClass|校验器Class|
|boxLastRow|数据校验最多校验多少行，默认是正文第一行|
|pattern|校验的时间格式，默认``yyyy-MM-dd``|
|operatorType|操作类型，默认``OperatorType.BETWEEN``|
|expr1|表达式1，默认``1970-01-01``|
|expr2|表达式2，默认``2999-01-01``|
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
|validClass|校验器Class|
|combobox|范围值，数组类型|
|boxLastRow|数据校验最多校验多少行，默认是该列表头下的正文第一行|
|showErrorBox|是否弹出错误框，默认``true``|
|showPromptBox|是否立即弹出，默认``true``|
|rank|提示框级别，默认``Rank.WARING``警告级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容|
#### 5、@NumericValid
**数据类型校验，使用在字段上，注解参数如下**     

|参数|描述|
|-----|-----|
|validClass|校验器Class|
|boxLastRow|数据校验最多校验多少行，默认是该列表头下的正文第一行|
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
#### 6、@ExcelEnumConvert
**枚举转换器，使用在枚举类型的字段上，注解参数如下**     

|参数|描述|
|---|---|
|convert|实现了``EnumConvert``接口的类Class|
## 三、使用说明
### 1、定义Excel对应的实体
**只需要在实体类的字段上加上对应注解即可**
```java
@Data //这是lombok的注解，帮完成get、set等方法
@Excel("用户列表")
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField("用户名")
    private String userName;

    @ExcelField(value = "创建时间",pattern = "yyyy-MM-dd")
    private Date createTime;
}
```
### 2、Excel导出
**如果只是导出Excel模板，只需要在``write()``方法中传入null即可**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    @ApiOperation(value = "导出用户")
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, response)
                .write(users)
                .flush();
    }
}
```
**排除某些带了``@ExcelField``注解的字段，只需在``createWriter()``方法中``ignores``参数指定你要忽略的字段，``字段名要和实体一致``**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    @ApiOperation(value = "导出用户")
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, response,"id","createTime")
                .write(users)
                .flush();
    }
}
```
**指定sheet导出，不指定的话会导出到默认名称的sheet中**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    @ApiOperation(value = "导出用户")
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, response)
                .write(users,"用户sheet")
                .flush();
    }
}
```
**如果需要将不同规则数据导入到不同的sheet中或者是在同一个sheet中分层，可以多次调用``write()``方法，如果不指定sheet的名称则会写入到默认名称的sheet**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    @ApiOperation(value = "导出用户")
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        List<User> users2 = userService.userList();
        ExcelFactory.createWriter(User.class, response)
                .write(users)
                .write(users2,"sheet2")
                .flush();   
    }
}
```
**指定大标题，该操作要在每次调用``write()``之前，仅对本次调用有效**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    @ApiOperation(value = "导出用户")
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, response)
                //指定大标题占用的行数和内容
                .bigTitle(() -> new BigTitle(3,"我是大标题"))
                .write(users)
                .flush();
    }
}
```
**重置当前导出的Excel与实体的关联，该操作要在调用``write()``之前**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private OrderService orderService;

    @GetMapping("/user")
    @ApiOperation(value = "导出用户")
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        List<Order> orderList = orderService.orderList();
        ExcelFactory.createWriter(User.class, response)
                .write(users)
                //重置，如果要忽略某些字段不导出，在该方法 ignores 参数指定即可
                //注意点和上文介绍的导出一致
                .resetExcelClass(Order.class)
                .write(orderList)
                .flush();
    }
}
```
**在整个链式调用完毕后，一定要在最后调用``flush()``方法，否则数据不会写入到Excel文件中**
### 3、Excel导入
**通过``get()``方法获取导入的数据，该方法为``最终操作``，整个导入会结束**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userSerivce;

    @PostMapping("/user_import")
    @ApiOperation("导入")
    public ResponseEntity userImport(MultipartFile file) throws IOException {
        List<User> users = ExcelFactory.createReader(file.getInputStream(), User.class)
                //不指定sheet名称，会去读默认名称的sheet
                .read()
                .get();
        userService.saveUserList(users);
        return ResponseEntity.ok("导入成功");
    }
}
```
**很多时候我们数据太多而写在了不同的sheet中，这时可能需要分开读取每个sheet，使用上面的``get()``方法获取导入的结果肯定是不行的，这时可以通过创建监听者去监听每次导入的结果并执行对应的操作**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userSerivce;

    @PostMapping("/user_import")
    @ApiOperation("导入")
    public ResponseEntity userImport(MultipartFile file) throws IOException {
        ExcelFactory.createReader(file.getInputStream(), User.class)
                .read()
                .listener(e -> userService.saveUserList(e))
                .read("sheet2")
                .listener(e -> userService.saveUserList(e));
        return ResponseEntity.ok("导入成功");
    }
}
```
**导入的Excel存在大标题的话，需要指定列表头开始的下标，对应Excel文件列表头那一行左边的数字序号，该操作要在调用``read()``前进行**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userSerivce;

    @PostMapping("/user_import")
    @ApiOperation("导入")
    public ResponseEntity userImport(MultipartFile file) throws IOException {
        List<User> users = ExcelFactory.createReader(file.getInputStream(), User.class)
                .headerIndex(2)
                .read()
                .get();
        userService.saveUserList(users);
        return ResponseEntity.ok("导入成功");
    }
}
```
**限制读取行数，该行数为你要截止读取到Excel文件的哪一行(包括本行)，该操作要在调用``read()``前进行**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userSerivce;

    @PostMapping("/user_import")
    @ApiOperation("导入")
    public ResponseEntity userImport(MultipartFile file) throws IOException {
        List<User> users = ExcelFactory.createReader(file.getInputStream(), User.class)
                .endIndex(5)
                .read()
                .get();
        userService.saveUserList(users);
        return ResponseEntity.ok("导入成功");
    }
}
```
### 4、枚举转换器
**实体存在枚举类型的字段，在导入导出时需要指定枚举转换器，实现``EnumConvert``接口并重写其中方法，导出会显示你指定的内容，导入会转为指定的枚举**
```java
/**
 * 性别
 *
 * @author Gjing
 **/
@Getter
public enum GenderEnum {
    MAN(1, "男"),
    WOMAN(2, "女");
    private int type;
    private String desc;

    GenderEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * excel导入的时候转换成枚举用的
     *
     * @param s desc
     * @return GenderEnum
     */
    public static GenderEnum of(String s) {
        for (GenderEnum genderEnum : GenderEnum.values()) {
            if (genderEnum.getDesc().equals(s)) {
                return genderEnum;
            }
        }
        throw new NullPointerException("没找到你的枚举");
    }
    /**
     * 自定义枚举转换器
     */
    public static class MyExcelEnumConvert implements EnumConvert<GenderEnum, String> {
        @Override
        public GenderEnum toEntityAttribute(String s) {
            return of(s);
        }

        @Override
        public String toExcelAttribute(GenderEnum genderEnum) {
            return genderEnum.desc;
        }
    }
}
```
**字段使用注解并指定你的转换类**
```java
@Excel("用户列表")
public class User {

    @ExcelField("性别")
    @ExcelEnumConvert(convert = GenderEnum.MyExcelEnumConvert.class)
    private GenderEnum genderEnum;
}
```
### 5、数据校验
**在导出模板时，有时候需要让用户填写指定规则的内容，这时可以进行数据格式校验**
```java
@Excel("用户列表")
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField("用户名")
    //在当前列表头下方的十行单元格进行数据校验，必须输入长度大于等于3的文本
    @NumericValid(validationType = TEXT_LENGTH, expr1 = "3", operatorType = GREATER_OR_EQUAL,boxLastRow = 10)
    private String userName;

    @ExcelField("性别")
    //指定该列表头下的第一行单元格只能选取这两个值
    @ExplicitValid(combobox = {"男,女"})
    @ExcelEnumConvert(convert = GenderEnum.MyExcelEnumConvert.class)
    private GenderEnum genderEnum;

    @ExcelField(value = "创建时间",pattern = "yyyy-MM-dd")
    //在当前列表头下方的第一行单元格中，时间只能输入在2019-10-11至2019-10-13范围的时间
    @DateValid(expr1 = "2019-10-11",expr2 = "2019-10-13")
    private Date createTime;
}
```
## 四、拓展
**之前讲解的使用的一些功能都是采取默认实现的，有时候有点自己的想法，也可以进行自定义其中的功能**
### 1、自定义Excel样式
**实现``ExcelStyle``接口，里面有设置大标题、正文、列表头样式的三个方法，选择你要自定义的方法进行重写即可，以下演示了自定义大标题样式**
```java
/**
 * 自己定义的样式
 * @author Gjing
 **/
public class MyExcelStyle implements ExcelStyle {

    @Override
    public CellStyle setTitleStyle(CellStyle cellStyle) {
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }
}
```
**在对应实体``@Excel``注解指定你自定义的样式类**
```java
@Excel(value = "用户列表",style = MyExcelStyle.class)
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField("用户名")
    private String userName;

    @ExcelField(value = "创建时间",pattern = "yyyy-MM-dd")
    private Date createTime;
}
```
### 2、自定义校验注解的逻辑
**通过上文的介绍，知道一共有三种校验的注解，这三个注解分别都可以自定义实现逻辑，实现``ExcelValidation``接口，并选择你需要自定义哪些注解的逻辑**
```java
/**
 * 自己实现校验逻辑
 * @author Gjing
 **/
public class MyValid implements ExcelValidation {
    @Override
    public void valid(DateValid dateValid, Sheet sheet, int firstRow, int firstCol, int lastCol) {

    }

    @Override
    public void valid(NumericValid numericValid, Sheet sheet, int firstRow, int firstCol, int lastCol) {

    }

    @Override
    public void valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int firstCol, int lastCol) {

    }
}
```
**修改你实体类中对应注解的默认处理类``validClass``，没有指定的注解还是会走默认处理**
```java
@Excel("用户列表")
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField("用户名")
    @NumericValid(validClass = MyValid.class,validationType = TEXT_LENGTH, expr1 = "3", operatorType = GREATER_OR_EQUAL,boxLastRow = 10)
    private String userName;

    @ExcelField(value = "创建时间",pattern = "yyyy-MM-dd")
    //在当前列表头下方的第一行单元格中，时间只能输入在2019-10-11至2019-10-13范围的时间
    @DateValid(expr1 = "2019-10-11",expr2 = "2019-10-13")
    private Date createTime;
}
```
### 3、自定义导出处理器
**自定义导出的核心处理器，需实现``ExcelWriterResolver``接口并重写其中的方法**
```java
/**
 * 自定义导出处理器
 * @author Gjing
 **/
public class MyWriteResolver implements ExcelWriterResolver {
    @Override
    public void write(List<?> list, Workbook workbook, String s, List<Field> list1, MetaStyle metaStyle, BigTitle bigTitle) {

    }

    @Override
    public void flush(HttpServletResponse httpServletResponse, String s) {

    }
}
```
**在导出时重置处理器，该方法要在你``所有链式操作之前``调用**
```java
/**
 * @author Gjing
 **/
@RestController
@Api(tags = "用户")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/user")
    @ApiOperation(value = "使用自定义的导出处理器导出用户模板")
    public void exportUser(HttpServletResponse response) {
        ExcelFactory.createWriter(User.class, response)
                //重置处理器
                .resetResolver(MyWriteResolver::new)
                .write(null)
                .flush();
    }
}
```
### 4、自定义导入处理器
**自定义导入处理器，需实现``ExcelReaderResolver``接口并重写其中的方法**
```java
/**
 * 自定义导入处理器
 * @author Gjing
 **/
public class MyReaderResolver implements ExcelReaderResolver {
    @Override
    public void read(InputStream inputStream, Class<?> aClass, Listener<List<Object>> listener, int i, int i1, String s) {

    }
}

```
**在导出的方法中重置处理器，该操作要在``所有链式操作前``调用**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {
    @Resource
    private UserService userSerivce;

    @PostMapping("/user_import")
    @ApiOperation("导入")
    public ResponseEntity userImport(MultipartFile file) throws IOException {
        ExcelFactory.createReader(file.getInputStream(), User.class)
                .resetResolver(MyReaderResolver::new)
                .read()
                .listener(e -> userService.saveUserList(e))
        return ResponseEntity.ok("导入成功");
    }
}
```
---