package com.iblock.service.bo;

import com.iblock.dao.po.Designer;
import com.iblock.dao.po.Manager;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserGeo;
import lombok.Data;

/**
 * Created by baidu on 16/2/10.
 */

@Data
public class UserUpdateBo {

    private User user;
    private Manager manager;
    private Designer designer;
    private UserGeo userGeo;
}
