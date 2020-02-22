![](https://img.shields.io/badge/version-1.2.4-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;     

**采用注解方式的导入导出，项目中可以便捷的进行使用**
## 一、安装依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-excel</artifactId>
    <version>1.2.4</version>
</dependency>
```
## 二、注解说明
### 1、@Excel
**实体上使用，声明Excel与该实体存在映射，注解参数如下**     

|参数|描述|
|---|---|
|value|Excel导出的文件名，优先级``低``于方法传入|
|type|Excel导出的文档类型，默认``XLS``|
|style|Excel导出后的样式，此处配置是``全局性``的，不指定会走默认样式处理|
|readCallback|导入excel的时候的回调|
### 2、@ExcelField
**在使用了``@Excel``注解的对象字段上使用，将字段与列表头绑定，以下称为列表头字段。没有使用该注释的字段将不会作为列表头。注解的参数如下**     

|参数|描述|
|---|---|
|value|列表头名称, ``必填``|
|pattern|列表头字段属于时间类型且需要在导出的时候格式化的时候配置|
|width|该列表头对应整列的单元格宽度，默认``5120``|
|autoMerge|是否自动纵向合并多行相同的数据，默认``false``|
|style|设置当前列表头样式，``优先级高于全局配置``|
|allowEmpty|是否允许空值，默认``true``|
|strategy|值为空时候的策略，策略有：``jump``(跳过当前这条数据)、``error``(抛出异常)|
|message|执行``策略为error``的时候抛出的异常信息|
|sum|是否需要统计，默认``false``。可以通过``format``设置保存的数字格式，默认保存``整数``，需要统计的字段``不要出现在第一列``|
|sort|列表头排序，序号``越小越靠前``，当序号相同时会默认按实体类的字段顺序进行排序|
### 3、@DateValid
**对该列表头下方的单元格加入时间校验，使用在列表头字段，``只在导出模板时有效``，注解参数如下**     

|参数|描述|
|---|---|
|validClass|校验器Class|
|boxLastRow|加入多少行，默认只给正文第一行加入|
|pattern|校验的时间格式，默认``yyyy-MM-dd``|
|operatorType|操作类型，默认``OperatorType.BETWEEN``|
|expr1|表达式1，默认``1970-01-01``|
|expr2|表达式2，默认``2999-01-01``|
|showErrorBox|是否弹出错误框，默认``true``|
|rank|提示框级别，默认``Rank.STOP``级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容| 
### 4、@ExplicitValid
**对该列表头下方的单元格加入下拉框，使用在列表头字段，``只在导出模板时有效``，注解参数如下**     

|参数|描述|
|-----|-----|
|validClass|校验器Class|
|combobox|下拉框中的内容，可在注解直接配置也可以在方法中设置，``方法设置优先级高于此处配置``|
|boxLastRow|加入多少行，默认只给正文第一行加入|
|showErrorBox|是否弹出错误框，默认``true``|
|rank|提示框级别，默认``Rank.STOP``级别|
|link|指定该列表头的上级单元格序号, 一般为你``指定列表头的序号-1``，比如上级是第一个单元格，那么link就是``0``|
|errorTitle|错误框标题|
|errorContent|详细错误内容|
#### 5、@NumericValid
**对该列表头下方的单元格加入数据类型校验，使用在列表头字段，可对文本长度、数字大小进行校验``只在导出模板时有效``，注解参数如下**     

|参数|描述|
|-----|-----|
|validClass|校验器Class|
|boxLastRow|加入多少行，默认只给正文第一行加入|
|operatorType|操作类型，默认``OperatorType.LESS_OR_EQUAL``|
|validType|校验类型，默认``ValidType.TEXT_LENGTH``|
|expr1|表达式1，在表达式2前面，默认``0``|
|expr2|表达式2，在操作类型为``BETWEEN``和``NOT_BETWEEN``情况下必填|
|showErrorBox|是否弹出错误框，默认``true``|
|rank|提示框级别，默认``Rank.STOP``级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容| 
#### 6、@ExcelEnumConvert
**枚举转换器，使用在枚举类型的列表头字段，导入导出时未检测到转换器会报错，注解参数如下**     

|参数|描述|
|---|---|
|convert|实现了``EnumConvert``接口的Class|
## 三、使用说明
### 1、定义Excel对应的实体
**只需要在实体类和字段上加上对应注解即可**
```java
@Data //这是lombok的注解，帮完成get、set等方法
@Excel("用户列表")
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField(value = "用户名", sort = 1)
    private String userName;

    @ExcelField(value = "性别", autoMerge= true)
    private GenderEnum gender;
    //导出时按照设置的格式进行格式化时间
    @ExcelField(value = "创建时间",pattern = "yyyy-MM-dd")
    private Date createTime;
}
```
### 2、Excel导出
**如果只是导出Excel模板，只需要在``write()``方法中的``data``参数传入``null``即可**
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, resultVO)
                .write(users)
                .flush();
    }
}
```
**忽略某些列表头字段，只需在``createWriter()``方法中``ignores``参数指定你要忽略的列表头字段的名称，``字段名要与实体类中一致``**
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, resultVO,"id","createTime")
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, resultVO)
                .write(users,"用户sheet")
                .flush();
    }
}
```
**如果需要将不同规则数据导入到不同的sheet中或者是在同一个sheet中，可以多次调用``write()``方法，不指定sheet的名称则会写入到默认名称的sheet，如果导入在``同一个sheet中且映射的实体为同一个``，那么只会出现一次列表头**
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList();
        List<User> users2 = userService.userList();
        ExcelFactory.createWriter(User.class, resultVO)
                .write(users)
                .write(users2,"sheet2")
                .flush();   
    }
}
```
**指定大标题，在``write()``方法的``bigTitlte``参数设置即可**
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, resultVO)
                //指定大标题占用的行数和内容
                .write(users,new BigTitle(3,"我是大标题"))
                .flush();
    }
}
```
**重置当前导出的Excel映射实体，该操作要在每次需要调用``write()``写入之前**
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList();
        List<Order> orderList = orderService.orderList();
        ExcelFactory.createWriter(User.class, resultVO)
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
    private UserService userService;

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
**很多时候我们数据太多而写在了不同的sheet中，这时可能需要分开读取每个sheet，使用上面的``get()``方法获取导入的结果肯定是不行的，这时可以通过订阅方式获取成功导入后的数据，``read()``方法读取的结果并执行对应的操作**
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
                .subscribe(e -> userService.saveUserList(e))
                .read("sheet2")
                .subscribe(e -> userService.saveUserList(e));
        return ResponseEntity.ok("导入成功");
    }
}
```
**导入的Excel存在大标题的话，需要指定列表头开始的下标，下标你为你导出这个模板时设置的大标题的行数，该操作要在调用``read()``读取前设置**
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
**设置你要读取多少行数据，该操作要在调用``read()``读取前进行设置**
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
                .readLines(5)
                .read()
                .get();
        userService.saveUserList(users);
        return ResponseEntity.ok("导入成功");
    }
}
```
**导入时如果需要每次读取完一行获取到一个对象后做一些自己的处理，比如校验参数是否必填、记录数据等等，可以配置下读取回调**
```java
/**
 * 导入时候的回调
 *
 * @author Gjing
 **/
