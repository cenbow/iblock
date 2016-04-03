package com.iblock.dao;

import com.iblock.dao.po.CommonExperience;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonExperienceDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMMON_EXPERIENCE
     *
     * @mbggenerated Sun Feb 14 20:53:38 CST 2016
     */
    int insert(CommonExperience record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMMON_EXPERIENCE
     *
     * @mbggenerated Sun Feb 14 20:53:38 CST 2016
     */
    int insertSelective(CommonExperience record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMMON_EXPERIENCE
     *
     * @mbggenerated Sun Feb 14 20:53:38 CST 2016
     */
    CommonExperience selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMMON_EXPERIENCE
     *
     * @mbggenerated Sun Feb 14 20:53:38 CST 2016
     */
    int updateByPrimaryKeySelective(CommonExperience record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMMON_EXPERIENCE
     *
     * @mbggenerated Sun Feb 14 20:53:38 CST 2016
     */
    int updateByPrimaryKeyWithBLOBs(CommonExperience record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMMON_EXPERIENCE
     *
     * @mbggenerated Sun Feb 14 20:53:38 CST 2016
     */
    int updateByPrimaryKey(CommonExperience record);

    List<CommonExperience> selectByUsers(@Param("src") Long src, @Param("target") Long target);
}