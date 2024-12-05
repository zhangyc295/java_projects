package org.example.java_doc_searcher_spring.model;

import lombok.Data;

//每个文档对象的基本信息
//id 标题  网址  内容
@Data
public class DocInfo {
    private int docId;
    private String title;
    private String url;
    private String content;
}
