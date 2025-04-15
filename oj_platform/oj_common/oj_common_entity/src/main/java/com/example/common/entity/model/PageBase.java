package com.example.common.entity.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageBase {
    private Integer pageSize = 10;
    private Integer pageNumber = 1;   //分页参数
    // 默认显示第一页 显示10条数据
}
