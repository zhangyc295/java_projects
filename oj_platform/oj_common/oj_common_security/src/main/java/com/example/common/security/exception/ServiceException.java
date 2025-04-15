package com.example.common.security.exception;

import com.example.common.entity.enums.ResultCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private ResultCode resultCode;

    public ServiceException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
