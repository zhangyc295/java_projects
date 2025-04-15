package com.example.common.security.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.entity.enums.ResultCode;
import com.example.common.entity.model.Result;
import com.example.common.security.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {

    // 请求方式不支持 HttpRequestMethodNotSupportedException.class
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求'{}',不支持'{}'请求", requestURI, e.getMethod());
        return Result.fail(ResultCode.ERROR);
    }

    // 拦截运行时异常 RuntimeException.class
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求'{}',发生异常", requestURI, e);
        return Result.fail(ResultCode.ERROR);
    }

    // 系统异常 Exception.class
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生异常", requestURI, e);
        return Result.fail(ResultCode.ERROR);
    }

    // 服务异常 ServiceException.class
    @ExceptionHandler(ServiceException.class)
    public Result<?> handleException(ServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        ResultCode resultCode = e.getResultCode();
        log.error("请求地址'{}',发生业务异常：{}", requestURI, resultCode.getMsg(), e);
        return Result.fail(resultCode);
    }

    // 参数校验失败 BindException.class
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        String message = join(e.getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return Result.fail(ResultCode.FAILED_PARAMS.getCode(), message);
    }

    private <E> String join(Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
        if (CollUtil.isEmpty(collection)) {
            return StrUtil.EMPTY;
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(delimiter));
    }
}