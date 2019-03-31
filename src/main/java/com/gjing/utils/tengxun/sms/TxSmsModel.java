package com.gjing.utils.tengxun.sms;

import lombok.*;

/**
 * @author Gjing
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxSmsModel {
    /**
     * 短信应用Id
     */
    @NonNull
    private Integer appId;
    /**
     * 短信应用appKey
     */
    @NonNull
    private String appKey;
    /**
     * 需要发送短信的手机号码 {"21212313123", "12345678902", "12345678903"}
     */
    private String[] phoneNumbers;
    /**
     * 数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
     */
    private String[] params;
    /**
     * 短信模板ID，需要在短信应用中申请
     */
    @NonNull
    private Integer smsTemplateId;
    /**
     * 短信签名
     */
    @NonNull
    private String smsSign;
}
