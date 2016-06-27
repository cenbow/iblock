package com.iblock.web.request.admin;

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
    private String userid;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private Integer role;

    public User toUser() {
        User user = new User();
        user.setRole(role.byteValue());
        user.setMobile(userid);
        user.setUserName(username);
        user.setPassword(password);
        return user;
    }
}
