package com.example.system.model;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("oj_test")
public class TestModel {
    private Integer testId;
    private String title;
    private String content;

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
