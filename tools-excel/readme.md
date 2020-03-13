![](https://img.shields.io/badge/version-1.4.1-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;     

**Java版Excel导入导出，可以灵活的在项目中进行使用**
## 一、安装依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-excel</artifactId>
    <version>1.4.1</version>
</dependency>
```
## 二、注解说明
### 1、@Excel
**在实体对象上使用，声明这是一个Excel实体，注解参数如下**     

|参数|描述|
|---|---|
|value|Excel导出的文件名，优先级``低``于方法传入|
|type|Excel导出的文档类型，默认``XLS``|
|headHeight|列表头的行高，默认``350``|
|maxSize|当文档类型为``XLSX``时，保留在内存中的条数，超出会将其写入到本地|
|bufferSize|当文档类型为``XLSX``时，允许在内存中保存的字节|
|style|Excel导出后的样式，此处配置是``全局性``的，不指定会走默认样式处理|
### 2、@ExcelField
**在实体的字段上使用，该注解会将当前字段映射为Excel的表头，``下文简称表头``。没有使用该注释的字段将不会作为表头出现在Excel中。注解的参数如下**     

|参数|描述|
|---|---|
|value|表头名称, ``必填``|
|width|表头所在列的整列单元格宽度，默认``5120``|
|style|表头的样式，``优先级高于全局配置``|
|sort|表头出现在Excel的顺序，序号``越小越靠前``，当序号相同时会默认按实体的``字段先后顺序``进行排序|
|format|导出Excel时表头下方的单元格格式，``优先级高于设置样式时指定``|
|autoMerge|表头所在列是否需要自动纵向合并相邻且值相同的单元格，默认``false``|
|allowEmpty|表头下方的单元格是否允许空值，默认``true``，在``@ExcelAssert``校验前执行|
|sum|表头导出时整列是否需要求和，默认``false``|
|convert|数据转换器，对单元格内容进行数据加工的时候使用|
### 3、@Sum
**在@ExcelField注解的sum参数使用，该注解会对所在表头下的整列进行末尾求和，``要求和的表头不应为第一列``** 
    
|参数|描述|
|---|---|
|open|是否开启，默认``false``|
|value|求和的描述，整个Excel实体中只会找``第一个开启``了求和的表头中设置的描述|
|format|求和后的数字格式，默认整数|
|height|行高，默认``300``|
### 4、@Merge
**在@ExcelFiled注解的autoMerge参数使用，使用该注解后会自动纵向合并当前表头下相邻且值相同的单元格。``示例用法可参考第五节：注解的使用``**     

|参数|描述|
|---|---|
|open|是否开启，默认``false``|
|empty|null值或者空字符串是否也要合并，默认``false``|
|callback|合并回调接口类，可以通过回调进行自定义设置合并规则|
### 5、@ExcelAssert
**导入时对表头下方的单元格内容进行数据有效性判断，如数据的文本长度、数字大小、是否为空、正则匹配等等。。在表头的``非空策略后执行``，注解参数如下**       

|参数|描述|
|---|---|
|expr|EL表达式，``#号后面的字段名称一定要正确``，表达式结果必须为``boolean``|
|message|数据不符合时的异常信息|
### 6、@ExcelDateValid
**表头上使用，使用后会在导出模板时对表头下方的单元格加上时间校验，注解参数如下。``示例用法可参考第五节：注解的使用``**     

|参数|描述|
|---|---|
|validClass|校验器|
|rows|需要给多少行单元格加上校验，默认``200``|
|pattern|校验的时间格式，默认``yyyy-MM-dd``，配置后只允许输入该格式|
|operatorType|操作类型，默认``OperatorType.BETWEEN``|
|expr1|表达式1，默认``1970-01-01``|
|expr2|表达式2，默认``2999-01-01``|
|showErrorBox|是否弹出错误框，默认``true``|
|rank|提示框级别，默认``Rank.STOP``级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容| 
### 7、@ExcelDropdownBox
**表头上使用，使用后会在导出模板时对表头下方的单元格增加下拉框，注解参数如下。``示例用法可参考第五节：注解的使用``**     

|参数|描述|
|-----|-----|
|validClass|校验器|
|combobox|下拉框中的内容，``方法设置优先级高于注解配置``|
|rows|需要给多少行单元格加上下拉框，默认``200``|
|showErrorBox|是否弹出错误框，默认``true``|
|rank|提示框级别，默认``Rank.STOP``级别|
|link|指定当前表头的父级序号，指定后会与父级形成级联关系，该序号为要关联的父级单元格``序号-1``，具体序号可参考``@ExcelField``注解中sort参数的描述。例：父级是第一个单元格，那么link序号为``0``。，``示例用法可参考第五节：注解的使用``|
|errorTitle|错误框标题|
|errorContent|详细错误内容|
### 8、@ExcelNumericValid
**表头使用，会在导出模板时对该表头下方的单元格加入文本长度、数字大小校验，注解参数如下。``示例用法可参考第五节：注解的使用``**     

|参数|描述|
|-----|-----|
|validClass|校验器Class|
|rows|需要给多少行单元格加上校验，默认``200``|
|operatorType|操作类型，默认``OperatorType.LESS_OR_EQUAL``|
|validType|校验类型，默认``ValidType.TEXT_LENGTH``|
|expr1|表达式1，在表达式2前面，默认``0``|
|expr2|表达式2，在操作类型为``BETWEEN``和``NOT_BETWEEN``情况下必填|
|showErrorBox|是否弹出错误框，默认``true``|
|rank|提示框级别，默认``Rank.STOP``级别|
|errorTitle|错误框标题|
|errorContent|详细错误内容| 
### 9、@ExcelEnumConvert
**枚举转换器，在字段类型为枚举时需要配置，否则在导入导出时未检测到转换器会抛出未找到转换器异常，注解参数如下。``示例用法可参考第五节：注解的使用``**     

|参数|描述|
|---|---|
|convert|实现了``EnumConvert``接口的类|
### 10、@ExcelDataConvert
**数据转换器，导入导出时需要对数据进行加工处理的时候使用**     

|参数|描述|
|---|---|
|expr1|导出时的EL表达式|
|expr2|导入时的EL表达式|
## 三、导出
### 1、定义Excel实体
**只需要在实体类和字段上加上对应注解即可**
```java
@Data //这是lombok的注解，帮完成get、set等方法
@Excel("用户列表")
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField(value = "用户名", sort = 1)
    private String userName;

    @ExcelField(value = "性别")
    private GenderEnum gender;

    @ExcelField(value = "创建时间",format = "yyyy-MM-dd")
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
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList();
        ExcelFactory.createWriter(User.class, response)
                .write(users)
                .flush();
    }
}
```
**忽略导出某些表头，只需在``createWriter()``方法中``ignores``参数指定你要忽略的表头**
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
        ExcelFactory.createWriter(User.class, response,"用户id","创建时间")
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
**导出到不同的sheet或导出多次到同一个sheet中，只需多次调用``write()``方法，不指定sheet的名称则会写入到默认名称的sheet。导出时如若Excel实体为同一个且导出在同一个sheet时，只会出现一次表头**
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
**导出时指定大标题，在``write()``方法的``bigTitlte``参数设置即可**
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
                .write(users,new BigTitle(3,"我是大标题"))
                .flush();
    }
}
```
**重置当前导出的Excel实体，该操作要在每次需要调用``write()``之前**
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
**整个链式调用完毕后，一定要在最后调用``flush()``方法，否则数据不会写入到Excel文件中**
## 四、导入
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
**读取多个sheet中的数据时，无法通过``get()``方法进行获取数据，这时可以通过订阅的方式去订阅导入后的数据，该订阅方法时一个函数式接口，你可以自行做一些处理**
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
        ExcelFactory.createReader(file.getInputStream(), User.class)
                .read()
                .subscribe(e -> userService.saveUserList(e))
                .read("sheet2")
                .subscribe(e -> userService.saveUserList(e));
        return ResponseEntity.ok("导入成功");
    }
}
```
**导入时Excel存在大标题的时候，需要指定表头的下标，下标你为你导出这个模板时设置的大标题的行数(``rows``)，要在调用``read()``前配置**
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
                .headerIndex(2)
                .read()
                .get();
        userService.saveUserList(users);
        return ResponseEntity.ok("导入成功");
    }
}
```
**限制读取数据的条数，要在调用``read()``前配置**
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
                .readLines(5)
                .read()
                .get();
        userService.saveUserList(users);
        return ResponseEntity.ok("导入成功");
    }
}
```
**导入时会在每次读取完一行或者读取到您设置不允许为空的情况下发生回调，您可以通过回调进行参数校验、异常数据记录等等，可以实现``ReadCallback``接口并重写自己需要的回调方法，该接口支持泛型**
```java
/**
 * 导入时的回调
 *
 * @author Gjing
 **/
