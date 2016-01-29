package com.iblock.workflow.dtos;

import com.iblock.workflow.enums.ProcessError;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: qihong
 * Date: 14-9-10
 * Time: 下午1:15
 * To change this template use File | Settings | File Templates.
 */
@Data
public class ProcessResultDTO extends BaseResultDTO {

    private String processId;

    public ProcessResultDTO(){
        super();
    }

    public ProcessResultDTO(ProcessError processError){
        super(processError);
    }

    public ProcessResultDTO(ProcessError processError, String processId){
        super(processError);
        this.processId = processId;
    }

    public ProcessResultDTO(int code, int message, String processId){
        super();
        this.processId = processId;
    }

}
