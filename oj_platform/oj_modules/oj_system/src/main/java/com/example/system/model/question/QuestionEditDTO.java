package com.example.system.model.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionEditDTO extends QuestionAddDTO{

    @JsonFormat(shape = JsonFormat.Shape.STRING)  // 关键注解：强制转为字符串
    private Long questionId;
}
