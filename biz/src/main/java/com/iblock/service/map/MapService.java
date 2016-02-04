package com.iblock.service.map;

import com.iblock.common.utils.HttpUtils;
import com.iblock.dao.DistrictDao;
import com.iblock.dao.po.District;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baidu on 16/2/3.
 */
@Component
public class MapService {

    private final String AK = "30lR9vuFpoSkKFVAL9QImw9T";
    private final String SK = "NsqGKXQ5yxXytLqIgOtCPznFYVD4gjL5";

    @Autowired
    private DistrictDao districtDao;

    public List<District> getDistrict(int cityId) {
        return districtDao.selectByCity(cityId);
    }

    public String suggest(String address, int city) throws UnsupportedEncodingException {
        String url = "http://api.map.baidu.com/place/v2/suggestion?";
        Map paramsMap = new LinkedHashMap<String, String>();
        paramsMap.put("query", address);
        paramsMap.put("region", String.valueOf(city));
        paramsMap.put("output", "json");
        paramsMap.put("ak", AK);

        String paramsStr = this.toQueryString(paramsMap);
        String wholeStr = new String("/place/v2/suggestion?" + paramsStr + SK);
        String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
        paramsMap.put("sn", this.MD5(tempStr));
        return HttpUtils.get(url + this.toQueryString(paramsMap));
    }

    public String toQueryString(Map<?, ?> data) throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        MapService service = new MapService();
        System.out.print(service.suggest("绿洲康城亲水湾", 289));
    }
}
