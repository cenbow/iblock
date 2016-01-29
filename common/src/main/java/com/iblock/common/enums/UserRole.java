package com.iblock.common.enums;

/**
 * Created by qihong on 15/12/21.
 */
public enum UserRole {

    EMPLOYER(1, "委托方"), EMPLOYEE(2, "被雇佣者"), MANAGER(3, "项目经理"), ADMINISTRATOR(4, "系统管理员");

    private int role;
    private String name;

    UserRole(int role, String name) {
        this.role = role;
        this.name = name;
    }

    public static UserRole getByCode(int code) {
        for (UserRole role : UserRole.values()) {
            if (role.getRole() == code) {
                return role;
            }
        }
        return null;
    }


    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
