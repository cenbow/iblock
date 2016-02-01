package com.iblock.dao.po;

import java.util.Date;

public class Comment {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.id
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.project_id
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Long projectId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.comment_user
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Long commentUser;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.score
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Float score;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.target_user
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Long targetUser;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.status
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Byte status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.add_time
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Date addTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column COMMENT.update_time
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.id
     *
     * @return the value of COMMENT.id
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.id
     *
     * @param id the value for COMMENT.id
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.project_id
     *
     * @return the value of COMMENT.project_id
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.project_id
     *
     * @param projectId the value for COMMENT.project_id
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.comment_user
     *
     * @return the value of COMMENT.comment_user
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Long getCommentUser() {
        return commentUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.comment_user
     *
     * @param commentUser the value for COMMENT.comment_user
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setCommentUser(Long commentUser) {
        this.commentUser = commentUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.score
     *
     * @return the value of COMMENT.score
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Float getScore() {
        return score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.score
     *
     * @param score the value for COMMENT.score
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setScore(Float score) {
        this.score = score;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.target_user
     *
     * @return the value of COMMENT.target_user
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Long getTargetUser() {
        return targetUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.target_user
     *
     * @param targetUser the value for COMMENT.target_user
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setTargetUser(Long targetUser) {
        this.targetUser = targetUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.status
     *
     * @return the value of COMMENT.status
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.status
     *
     * @param status the value for COMMENT.status
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.add_time
     *
     * @return the value of COMMENT.add_time
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.add_time
     *
     * @param addTime the value for COMMENT.add_time
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column COMMENT.update_time
     *
     * @return the value of COMMENT.update_time
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column COMMENT.update_time
     *
     * @param updateTime the value for COMMENT.update_time
     *
     * @mbggenerated Mon Feb 01 11:51:28 CST 2016
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}