package com.iblock.workflow.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-18
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
@Data
public class ReceiveSignalDTO implements Serializable {

    private String processId;
    private int signal;
    private String memo;

    @Override
    public String toString() {
        return "{" +
                "processId=" + processId +
                ",signal=" + signal +
                ",memo=" + memo +
                "}";
    }
}
