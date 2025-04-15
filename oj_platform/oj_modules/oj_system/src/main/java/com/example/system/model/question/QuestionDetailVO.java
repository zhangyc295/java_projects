package com.example.system.model.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDetailVO {          //题目编辑展示详情

    @JsonFormat(shape = JsonFormat.Shape.STRING)  // 关键注解：强制转为字符串
    private Long questionId;

    private String title;
    private Integer difficulty;
    private Integer timeLimit;
    private Integer spaceLimit;
    private String content;
    private String questionCase;
    private String defaultCode;
    private String mainFunc;

}
