![](https://img.shields.io/badge/version-2.0.4-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;     

**简单、快速的导入导出Excel**     
<span id="top"></span>
## 一、安装依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-excel</artifactId>
    <version>2.0.4</version>
</dependency>
```
## 二、常用注解
### 1、@Excel
**声明实体类与Excel存在映射关系**    

|参数|描述|
|---|---|
|value|导出Excel时的文件名，也可以在导出时通过方法设置(优先级高于注解设置) ``两种方式都没指定的情况下会默认为当前日期``|
|type|Excel文件类型，默认``XLS``|
|windowSize|窗口大小，导出时如果已经写出的数据超过指定大小，则将其刷新到磁盘 ``适用于XLSX``|
|cacheRowSize|导入时需要保存Excel文件中的多少行数据 ``适用于XLSX``|
|bufferSize|将InputStream读入文件时使用的缓冲区大小 ``适用于XLSX``|
### 2、@ExcelField
**声明实体类内的字段与Excel表头存在映射关系**     

|参数|描述|
|---|---|
|value|表头名称，该参数为一个数组，数组中的``最后一个值为实际的表头名称``|
|width|表头所在列的整列宽度|
|sort|表头出现在Excel文件中的先后顺序，建议要么都配置要么全不配置，不配置的情况下``默认按实体中字段顺序``|
|format|表头所在列的整列单元格格式，格式参照Excel文件中的单元格格式|
|autoMerge|表头下方是否开启纵向合并|
|allowEmpty|导入时，表头下方的单元格是否允许为空|
|convert|数据转换器，可以在导入导出时对数据进行转换|      
<span id="convert"></span>
### 3、@ExcelDataConvert
**数据转换器，可以用于在导入导出时对内容进行转换，[EL表达式用法参考](http://www.manongjc.com/article/8467.html)，     [查看用例](#convert_use)**    

|参数|描述|
|---|---|
|expr1|导出时的EL表达式|
|expr2|导入时的EL表达式|     
<span id="assert"></span>
### 4、@ExcelAssert
**导入时对单元格的内容进行数据校验，[查看用例](#assert_use)**     

|参数|描述|
|---|---|
|expr|EL表达式，结果必须满足``Boolean``类型|
|message|不满足表达式条件时抛出的异常信息|       
<span id="dropdown"></span>
### 5、@ExcelDropdownBox
**导出时给列表头下方的单元格增加下拉选项，[查看用例](#dropdown_use)**      

|参数|描述|
|---|---|
|combobox|选项内容，此处配置的选项``不允许超过25个``，大量选项的请使用方法设置。两种方式都存在时会以方法设置的为准|
|rows|列表头下方需要给多少行加上下拉框|
|showErrorBox|填入单元格的内容不是下拉框中的选项，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|
|link|父级列表头序号(假设父级是第一列的话，那么就是``0``)|     
<span id="date"></span>
### 6、@ExcelDateValid
**导出时给列表头下方的单元格加上时间格式校验，[查看用例](#date_use)**      

|参数|描述|
|---|---|
|rows|列表头下方需要给多少行加上下拉框|
|pattern|时间格式|
|operatorType|操作类型，可选择小于、大于、区间等|
|expr1|时间表达式1，如：2020-04-10|
|expr2|时间表达式2，仅在操作类型为``between``或者``not_between``时会取该表达式|
|showErrorBox|填入单元格的内容不是下拉框中的选项，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|
|showTip|点击单元格是否出现提示框|
|tipTitle|提示标题|
|tipContent|提示内容|      
<span id="number"></span>
### 7、@ExcelNumericValid
**导出时给列表头下方单元格增加数字或者文本数据校验，[查看用例](#number_use)**     

|参数|描述|
|---|---|
|rows|列表头下方需要给多少行加上下拉框|
|operatorType|操作类型，可选择小于、大于、区间等|
|validType|校验类型，可选择整数、文本、小数|
|expr1|表达式1，如：校验类型为整数、操作类型为小于时，表达式填了``1``, 表示输入的数字必须小于1|
|expr2|表达式2，仅在操作类型为``between``或者``not_between``时取该表达式|
|showErrorBox|填入单元格的内容不是下拉框中的选项，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|
|showTip|点击单元格是否出现提示框|
|tipTitle|提示标题|
|tipContent|提示内容|
## 三、Excel导出
<span id="single"></span>
### 1、单表头
**定义Excel映射实体, ``@Data``是lombok的注解**
```java
/**
 * @author Gjing
 **/
@Data
@Excel("单级表头")
public class SingleHead {
    @ExcelField("姓名")
    private String userName;

    @ExcelField(value = "年龄", format = "0")
    private Integer userAge;

    @ExcelField("性别")
    private Gender gender;

    @ExcelField("爱好")
    private String favorite;
}
```
```java
/**
 * @author Gjing
 **/
@RestController
public class UserController {

    @GetMapping("/test_export")
    @ApiOperation("导出一级表头")
    public void testExport(HttpServletResponse response) {
        //指定映射的实体为刚刚定义的
        ExcelFactory.createWriter(SingleHead.class, response)
                .write(null)
                .flush();
    }
}
```
![single](https://user-gold-cdn.xitu.io/2020/4/10/171637e42b1f4cdc?w=565&h=161&f=png&s=3242)
<span id="multi"></span>
### 2、多级表头
**数组中的每个值代表着一级表头**
```java
/**
 * @author Gjing
 **/
@Data
@Excel("多级表头")
public class MultiHead {
    @ExcelField({"用户名","用户名"})
    private String userName;

    @ExcelField({"年龄","年龄"})
    private Integer age;

    @ExcelField({"形态","身高"})
    private BigDecimal height;

    @ExcelField({"形态","体重"})
    private BigDecimal weight;
}
```
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test_export")
    @ApiOperation("多级表头")
    public void testExport(HttpServletResponse response) {
        ExcelFactory.createWriter(MultiHead.class, response)
                //需要在write前激活多级表头，否则不会自动合并
                .enableMultiHead()
                .write(null)
                .flush();
    }
}
```
![multi](https://user-gold-cdn.xitu.io/2020/4/10/171637e42dad8f9e?w=645&h=176&f=png&s=6960)
### 3、带大标题
[参考实体](#single)    

**大标题的起始行是你要插入的sheet中最后一条数据的下一行，如果sheet中没有数据，就是第一行。你可以配置大标题占用的行数和起始单元格下标(默认第一个单元格)和结束单元格下标(默认跟随表头的数量)**
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test")
    @ApiOperation("含大标题")
    public void testExport(HttpServletResponse response) {
        ExcelFactory.createWriter(SingleHead.class, response)
                //大标题占用两行
                .writeTitle(new BigTitle(2, "我是大标题"))
                .write(null)
                .flush();
    }
}
```
![title](https://user-gold-cdn.xitu.io/2020/4/10/171637e42d9aac99?w=570&h=166&f=png&s=6291)     
<span id="dropdown_use"></span>
### 4、下拉框
**单元格增加下拉框  >> [注解说明](#dropdown)**      
* 通过注解设置
```java
/**
 * @author Gjing
 **/
@Data
@Excel("下拉框导出")
public class SingleHead {
    @ExcelField("性别")
    @ExcelDropdownBox(combobox = {"男", "女"})
    private Gender gender;

    @ExcelField("爱好")
    private String favorite;
}
```
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test_export")
    @ApiOperation("带下拉框")
    public void testExport(HttpServletResponse response) {
        ExcelFactory.createWriter(SingleHead.class, response)
                //需要在write前激活校验
                .enableValid()
                .write(null)
                .flush();
    }
}
```
* 通过方法设置
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test_export")
    @ApiOperation("带下拉框")
    public void testExport(HttpServletResponse response) {
        Map<String, String[]> genderMap = new HashMap<>(8);
        //key为实体类的字段名，使用方法进行设置时，实体字段的@ExcelDropdownBox注解不在需要指定combobox
        //如果指定了也会去覆盖注解中的值
        genderMap.put("gender", new String[]{"男", "女"});
        ExcelFactory.createWriter(SingleHead.class, response)
                .enableValid()
                .write(null, genderMap)
                .flush();
    }
}
```
![下拉框](https://user-gold-cdn.xitu.io/2020/4/10/171637e427dcbd35?w=281&h=98&f=png&s=3060)
### 5、级联下拉框
**导出Excel时给列表头下方的单元格执行级联的下拉框**
```java
/**
 * @author Gjing
 **/
@Data
@Excel("级联下拉框导出")
public class SingleHead {
    @ExcelField("性别")
    @ExcelDropdownBox(combobox = {"男", "女"})
    private Gender gender;

    @ExcelField("爱好")
    @ExcelDropdownBox(link = "0")
    private String favorite;
}
```
**这里比普通下拉框多了一个步骤，需要增加级联下拉框监听器，你可以使用默认的，也可以使用自己实现的，自己实现的话需要实现``ExcelCascadingDropdownBoxListener``接口，这里演示就使用默认的**
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test_export")
    @ApiOperation("带级联下拉框")
    public void testExport(HttpServletResponse response) {
    	//传入二级下拉框的内容，key为你父级对应的值，以下设置了根据性别不同展示不同的爱好选项
        Map<String, String[]> boxValues = new HashMap<>(8);
        boxValues.put("男", new String[]{"游戏", "运动"});
        boxValues.put("女", new String[]{"逛街", "吃"});
        ExcelFactory.createWriter(SingleHead.class, response)
                //需要在write前激活校验
                .enableValid()
                //使用默认的级联下拉框监听器
                .addListener(new DefaultCascadingDropdownBoxListener(boxValues))
                .write(null)
                .flush();
    }
}
```
![级联](https://user-gold-cdn.xitu.io/2020/4/10/171637e434f81597?w=297&h=98&f=png&s=3762)
<span id="date_use"></span>
### 6、时间校验
**导出时给列表头下方的单元格增加时间校验  >> [注解说明](#date)** 
```java
/**
 * @author Gjing
 **/
@Data
@Excel
public class SingleHead {
    @ExcelField("姓名")
    private String userName;

    @ExcelField(value = "年龄", format = "0")
    private Integer userAge;

    @ExcelField(value = "生日", format = "yyyy-MM-dd")
    @ExcelDateValid(expr1 = "2000-01-01", operatorType = LESS_OR_EQUAL, errorContent = "出生日期不能超过2000年")
    private Date birthday;
}
```
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test_export")
    @ApiOperation("带时间校验")
    public void testExport(HttpServletResponse response) {
        ExcelFactory.createWriter(SingleHead.class, response)
                //需要在write前激活校验
                .enableValid()
                .write(null)
                .flush();
    }
}
```
![时间校验](https://user-gold-cdn.xitu.io/2020/4/10/171637e434efd885?w=476&h=110&f=png&s=6274)    
<span id="number_use"></span>
### 7、数字、文本校验
**导出时给列表头下方的单元格增加数值校验。可以对数字的大小，文本的长度进行校验  >>  [注解说明](#number)**
```java
/**
 * @author Gjing
 **/
@Data
@Excel
public class SingleHead {
    @ExcelField("姓名")
    //对输入的名称字数进行校验，字数限制小于4
    @ExcelNumericValid(validType = TEXT_LENGTH,operatorType = LESS_THAN,expr1 = "4",errorContent = "姓名字数小于4")
    private String userName;
}
```
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {

    @GetMapping("/test_export")
    @ApiOperation("带数值校验")
    public void testExport(HttpServletResponse response) {
        ExcelFactory.createWriter(SingleHead .class, response)
                //需要在write前激活校验
                .enableValid()
                .write(null)
                .flush();
    }
}
```
![数值校验](https://user-gold-cdn.xitu.io/2020/4/10/171637e45d734aa7?w=143&h=117&f=png&s=3780)
<span id="convert_use"></span>
### 7、数据转换
**导出时对数据进行加工或者添加默认值，支持注解方式和接口方式  >>  [注解参考](#convert)**
* 注解方式
```java
/**
 * @author Gjing
 **/
@Data
@Excel
public class SingleHead {
    @ExcelField("姓名")
    private String userName;

    @ExcelField(value = "年龄", format = "0")
    //对每个人的年龄乘以10
    @ExcelDataConvert(expr1 = "#userAge * 10")
    private Integer userAge;
}
```
<span id="convert_use_interface"></span>
* 接口方式
```java
/**
 * @author Gjing
 **/
public class MyDataConvert implements DataConvert<SingleHead> {

    @Override
    public Object toEntityAttribute(Object o, Field field) {
        return null;
    }

    @Override
    public Object toExcelAttribute(SingleHead singleHead, Object value, Field field) {
        return (int) value * 10;
    }
}
```
**实现接口后需要在你需要转换的字段上指定转换器**
```java
/**
 * @author Gjing
 **/
@Data
@Excel
public class SingleHead {
    @ExcelField("姓名")
    private String userName;

    @ExcelField(value = "年龄", format = "0", convert = MyDataConvert.class)
    private Integer userAge;
}
```
### 8、添加监听器
**框架中提供了``ExcelCellWriteListener(单元格监听器)``、``ExcelRowWriteListener(行监听器)``、``ExcelSheetWriteListener(sheet监听器)``、``ExcelWorkbookWriteListener(workbook监听器)``、``ExcelCascadingDropdownBoxListener(级联下拉框监听器)``、``ExcelStyleWriteListener(样式监听器)``六个监听器，这里演示通过样式监听器实现自定义样式，该监听器继承了单元格监听器和行监听器，因此你可以在样式监听器中同时设置样式和对单元格和行进行附加操作**
```java
/**
 * 自定义样式监听器，这里只是演示，就只创建了一个样式
 * @author Gjing
 **/
public class MyStyleListener implements ExcelStyleWriteListener {
    private CellStyle cellStyle;
    
    @Override
    public void init(Workbook workbook) {
        this.cellStyle = workbook.createCellStyle();
        this.cellStyle.setAlignment(HorizontalAlignment.CENTER);
        this.cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    @Override
    public void setTitleStyle(Cell cell) {
        //这里设置大标题样式
    }

    @Override
    public void setHeadStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int index, int colIndex) {
    	//这里设置表头样式	
		cell.setCellStyle(this.cellStyle);
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int index, int colIndex) {
    	//这是设置正文样式
    }

    @Override
    public void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, String headName, int index,
                         int colIndex, boolean isHead, Object value) {
       //该方法是ExcelCellWriteListener中的，会在每次单元格填充内容之后触发，这里是为了设置样式，
       //你也可以实现其他的逻辑
        if (isHead) {
            //如果是表头我就设置每列的样式和宽度
            sheet.setColumnWidth(colIndex, excelField.width());
            this.setHeadStyle(row, cell, excelField, field, headName, index, icolIndex);
        }
    }

    @Override
    public void completeRow(Sheet sheet, Row row, int index, boolean isHead) {
        //该方法是ExcelRowWriteListener中的，会在每一行结束之后触发，你可以在这里实现其他的逻辑
    }
}
```
[实体参考](#single)
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private UserService userService;
	
    @GetMapping("/test_export")
    @ApiOperation("带数值校验")
    public void testExport(HttpServletResponse response) {
        //关闭初始化默认样式监听器
        ExcelFactory.createWriter(SingleHead.class, response, false)
                //加入自己定义的样式，要在write方法前调用
                .addListener(new MyStyleListener())
                .write(this.userService.userList())
                .flush();
    }
}
```
![自定义样式](https://user-gold-cdn.xitu.io/2020/4/10/171637e45d463be7?w=562&h=243&f=png&s=15612)
**导出方法调用最后一定要使用``flush()``方法进行数据刷新到Excel文件中**
## 三、导入
### 1、单表头
[参考实体](#single)
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private UserService userService;
	
    @PostMapping("/user_import")
    @ApiOperation("导入单表头")
    public void userImport(MultipartFile file) throws IOException {
        ExcelFactory.createReader(file, SingleHead.class)
                //在read()方法前通过订阅方法增加一个结果监听器，该监听器会在每一次read()结束之后触发
                .subscribe(e -> this.userService.saveUsers(e))
                .read()
                .end();
    }
}
```
### 2、多级表头
[参考实体](#multi)
**前文有提到多级表头时，最后一级为实际表头，所以要在导入时指定实际表头开始下标，由于导出的模板映射实体设置两级表头，因此这里的实际表头为下标为``1``(Excel行和列下标都是``默认0开始的``)**
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private UserService userService;
	
    @PostMapping("/user_import")
    @ApiOperation("导入单表头")
    public void userImport(MultipartFile file) throws IOException {
        ExcelFactory.createReader(file, SingleHead.class)
                //在read()方法前通过订阅方法增加一个结果监听器，该监听器会在每一次read()结束之后触发
                //如果Excel中数据量太大，不建议使用结果监听器，会造成生成了过多的映射实体对象造成内存溢出
                .subscribe(e -> this.userService.saveUsers(e))
                .read(1)
                .end();
    }
}
```
### 3、自定义监听器
**在导入时增加一系列监听器来执行一些拓展，框架中提供了``ExcelEmptyReadListener(非空策略监听器)``、``ExcelResultReadListener(结果监听器)``、``ExcelRowReadListener(行读取监听器)``三种监听器，这里演示添加一个行读取监听器**
```java
/**
 * 自定义行读取监听器
 * @author Gjing
 **/
public class MyReadRowListener implements ExcelRowReadListener<SingleHead> {

    @Override
    public boolean readRow(SingleHead singleHead, List<Object> otherValues, int rowIndex, boolean isHead, boolean isBody) {
        //如果Excel的数据量太大，为了避免生成的映射实体对象过多造成内存溢出，可以选择
        //在这里对当前对象进行存储，然后达到一定数量后就存数据库，然后clear掉这些数据
        System.out.println("读完一行");
        return false;
    }

    @Override
    public Object readCell(Object cellValue, Field field, int rowIndex, int colIndex, boolean isHead, boolean isBody) {
        //每一个单元格读取成功触发该方法
        return cellValue;
    }

    @Override
    public void readFinish(ExcelReaderContext<SingleHead> context) {
        //全部读完时触发该方法
    }
}
```
[参考实体](#single)
```java
/**
 * @author Gjing
 **/
@RestController
public class TestController {
    @Resource
    private UserService userService;
	
    @PostMapping("/user_import")
    @ApiOperation("导入单表头")
    public void userImport(MultipartFile file) throws IOException {
        ExcelFactory.createReader(file, SingleHead.class)
                .addListener(new MyReadRowListener())
                .read()
                .end();
    }
}
```
<span id="assert_use"></span>
### 4、数据校验
**在导入时对数据进行校验，这里演示导入时姓名不能为空  >>  [注解参考](#assert)**
```java
/**
 * @author Gjing
 **/
@Data
@Excel
public class SingleHead {
    @ExcelField("姓名")
    @ExcelAssert(expr = "#userName != null")
    private String userName;
}
```
### 5、数据转换
**导入时对读取到的单元格内容进行加工，支持注解方式和接口方式  >>  [注解参考](#convert)**
```java
/**
 * @author Gjing
 **/
@Excel
@Data
public class SingleHead {
    @ExcelField("性别")
    //导入时对'男' '女'中文进行枚举转换为对应枚举
    @ExcelDataConvert(expr2 = "T(com.gjing.projects.excel.demo.enums.Gender).of(#gender)")
    private Gender gender;
}
```
**枚举如下**
```java
/**
 * @author Gjing
 **/
@Getter
public enum Gender {
    /**
     * 性别
     */
    MAN(1,"男"), WO_MAN(2, "女");
    private int type;
    private String desc;

    Gender(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static Gender of(String desc) {
        for (Gender gender : Gender.values()) {
            if (gender.desc.equals(desc)) {
                return gender;
            }
        }
        return null;
    }
}
```
**接口方式这里不在演示，同导出时一样  >>  [导出接口方式转换](#convert_use_interface)**       

**在导入调用结束后，一定要在最后调用``end()``方法对流进行关闭**       

[**置顶**](#top)

---
**Demo地址：[excel-demo](https://github.com/archine/excel-demo)**
