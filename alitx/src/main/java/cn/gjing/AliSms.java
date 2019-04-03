package cn.gjing;

import cn.gjing.annotation.ExcludeParam;
import cn.gjing.annotation.NotNull2;
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
 * ali sms service
 **/
public class AliSms {
    private static Gson gson = new Gson();

    /**
     * 实例
     */
    private static IAcsClient instance = null;

    /**
     * 实例化
     *
     * @return 实例
     */
    private static IAcsClient getInstance(DefaultProfile profile) {
        if (instance == null) {
            synchronized (AliOss.class) {
                if (instance == null) {
                    instance = new DefaultAcsClient(profile);
                }
            }
        }
        return instance;
    }

    /**
     * 短信发送接口，支持在一次请求中向多个不同的手机号码发送同样内容的短信。最多可以向1000个手机号码发送同样内容的短信。
     * @param smsModel 阿里短信模板
     * @param phoneNumbers      接收短信的手机号码(仅支持大陆),支持对多个手机号码发送短信，手机号码之间以英文逗号分隔
     * @param smsTemplateCode  短信模板CODE，请在控制台模板管理页面模板CODE一列查看。
     * @param smsTemplateParam 短信模板变量对应的实际值,map格式,可空（传null或者空map）
     * @param smsSignName      短信签名名称
     * @return 发送结果, 返回内容中含有ok表示发送成功 BizId:回执id；code，状态码；message：状态吗的描述；RequestId：请求id
     * @see <a href="https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.14.633f56e06vZoyq"></a>
     */
    @NotNull2
    public static String send(AliSmsModel smsModel, String phoneNumbers, String smsTemplateCode,@ExcludeParam Map<String, String> smsTemplateParam,
                                           String smsSignName) {
        DefaultProfile profile = DefaultProfile.getProfile("default", smsModel.getAccessKeyId(), smsModel.getAccessKeySecret());
        IAcsClient client = getInstance(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(Sms.SMS.getApi());
        request.setAction(Sms.SMS.getAction());
        request.setVersion(Sms.SMS.getVersion());
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("TemplateCode", smsTemplateCode);
        if (ParamUtil.paramIsNotEmpty(smsTemplateParam)) {
            request.putQueryParameter("TemplateParam", gson.toJson(smsTemplateParam));
        }
        request.putQueryParameter("SignName", smsSignName);
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getData();
        } catch (ClientException e) {
            throw new SmsException(e.getMessage());
        }
    }

    /**
     * 查询短信发送记录
     * @param smsModel 阿里短信模板
     * @param phoneNumber  接受短信的手机号
     * @param sendData     发送日期，yyyyMMdd格式（20181207） 支持查询 最近30天的记录
     * @param pageSize     分页查看发送记录，指定每页显示的短信数量，取值范围1-50
     * @param currentPage  指定发送记录要查看的页码
     * @return 响应结果，返回OK代表请求成功,其他响应信息请查看下面链接
     * @see <a href="https://help.aliyun.com/document_detail/101346.html?spm=a2c4g.11186623.2.13.450fbc454bQfCJ"></a>
     */
    @NotNull2
    public static synchronized String querySendDetails(AliSmsModel smsModel, String phoneNumber, String sendData, String pageSize, String currentPage) {
        if (!ParamUtil.isMobileNumber(phoneNumber)) {
            throw new SmsException("Specified parameter phoneNumber is not valid");
        }
        DefaultProfile profile = DefaultProfile.getProfile("default", smsModel.getAccessKeyId(),smsModel.getAccessKeySecret());
        IAcsClient client = getInstance(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(Sms.QUERY.getApi());
        request.setVersion(Sms.QUERY.getVersion());
        request.setAction(Sms.QUERY.getAction());
        request.putQueryParameter("PhoneNumber", phoneNumber);
        request.putQueryParameter("SendDate", ParamUtil.removeSymbol2(sendData, "-"));
        request.putQueryParameter("PageSize", pageSize);
        request.putQueryParameter("CurrentPage", currentPage);
        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getData();
        } catch (ClientException e) {
            throw new SmsException(e.getMessage());
        }
    }
}
