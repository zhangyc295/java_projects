package com.example.friend.service;

import com.example.common.entity.model.LoginUserVO;
import com.example.common.entity.model.Result;
import com.example.friend.model.client.ClientDTO;
import com.example.friend.model.client.ClientDetailVO;
import com.example.friend.model.client.ClientPasswordDTO;
import com.example.friend.model.client.ClientUpdateDTO;

public interface ClientService {
    boolean sendCode(ClientDTO clientDTO);

    String codeLogin(String telephone, String code);

    Result<String> accountLogin(String userName, String userPassword);

    boolean logout(String token);

    Result<LoginUserVO> info(String token);

    ClientDetailVO detail();

    int edit(ClientUpdateDTO clientUpdateDTO);

    int editPassword(ClientPasswordDTO clientPasswordDTO);

    int headImageUpdate(String headImage);
}
