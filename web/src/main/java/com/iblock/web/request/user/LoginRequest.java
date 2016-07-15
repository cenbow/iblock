package com.iblock.web.request.user;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created by qihong on 16/1/27.
 */
@Data
public class LoginRequest {

    @NotBlank
    private String mobile;
    @NotBlank
    private String password;

}
