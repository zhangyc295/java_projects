package com.example.system.model.contest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContestAddDTO {

    @NotBlank(message = "竞赛题目不能为空")
    @NotNull(message = "竞赛题目不能为空")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

//    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
