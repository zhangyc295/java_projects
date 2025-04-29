package com.example.friend.model.contest;

import com.example.common.entity.model.PageBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContestShowDTO extends PageBase {

    private String title;
    private String startTime;
    private String endTime;

    private Integer type;   //0未完赛  1已完赛
}
