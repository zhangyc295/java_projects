package com.example.system.model.contest;

import com.example.common.entity.model.PageBase;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ContestShowDTO extends PageBase {

    private String title;
    private String startTime;
    private String endTime;
}
