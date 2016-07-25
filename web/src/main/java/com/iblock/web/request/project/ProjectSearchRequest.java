package com.iblock.web.request.project;

import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.service.info.KVInfo;
import com.iblock.service.search.ProjectCondition;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

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

    public ProjectCondition toBean() {
        ProjectCondition bean = new ProjectCondition();
        bean.setOffset((pageNo - 1) * pageSize);
        bean.setPageSize(pageSize);
        bean.setStatus(Arrays.asList(ProjectStatus.RECRUITING.getCode()));
        if (CollectionUtils.isNotEmpty(skill)) {
            List<Integer> list = new ArrayList<Integer>();
            for (KVInfo kv : skill) {
                list.add(kv.getId());
            }
            bean.setSkill(list);
        }

        if (CollectionUtils.isNotEmpty(city)) {
            List<Integer> list = new ArrayList<Integer>();
            for (KVInfo kv : city) {
                list.add(kv.getId());
            }
            bean.setCity(list);
        }

        if (CollectionUtils.isNotEmpty(industry)) {
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
