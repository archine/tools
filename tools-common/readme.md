# tools-common
![](https://img.shields.io/badge/version-1.0.1-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 提供参数校验与处理，excel导出，时间转换，数据加密,线程池,验证码,发送邮件等功能...
 > **推荐使用最新版本**    
 
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-common/" title="公用组件包">tools-common</a>
> **使用方式**
---
注解:
```
* @NotNull: 可以在普通程序和web程序中使用,适用于方法参数校验,如若要排除方法中的某个参数,搭配使用@ExcludeParam注解到指定参数上;
* @NotNull2: 只在web程序中使用,适用于方法,如若要排除方法中的某个参数不检验,可进行@NotNull(exclude={"参数名1","参数名2"}),:exclamation: 参数名必须与方法的参数名相同,   
                         默认异常信息为参数不能为空,可以自定义异常信息@NotNull(message="您要使用的异常异常");   
* @EnableCors: 允许跨域,标注在启动类上
```  
返回结果模板:   
* ResultVo: 通用返回结果模板,包含code(状态码),message(提示信息),data(数据)三个参数,里面包含success和error静态方法,直接调用即可.
* PageResult: 分页查询返回结果模板,包含data(数据)和totalPage(总页数),使用时可以直接使用builder构造,也可以调用其中of方法.
* ErrorResult: 错误返回模板, 里面包含failure(状态码400时使用,里包含code和message,code用于进一步确定错误),error(服务器型异常,一般用于500等,只包含message)
---
Excel:   
* 导出: :exclamation: response, headers,title不能为空 
```
@RequestMapping("/excel")
@ApiOperation(value = "excel导出", httpMethod = "GET", response = ApiResponse.class)
public void zsyProductTemplate(HttpServletResponse response) {
    ExcelUtil.excelExport(response, "要导出的数据(没有数据可以传null)", "excel表头", "excel文件名", "excel额外的内容(不需要可以传null,一般用于介绍或者总概括excel)");
}
```
实用工具类:   
* ParamUtil: 主要提供参数校验、处理,匹配等等;
```
里面主要包括(方法名:对应功能): 
    1.isEmpty: 判断给定参数是否为空; 2.isNotEmpty: 不为空; 3.listHasEmpty: 判断集合里是否含空; 4.requireNotNull: 不为空返回原值;   
    5.multiEmpty: 多参数判断是否含空; 6.equals: 判断两个参数是否相等; 7.trim: 去除参数或者集合里每个参数的多余的空;   
    8.toUpperCase/toLowerCase: 大小写; 9.removeSymbol: 移除字符串两边的符号,对应的startSymbol和endSymbol为首尾坐标; 10.removeSymbol2:移除参数中所有的符号;   
    11.split: 根据符号分割; 12.contains: 判断数组里是否含某值; 13.其他手机号,邮箱等匹配.
```
* TimeUtil: 主要用于操作时间和日期;
```
里面包括(方法名:对应功能):
    1.getDateAsString: 获取文本时间; 2.getDate:获取Date; 3.stringDateToCalendar: 文本时间转成日期; 4.calendarToDate:日期转Date; 5.calendarToStringDate: 日期转文本;   
    6.getAllDaysOfMonth: 获取某个日期的当月总天数; 7.getDays: 获取给定日期的天数; 8.getYears:获取给定日期的年份; 9:getMonth:获取给定日期的月份; 10.addMonth:给定日期增加月份.   
    11.addDay:日期上增加天数; 12.stringDateToStamp: 文本时间转为时间戳; 13.stampToStringDate:时间戳转为文本时间; 14: dateBetween:获取两个日期相差的天数,带include的为包括今天;   15.dateToLocalDateTime:Date转LocalDateTime; 16.dateToLocalDate:Date转LocalDate; 17. localDateToDate: LocalDate转Date; 18.localDateToString: localDate转String;   
    19: stringToLocalDate: String格式日期转LocalDate; 20: localDateTimeToStamp: localDateTime转时间戳; 21: getYearsByStartTime: 查询指定日期距离当前多少年;   
    22: stampToLocalDateTime: 时间戳转LocalDateTime.......
```
* EncryptionUtil: 主要用于加密,目前含有MD5、sha256Hmac、sha1Hmac、base64;
* ExecutorUtil: 线程池工具类，暂时含有无返回值调用和有返回值调用;
```
里面包括(方法名:对应功能):
    1. execute: 无返回值调用; 2.submit: 有返回值调用; 3.cancel:任务移除等待队列;
```
* AuthCodeUtil: 简单验证码工具类, 目前只支持英文和数字混合验证码,后期会加上拼图等类型验证码;
```
//第一种情况: 生成验证码到本地指定路径,以下为简单测试,具体逻辑根据业务需求自行设计
public static void main(String[] args) {
    AuthCodeUtil authCodeUtil = new AuthCodeUtil(160,40,5,150);
    try {
        String path="指定的路径/文件名/".png";
        System.out.println(vCode.getCode()+" >"+path);
        authCodeUtil.write(path);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
//第二种情况: 以流的方式返回给前端
@RequestMapping("/code")
public void getCode(HttpServletResponse response, HttpServletRequest request) throws IOException {
    AuthCodeUtil authCodeUtil = new AuthCodeUtil(100, 50, 4, 50);
    response.setContentType("image/jpeg");
    //禁止图像缓存
    response.setHeader("param", "no-cache");
    response.setDateHeader("Expires", 0);
    //该案例将验证码存在session中,具体业务中根据场景自行设计
    HttpSession session = request.getSession();
    session.setAttribute("code", authCodeUtil.getCode());
    authCodeUtil.write(response.getOutputStream());
}
/**
 * 验证码验证,由于示例为服务端session存储,所以下面为session方式验证,具体根据个人存储条件相应更改
 * @param code 前端传来的验证码
 * @param request request
 * @return .
 */
@RequestMapping("/verifyCode")
@NotNull(exclude = {"request"})
public ResultVo verifyCode(String code,HttpServletRequest request) {
    HttpSession httpSession = request.getSession();
    String sessionCode = (String) httpSession.getAttribute("code");
    if (ParamUtil.paramIsEmpty(sessionCode)) {
        return ResultVo.error(HttpStatus.BAD_REQUEST.getMsg());
    }
    if (sessionCode.toLowerCase().equals(code.toLowerCase())) {
        return ResultVo.success();
    }
    return ResultVo.error("invalid code");
}
```
* EmailUtil: 邮件工具类,支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功,否则抛出GjingException.
```
里面参数主要包括: host(smtp服务器地址,比如qq邮箱:smtp.qq.com);password(发送者邮箱密码,有些邮箱需要用授权码代替密码);from(发送人邮箱地址);subject(邮件主题);body(邮件内容,支持html);   
files(要发送的附件物理地址,不要可以传null或者空数组);tos(收件人邮箱账号,多个逗号隔开);copyTo(抄送人邮箱地址,多个逗号隔开,不抄送可以传null或者空字符串"");
案例:
public static void main(String[] args) {
    boolean b = EmailUtil.getInstance("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
            .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
    if (b) {
        System.out.println("发送成功");
    }
}
```