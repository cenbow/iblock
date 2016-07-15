package com.iblock.service.message;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.iblock.service.utils.RedisUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by baidu on 16/7/15.
 */
@Component
@Log4j
public class SMSService {

    @Value("${sms.server.ip}")
    private String serverIP;
    @Value("${sms.server.port}")
    private String serverPort;
    @Value("${sms.account.sid}")
    private String accountSID;
    @Value("${sms.account.token}")
    private String accountToken;
    @Value("${sms.app.id}")
    private String appID;
    @Value("${sms.validate.time}")
    private Long time;
    @Autowired
    private RedisUtils redisUtils;

    public boolean sendVerifyCode(String mobile) {
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init(serverIP, serverPort);
        restAPI.setAccount(accountSID, accountToken);
        restAPI.setAppId(appID);
        String code = getRandomString(6);
        redisUtils.put("verify_" + mobile, code, time);
        HashMap<String, Object> result = restAPI.sendTemplateSMS(mobile, "1", new String[]{code, String.valueOf
                (time / 60)});
        if (!"000000".equals(result.get("statusCode"))) {
            log.error("verify code send error, 错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
        }
        return "000000".equals(result.get("statusCode"));
    }

    public boolean checkVerifyCode(String mobile, String code) {
        return code.equals(redisUtils.fetch("verify_" + mobile));
    }

    public String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
