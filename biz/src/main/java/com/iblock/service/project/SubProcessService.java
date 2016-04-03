package com.iblock.service.project;

import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.WorkflowType;
import com.iblock.dao.SubProcessDao;
import com.iblock.dao.WorkflowLogDao;
import com.iblock.dao.po.SubProcess;
import com.iblock.dao.po.WorkflowLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/2/1.
 */
@Component
public class SubProcessService {

    @Autowired
    private SubProcessDao subProcessDao;
    @Autowired
    private WorkflowLogDao workflowLogDao;

    public boolean create(String name, Long processId) {
        SubProcess process = new SubProcess();
        process.setAddTime(new Date());
        process.setProjectId(processId);
        process.setName(name);
        process.setStatus((byte) CommonStatus.NORMAL.getCode());
        return subProcessDao.insertSelective(process) > 0;
    }

    public boolean update(String name, Long id, Long projectId) {
        SubProcess process = subProcessDao.selectByPrimaryKey(id);
        if (process == null || !process.getProjectId().equals(projectId)) {
            return false;
        }
        process.setName(name);
        return subProcessDao.updateByPrimaryKeySelective(process) > 0;
    }

    public boolean delete(Long id, Long projectId) {
        SubProcess process = subProcessDao.selectByPrimaryKey(id);
        if (process == null || !process.getProjectId().equals(projectId)) {
            return false;
        }
        process.setStatus((byte) CommonStatus.DELETE.getCode());
        return subProcessDao.updateByPrimaryKeySelective(process) > 0;
    }

    public List<SubProcess> findByProcessId(Long processId) {
        return subProcessDao.selectByProcess(processId);
    }

    public List<SubProcess> findByInstId(String instId) {
        WorkflowLog log = workflowLogDao.selectByInstId(instId, WorkflowType.SUB_PROCESS.getCode());
        if (log == null) {
            return null;
        }
        return subProcessDao.selectByProcess(log.getOutBizId());
    }
}
