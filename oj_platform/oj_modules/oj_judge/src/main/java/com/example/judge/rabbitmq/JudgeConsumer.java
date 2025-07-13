package com.example.judge.rabbitmq;

import com.example.common.entity.constants.RabbitMQConstants;
import com.example.judge.service.JudgeService;
import com.example.openfeign.model.JudgeSubmitDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgeConsumer {

    @Autowired
    private JudgeService judgeService;

    @RabbitListener(queues = RabbitMQConstants.OJ_PLATFORM_QUEUE)
    public void consume(JudgeSubmitDTO judgeSubmitDTO) {
        log.info("收到消息为: {}", judgeSubmitDTO);
        judgeService.doJudgeJavaCode(judgeSubmitDTO);
    }
}