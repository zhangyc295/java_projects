package org.example.java_doc_searcher_spring.model;

import lombok.Data;

@Data
//对文档修改后的结果 把内容变为摘要（缩短显示内容）
public class Result {
    private String title;
    private String url;
    private String describe;
    // 正文的一段摘要
}
