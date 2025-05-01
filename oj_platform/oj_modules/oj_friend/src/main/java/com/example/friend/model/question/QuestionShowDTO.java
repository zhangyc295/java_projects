package com.example.friend.model.question;

import com.example.common.entity.model.PageBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionShowDTO extends PageBase {

    private String keyword;

    private Integer difficulty;
}
