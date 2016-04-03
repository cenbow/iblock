package com.iblock.web.info;

import com.iblock.dao.po.User;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by qihong on 16/1/27.
 */
@Data
public class UserInfo implements Serializable {

    private Long userId;
    private String userName;
    private int role;

    public UserInfo() {
    }

    public UserInfo (User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.role = user.getRole();
    }
}
