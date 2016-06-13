package com.iblock.web.request.project;

import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.web.info.KVInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baidu on 16/6/10.
 */
@Data
public class ProjectSearchRequest {

    private String keyword;
    private Integer minPay;
    private Integer maxPay;
    private List<KVInfo> city;
    private List<KVInfo> industry;
    private Integer pageNo;
    private Integer pageSize;

    public ProjectSearchBean toBean() {
        ProjectSearchBean bean = new ProjectSearchBean();
        bean.setPageNo(pageNo);
        bean.setPageSize(pageSize);
        if (city != null) {
            List<Integer> list = new ArrayList<Integer>();
            for (KVInfo kv : city) {
                list.add(kv.getId());
            }
            bean.setCity(list);
        }

        if (industry != null) {
            List<Integer> list = new ArrayList<Integer>();
            for (KVInfo kv : industry) {
                list.add(kv.getId());
            }
            bean.setIndustry(list);
        }
        bean.setKeyword(keyword);
        bean.setMaxPay(maxPay);
        bean.setMinPay(minPay);
        return bean;
    }


}
