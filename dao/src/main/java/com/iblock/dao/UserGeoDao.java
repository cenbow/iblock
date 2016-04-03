package com.iblock.dao;

import com.iblock.dao.po.UserGeo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserGeoDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_GEO
     *
     * @mbggenerated Wed Feb 03 15:30:52 CST 2016
     */
    int insert(UserGeo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_GEO
     *
     * @mbggenerated Wed Feb 03 15:30:52 CST 2016
     */
    int insertSelective(UserGeo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_GEO
     *
     * @mbggenerated Wed Feb 03 15:30:52 CST 2016
     */
    UserGeo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_GEO
     *
     * @mbggenerated Wed Feb 03 15:30:52 CST 2016
     */
    int updateByPrimaryKeySelective(UserGeo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table USER_GEO
     *
     * @mbggenerated Wed Feb 03 15:30:52 CST 2016
     */
    int updateByPrimaryKey(UserGeo record);

    List<UserGeo> selectByCoordinate(@Param("maxLat") double maxLat, @Param("minLat") double minLat, @Param("maxLon")
    double maxLon, @Param("minLon") double minLon);

    List<UserGeo> selectByDistinct(int distinctId);
}