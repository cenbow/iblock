package com.iblock.service.user;

import com.iblock.common.enums.CommonStatus;
import com.iblock.common.enums.UserRole;
import com.iblock.dao.IndustryDao;
import com.iblock.dao.JobInterestDao;
import com.iblock.dao.ManagerDao;
import com.iblock.dao.SkillDao;
import com.iblock.dao.UserDao;
import com.iblock.dao.UserGeoDao;
import com.iblock.dao.UserRatingDao;
import com.iblock.dao.po.Industry;
import com.iblock.dao.po.Manager;
import com.iblock.dao.po.Skill;
import com.iblock.dao.po.User;
import com.iblock.dao.po.UserDetail;
import com.iblock.dao.po.UserGeo;
import com.iblock.dao.po.UserRating;
import com.iblock.service.bo.UserUpdateBo;
import com.iblock.service.message.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qihong on 16/1/25.
 */
@Component
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserGeoDao userGeoDao;
    @Autowired
    private ManagerDao managerDao;
    @Autowired
    private SkillDao skillDao;
    @Autowired
    private IndustryDao industryDao;
    @Autowired
    private UserRatingDao userRatingDao;
    @Autowired
    private MessageService messageService;
    @Autowired
    private JobInterestDao jobInterestDao;

    public boolean profileComplete(Long userId) {
        User user = userDao.selectByPrimaryKey(userId);
        if (user.getRole().intValue() == UserRole.MANAGER.getRole()) {
            Manager manager = managerDao.selectByUser(userId);
            return StringUtils.isNotBlank(manager.getCompanyName()) && StringUtils.isNotBlank(manager.getDesc());
        } else if (user.getRole().intValue() == UserRole.DESIGNER.getRole()) {
            if (user.getEducation() == null || user.getEducation() <= 0) {
                return false;
            }
            if (StringUtils.isBlank(user.getSkills())) {
                return false;
            }
            if (jobInterestDao.selectByUser(userId) == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean rate(Long userId, Integer rating, Long operator, Long msgId) {
        UserRating userRating = new UserRating();
        userRating.setOperator(operator);
        userRating.setUserId(userId);
        userRating.setStatus((byte) CommonStatus.NORMAL.getCode());
        userRating.setRating(rating);
        userRating.setAddTime(new Date());
        messageService.finish(msgId, operator);
        return userRatingDao.insertSelective(userRating) > 0;
    }

    public double getRating(Long userId) {
        Double d = userRatingDao.selectAVG(userId);
        if (d == null) {
            return 5;
        }
        return d;
    }

    public User login(String userName, String password) {
        return userDao.selectUser(userName, password);
    }

    public User getUser(Long userId) {
        return userDao.selectByPrimaryKey(userId);
    }

    public List<User> getUsersByRole(Integer role) {
        return userDao.selectByRole(role);
    }

    public List<User> getUsersByStatus(Integer status) {
        return userDao.selectByStatus(status);
    }

    public UserDetail getUserDetail(Long userId) {
        return userDao.selectDetailById(userId);
    }

    public boolean update(User user) {
        return userDao.updateByPrimaryKeySelective(user) > 0;
    }

    public UserGeo getUserGeo(Long userId) {
        return userGeoDao.selectByUser(userId);
    }

    @Transactional
    public boolean signUp(UserUpdateBo bo) {
        simpleAdd(bo.getUser());
        bo.getUserGeo().setUserId(bo.getUser().getId());
        if (bo.getUser().getRole().intValue() == UserRole.MANAGER.getRole()) {
            bo.getManager().setUserId(bo.getUser().getId());
            managerDao.insertSelective(bo.getManager());
        }
        userGeoDao.insertSelective(bo.getUserGeo());
        return true;
    }

    public boolean simpleAdd(User user) {
        user.setAddTime(new Date());
        user.setLastMsgTime(new Date());
        return userDao.insertSelective(user) > 0;
    }

    @Transactional
    public boolean update(UserUpdateBo bo) {
        userDao.updateByPrimaryKey(bo.getUser());
        if (bo.getUser().getRole().intValue() == UserRole.MANAGER.getRole() && bo.getManager() != null) {
            managerDao.updateByPrimaryKey(bo.getManager());
        }
        if(bo.getUserGeo() != null) {
            if (bo.getUserGeo().getId() != null) {
                userGeoDao.updateByPrimaryKey(bo.getUserGeo());
            } else {
                bo.getUserGeo().setAddTime(new Date());
                userGeoDao.insertSelective(bo.getUserGeo());
            }
        }
        return true;
    }

    public boolean addSkill(String name) {
        if (skillDao.selectByName(name) != null) {
            return false;
        }
        Skill skill = new Skill();
        skill.setAddTime(new Date());
        skill.setName(name);
        return skillDao.insertSelective(skill) > 0;
    }

    public boolean deleteSkill(Integer id) {
        Skill skill = skillDao.selectByPrimaryKey(id);
        skill.setStatus((byte) CommonStatus.DELETE.getCode());
        return skillDao.updateByPrimaryKeySelective(skill) > 0;
    }


    public List<Skill> getSkills() {
        return skillDao.selectAll();
    }

    public Manager getManager(Long userId) {
        return managerDao.selectByUser(userId);
    }

    public List<Skill> getSkillByIds(String ids) {
        List<Integer> list = new ArrayList<Integer>();
        for (String s : ids.split(",")) {
            if (StringUtils.isNotBlank(s)) {
                list.add(Integer.parseInt(s));
            }
        }
        return skillDao.selectByIds(list);
    }

    public List<Industry> getIndustries() {
        return industryDao.selectAll();
    }

    public List<Industry> getIndustryByIds(String ids) {
        List<Integer> list = new ArrayList<Integer>();
        for (String s : ids.split(",")) {
            if (StringUtils.isNotBlank(s)) {
                list.add(Integer.parseInt(s));
            }
        }
        return industryDao.selectByIds(list);
    }

    public boolean addIndustry(String name) {
        if (industryDao.selectByName(name) != null) {
            return false;
        }
        Industry industry = new Industry();
        industry.setAddTime(new Date());
        industry.setName(name);
        return industryDao.insertSelective(industry) > 0;
    }

    public User getByMobile(String mobile) {
        return userDao.selectByMobile(mobile);
    }

    public boolean deleteIndustry(Integer id) {
        Industry industry = industryDao.selectByPrimaryKey(id);
        industry.setStatus((byte) CommonStatus.DELETE.getCode());
        return industryDao.updateByPrimaryKeySelective(industry) > 0;
    }

    public List<User> batchGet(List<Long> ids) {
        return userDao.batchSelect(ids);
    }
}
