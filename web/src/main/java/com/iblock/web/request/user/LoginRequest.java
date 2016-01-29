package com.iblock.web.request.user;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created by baidu on 16/1/27.
 */
@Data
public class LoginRequest {

    @NotBlank
    private String userName;
    @NotBlank
    private String password;
    @Min(1)
    private int role;

    public boolean validate() {
        return StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password) && role > 0;
    }
}
