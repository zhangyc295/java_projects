package com.example.common.entity.model;

import com.example.common.entity.enums.ResultCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TableResult {

    //总记录数
    private long total;

    //列表数据
    private List<?> rows;

    //状态码
    private int code;
    private String msg;

    public TableResult() {

    }

    // 查询为空
    public static TableResult empty() {
        TableResult tableResult = new TableResult();
        tableResult.setCode(ResultCode.SUCCESS.getCode());
        tableResult.setMsg(ResultCode.SUCCESS.getMsg());
        tableResult.setRows(new ArrayList<>());
        tableResult.setTotal(0);
        return tableResult;
    }

    public static TableResult success(List<?> list, long total) {
        TableResult tableResult = new TableResult();
        tableResult.setCode(ResultCode.SUCCESS.getCode());
        tableResult.setMsg(ResultCode.SUCCESS.getMsg());
        tableResult.setRows(list);
        tableResult.setTotal(total);
        return tableResult;
    }
}