public class MyReadCallback implements ReadCallback<Object> {
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
     * 当读取到某个表头下的单元格是空的时候，同时该表头设置了不允许为空，会产生该回调
     * @param field 当前为空的字段
     * @param excelField 该字段上的@ExcelField注解
     * @param rowIndex 当前单元格所在的行数下标，下标是从0开始的
     * @param colIndex 当前单元格所在的列数，下标是从0开始的
     */
    @Override
    public void readJump(Field field, ExcelField excelField, int rowIndex, int colIndex) {
        System.out.println("当前错误的是第：" + (rowIndex + 1) + "行，第: " + (colIndex + 1) + "列");
    }

     /**
      * 读取到每一个非空单元格时会触发该回调
      * @param val 单元格内容
      * @param field 当前字段
      * @param rowIndex 当前单元格所在的行数下标，下标是从0开始的
      * @param colIndex 当前单元格所在的列数，下标是从0开始的
      * @return 加工后的val
      */
     @Override
     public Object readCol(Object val, Field field, int rowIndex, int colIndex) {
         return val;
     }
}
```
**实现回调接口后可在调用``read()``方法时进行设置**
```java
@RestController
public class TestController{
    @Resource
    private UserService userService;
    
    @PostMapping("/user_import")
    public ResponseEntity<String> user1ImportLimit(MultipartFile file) throws IOException {
        ExcelFactory.createReader(file.getInputStream(), User.class)
                .read(MyReadCallback::new)
                .subscribe(e -> this.userService.saveAllUser(e));
        return ResponseEntity.ok("导入成功");
    }
}
```
## 五、注解的使用
### 1、枚举转换器
**字段为枚举类型，在导入导出时需要指定枚举转换器，只需实现``EnumConvert``接口，然后在对应字段使用**
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
**表头字段上使用``@ExcelEnumConvert``注解并指定转换器**
```java
@Excel("用户列表")
public class User {

