package com.example.judge.service;

import com.example.judge.model.SandBoxExecuteResult;

import java.util.List;

public interface SandBoxService {
    SandBoxExecuteResult executeJavaCode(Long userId, String submitCode, List<String> inputList);
}
