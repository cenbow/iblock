package com.iblock.common.enums;

/**
 * Created by baidu on 16/4/3.
 */
public enum TaskDefinitionKey {

    SUBMIT_PROJECT("SubmitProject"), MANAGER_AUDIT("ManagerAudit"), AGENT_CONFIRM("AgentConfirm"),
    SEND_MANAGER_AUDIT_FAIL_MSG("SendManagerAuditFailMsg"), HIRE_DESIGNER("HireDesigner"), SUB_PROCESS_RUNNING
            ("SubprocessRunning"), ACCEPTANCE("Acceptance"), SEND_CASHING_NOTICE("SendCashingNotice"), PAY("Pay");

    private String key;

    TaskDefinitionKey(String key) {
        this.key = key;
    }

    public boolean is(String key) {
        return this.key.equals(key);
    }

    public String getKey() {
        return key;
    }
}
