package cn.gjing.tools.common.util;

import cn.gjing.tools.common.annotation.Exclude;
import cn.gjing.tools.common.annotation.NotNull;
import cn.gjing.tools.common.exception.HttpException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * @author Gjing
 **/
public class EmailUtils {

    private String host;
    private String password;
    private String from;

    private EmailUtils(String host, String password, String from) {
        if (ParamUtils.multiEmpty(host, password, from)) {
            throw new NullPointerException("Instantiation exception, Parameters cannot be null");
        }
        this.host = host;
        this.password = password;
        this.from = from;
    }

    /**
     * 获取实例
     *
     * @param host     smtp服务器地址
     * @param password 发送者的授权码
     * @param from     发送者邮箱地址
     * @return e
     */
    public static EmailUtils of(String host, String password, String from) {
        return new EmailUtils(host, password, from);
    }

    /**
     * 发送普通email,支持html格式
     *
     * @param subject 主题
     * @param body    文本内容
     * @param tos     接收方邮件地址,多个逗号隔开
     * @param copyTo  抄送人,可以多个,逗号隔开,不需要抄送,传null或者""
     * @return true为发送成功
     */
    @NotNull
    public boolean sendEmail(@Exclude String subject, @Exclude String body, String tos, @Exclude String copyTo) {
        try {
            Properties props = getProperties();
            Session session = Session.getInstance(props);
            Message msg = setSubjectWithFrom(subject, session);
            setRecipient(tos, copyTo, msg);
            MimeMultipart mm = getMimeMultipart(body);
            msg.setContent(mm);
            msg.saveChanges();
            startSend(session, msg);
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
        return true;
    }

    /**
     * 发送带附件的email,文本支持html格式
     *
     * @param subject 主题
     * @param body    文本内容
     * @param files   附件地址
     * @param tos     接收方邮件地址,多个逗号隔开
     * @param copyTo  抄送人,可以多个,逗号隔开,不需要抄送,传null或者""
     * @return true为发送成功
     */
    @NotNull
    public boolean sendEmail(@Exclude String subject, @Exclude String body, @Exclude String[] files, String tos, @Exclude String copyTo) {
        try {
            Properties props = getProperties();
            Session session = Session.getInstance(props);
            Message msg = setSubjectWithFrom(subject, session);
            setRecipient(tos, copyTo, msg);
            // 设置邮件主体内容(包括html文本和附件)
            MimeMultipart mm = getMimeMultipart(body);
            // 设置多个附件
            if (ParamUtils.isNotEmpty(files)) {
                for (String f : files) {
                    // 设置附件部分
                    MimeBodyPart attachment = new MimeBodyPart();
                    // 读取文件
                    DataHandler dh = new DataHandler(new FileDataSource(f));
                    // 将文件关联到附件上
                    attachment.setDataHandler(dh);
                    // 设置附件的文件名（需要编码避免中文乱码）
                    attachment.setFileName(MimeUtility.encodeText(dh.getName()));
                    mm.addBodyPart(attachment);
                }
            }
            msg.setContent(mm);
            msg.saveChanges();
            startSend(session, msg);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
        return true;
    }

    /**
     * 设置主题和发送人
     * @param subject 主题
     * @param session 会话
     * @return message
     * @throws MessagingException email异常
     */
    private Message setSubjectWithFrom(String subject, Session session) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setSubject(subject);
        msg.setFrom(new InternetAddress(from));
        return msg;
    }

    /**
     * 设置收件人和抄送人
     * @param tos 收件人
     * @param copyTo 抄送人
     * @param msg 消息
     * @throws MessagingException email异常
     */
    private void setRecipient(String tos, String copyTo, Message msg) throws MessagingException {
        if (ParamUtils.split(tos, ",").length == 1) {
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(tos));
        } else {
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(tos));
        }
        if (ParamUtils.isNotEmpty(copyTo)) {
            InternetAddress[] copyToArr = InternetAddress.parse(copyTo);
            msg.setRecipients(Message.RecipientType.CC, copyToArr);
        }
    }


    /**
     * 获取配置信息
     * @return Properties
     */
    private Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", host);
        return props;
    }
    /**
     * 开始发送
     * @param session 邮件会话
     * @param msg 消息
     * @throws MessagingException email异常
     */
    private void startSend(Session session, Message msg) throws MessagingException {
        Transport transport = session.getTransport();
        transport.connect(from, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    /**
     * 设置邮件主体内容(包括html文本和附件)
     * @param body 主体
     * @return MimeMultipart
     * @throws MessagingException email异常
     */
    private MimeMultipart getMimeMultipart(String body) throws MessagingException {
        MimeMultipart mm = new MimeMultipart();
        MimeBodyPart html = new MimeBodyPart();
        html.setContent(body, "text/html;charset=UTF-8");
        mm.addBodyPart(html);
        return mm;
    }

}
