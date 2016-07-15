package com.iblock.web.request.user;

import lombok.Data;

/**
 * Created by baidu on 16/7/15.
 */
@Data
public class ResetMobileRequest {

    private String mobile;
    private String verifyCode;
    private String password;
}
