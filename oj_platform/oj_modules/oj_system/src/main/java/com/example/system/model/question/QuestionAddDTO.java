package com.example.system.model.question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionAddDTO {

    @NotBlank(message = "题目不能为空")
    private String title;

    @Min(value = 1, message = "难度最低为简单")
    @Max(value = 3, message = "难度最高为困难")
    private Integer difficulty;

    private Integer timeLimit;
    private Integer spaceLimit;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "测试用例不能为空")
    private String questionCase;

    @NotBlank(message = "默认代码块不能为空")
    private String defaultCode;

    @NotBlank(message = "main函数不能为空")
    private String mainFunc;
}
