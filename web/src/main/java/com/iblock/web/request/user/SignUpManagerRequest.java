package com.iblock.web.request.user;

import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.common.utils.MD5Utils;
import com.iblock.dao.po.Manager;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserGeo;
import com.iblock.service.bo.UserUpdateBo;
import com.iblock.web.info.GeoInfo;
import lombok.Data;

import java.util.Date;

/**
 * Created by baidu on 16/7/15.
 */
@Data
public class SignUpManagerRequest {

    private String username;
    private String password;
    private String mobile;
    private String verifyCode;
    private String corporateName;
    private String corporateBio;
    private Integer gender;
    private GeoInfo geo;

    public UserUpdateBo toUserBo() {
        UserUpdateBo bo = new UserUpdateBo();

        User user = new User();
        user.setUserName(username);
        user.setPassword(MD5Utils.encrypt(password));
        user.setMobile(mobile);
        if (gender != null) {
            user.setSex(gender.equals(1));
        }
        user.setStatus((byte) CommonStatus.NORMAL.getCode());
        user.setAddTime(new Date());
        user.setRole((byte) UserRole.MANAGER.getRole());
        bo.setUser(user);

        UserGeo userGeo = new UserGeo();
        userGeo.setAddress(geo.getAddress());
        userGeo.setCityId(geo.getCity().getId());
        userGeo.setDistrict(geo.getDistrict());
        userGeo.setLatitude(geo.getLat());
        userGeo.setLongitude(geo.getLng());
        userGeo.setCity(geo.getCity().getName());
        userGeo.setAddTime(new Date());
        userGeo.setStatus((byte) CommonStatus.NORMAL.getCode());
        bo.setUserGeo(userGeo);

        Manager manager = new Manager();
        manager.setCompanyName(corporateName);
        manager.setDesc(corporateBio);
        manager.setStatus((byte) CommonStatus.NORMAL.getCode());
        manager.setAddTime(new Date());
        bo.setManager(manager);
        return bo;
    }
}
