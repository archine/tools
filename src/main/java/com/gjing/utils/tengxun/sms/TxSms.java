package com.gjing.utils.tengxun.sms;

import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.gjing.ex.SmsException;

/**
 * @author archine
 * 无论单发/群发短信还是指定模板 ID 单发/群发短信都需要从控制台中申请模板并且模板已经审核通过，才可能下发成功，否则返回失败
 **/
public class TxSms {

    /**
     * 指定模板 ID 单发短信
     * @param txSmsModel 腾讯短信model
     * @return 发送结果 0 表示成功(计费依据)，非 0 表示失败 更多状态吗请看下方链接
     * @see <a href="https://cloud.tencent.com/document/product/382/3771"></a>
     */
    public static String send(TxSmsModel txSmsModel) {
        try {
            SmsSingleSender sender = new SmsSingleSender(txSmsModel.getAppId(), txSmsModel.getAppKey());
            SmsSingleSenderResult result = sender.sendWithParam("86", txSmsModel.getPhoneNumbers()[0],
                    // 签名参数未提供或者为空时，会使用默认签名发送短信
                    txSmsModel.getSmsTemplateId(), txSmsModel.getParams(), txSmsModel.getSmsSign(), "", "");
            return result.toString();
        } catch (Exception e) {
            throw new SmsException(e.getMessage());
        }
    }

    /**
     * 指定模板 ID 群发短信,群发一次请求最多支持200个号码。
     * @param txSmsModel tx短信模块
     * @return 发送结果 0 表示成功(计费依据)，非 0 表示失败
     * @see <a href="https://cloud.tencent.com/document/product/382/3771"></a>
     */
    public static String multiSend(TxSmsModel txSmsModel) {
        try {
            SmsMultiSender multiSender = new SmsMultiSender(txSmsModel.getAppId(), txSmsModel.getAppKey());
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsMultiSenderResult result =  multiSender.sendWithParam("86", txSmsModel.getPhoneNumbers(),
                    txSmsModel.getSmsTemplateId(), txSmsModel.getParams(), txSmsModel.getSmsSign(), "", "");
            return result.toString();
        } catch (Exception e) {
            throw new SmsException(e.getMessage());
        }
    }

}
