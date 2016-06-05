package com.iblock.web.info;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by baidu on 16/6/5.
 */
@Data
@AllArgsConstructor
public class SkillInfo implements Serializable {

    private int id;
    private String name;

    public SkillInfo() {}
}
