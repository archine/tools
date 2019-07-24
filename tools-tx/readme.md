# tools-tx
![](https://img.shields.io/badge/version-1.0.2-green.svg) &nbsp; 
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
* 指定模板ID与手机号发送短信, **参数必须与你选择的模板参数对应和顺序对应,如果没有参数可以传空数组**, 发送结果 0 表示成功(计费依据)，非 0 表示失败 ,
  更多错误码请前往: <a href="https://cloud.tencent.com/document/product/382/3771">错误码</a>
```java
@PostMapping("/send-tx")
@ApiOperation(value = "腾讯短信", httpMethod = "POST")
public ResponseEntity sendTx() {
    String send = TxSms.of(appid, "appkey")
            .send("157******", new String[]{"参数1".....}, 模板id, "短信签名");
    return ResponseEntity.ok(send);
}
```
* 指定模板ID群发短信, 群发一次请求**最多支持200个号码**。发送结果 result为0 表示成功(计费依据)，非 0 表示失败,   
  更多错误码请前往: <a href="https://cloud.tencent.com/document/product/382/3771">错误码</a>
```java
@PostMapping("/send-tx-multi")
@ApiOperation(value = "腾讯群发短信", httpMethod = "POST")
public ResponseEntity sendTxMulti() {
    String send = TxSms.of(appid, "appkey")
            .multiSend(new String[]{"157****","168*******"}, new String[]{"参数", "参数"....}, 模板id, "短信签名");
    return ResponseEntity.ok(send);
}

```