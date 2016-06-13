package com.iblock.service.message;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * Created by baidu on 16/2/14.
 */
@Data
public class Msg {

    private int type;
    private int action;
    private String content;
    private String service;
    private Set<String> params;
    private Map<String, String> inputType;
}
