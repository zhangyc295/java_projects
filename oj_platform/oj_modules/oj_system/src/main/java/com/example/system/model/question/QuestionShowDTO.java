package com.example.system.model.question;

import com.example.common.entity.model.PageBase;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class QuestionShowDTO extends PageBase{

    private Integer difficulty;
    private String title;

    private String selectedQuestionId;

    // 多个题目id 转为字符串selectedQuestionId
    private Set<Long> selectedQuestionIdSet;

}