public class MyReadCallback implements ReadCallback {
    /**
     * 读取完一行时发生的回调
     *
     * @param value    这一行读完得到的对象
     * @param rowIndex 这行的下标，下标是从0开始的
     * @return Object 将该value原样返回
     */
    @Override
    public Object readLine(Object value, int rowIndex) {
        System.out.println("当前这行的对象：" + value.toString());
        return value;
    }

    /**
     * 当读取到某个列表头下的单元格是空的时候，同时该列表头设置了不允许为空，会产生该回调
     *
     * @param rowIndex 当前单元格所在的行数下标，下标是从0开始的
     * @param colIndex 当前单元格所在的列数，下标是从0开始的
     */
    @Override
    public void readJump(int rowIndex, int colIndex) {
        System.out.println("当前错误的是第：" + (rowIndex + 1) + "行，第: " + (colIndex + 1) + "列");
    }
}
```
**在Excel注解上修改默认的回调class**
```java
@Excel(value = "用户列表",readCallback = MyReadCallback.class)
public class User {
    @ExcelField(value = "用户名", sort = 1)
    private String userName;
}
```
### 4、枚举转换器
**列表头字段为枚举类型，在导入导出时需要指定枚举转换器，实现``EnumConvert``接口并重写其中方法**
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
**列表头字段上使用注解并指定你的转换类**
```java
@Excel("用户列表")
public class User {

    @ExcelField("性别")
    @ExcelEnumConvert(convert = GenderEnum.MyExcelEnumConvert.class)
    private GenderEnum genderEnum;
}
```
### 5、加入下拉框
**在实体属性直接通过注解的``combobox``指定下拉框中的内容**
```java
@Excel("用户列表")
public class User {

