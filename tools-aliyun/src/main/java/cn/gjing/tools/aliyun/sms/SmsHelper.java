package cn.gjing.tools.aliyun.sms;

import cn.gjing.tools.aliyun.AliyunMeta;
import cn.gjing.tools.aliyun.ToolsAliyunException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

import java.util.Map;

/**
 * SMS sending assistant, used to send SMS messages in search of SMS sending records
 *
 * @author Gjing
 **/
public class SmsHelper {
    private final SmsMeta smsMeta;
    private final IAcsClient client;
    private final String DO_MAIN = "dysmsapi.aliyuncs.com";
    private final String VERSION = "2017-05-25";

    public SmsHelper(AliyunMeta aliyunMeta, SmsMeta smsMeta) {
        this.smsMeta = smsMeta;
        DefaultProfile profile = DefaultProfile.getProfile(this.smsMeta.getRegion(), aliyunMeta.getAccessKey(), aliyunMeta.getAccessKeySecret());
        this.client = new DefaultAcsClient(profile);
    }

    /**
     * Send SMS
     *
     * @param phones Mobile phone number, multiple with English commas separated, the maximum of 1000
     * @return Send the receipt
     */
    public String send(String phones) {
        return this.send(phones, this.smsMeta.getTemplateCode(), this.smsMeta.getSignName(), null);
    }

    /**
     * Send SMS
     *
     * @param phones       Mobile phone number, multiple with English commas separated, the maximum of 1000
     * @param templateCode SMS template code
     * @param signName     SMS sign name
     * @return Send the receipt
     */
    public String send(String phones, String templateCode, String signName) {
        return this.send(phones, templateCode, signName, null);
    }

    /**
     * Send SMS
     *
     * @param phones Mobile phone number, multiple with English commas separated, the maximum of 1000
     * @param param  The actual value of the SMS template variable
     * @return Send the receipt
     */
    public String send(String phones, Map<String, ?> param) {
        return this.send(phones, this.smsMeta.getTemplateCode(), this.smsMeta.getSignName(), param);
    }

    /**
     * Send SMS
     *
     * @param phones       Mobile phone number, multiple with English commas separated, the maximum of 1000
     * @param templateCode SMS template code
     * @param param        The actual value of the SMS template variable
     * @return Send the receipt
     */
    public String send(String phones, String templateCode, Map<String, ?> param) {
        return this.send(phones, templateCode, this.smsMeta.getSignName(), param);
    }

    /**
     * Send SMS
     *
     * @param phones       Mobile phone number, multiple with English commas separated, the maximum of 1000
     * @param templateCode SMS template code
     * @param signName     SMS sign name
     * @param param        The actual value of the SMS template variable
     * @return Send the receipt
     */
    public String send(String phones, String templateCode, String signName, Map<String, ?> param) {
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(DO_MAIN);
        request.setVersion(VERSION);
        request.setAction("sendSms");
        request.putQueryParameter("PhoneNumbers", phones);
        if (param != null) {
            request.putQueryParameter("TemplateParam", new Gson().toJson(param));
        }
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("SignName", signName);
        try {
            CommonResponse response = this.client.getCommonResponse(request);
            return response.getData();
        } catch (ClientException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }

    /**
     * Query the record of sending the specified mobile phone number
     *
     * @param phone    Mobile phone number
     * @param sendDate Send date，Format for yyyy-MM-dd
     * @param page     Query page
     * @param row      No more than 50 pages per page
     * @return Query results
     */
    public String findSendDetail(String phone, String sendDate, long page, long row) {
        if (row > 50) {
            throw new IllegalArgumentException("每页条数不能大于50");
        }
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(DO_MAIN);
        request.setVersion(VERSION);
        request.setAction("QuerySendDetails");
        request.putQueryParameter("PhoneNumber", phone);
        request.putQueryParameter("SendDate", sendDate.replaceAll("-", ""));
        request.putQueryParameter("PageSize", String.valueOf(row));
        request.putQueryParameter("CurrentPage", String.valueOf(page));
        try {
            CommonResponse commonResponse = this.client.getCommonResponse(request);
            return commonResponse.getData();
        } catch (ClientException e) {
            throw new ToolsAliyunException(e.getMessage());
        }
    }
}
