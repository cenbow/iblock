package com.iblock.dao;

import com.iblock.dao.po.Project;

public interface ProjectDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PROJECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int insert(Project record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PROJECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int insertSelective(Project record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PROJECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    Project selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PROJECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int updateByPrimaryKeySelective(Project record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PROJECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int updateByPrimaryKeyWithBLOBs(Project record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table PROJECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int updateByPrimaryKey(Project record);
}