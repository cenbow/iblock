package com.iblock.web.request.security;

import lombok.Data;

/**
 * Created by baidu on 16/7/19.
 */
@Data
public class ResetPasswordRequest {

    private String phone;
    private String token;
    private String password;
}
