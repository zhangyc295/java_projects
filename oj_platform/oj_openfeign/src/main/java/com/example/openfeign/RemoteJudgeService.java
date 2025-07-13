package com.example.openfeign;

import com.example.common.entity.constants.NacosConstants;
import com.example.openfeign.model.JudgeSubmitDTO;
import com.example.common.entity.model.Result;
import com.example.openfeign.model.SubmitResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "RemoteJudgeService", value = NacosConstants.JUDGE_SERVICE)
public interface RemoteJudgeService {

    @PostMapping("/judge/doJudgeJavaCode")
    Result<SubmitResultVO> doJudgeJavaCode(@RequestBody JudgeSubmitDTO judgeSubmitDTO);
}