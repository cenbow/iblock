package com.iblock.dao.po;

public class User {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.id
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.user_name
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    private String userName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.password
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.role
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    private Byte role;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column USER.status
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    private Byte status;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.id
     *
     * @return the value of USER.id
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column USER.id
     *
     * @param id the value for USER.id
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.user_name
     *
     * @return the value of USER.user_name
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
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
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
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
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
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
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
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
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
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
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    public void setRole(Byte role) {
        this.role = role;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column USER.status
     *
     * @return the value of USER.status
     *
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
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
     * @mbggenerated Mon Jan 25 16:29:39 CST 2016
     */
    public void setStatus(Byte status) {
        this.status = status;
    }
}