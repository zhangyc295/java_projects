package com.example.common.mybatis;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 获取操作人的 id
        this.strictInsertFill(metaObject, "createdBy", Long.class, 1L);
    }

//    @Override
//    public void updateFill(MetaObject metaObject) {
//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
//        this.strictUpdateFill(metaObject, "updatedBy", Long.class, 1L);
//
//    }


    // 无论字段是否为null  强制执行
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updatedBy", 1L, metaObject);
    }
}
