package com.iblock.web.request.project;

import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.service.info.KVInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by baidu on 16/6/10.
 */
@Data
public class ProjectSearchRequest {

    private String keyword;
    private Integer minPay;
    private Integer maxPay;
    private List<KVInfo> skill;
    private List<KVInfo> city;
    private List<KVInfo> industry;
    private Integer pageNo;
    private Integer pageSize;

    public ProjectSearchBean toBean() {
        ProjectSearchBean bean = new ProjectSearchBean();
        bean.setOffset((pageNo - 1) * pageSize);
        bean.setPageSize(pageSize);
        bean.setStatus(Arrays.asList(ProjectStatus.RECRUITING.getCode()));
        if (skill != null) {
            List<Integer> list = new ArrayList<Integer>();
            for (KVInfo kv : skill) {
                list.add(kv.getId());
            }
            bean.setSkill(list);
        }

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
        bean.setFreeze(false);
        return bean;
    }


}
