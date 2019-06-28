# tools-common
![](https://img.shields.io/badge/version-1.0.4-green.svg) &nbsp; ![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp;
 ![](https://img.shields.io/badge/builder-success-green.svg)   
 提供参数校验与处理，excel导出，时间转换，数据加密,线程池,验证码,发送邮件,开启跨域等功能...
 > **推荐使用最新版本，由于项目使用了lombok，请确保本地开发工具安装了lombok插件**    
 
**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-common/" title="公用组件包">tools-common</a>
> **使用方式**
---
注解:
* @NotNull: 可以在普通程序和web程序中使用,适用于方法参数校验,如若要排除方法中的某个参数,搭配使用@ExcludeParam注解到指定参数上;
* @NotNull2: 只在web程序中使用,适用于方法,如若要排除方法中的某个参数不检验,可进行@NotNull2(exclude={"参数名1","参数名2"}),:exclamation: 参数名必须与方法的参数名相同,   
                         默认异常信息为参数不能为空,可以自定义异常信息@NotNull2(message="您要使用的异常异常");   
* @EnableCors（SpringBoot环境使用）: 允许跨域,标注在启动类上
```yaml
cors:
  allowed-methods: POST,GET,DELETE,PUT
  allowed-headers:
  allowed-origins:
  path: /**
  max-age: 1800
```
返回结果模板:   
* ResultVo: 通用返回结果模板,包含code(状态码),message(提示信息),data(数据)三个参数
* PageResult: 分页查询返回结果模板,包含data(数据)和totalPage(总页数)以及CurrentPage(当前页数),使用时可以直接使用builder构造,也可以调用其中of方法.
* ErrorResult: 错误返回模板, 里面包含failure(状态码400时使用,里包含code和message,code用于进一步确定错误),error(服务器型异常,一般用于500等,只包含message)
---
Excel:   
* 导出: :exclamation: response, headers,title不能为空 
```
@RequestMapping("/excel")
public void excel(HttpServletResponse response) {
    String[] headers = {"标题1", "标题2"};
    ExcelUtil.excelExport(response, null, headers, "测试无内容excel", null);
}

@RequestMapping("/excel2")
public void excelContainsValue(HttpServletResponse response) {
    String[] headers = {"标题1", "标题2"};
    List<Object[]> data = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        Object[] objects = new Object[headers.length];
        objects[0] = i;
        objects[1] = i+1;
        data.add(objects);
    }
    ExcelUtil.excelExport(response, data, headers, "测试含有内容excel", null);
}

@RequestMapping("/excel3")
public void excelContainsInfo(HttpServletResponse response) {
    String[] headers = {"标题1", "标题2"};
    List<Object[]> data = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        Object[] objects = new Object[headers.length];
        objects[0] = i;
        objects[1] = i+1;
        data.add(objects);
    }
    ExcelUtil.excelExport(response, data, headers, "测试含有详情的excel", "详情");
}
```
实用工具类:   
* ParamUtil: 主要提供参数校验、处理,匹配等等;
```
里面主要包括(方法名:对应功能): 
    1.isEmpty: 判断给定参数是否为空; 2.isNotEmpty: 不为空; 3.listHasEmpty: 判断集合里是否含空; 4.requireNotNull: 不为空返回原值;   
    5.multiEmpty: 多参数判断是否含空; 6.equals: 判断两个参数是否相等; 7.trim: 去除参数或者集合里每个参数的多余的空;   
    8.removeSymbol: 移除字符串两边的符号,对应的startSymbol和endSymbol为首尾坐标; 9.removeSymbol2:移除参数中所有的符号;   
    10.split: 根据符号分割; 11.contains: 判断数组里是否含某值; 13.其他手机号,邮箱等匹配.
```
* TimeUtil: 主要用于操作时间和日期;
```
里面包括(方法名:对应功能):
    1.dateToString: 获取文本时间; 2.stringToDate:获取Date; 3.stringDateToCalendar: 文本时间转成日期; 4.calendarToDate:日期转Date; 5.calendarToStringDate: 日期转文本;   
    6.getAllDaysOfMonth: 获取某个日期的当月总天数; 7.getDays: 获取给定日期的天数; 8.getYears:获取给定日期的年份; 9:getMonth:获取给定日期的月份; 10.addMonth:给定日期增加月份.   
    11.addDay:日期上增加天数; 12.stringDateToStamp: 文本时间转为时间戳; 13.stampToStringDate:时间戳转为文本时间; 14: dateBetween:获取两个日期相差的天数,带include的为包括今天;   15.dateToLocalDateTime:Date转LocalDateTime; 16.dateToLocalDate:Date转LocalDate; 17. localDateToDate: LocalDate转Date; 18.localDateToString: localDate转String;   
    19: stringToLocalDate: String格式日期转LocalDate; 20: localDateTimeToStamp: localDateTime转时间戳; 21: getYearsByStartTime: 查询指定日期距离当前多少年;   
    22: stampToLocalDateTime: 时间戳转LocalDateTime.......
```
* EncryptionUtil: 主要用于加密,目前含有MD5、sha256Hmac、sha1Hmac、base64、Aes;
* AuthCodeUtil: 简单验证码工具类, 目前只支持英文和数字混合验证码,后期会加上拼图等类型验证码;
```
//第一种情况: 生成验证码到本地指定路径,以下为简单测试,具体逻辑根据业务需求自行设计
public static void main(String[] args) {
    AuthCodeUtil authCodeUtil = new AuthCodeUtil(160,40,5,150);
    try {
        String path="/Users/colin/Desktop/q/code2.png";
        //写入到本地时可以通过getCode()方法获取生成的验证码
        String code = authCodeUtil.writeToLocal(path).getCode();
        System.out.println(code);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
//第二种情况: 以流的方式返回给前端
@GetMapping("/code")
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
@PostMapping("/verifyCode")
@NotNull2(exclude = {"request"})
public ResultVo verifyCode(String code, HttpServletRequest request) {
    HttpSession httpSession = request.getSession();
    String sessionCode = (String) httpSession.getAttribute("code");
    if (ParamUtil.isEmpty(sessionCode)) {
        return ResultVo.error(null, "code不存在");
    }
    if (sessionCode.toLowerCase().equals(code.toLowerCase())) {
        return ResultVo.success();
    }
    return ResultVo.error(null,"invalid code");
}
```
* EmailUtil: 邮件工具类,支持普通邮件和带附件邮件,支持html格式文本,支持群发和抄送,返回true为发送成功
```
里面参数主要包括: host(smtp服务器地址,比如qq邮箱:smtp.qq.com);password(发送者邮箱密码,有些邮箱需要用授权码代替密码);from(发送人邮箱地址);subject(邮件主题);body(邮件内容,支持html);   
files(要发送的附件物理地址,不要可以传null或者空数组);tos(收件人邮箱账号,多个逗号隔开);copyTo(抄送人邮箱地址,多个逗号隔开,不抄送可以传null或者空字符串"");
案例:
public static void main(String[] args) {
    boolean b = EmailUtil.of("smtp.qq.com", "发送人密码或者授权码", "发送人邮箱")
            .sendEmail("主题", "内容",new String[]{"附件物理地址"},"收件人邮箱地址", "抄送人邮箱地址");
    if (b) {
        System.out.println("发送成功");
    }
}
```
---
**详细教程可前往博客查看: [JAVA开发常用工具](https://yq.aliyun.com/articles/704350?spm=a2c4e.11155435.0.0.68153312Yeo5xN)**