package com.example.judge.model;

import com.example.common.entity.enums.CodeRunStatus;
import lombok.Data;

import java.util.List;

@Data
public class SandBoxExecuteResult {

    private CodeRunStatus runStatus;  //执行结果

    private String errorMessage;      //异常信息

    private List<String> outputList;  //执行结果

    private Long useMemory;  //占用内存   kb

    private Long useTime;    //消耗时间   ms

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, String errorMsg) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setErrorMessage(errorMsg);
        return result;
    }

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setErrorMessage(runStatus.getMsg());
        return result;
    }

    public static SandBoxExecuteResult fail(CodeRunStatus runStatus, List<String> outputList, Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setErrorMessage(runStatus.getMsg());
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }

    public static SandBoxExecuteResult success(CodeRunStatus runStatus, List<String> outputList, Long useMemory, Long useTime) {
        SandBoxExecuteResult result = new SandBoxExecuteResult();
        result.setRunStatus(runStatus);
        result.setOutputList(outputList);
        result.setUseMemory(useMemory);
        result.setUseTime(useTime);
        return result;
    }

}