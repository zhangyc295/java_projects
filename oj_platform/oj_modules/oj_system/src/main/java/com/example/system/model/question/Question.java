package com.example.system.model.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import lombok.Getter;
import lombok.Setter;

@TableName("oj_question")
@Getter
@Setter
public class Question extends TimeBase {
    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId;

    private String title;
    private Integer difficulty;
    private Integer timeLimit;
    private Integer spaceLimit;
    private String content;
    private String questionCase;
    private String defaultCode;
    private String mainFunc;

    private Integer deleteFlag;  //0未删除1回收站2已删除
}