    @ExcelField("性别")
    @ExcelEnumConvert(convert = GenderEnum.MyExcelEnumConvert.class)
    private GenderEnum genderEnum;
}
```
### 2、数据转换器
**该转换器主要提供导入导出时对某个单元格进行数据加工，如数据替换、默认值设置。。转换器一共有接口方式与注解方式，``同时出现只取其一``**
#### a、实现接口方式
**实现``DataConvert``接口，该接口支持泛型，泛型为你的实体类型**
```java
/**
 * @author Gjing
 **/
public class MyDataConvert implements DataConvert<User> {
    /**
     * 转实体的时候会触发
     * @param o 当前单元格的值
     * @param field 当前属性
     * @return 加工后的单元格值
     */
    @Override
    public Object toEntityAttribute(Object o, Field field) {
        return o;
    }

    /**
     * 写入Excel的时候会触发
     * @param user1 当前对象
     * @param o 当前属性的值
     * @param field  当前属性
     * @return 加工后的属性值
     */
    @Override
    public Object toExcelAttribute(User1 user1, Object o, Field field) {
        return o;
    }
}
```
**在你要自定义数据转换器的表头使用**
```java
@Excel("用户列表")
public class User {

    @ExcelField(value = "备注",convert = MyDataConvert.class)
    private String remark;
}
```
#### b、注解方式
**通过使用``@ExcelDataConvert``注解进行设置表达式，以下演示了设置默认值**
```java
@Excel("用户列表")
public class User {

    @ExcelField(value = "备注")
    @ExcelDataConvert(expr1 = "'我是备注'")
    private String remark;
}
```
### 3、下拉框
#### a、注解指定
**在实体属性直接通过注解的``combobox``指定下拉框中的内容**
```java
@Excel("用户列表")
public class User {

