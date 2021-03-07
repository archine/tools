![](https://img.shields.io/badge/version-2021.5.1-green.svg) &nbsp; ![](https://img.shields.io/badge/builder-success-green.svg) &nbsp;
![](https://img.shields.io/badge/Author-Gjing-green.svg) &nbsp;       

**简单、快速的在项目中进行Excel导入导出**
# 一、导入依赖
```xml
<dependency>
    <groupId>cn.gjing</groupId>
    <artifactId>tools-excel</artifactId>
    <version>2021.5.1</version>
</dependency>
```
# 前言
> **1、名词意义**:
> * **数据单元格:**  Excel列下方的单元格，除了表头那几行的单元格，是真正填写数据内容的单元格
> * **表头单元格:** Excel列下方表头那几行的单元格
> * **真实表头:** Excel列中可以设置一个表头数组，数组中最后一个值为真实表头，如数组中设置了三个元素，那么第三个元素为真实表头，第二个为第三个的父级表头，第一个为第二个和第三个的父级表头，也就是说前一个元素是后一个元素的父级。如果数组中只有一个元素，那么就只有自己了。   
> 
> **2、真实表头下标计算公式:** 大标题行数 + 表头级数 - 1
# 二、注解说明
## ~ 基本注解
### 1、@Excel(Excel文件注解)
标注在实体类，声明该实体类与Excel文件存在映射关系 (简称``Excel实体``)。在导入导出时如果指定的实体类没有增加该注解，会抛出异常提示实体类未找到该注解

|参数|描述|
|---|---|
|value|导出Excel的文件名，也可以在导出时通过方法设置(优先级高于注解设置) ``两种方式都没指定的情况下会默认为当前日期``|
|type|Excel文件类型，默认``XLS``|
|windowSize|窗口大小，导出时如果已经写出的数据超过指定大小，则将其刷新到磁盘 ``仅适用于XLSX``|
|cacheRowSize|导入时需要保存Excel文件中的多少行数据 ``仅适用于XLSX``|
|bufferSize|将InputStream读入文件时使用的缓冲区大小 ``仅适用于XLSX``|
|headerHeight|表头行高|
|bodyHeight|正文行高|
|uniqueKey|映射实体唯一key，默认为实体类全路径名|    

### 2、@ExcelField(Excel列注解)
标注在实体类字段，声明该字段对应Excel文件中的一列 (简称``Excel列``)。未通过该注解标注的将不会产生映射

|参数|描述|
|---|---|
|value|表头数组，数组长度对应表头级数，数组中最后一个值为真实表头名称 (简称``真实表头``)，同一个Excel实体指定的表头名称数组``长度必须一致``|
|width|宽度|
|order|顺序，如果指定了那么会从小到大进行排序，不指定的情况下``默认按类中字段出现的顺序，如果继承的父类中也有Excel列，那么会追加到本类的Excel列后面``。导出时依次写出到Excel文件中|
|format|单元格格式，默认``常规``，可以使用``@``更改格式为文本、``0``为整数、``0.00``为两位小数、``yyyy-MM-dd``为年-月-日。更多格式请参照Excel文件中的单元格格式|
|autoMerge|数据单元格纵向合并，与``合并注解``搭配使用|
|required|数据单元格必填，默认``false``|
|trim|导入时数据单元格读取到的内容如果是文本就去除内容两边的空格，默认``false``|
|color|表头背景填充色|
|fontColor|表头字体颜色|
|convert|数据转换器，同一个转换器对象在一次完整的链路调用直到结束只会实例化一次|     

### 3、@Merge(合并注解)
与Excel列注解中的``autoMerge``参数搭配使用     

|参数|描述|
|---|---|
|enable|开启纵向合并，默认``false``|
|empty|碰到空值是否也合并，默认``false``|
|callback|自动合并回调器，默认使用框架中提供的自动合并回调器|

## ~ 功能性注解
这些注解都作用在Excel列上，普通字段无效
### 1、@ExcelDataConvert(数据转换注解)
导入导出时对内容进行数据转换

|参数|描述|
|---|---|
|expr1|导出的EL表达式|
|expr2|导入的EL表达式|    

### 2、@ExcelAssert(断言注解)
导入时对读取到的数据单元格内容进行数据断言，判断数据是否满足表达式

|参数|描述|
|---|---|
|expr|EL表达式，表达式为一个boolean条件|
|message|不满足表达式的条件时抛出的异常信息|       

## ~ 校验注解
运用在``导出Excel模板时``，给数据单元格增加公式从而限制用户输入的内容、数字大小、文本长度等，注意：当碰到用户在复制其他地方的单元格内容过来时，如果把复制过来的单元格格式、公式等一起黏贴进我们的数据单元格内，那么就会覆盖我们给这个数据单元格加上的公式，从而导致无效。``导出模板时默认是不会开启这些注解的，需要我们调用导出时同时设置开启校验注解属性``
### 1、@ExcelDropdownBox(下拉框注解)
导出时给数据单元格增加下拉框

|参数|描述|
|---|---|
|combobox|下拉框选项数组，此处配置的选项``不允许超过25个``，大量选项的情况请通过方法设置。两种方式都存在时会以方法设置的为准|
|rows|多少行数据单元格加上下拉框，默认100|
|showErrorBox|填入数据单元格的内容不是下拉框中的选项，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|
|link|当前表头的父级表头下标，与父级进行级联|     

### 2、@ExcelDateValid(数据校验注解)
导出时给数据单元格加上时间格式校验，限制用户输入的时间要在我设置的范围内

|参数|描述|
|---|---|
|rows|多少行数据单元格加上校验，默认100|
|pattern|时间格式|
|operatorType|操作类型，可选择小于、大于、区间等|
|expr1|时间表达式1，如：2020-04-10|
|expr2|时间表达式2，仅在操作类型为``between``或者``not_between``时会取该表达式，没有用这两种类型的时候只需要写表达式1即可|
|showErrorBox|填入数据单元格的内容不符合要求，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|
|showTip|点击单元格是否出现提示框|
|tipTitle|提示标题|
|tipContent|提示内容|      

### 3、@ExcelNumericValid(数字文本校验注解)
导出时给数据单元格增加数字或者文本数据校验，限制用户输入的文本长度范围或者数字的大小范围

|参数|描述|
|---|---|
|rows|多少行数据单元格加上校验，默认100|
|operatorType|操作类型，可选择小于、大于、区间等|
|validType|校验类型，可选择整数、文本长度、小数|
|expr1|表达式1，如：1|
|expr2|表达式2，仅在操作类型为``between``或者``not_between``时取该表达式，没有用这两种类型的时候只需要写表达式1即可|
|showErrorBox|填入数据单元格的内容不符合要求，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|
|showTip|点击单元格是否出现提示框|
|tipTitle|提示标题|
|tipContent|提示内容|    

### 4、@ExcelRepeatValid(数据重复校验注解)
导出时给数据单元格增加数据重复校验，限制用户在表头所在列输入同样的内容

|参数|描述|
|---|---|
|rows|多少行数据单元格加上校验，默认100|
|longTextNumber|长数字文本类型，Excel文件如果碰到数字长度超过15位时，Excel文件会``自动将15位后的数字转换为0``，间接导致重复内容检查错误，所以如果内容是数字且超过了15位需要设置为``true``，默认``false``，该属性设置``true``后需要将表头的单元格格式设置为``@``|
|showErrorBox|填入数据单元格的内容出现重复，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|   

### 5、@ExcelCustomValid(自定义公式校验注解)
导出时给数据单元格增加自定义公式的数据校验，限制用户输入的内容

|参数|描述|
|---|---|
|formula|公式，参考Excel的公式|
|rows|多少行数据单元格加上校验，默认100|
|showErrorBox|填入单元格的内容不符合要求，是否打开错误框|
|rank|错误框级别|
|errorTitle|错误框标题|
|errorContent|错误提示信息|      

## ~ 驱动导出模式注解
### 1、@EnableExcelDrivenMode
在启动类标注该注解即可开启Excel注解驱动模式，当开启了驱动模式时即可在方法上通过注解对Excel进行导入导出

### 2、@ExcelRead(驱动导入注解)
标注在Controller指定方法上，声明该方法是一个Excel导入方法

|参数|描述|
|---|---|
|value|要读取的Sheet名称，默认``Sheet1``|
|check|是否检查Excel文件与当前导入时设置的Excel实体是否匹配，默认``false``|
|~~metaInfo~~|是否需要读取元信息，比如标题，默认``false``|
|headBefore|读取真实表头前面的所有行，默认``false``，只从真实表头下标开始读取|
|ignores|导入时要忽略的表头，如果表头是父级表头的话，那么其子表头也会被忽略|
|headerIndex|真实表头下标，默认0|      

### 3、@ExcelWrite(驱动导出注解)
标注在Controller指定方法上，声明该方法是一个Excel导出方法

|参数|描述|
|---|---|
|mapping|Excel实体|
|ignores|忽略的表头，如果表头是父级表头，那么其下面的子表头名称也会被忽略|
|value|Excel文件名，如果未指定且映射实体中``Excel``注解也未指定，则会使用当前日期作为文件名|
|sheet|导出数据到哪个sheet，默认``Sheet1``|
|needValid|开启校验注解，默认``false``|
|needHead|需要写出表头，默认``true``|
|multiHead|多级表头，默认``false``|
|initDefaultStyle|使用默认样式监听器，默认``true``|
|bind|Excel文件与当前Excel实体进行绑定，默认``true``|   

# 三、常用对象
## ~ 基本对象
### 1、BigTitle
**用于给Excel文件增加大标题**

|属性|描述|
|---|---|
|lines|大标题需要占用多少行，默认``2``|
|firstCol|开始Excel列下标，默认第一个Excel列下标|
|lastCol|截止Excel列下标，默认为最后一个Excel列的下标|
|content|内容|
|color|背景填充颜色|
|fontColor|字体颜色，默认``黑色``|
|rowHeight|行高|
|alignment|水平位置，默认``靠左``|
|bold|字体加粗，默认``false``|
|fontHeight|字体高度，默认``250``|
|index|样式索引，默认0，如果设置的样式索引已存在则会取已存在的，否则会新创建一个样式|
|callback|大标题回调，会在填充内容到单元格前进行触发，这个参数是个函数式接口，第一个参数是WorkBook对象，第二个参数是当前cell，第三个参数是当前BigTitle对象|     

## ~ 驱动模式对象
### 1、ExcelReadWrapper\<R>
驱动方式导入时的数据构造器，作为被驱动导入注解标注过的方法返回值类型。泛型R为你的Excel实体类型

|方法|描述|
|---|---|
|build|初始化一个``wrapper``|
|data|导入的Excel文件或者文件流|
|listener|导入时需要用到的监听器|
|subscribe|添加结果监听器|
|ignores|忽略读取的表头|    

### 2、ExcelWriteWrapper
驱动方式导出时的数据构造器，作为被驱动导出注解标注过的方法返回值类型

|方法|描述|
|---|---|
|build|初始化一个``wrapper``，可以选择是否在初始化时传入要导出的数据|
|listener|导出时的监听器|
|title|导出的大标题|
|data|导出的数据|
|ignores|需要忽略的表头|
|boxValues|设置下拉框的内容，此处设置优先级高于下拉框注解中设置|
|fileName|导出的文件名，如果此处未设置则会取驱动导出注解中的值，如果注解中也未指定，则取Excel实体中的@Excel注解设置的文件名，如果一样未设置则使用当前时间|   

## ~ 导出监听器
导出时通过``addListener()``方法将监听器实例添加到执行器
### 1、ExcelCascadingDropdownBoxListener
级联下拉框监听器，该监听器主要运用在你Excel实体中有Excel列设置了级联下拉框的时候，框架中提供了一个默认实现的级联监听器``DefaultCascadingDropdownBoxListener``，你也可以自己实现。该监听器需要搭配下拉框注解中的``link``参数进行使用。
### 2、ExcelCellWriteListener
单元格监听器，该监听器主要运用在需要对单元格赋值前、赋值后做拓展操作的时候，比如修改要赋值到单元格的内容、单元格追加创建。。。
### 3、ExcelRowWriteListener
行监听器，该监听器主要运用在需要对当前这一行做拓展操作的时候，该监听器触发的时候，说明这一行已经完成了，即将开始下一行。
### 4、ExcelSheetWriteListener
Sheet监听器，该监听主要运用在需要在Sheet创建完成后做拓展操作。
### 5、ExcelWorkbookWriteListener
WorkBook监听器，该监听器主要运用所有数据都写出完毕了，即将刷新数据并实现下载的时候，比如要设置某一列进行求和。
### 6、ExcelStyleWriteListener
样式监听器，主要用于设置导出的Excel文件中的样式。框架中提供了一个默认实现的样式监听器``DefaultExcelStyleWriteListener``，如果对默认的整体样式不喜欢，可以自己实现。如果要运用自己的样式监听器，需要在导出时设置``initDefaultStyle``参数为false，然后将自己的样式监听器实例添加到执行器。
## ~ 导出回调器
### 1、ExcelAutoMergeCallback
自动合并回调器，该回调器主要运用在你导出时，需要将上下相邻的单元格进行合并。框架中提供了一个默认实现的自动合并回调监听器``DefaultExcelAutoMergeCallback``，该监听器实现上下相邻的单元格只要内容相同就进行合并。如果你有不同的业务场景请自行实现回调器。通过在合并注解中的``callback``参数指定。
## ~ 导入监听器
导入时通过``addListener()``方法将监听器实例添加到执行器
### 1、ExcelRowReadListener
行监听器，该监听器主要运用在导入时需要对开始读取数据前、读取成功一个单元格后、读取完一行后、所有数据读取完毕后进行拓展操作。
### 2、ExcelResultReadListener
结果监听器，一旦设置了该监听器，那么会缓存读取到每一行数据生成Excel实体对象。该监听器主要运用在数据全部完毕后要进行拓展操作，比如数据存库、打印所有数据等。
### 3、ExcelEmptyReadListener
空值监听器，该监听器需要搭配``Excel列中required``参数使用。该监听器会在导入时判断读取到数据单元格的内容是否为空，如果为空，且``required``为``true``，就会触发。
## ~ 数据转换器
### 1、DataConvert
数据转换器，导入导出时对内容进行修改，转换器需要搭配``Excel列注解的convert参数``。主要运用在需要修改内容为自己要的格式，比如枚举类型的Excel列，我们导出时要的是中文，导入的时候需要将中文描述转成枚举。。。
## ~ 工具类
### 1、ExcelUtils
里面包括操作excel的一些方法，比如增加校验、设置单元格内容、合并、创建公式、创建字体等。。
# 四、使用案例
**使用案例请前往查看使用案例: [excel-demo](https://github.com/archine/excel-demo)**



[**置顶**](#top)

---