package com.iblock.service.project;

import com.iblock.common.bean.Page;
import com.iblock.common.bean.ProjectSearchBean;
import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.HireStatus;
import com.iblock.common.enums.MessageAction;
import com.iblock.common.enums.ProjectStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.common.exception.InnerLogicException;
import com.iblock.common.exception.InvalidRequestException;
import com.iblock.dao.ProjectDao;
import com.iblock.dao.ProjectDesignerDao;
import com.iblock.dao.ProjectSkillDao;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.ProjectDesigner;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.dao.po.ProjectSkillDetail;
import com.iblock.dao.po.User;
import com.iblock.service.bo.ProjectAcceptBo;
import com.iblock.service.message.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baidu on 16/2/1.
 */
@Component
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectDesignerDao projectDesignerDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ProjectSkillDao projectSkillDao;

    public List<ProjectSkillDetail> getSkills(Long projectId) {
        return projectSkillDao.selectByProject(projectId);
    }


    public List<User> getDesigners(Long project) {
        List<ProjectDesigner> designers = projectDesignerDao.selectByProject(project);
        List<User> list = new ArrayList<User>();
        if (CollectionUtils.isEmpty(designers)) {
            return list;
        }
        List<Long> ids = new ArrayList<Long>();
        for (ProjectDesigner designer : designers) {
            ids.add(designer.getDesignerId());
        }
        return userDao.batchSelect(ids);
    }
    @Transactional
    public long save(Project p, List<ProjectSkill> skills, Long managerId) throws InvalidRequestException {
        if (p.getId() == null) {
            p.setAddTime(new Date());
            p.setManagerId(managerId);
            p.setStatus((byte) ProjectStatus.AUDIT.getCode());
            p.setFreeze(false);
            projectDao.insertSelective(p);
        } else {
            Project tmp = projectDao.selectByPrimaryKey(p.getId());
            if (!tmp.getManagerId().equals(managerId) || (tmp.getStatus().intValue() != ProjectStatus.AUDIT.getCode()
            && tmp.getStatus().intValue() != ProjectStatus.AUDIT_DENY.getCode())) {
                throw new InvalidRequestException();
            }
            projectDao.updateByPrimaryKeySelective(p);

        }
        projectSkillDao.disable(p.getId());
        if (CollectionUtils.isNotEmpty(skills)) {
            for (ProjectSkill skill : skills) {
                skill.setProjectId(p.getId());
                projectSkillDao.insertSelective(skill);
            }
        }
        return p.getId();
    }

    public boolean update(Project p) {
        return projectDao.updateByPrimaryKeySelective(p) > 0;
    }


    @Transactional
    public boolean terminate(long id, long managerId) throws InvalidRequestException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getManagerId().equals(managerId)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.TERMINATION.getCode());
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean start(long id, long agentId) throws InvalidRequestException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getAgentId().equals(agentId)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.ONGOING.getCode());
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean end(long id, long agentId) throws InvalidRequestException, InnerLogicException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getAgentId().equals(agentId)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.FINISH.getCode());
        List<ProjectDesigner> designers = projectDesignerDao.selectByProject(pro.getId());
        if (designers != null) {
            Map<String, String> managerMap = new HashMap<String, String>();
            Map<String, String> designerMap = new HashMap<String, String>();
            managerMap.put("id", String.valueOf(id));
            managerMap.put("userid", String.valueOf(pro.getManagerId()));
            managerMap.put("rating", "5");
            designerMap.put("id", String.valueOf(id));
            designerMap.put("rating", "5");
            for (ProjectDesigner designer : designers) {
                designerMap.put("userid", String.valueOf(designer.getDesignerId()));
                messageService.send(-1L, pro.getManagerId(), MessageAction.DESIGNER_RATING, null, null, designer
                        .getDesignerId(), pro, designerMap);
                messageService.send(-1L, designer.getDesignerId(), MessageAction.MANAGER_RATING, pro.getManagerId(), null,
                        null, pro, designerMap);
            }
        }
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean completeHire(long id, long agentId) throws InvalidRequestException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getAgentId().equals(agentId)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.READY.getCode());
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean hire(long id, long userId, long agentId) throws InvalidRequestException, InnerLogicException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getAgentId().equals(agentId)) {
            throw new InvalidRequestException("you have no auth, agent id is wrong");
        }
        if (pro.getStatus().intValue() != ProjectStatus.RECRUITING.getCode()) {
            throw new InvalidRequestException("project status is wrong, hire is not allowed");
        }
        User user = userDao.selectByPrimaryKey(userId);
        if (user.getStatus().intValue() != CommonStatus.NORMAL.getCode()) {
            throw new InvalidRequestException("designer status is invalid");
        }
        if (user.getRole().intValue() != UserRole.DESIGNER.getRole()) {
            throw new InvalidRequestException("designer role is invalid");
        }
        ProjectDesigner designer = new ProjectDesigner();
        designer.setStatus((byte) HireStatus.HIRING.getCode());
        designer.setAddTime(new Date());
        designer.setDesignerId(userId);
        designer.setProjectId(id);
        Map<String, String> params = new HashMap<String, String>();
        params.put("hireid", String.valueOf(pro.getId()));
        params.put("accept", "true");
        messageService.send(-1L, userId, MessageAction.HIRE, pro.getManagerId(), null, userId, pro, params);
        return projectDesignerDao.insertSelective(designer) > 0;
    }

    @Transactional
    public boolean acceptHiring(long id, long userId, boolean accept) throws InvalidRequestException,
            InnerLogicException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        ProjectDesigner designer = projectDesignerDao.selectByProjectAndDesigner(id, userId);
        if (designer == null) {
            throw new InvalidRequestException("wrong designer");
        }
        if (pro.getStatus().intValue() != ProjectStatus.RECRUITING.getCode()) {
            throw new InvalidRequestException("project status is wrong, hire is not allowed");
        }
        User user = userDao.selectByPrimaryKey(userId);
        if (user.getStatus().intValue() != CommonStatus.NORMAL.getCode()) {
            throw new InvalidRequestException("designer status is invalid");
        }
        if (user.getRole().intValue() != UserRole.DESIGNER.getRole()) {
            throw new InvalidRequestException("designer role is invalid");
        }
        designer.setStatus((byte) (accept ? HireStatus.ACCEPT.getCode(): HireStatus.DENY.getCode()));
        Map<String, String> params = new HashMap<String, String>();
        params.put("hireid", String.valueOf(pro.getId()));
        messageService.send(-1L, userId, accept ? MessageAction.ACCEPT_HIRE : MessageAction.DENY_HIRE, null, null,
                userId, pro, params);
        if (accept) {
            pro.setHired(pro.getHired() + 1);
            projectDao.updateByPrimaryKeySelective(pro);
        }
        return projectDesignerDao.updateByPrimaryKey(designer) > 0;
    }

    public Project get(Long id) {
        return projectDao.selectByPrimaryKey(id);
    }

    public Page<Project> search(ProjectSearchBean search) {
        if (CollectionUtils.isEmpty(search.getSkill())) {
            search.setSkill(null);
        }
        if (CollectionUtils.isEmpty(search.getCity())) {
            search.setCity(null);
        }
        if (CollectionUtils.isEmpty(search.getIndustry())) {
            search.setIndustry(null);
        }
        List<Project> list = projectDao.list(search);
        int size = projectDao.size(search);
        return new Page<Project>(list, search.getOffset() / search.getPageSize() + 1, search.getPageSize(), size, search
                .getOrder(), search.getOrderBy());
    }

    @Transactional
    public boolean accept(ProjectAcceptBo bo) throws InvalidRequestException, InnerLogicException, IOException {
        Project p = projectDao.selectByPrimaryKey(bo.getId());
        if (p == null || p.getStatus().intValue() != ProjectStatus.AUDIT.getCode()) {
            throw new InvalidRequestException();
        }
        p.setId(bo.getId());
        p.setStatus((byte) (bo.getAccept() ? ProjectStatus.RECRUITING.getCode() : ProjectStatus.AUDIT_DENY.getCode()));
        messageService.send(-1L, p.getManagerId(), bo.getAccept() ? MessageAction.AUDIT_SUCCESS : MessageAction
                .AUDIT_FAIL, null, p.getAgentId(), null, p, null);
        return projectDao.updateByPrimaryKeySelective(p) > 0;
    }

}
