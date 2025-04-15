package com.example.system.model.question;

import com.example.common.entity.model.PageBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionShowDTO extends PageBase{
    private Integer difficulty;
    private String title;
}
