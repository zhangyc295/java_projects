package com.example.system.service;

import com.example.common.entity.model.AdminVO;
import com.example.common.entity.model.Result;
import com.example.system.model.admin.AdminSaveDTO;

public interface AdminService {

    Result<String> login(String admin, String password);

    int add(AdminSaveDTO adminSaveDTO);


    Result<AdminVO> info(String token);

    boolean logout(String token);
}
