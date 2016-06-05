package com.iblock.web.request.user;

import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.po.Manager;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserGeo;
import com.iblock.service.bo.UserUpdateBo;
import com.iblock.web.info.GeoInfo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Created by baidu on 16/2/8.
 */
@Data
public class SignUpRequest {

    // todo geo str?
    // todo validate
    private String username;
    private String password;
    private String mobile;
    private String validationCode;
    private String corporateName;
    private String corporateBio;
    private String location;
    private int role;
    private Integer gender;
    private int province;
    private int city;
    private int district;
    private String street;

    public UserUpdateBo toUserBo() {
        UserUpdateBo bo = new UserUpdateBo();

        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setMobile(mobile);
        if (gender != null) {
            user.setSex(gender.equals(1));
        }
        user.setRole((byte) role);
        user.setStatus((byte) CommonStatus.NORMAL.getCode());
        user.setAddTime(new Date());
        bo.setUser(user);

        UserGeo userGeo = new UserGeo();
        userGeo.setAddress(street);
        userGeo.setDistrictId(district);
        userGeo.setCityId(city);
        userGeo.setProvinceId(province);
        userGeo.setAddTime(new Date());
        userGeo.setStatus((byte) CommonStatus.NORMAL.getCode());
        bo.setUserGeo(userGeo);

        if (role == UserRole.MANAGER.getRole()) {
            Manager manager = new Manager();
            manager.setCompanyName(corporateName);
            manager.setDesc(corporateBio);
            manager.setStatus((byte) CommonStatus.NORMAL.getCode());
            manager.setAddTime(new Date());
            bo.setManager(manager);
        }
        return bo;
    }

}