    @ExcelField("性别")
    @ExcelDropdownBox(combobox = {"男","女"})
    @ExcelEnumConvert(convert = GenderEnum.MyExcelEnumConvert.class)
    private GenderEnum genderEnum;
}
```
#### b、方法参数设置
**通过方法的``explicitValues``参数进行设置，该参数为map类型，key为表头字段名，value为下拉框的内容。下面演示了为表头字段加入下拉框内容**
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {

    @GetMapping("/user_empty")
    @ApiOperation(value = "导出用户模板")
    public void exportUserEmpty(HttpServletResponse response) {
        String[] arr = new String[]{"男","女"};
        Map<String, String[]> map = new HashMap<>(16);
        map.put("genderEnum", arr);
        ExcelFactory.createWriter(User.class, response)
                .write(null, map)
                .flush();
    }
}
```
#### c、级联下拉框
**设置带级联关系的下拉框，需要在实体属性中通过``link``参数指定父级的``表头序号-1``，比如父级是第一个单元格，那么link填的序号为``0``**     
```java
@Excel("订单列表")
public class Order {

    @ExcelDropdownBox(combobox = {"小红","小花"}, rows = 10)
    @ExcelField(value = "订单负责人", sort = 1)
    private String orderUser;

    @ExcelDropdownBox(link = "0")//指定父级为第一个单元格，也就是orderUser
    @ExcelField(value = "订单名称", sort = 2)
    private String orderName;
}
```
**指定序号后同样通过参数进行设置，但是在设置参数的时候``key变成了你要关联上级的那个值``，下面演示给两个订单负责人分别增加其拥有的订单，``上级的每个值只允许设置一次下级``**
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
### 4、动态设置合并规则
**如若Excel实体中每个表头都想有独立的纵向合并规则，那么可使用合并回调的方式进行设置，需实现``MergeCalback``接口，该接口需指定当前Excel实体**
```java
/**
 * 如果名字是张三，我才需要合并，否则不需要
 * @author Gjing
 **/
public class MyMergeCallback implements MergeCallback<User1> {
    //该方法是导出的数据第一条触发该回调
    @Override
    public void init(Object o) {
        
    }
    
    //第二条数据开始会连续触发该回调
    @Override
    public boolean toMerge(User1 user1, Field field, int i, int i1) {
        return "张三".equals(user1.getUserName());
    }
}
```
**在Excel实体的表头指定**
```java
@Excel("测试自定义合并导出")
public class User1 {
    @ExcelField(value = "用户名", autoMerge = @Merge(open = true,callback = MyMergeCallback.class))
    private String userName;
}
```
### 5、时间校验
**该注解默认采取的方式是设置时间范围，你也可以通过``operatorType``去修改，``expr2``只在操作类型为``between``或者``NotBetween``时需要设置，对于其他操作类型设置``expr1``即可**
```java
@Excel("用户列表")
public class User {

    @ExcelField(value = "创建时间",format = "yyyy-MM-dd")
    @ExcelDateValid(rows = 10, expr1 = "2019-10-11", expr2 = "2019-10-13")
    private Date createTime;
}
```
### 6、值校验
**对单元格进行值类型校验，下面演示了限制输入用户名的长度不能超过2位，对于其他判断方式可自行查看参数**
```java
@Excel("用户列表")
public class User {

    @ExcelField(value = "用户名",autoMerge = true,sort = 1)
    @ExcelNumericValid(rows = 10,expr1 = "2")
    private String userName;
}
```
### 7、单元格内容校验
**在表头增加``@ExcelAssert``注解，导入时会对该表头下的单元格内容进行校验，下面示例中设置了订单名称不能为空**
```java
@Excel("订单列表")
public class Order {
    @ExcelField("订单名称")
    @ExcelAssert(expr = "#orderName != null", message = "订单名称不能为空")
    private String orderName;
}
```
## 六、自定义
**之前讲解的使用的一些功能都是采取默认实现的，有时候有点自己的想法，也可以进行自定义其中的功能**
### 1、自定义Excel样式
**实现``ExcelStyle``接口，里面有设置大标题、正文、表头样式的三个方法，选择你要自定义的方法进行重写即可，以下演示了自定义大标题样式**
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

    @ExcelField(value = "创建时间",format = "yyyy-MM-dd")
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
    public void exportUser(HttpServletResponse response) {
        List<User> users = userService.userList(0);
        ExcelFactory.createWriter(User.class, response)
                .write(users, MyExcelStyle::new)
                .flush();
    }
}
```
### 2、自定义校验注解的处理流程
**自定义校验注解的处理逻辑，可以实现``ExcelDateValidation``、``ExcelNumericValidation``、``ExcelDropdownBoxValidation``接口，下面举例实现``ExcelDropdownBoxValidation``接口**
```java
/**
 * @author Gjing
 **/
public class MyValid implements ExcelDropdownBoxValidation {
    @Override
    public boolean valid(ExcelDropdownBox excelDropdownBox, Workbook workbook, Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol, boolean locked, 
        String fieldName, Map<String, String[]> values) {
        return locked;
    }
}
```
**修改你实体类中对应注解的默认处理类，没有指定的注解还是会走默认处理**
```java
@Excel("用户列表")
public class User {
    @ExcelField("用户Id")
    private Long id;

    @ExcelField("用户名")
    @ExcelDropdownBox(validClass = MyValid.class,rows = 10)
    private String userName;

    @ExcelField(value = "创建时间",format = "yyyy-MM-dd")
    //在当前表头下方的第一行单元格中，时间只能输入在2019-10-11至2019-10-13范围的时间
    @ExcelDateValid(expr1 = "2019-10-11",expr2 = "2019-10-13")
    private Date createTime;
}
```
---
**Demo地址：[excel-demo](https://github.com/archine/excel-demo)**
