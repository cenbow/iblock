package com.iblock.workflow.dtos;

import com.iblock.workflow.enums.ProcessError;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-11-5
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */
@Data
public class SignResultDTO extends BaseResultDTO {


    private Map<String, String> taskMap;

    public SignResultDTO(){
        super();
        this.taskMap = new HashMap<String, String>();
    }

    public SignResultDTO(ProcessError processError){
        super(processError);
        this.taskMap = new HashMap<String, String>();
    }

    public SignResultDTO(ProcessError processError, Map<String, String> taskMap){
        super(processError);
        this.taskMap = taskMap;
    }

}
