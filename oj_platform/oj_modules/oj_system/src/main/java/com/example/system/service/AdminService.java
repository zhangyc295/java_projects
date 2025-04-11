package com.example.system.service;

import com.example.common.entity.model.Result;

public interface AdminService {
    Result<String> login(String admin, String password);
}
