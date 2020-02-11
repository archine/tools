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
 * @author Gjing
 **/
public class SmsHelper {
    private SmsMeta smsMeta;
    private IAcsClient client;
    private final String DO_MAIN = "dysmsapi.aliyuncs.com";
    private final String VERSION = "2017-05-25";

    public SmsHelper(AliyunMeta aliyunMeta, SmsMeta smsMeta) {
        this.smsMeta = smsMeta;
        DefaultProfile profile = DefaultProfile.getProfile(this.smsMeta.getRegion(), aliyunMeta.getAccessKey(), aliyunMeta.getAccessKeySecret());
        this.client = new DefaultAcsClient(profile);
    }

    public String send(String phones) {
        return this.send(phones, this.smsMeta.getTemplateCode(), this.smsMeta.getSignName(), null);
    }

    public String send(String phones, String templateCode, String signName) {
        return this.send(phones, templateCode, signName, null);
    }

    public String send(String phones, Map<String, ?> param) {
        return this.send(phones, this.smsMeta.getTemplateCode(), this.smsMeta.getSignName(), param);
    }

    /**
     * 发送短信
     *
     * @param phones       手机号,多个用英文逗号隔开,上限为1000个
     * @param templateCode 短信模板code
     * @param signName     短信签名名称
     * @param param        短信模板变量对应的实际值
     * @return 发送回执
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
     * 查询指定手机号的发送记录
     * @param phone 11位手机号
     * @param sendDate 发送日期，格式为：yyyy-MM-dd
     * @param page 页数
     * @param row 条数，不能大于50
     * @return 查询结果
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
