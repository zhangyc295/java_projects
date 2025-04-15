package com.example.common.entity.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.example.common.entity.model.Result;
import com.example.common.entity.model.TableResult;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class DatabaseController {
    public Result<Void> convertToResult(int rows) {
        if (rows > 0) {
            return Result.success();
        }
        return Result.fail();
    }

    public Result<Void> convertToResult(boolean ret) {
        if (ret) {
            return Result.success();
        }
        return Result.fail();
    }

    public TableResult convertToTableResult(List<?> list) {
        if (CollectionUtil.isEmpty(list)) {
            return TableResult.empty();
        }
        new PageInfo<>(list).getTotal();
//        return TableResult.success(list, list.size());
        return TableResult.success(list, new PageInfo<>(list).getTotal());
    }
}
