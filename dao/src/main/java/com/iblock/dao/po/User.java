package com.iblock.dao.po;

import java.util.Date;

public class User {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.id
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.user_name
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private String userName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.password
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.role
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private Byte role;

    private Byte education;

    private String skills;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.mobile
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private String mobile;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.sex
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private Boolean sex;

    private Boolean online;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.status
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private Byte status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.head_figure
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private String headFigure;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.add_time
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private Date addTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.update_time
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    private Date updateTime;

    private Date lastMsgTime;

    private String email;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.id
     *
     * @return the value of USER.id
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.id
     *
     * @param id the value for USER.id
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.user_name
     *
     * @return the value of USER.user_name
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public String getUserName() {
        return userName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.user_name
     *
     * @param userName the value for USER.user_name
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.password
     *
     * @return the value of USER.password
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.password
     *
     * @param password the value for USER.password
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.role
     *
     * @return the value of USER.role
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public Byte getRole() {
        return role;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.role
     *
     * @param role the value for USER.role
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setRole(Byte role) {
        this.role = role;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.mobile
     *
     * @return the value of USER.mobile
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.mobile
     *
     * @param mobile the value for USER.mobile
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.sex
     *
     * @return the value of USER.sex
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public Boolean getSex() {
        return sex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.sex
     *
     * @param sex the value for USER.sex
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.status
     *
     * @return the value of USER.status
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.status
     *
     * @param status the value for USER.status
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.head_figure
     *
     * @return the value of USER.head_figure
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public String getHeadFigure() {
        return headFigure;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.head_figure
     *
     * @param headFigure the value for USER.head_figure
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setHeadFigure(String headFigure) {
        this.headFigure = headFigure;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.add_time
     *
     * @return the value of USER.add_time
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.add_time
     *
     * @param addTime the value for USER.add_time
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.update_time
     *
     * @return the value of USER.update_time
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.update_time
     *
     * @param updateTime the value for USER.update_time
     *
     * @mbggenerated Sun Feb 14 20:06:43 CST 2016
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getEducation() {
        return education;
    }

    public void setEducation(Byte education) {
        this.education = education;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Date getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(Date lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}