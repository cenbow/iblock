package com.iblock.service.project;

import com.iblock.common.bean.Page;
import com.iblock.common.bean.PageModel;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.common.enums.WorkflowType;
import com.iblock.common.exception.InvalidRequestException;
import com.iblock.dao.ProjectDao;
import com.iblock.dao.WorkflowLogDao;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.WorkflowLog;
import com.iblock.service.bo.ProjectBo;
import com.iblock.service.bo.ProjectSearchBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by baidu on 16/2/1.
 */
@Component
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private WorkflowLogDao workflowLogDao;

    public long save(ProjectBo bo, Long managerId) {
        Project p;
        if (bo.getId() == null) {
            p = bo.toProject();
            p.setAddTime(new Date());
            p.setManagerId(managerId);
            p.setStatus((byte) ProjectStatus.DRAFT.getCode());
            projectDao.insertSelective(p);
        } else {
            p = projectDao.selectByPrimaryKey(bo.getId());
            if (!p.getManagerId().equals(managerId)) {
                return -1L;
            }
            bo.updateProject(p);
            projectDao.updateByPrimaryKeySelective(p);
        }
        return p.getId();
    }

    @Transactional
    public boolean publish(long id, String processId, long managerId) throws InvalidRequestException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (pro == null || !pro.getManagerId().equals(managerId)) {
            throw new InvalidRequestException();
        }
        WorkflowLog log = new WorkflowLog();
        log.setOutBizId(id);
        log.setWorkflowType((byte) WorkflowType.PROJECT.getCode());
        log.setWorkflowId(processId);
        log.setStatus((byte) CommonStatus.NORMAL.getCode());
        log.setAddTime(new Date());
        return workflowLogDao.insertSelective(log) > 0;
    }

    public Page<Project> search(ProjectSearchBean search) {
        List<Project> list = projectDao.list(search);
        int size = projectDao.size(search);
        return new Page<Project>(list, search.getPageNo(), search.getPageSize(), size, search.getOrder(), search
                .getOrderBy());
    }

    public String getWorkflowId(Long id) {
        return workflowLogDao.selectByProjectId(id).getWorkflowId();
    }

}
