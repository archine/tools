# tools-tx
![](https://img.shields.io/badge/version-1.0.1-green.svg) &nbsp; 
![](https://img.shields.io/badge/author-Gjing-green.svg) &nbsp; 
![](https://img.shields.io/badge/builder-success-green.svg)   
提供腾讯短信功能
> **推荐使用最新版本**   

**安装**
---
* <a href="https://mvnrepository.com/artifact/cn.gjing/tools-tx/" title="腾讯工具包">tools-tx</a>
> **使用方法**
---
腾讯短信:   
1. 构建sms模板;   
2. 调用方法 
* 指定模板ID单发短信, :exclamation: 手机号必须为单个,否则抛出号码长度异常,参数必须与你选择的模板参数对应和顺序对应;发送结果 0 表示成功(计费依据)，非 0 表示失败 ,
  更多错误码请前往: <a href="https://cloud.tencent.com/document/product/382/3771">错误码</a>
```
public static void main(String[] args) {
    TxSmsModel smsModel = TxSmsModel.builder().appId("您的appid").appKey("您的appid对应的key").smsTemplateId("短信模板id")
            .smsSign("短信签名").params("短信模板中对应的参数").phoneNumbers("接收短信的手机号").build();
    String result = TxSms.send(smsModel);
    System.out.println("result: "+result);
}
```
* 指定模板ID群发短信, :exclamation: 群发一次请求最多支持200个号码。发送结果 0 表示成功(计费依据)，非 0 表示失败,   
  更多错误码请前往: <a href="https://cloud.tencent.com/document/product/382/3771">错误码</a>
```
public static void main(String[] args) {
    TxSmsModel smsModel = TxSmsModel.builder().appId("您的appid").appKey("您的appid对应的key").smsTemplateId("短信模板id")
           .smsSign("短信签名").params("短信模板中对应的参数").phoneNumbers("接收短信的手机号").build();
    String result = TxSms.multiSend(smsModel);
    System.out.println("result: "+result);
}
```