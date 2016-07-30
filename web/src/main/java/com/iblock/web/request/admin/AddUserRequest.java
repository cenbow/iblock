package com.iblock.web.request.admin;

import com.iblock.common.utils.MD5Utils;
import com.iblock.dao.po.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by baidu on 16/6/26.
 */
@Data
public class AddUserRequest {

    @NotBlank
    private String mobile;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private Integer role;

    public User toUser() {
        User user = new User();
        user.setRole(role.byteValue());
        user.setMobile(mobile);
        user.setUserName(username);
        user.setPassword(MD5Utils.encrypt(password));
        return user;
    }
}