    @ExcelField("性别")
    @ExplicitValid(combobox = {"男","女"})
    @ExcelEnumConvert(convert = GenderEnum.MyExcelEnumConvert.class)
    private GenderEnum genderEnum;
}
```
**也可以在调用时通过方法的``explicitValues``参数进行设置，该参数为map类型，key为列表头字段名，value为下拉框的内容。下面演示了为列表头字段加入下拉框内容**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {

    @GetMapping("/user_empty")
    @ApiOperation(value = "导出用户模板")
    public void exportUserEmpty(HttpServletResponse resultVO) {
        String[] arr = new String[]{"男","女"};
        Map<String, String[]> map = new HashMap<>(16);
        map.put("genderEnum", arr);
        ExcelFactory.createWriter(User.class, resultVO)
                .write(null, map)
                .flush();
    }
}
```
**支持设置带级联关系的下拉框，需要在实体属性中通过``link``参数指定父级的``列表头序号-1``，比如父级是第一个单元格，那么link填的序号为``0``**     
```java
@Excel("订单列表")
public class Order {

    @ExplicitValid(combobox = {"小红","小花"},boxLastRow = 10)
    @ExcelField(value = "订单负责人", sort = 1)
    private String orderUser;

    @ExplicitValid(link = "0")//指定父级为第一个单元格，也就是orderUser
    @ExcelField(value = "订单名称", sort = 2)
    private String orderName;
}
```
**指定序号后按照上一个使用方法在调用的时候通过参数传递，但是在设置参数的时候``key变成了你要关联上级的那个值``，下面演示给两个订单负责人分别增加其拥有的订单，
``上级的每个值只允许设置一次下级``**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {

    @GetMapping("/order_template")
    @ApiOperation("导出级联模板")
    public void orderImportTemplate(HttpServletResponse response) {
        Map<String, String[]> sonMap = new HashMap<>(16);
        sonMap.put("小红", new String[]{"订单1", "订单2"});
        sonMap.put("小花", new String[]{"订单3", "订单4"});
        ExcelFactory.createWriter(Order.class, response)
                .write(null, sonMap)
                .flush();
    }
}
```
### 6、时间类型的校验
**该注解默认采取的方式是设置时间范围，你也可以通过``operatorType``去修改，``expr2``只在操作类型为``between``或者``NotBetween``时需要设置，对于其他操作类型设置``expr1``即可**
```java
@Excel("用户列表")
public class User {

    @ExcelField(value = "创建时间", pattern = "yyyy-MM-dd")
    @DateValid(boxLastRow = 10, expr1 = "2019-10-11", expr2 = "2019-10-13")
    private Date createTime;
}
```
### 7、值类型校验
**对单元格进行值类型校验，下面演示了限制输入用户名的长度不能超过2位，对于其他判断方式可自行查看参数**
```java
@Excel("用户列表")
public class User {

    @ExcelField(value = "用户名",autoMerge = true,sort = 1)
    @NumericValid(boxLastRow = 10,expr1 = "2")
    private String userName;
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
**在``@Excel``注解配置样式是属于全局的，同一个映射实体不管调用多少次``write()``都会采用这个样式，灵活性比较差，如果需要在某一次导出的时候使用另外一种样式可以采用``方法设置``**
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
    public void exportUser(HttpServletResponse resultVO) {
        List<User> users = userService.userList(0);
        ExcelFactory.createWriter(User.class, resultVO)
                .write(users, MyExcelStyle::new)
                .flush();
    }
}
```
### 2、自定义校验注解的逻辑
**自定义校验注解的处理逻辑，可以实现``ExcelDateValidation``、``ExcelNumericValidation``、``ExcelExplicitValidation``接口，下面举例实现``ExcelExplicitValidation``接口**
```java
/**
 * @author Gjing
 **/
public class MyValid implements ExcelExplicitValidation {
    @Override
    public boolean valid(ExplicitValid explicitValid, Workbook workbook, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, boolean locked, 
        String fieldName, Map<String, String[]> values) {
        return locked;
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
**在导出时重置处理器，该方法要在你调用``Write()``前设置**
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
    public void exportUser(HttpServletResponse resultVO) {
        ExcelFactory.createWriter(User.class, resultVO)
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
    public void read(InputStream inputStream, Class<?> excelClass, Listener<List<Object>> listener,int headerIndex, int readLines, String sheetName) {

    }
}

```
**在导出的方法中重置处理器，要在``所有链式操作前``设置**
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
                .subscribe(e -> userService.saveUserList(e));
        return ResponseEntity.ok("导入成功");
    }
}
```
---
**Demo地址：[excel-demo](https://github.com/archine/excel-demo)**