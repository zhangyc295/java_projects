package com.example.system.model.contest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

@Getter
@Setter
public class ContestQuestionAddDTO {
    private Long contestId;

    private LinkedHashSet<Long> questionId;   // 添加多个题目  保证顺序

}
