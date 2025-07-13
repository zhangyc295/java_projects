package com.example.friend.service;

import com.example.common.entity.model.Result;
import com.example.friend.model.client.ClientSubmitDTO;
import com.example.openfeign.model.SubmitResultVO;

public interface ClientQuestionService {
    Result<SubmitResultVO> submit(ClientSubmitDTO clientSubmitDTO);

   boolean submitByRabbitMQ(ClientSubmitDTO clientSubmitDTO);

    Result<SubmitResultVO> getQuestionResult(Long contestId, Long questionId, String submitTime);
}
