package com.example.system.model.contest;

import com.example.system.model.question.QuestionListVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)  //为空不返回
public class ContestDetailVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)  // 关键注解：强制转为字符串
    private Long contestId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private List<QuestionListVO> contestQuestionList;
}
