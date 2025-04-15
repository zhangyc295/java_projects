package com.example.system.model.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@TableName("oj_admin")
public class Admin extends TimeBase {
    //雪花算法生成ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long adminId; //主键

    private String nickName;
    private String adminAccount;
    private String adminPassword;

}
