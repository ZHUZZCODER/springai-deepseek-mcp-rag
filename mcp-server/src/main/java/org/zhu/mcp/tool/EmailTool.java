package org.zhu.mcp.tool;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class EmailTool {

    private final JavaMailSender mailSender;
    private final String from;

    @Autowired
    public EmailTool(JavaMailSender mailSender,@Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }


    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest {
        @ToolParam(description = "收件人的邮箱")
        private String email;
        @ToolParam(description = "发送邮件的标题/主题")
        private String subject;
        @ToolParam(description = "发送邮件的消息/正文内容")
        private String message;
        @ToolParam(description = "邮件的内容是否为html还是markdown格式，如果是markdown格式，则为1；如果是html格式，则为2;")
        private Integer contentType;
    }

    @Tool(description = "查询我的邮件/邮箱地址")
    public String getMyEmailAddress(){
        log.info("============== 调用mcp工具: getMyEmailAddress ==============");
        return "zhuzzhello@163.com";

    }

    @Tool(description = "给指定邮箱发送邮件信息，email为收件人邮箱，subject为邮件标题，message为邮件内容")
    public void sendMailMessage(EmailRequest emailRequest){
        log.info("============== 调用mcp工具: sendMailMessage ==============");
        log.info(String.format("============== 参数 emailRequest %s ==============",emailRequest.toString()));

        try {
            MimeMessage mineMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mineMessage);

            messageHelper.setFrom(from);
            messageHelper.setTo(emailRequest.getEmail());
            messageHelper.setSubject(emailRequest.getSubject());
            // messageHelper.setText(emailRequest.getMessage());

            // messageHelper.setText(convertToHtml(emailRequest.getMessage()),true);

            // 判断是html格式还是markdown格式
            if (emailRequest.getContentType() == 1){
                messageHelper.setText(convertToHtml(emailRequest.getMessage()),true);
            }else if(emailRequest.getContentType() == 2) {
                messageHelper.setText(emailRequest.getMessage(),true);
            }else {
                messageHelper.setText(emailRequest.getMessage());
            }

            mailSender.send(mineMessage);
        } catch (MessagingException e){
            log.error("============ 邮件发送失败，报错信息：{} ============",e.getMessage());
        }

    }

    // markdown转换成html
    public static String convertToHtml(String markdownStr){
        // 请使用flexmark-all帮我写一个markdown转换成html的代码
        MutableDataSet options = new MutableDataSet();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Parser parser = Parser.builder(options).build();
        return renderer.render(parser.parse(markdownStr));

    }
}
