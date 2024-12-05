package org.example.java_doc_searcher_spring.model;

import lombok.Data;

//weight 表示文档与关键词的相关性
@Data
public class Weight {
    private int weight;
    private int docId;
}
