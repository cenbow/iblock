package com.iblock.web.request.security;

import lombok.Data;

/**
 * Created by baidu on 16/7/19.
 */
@Data
public class ValidateVerifyCodeRequest {

    private String phone;
    private String verifyCode;
}
