package com.example.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.Base;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@TableName("oj_admin")
public class Admin extends Base {
    //雪花算法生成ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long adminId; //主键

    private String adminAccount;
    private String adminPassword;

}
