package com.example.common.message;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AliService {

    @Autowired
    @Qualifier("AliClient") // 明确指定要注入的 Bean 名称
    private Client aliClient;

    @Value("${aliyun.templateCode}")
    private String templateCode;

    @Value("${aliyun.signature}")
    private String signature;

    public boolean sendPhoneCode(String telephone, String code) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        return sendPhoneMessage(telephone, signature, templateCode, params);
    }

    private boolean sendPhoneMessage(String telephone, String signature, String templateCode, Map<String, String> params) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(telephone);
        sendSmsRequest.setSignName(signature);
        sendSmsRequest.setTemplateCode(templateCode);
        sendSmsRequest.setTemplateParam(JSON.toJSONString(params));
        try {
            SendSmsResponse sendSmsResponse = aliClient.sendSms(sendSmsRequest);
            SendSmsResponseBody sendSmsResponseBody = sendSmsResponse.getBody();
            if (!"OK".equals(sendSmsResponseBody.getCode())) {
                log.error("短信{}发送失败,原因{}", JSON.toJSONString(sendSmsRequest), sendSmsResponseBody.getMessage());
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("短信{}发送失败,原因{}", JSON.toJSONString(sendSmsRequest), e.getMessage());
            return false;
        }
    }
}
