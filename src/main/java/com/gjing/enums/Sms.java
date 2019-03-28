package com.gjing.enums;

import lombok.Getter;

/**
 * @author archine
 **/
@Getter
public enum Sms {
    /**
     * 1：sms单条短信，2：query（查询发送记录）
     */
    SMS("dysmsapi.aliyuncs.com","SendSms","2017-05-25"),
    QUERY("dysmsapi.aliyuncs.com","QuerySendDetails","2017-05-25"),
    Batch("dysmsapi.aliyuncs.com","SendBatchSms","2017-05-25");
    private String api;
    private String action;
    private String version;

    Sms(String api, String action, String version) {
        this.api = api;
        this.action = action;
        this.version = version;
    }
}
