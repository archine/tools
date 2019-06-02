package cn.gjing;

import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

import java.util.Objects;

/**
 * @author Gjing
 * 无论单发/群发短信还是指定模板 ID 单发/群发短信都需要从控制台中申请模板并且模板已经审核通过，才可能下发成功，否则返回失败
 **/
public class TxSms {

    /**
     * 短信应用Id
     */
    private Integer appId;
    /**
     * 短信应用appKey
     */
    private String appKey;

    private SmsSingleSender smsSingleSender;

    private SmsMultiSender smsMultiSender;

    private TxSms(Integer appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    public static TxSms of(Integer appId, String appKey) {
        Objects.requireNonNull(appId, "appId cannot be null");
        Objects.requireNonNull(appKey, "appKey cannot be null");
        return new TxSms(appId, appKey);
    }

    private TxSms getSmsSingleSender() {
        this.smsSingleSender = new SmsSingleSender(this.appId, this.appKey);
        return this;
    }

    private TxSms getSmsMultiSender() {
        this.smsMultiSender = new SmsMultiSender(appId, appKey);
        return this;
    }

    /**
     * 指定模板 ID 单发短信
     * @param params 数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
     * @param phoneNumber 需要发送短信的手机号码 157*******
     * @param smsSign  短信签名
     * @param smsTemplateId 短信模板ID，需要在短信应用中申请
     * @return 发送结果 0 表示成功(计费依据)，非 0 表示失败 更多状态吗请看下方链接
     * @see <a href="https://cloud.tencent.com/document/product/382/3771"></a>
     */
    public String send(String phoneNumber, String[] params, Integer smsTemplateId, String smsSign) {
        if (phoneNumber==null||phoneNumber.length()<1) {
            throw new IllegalArgumentException("PhoneNumbers cannot be null");
        }
        try {
            SmsSingleSenderResult result = this.getSmsSingleSender().smsSingleSender.sendWithParam("86", phoneNumber,
                    smsTemplateId, params, smsSign, "", "");
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * 指定模板 ID 群发短信,群发一次请求最多支持200个号码。
     * @param params 数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
     * @param phoneNumbers 需要发送短信的手机号码 {"21212313123", "12345678902", "12345678903"}
     * @param smsSign  短信签名
     * @param smsTemplateId 短信模板ID，需要在短信应用中申请
     * @return 发送结果 0 表示成功(计费依据)，非 0 表示失败
     * @see <a href="https://cloud.tencent.com/document/product/382/3771"></a>
     */
    public String multiSend(String[] phoneNumbers, String[] params, Integer smsTemplateId, String smsSign) {
        if (phoneNumbers==null||phoneNumbers.length<1) {
            throw new IllegalArgumentException("PhoneNumbers cannot be empty");
        }
        try {
            SmsMultiSender multiSender = this.getSmsMultiSender().smsMultiSender;
            // 签名参数未提供或者为空时，会使用默认签名发送短信
            SmsMultiSenderResult result = multiSender.sendWithParam("86", phoneNumbers,
                    smsTemplateId, params, smsSign, "", "");
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

}
