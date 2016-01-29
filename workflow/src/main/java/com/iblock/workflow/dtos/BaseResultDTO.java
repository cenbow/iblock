package com.iblock.workflow.dtos;

import com.iblock.workflow.enums.ProcessError;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-10
 * Time: 下午1:16
 * To change this template use File | Settings | File Templates.
 */
@Data
public class BaseResultDTO implements Serializable {

    private int code;
    private String message;

    public BaseResultDTO(){
        this.code = ProcessError.SYSTEM_ERROR.getCode();
        this.message = ProcessError.SYSTEM_ERROR.getMessage();
    }

    public BaseResultDTO(ProcessError processError){
        this.code = processError.getCode();
        this.message = processError.getMessage();
    }

    public BaseResultDTO(int code, String message){
        this.code = code;
        this.message = message;
    }
}
