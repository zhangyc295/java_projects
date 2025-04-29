package com.example.system.model.contest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.entity.model.TimeBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("contest_question_mapping")
public class ContestQuestion extends TimeBase {

    @TableId(type = IdType.ASSIGN_ID)
    private Long contestQuestionId;
    private Long contestId;
    private Long questionId;
    private Integer questionOrder;

}
