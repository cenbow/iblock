package com.iblock.dao;

import com.iblock.dao.po.SubProcessExpect;

public interface SubProcessExpectDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SUB_PROCESS_EXPECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int insert(SubProcessExpect record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SUB_PROCESS_EXPECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int insertSelective(SubProcessExpect record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SUB_PROCESS_EXPECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    SubProcessExpect selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SUB_PROCESS_EXPECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int updateByPrimaryKeySelective(SubProcessExpect record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table SUB_PROCESS_EXPECT
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    int updateByPrimaryKey(SubProcessExpect record);
}