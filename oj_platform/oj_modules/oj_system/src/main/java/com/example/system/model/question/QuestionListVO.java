package com.example.system.model.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class QuestionListVO {    //题目列表页展示详情

    @JsonFormat(shape = JsonFormat.Shape.STRING)  // 关键注解：强制转为字符串
    private Long questionId;

    private String title;
    private Integer difficulty;

    private String nickName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
