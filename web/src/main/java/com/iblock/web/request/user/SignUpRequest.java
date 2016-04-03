package com.iblock.web.request.user;

import com.iblock.dao.po.Manager;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserGeo;
import com.iblock.service.bo.UserUpdateBo;
import com.iblock.web.info.GeoInfo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * Created by baidu on 16/2/8.
 */
@Data
public class SignUpRequest {

    // todo geo id?
    private String username;
    private String passwd;
    private String contactPhone;
    private String verifyCode;
    private String corporateName;
    private String corporateBio;
    private GeoInfo geo;

    public UserUpdateBo toUserBo() {
        UserUpdateBo bo = new UserUpdateBo();

        User user = new User();
        user.setUserName(username);
        user.setPassword(passwd);
        user.setMobile(contactPhone);
        bo.setUser(user);

        if (geo != null) {
            UserGeo userGeo = new UserGeo();
            userGeo.setAddress(geo.getAddress());
            userGeo.setDistrict(geo.getDistrict());
            userGeo.setCity(geo.getCity());
            userGeo.setProvince(geo.getProvince());
            bo.setUserGeo(userGeo);
        }

        if (StringUtils.isNotBlank(corporateBio) || StringUtils.isNotBlank(corporateBio)) {
            Manager manager = new Manager();
            manager.setCompanyName(corporateName);
            manager.setDesc(corporateBio);
            bo.setManager(manager);
        }
        return bo;
    }

}
