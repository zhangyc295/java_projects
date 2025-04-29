package com.example.friend.model.quesition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class QuestionDetailVO {
    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId;

    private String title;
    private Integer difficulty;
    private Integer timeLimit;
    private Integer spaceLimit;
    private String content;
    private String defaultCode;

}
