package com.iblock.service.user;

import com.iblock.common.enums.TaskDefinitionKey;
import com.iblock.service.bo.TaskPredicateBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baidu on 16/2/1.
 */
@Component
public class TaskPermissionService {

    @Autowired
    private UserService userService;

    public List<Long> getCandidateUserList(TaskPredicateBo bo) {
        List<Long> list = new ArrayList<Long>();
        String key = bo.getTaskDefinitionKey();
        if (TaskDefinitionKey.MANAGER_AUDIT.is(key)) {

        }
        return list;
    }

}
