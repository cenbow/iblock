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
import com.iblock.dao.ProjectRatingDao;
import com.iblock.dao.ProjectSkillDao;
import com.iblock.dao.UserDao;
import com.iblock.dao.po.Project;
import com.iblock.dao.po.ProjectDesigner;
import com.iblock.dao.po.ProjectRating;
import com.iblock.dao.po.ProjectSkill;
import com.iblock.dao.po.ProjectSkillDetail;
import com.iblock.dao.po.User;
import com.iblock.service.bo.ProjectAcceptBo;
import com.iblock.service.info.ProjectSimpleInfo;
import com.iblock.service.message.MessageService;
import com.iblock.service.search.ProjectCondition;
import com.iblock.service.search.ProjectSearch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.queryparser.classic.ParseException;
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
    @Autowired
    private ProjectRatingDao projectRatingDao;
    @Autowired
    private ProjectSearch projectSearch;

    @Transactional
    public boolean rate(Long projectId, Integer rating, Long userId, Long msgId) {
        Project p = projectDao.selectByPrimaryKey(projectId);
        if (p.getStatus().intValue() != ProjectStatus.FINISH.getCode()) {
            return false;
        }
        if (CollectionUtils.isNotEmpty(projectRatingDao.selectByProjectAndUser(projectId, userId))) {
            return false;
        }
        ProjectRating projectRating = new ProjectRating();
        projectRating.setOperator(userId);
        projectRating.setProjectId(projectId);
        projectRating.setStatus((byte) CommonStatus.NORMAL.getCode());
        projectRating.setRating(rating);
        projectRating.setAddTime(new Date());
        messageService.finish(msgId, userId);
        return projectRatingDao.insertSelective(projectRating) > 0;
    }

    public double getRating(Long projectId) {
        Double d = projectRatingDao.selectAVG(projectId);
        if (d == null) {
            return 5;
        }
        return d;
    }

    public List<ProjectSkillDetail> getSkills(Long projectId) {
        return projectSkillDao.selectByProject(projectId);
    }

    public List<ProjectSkill> getSkills(List<Long> projects) {
        return projectSkillDao.selectByProjects(projects);
    }

    public List<ProjectDesigner> getProjectDesigner(Long userId) {
        return projectDesignerDao.selectByDesigner(userId);
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
    public long save(Project p, List<ProjectSkill> skills, Long managerId) throws InvalidRequestException, IOException {
        boolean isNew = p.getId() == null;
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
        if (isNew) {
            projectSearch.add(projectDao.selectByPrimaryKey(p.getId()));
        } else {
            projectSearch.update(projectDao.selectByPrimaryKey(p.getId()));
        }
        return p.getId();
    }

    public boolean update(Project p) throws IOException {
        boolean result = projectDao.updateByPrimaryKeySelective(p) > 0;
        projectSearch.update(p);
        return result;
    }


    @Transactional
    public boolean terminate(long id, long userId, int role) throws InvalidRequestException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (role == UserRole.MANAGER.getRole() || role == UserRole.AGENT.getRole()) {
            if (pro.getManagerId().intValue() != userId && pro.getAgentId().intValue() != userId) {
                throw new InvalidRequestException("用户无权限");
            }
        }
        if (role == UserRole.DESIGNER.getRole()) {
            throw new InvalidRequestException("用户无权限");
        }
        pro.setStatus((byte) ProjectStatus.TERMINATION.getCode());
        projectSearch.update(pro);
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean start(long id, long managerId) throws InvalidRequestException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getManagerId().equals(managerId)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.ONGOING.getCode());
        projectSearch.update(pro);
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean end(long id, long managerId) throws InvalidRequestException, InnerLogicException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getManagerId().equals(managerId)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.FINISH.getCode());
        List<ProjectDesigner> designers = projectDesignerDao.selectByProject(pro.getId());
        if (designers != null) {
            Map<String, String> managerMap = new HashMap<String, String>();
            Map<String, String> designerMap = new HashMap<String, String>();
            Map<String, String> proMap = new HashMap<String, String>();
            proMap.put("id", String.valueOf(pro.getId()));
            proMap.put("rating", "5");
            managerMap.put("id", String.valueOf(pro.getManagerId()));
            managerMap.put("rating", "5");
            designerMap.put("rating", "5");
            for (ProjectDesigner designer : designers) {
                designerMap.put("id", String.valueOf(designer.getDesignerId()));
                messageService.send(-1L, pro.getManagerId(), MessageAction.DESIGNER_RATING, null, null, designer
                        .getDesignerId(), pro, designerMap);
                messageService.send(-1L, designer.getDesignerId(), MessageAction.MANAGER_RATING, pro.getManagerId(), null,
                        null, pro, managerMap);
            }
        }
        projectSearch.update(pro);
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public boolean completeHire(long id, long manager) throws InvalidRequestException, IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getManagerId().equals(manager)) {
            throw new InvalidRequestException();
        }
        pro.setStatus((byte) ProjectStatus.READY.getCode());
        projectSearch.update(pro);
        return projectDao.updateByPrimaryKeySelective(pro) > 0;
    }

    @Transactional
    public void applyJob(long id, long designer) throws InvalidRequestException, InnerLogicException, IOException {
        Map<String, String> params = new HashMap<String, String>();
        Project p = projectDao.selectByPrimaryKey(id);
        messageService.send(-1L, p.getManagerId(), MessageAction.APPLY_JOB, null, null, designer, p, params);
        if (p.getAgentId() != null) {
            messageService.send(-1L, p.getAgentId(), MessageAction.APPLY_JOB, null, null, designer, p, params);
        }
    }

    @Transactional
    public boolean hire(long id, long userId, long opId) throws InvalidRequestException, InnerLogicException,
            IOException {
        Project pro = projectDao.selectByPrimaryKey(id);
        if (!pro.getAgentId().equals(opId) && !pro.getManagerId().equals(opId)) {
            throw new InvalidRequestException("you have no auth, agent id or manager id is wrong");
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
    public boolean acceptHiring(long id, long userId, long msgId, boolean accept) throws InvalidRequestException,
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
        messageService.finish(msgId, userId);
        messageService.send(-1L, userId, accept ? MessageAction.ACCEPT_HIRE : MessageAction.DENY_HIRE, null, null,
                userId, pro, params);
        if (accept) {
            pro.setHired(pro.getHired() + 1);
            projectSearch.update(pro);
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

    public Page<ProjectSimpleInfo> search(ProjectCondition condition) throws IOException, ParseException {
        return projectSearch.search(condition);
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
        projectSearch.update(p);
        return projectDao.updateByPrimaryKeySelective(p) > 0;
    }

}
