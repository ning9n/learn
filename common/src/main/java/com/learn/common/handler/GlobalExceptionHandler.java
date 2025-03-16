package com.learn.common.handler;

import com.learn.common.exception.BaseException;
import com.learn.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /*
    运行时异常通常是未预料到的错误，可能由开发人员疏忽或业务逻辑缺陷引起。
    受检异常通常是业务逻辑的一部分，是否统一拦截取决于业务需求。
    错误通常是JVM层面的严重问题,是不可恢复的严重问题，统一拦截的意义不大。
     */

    /**
     * 拦截未预料的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<String> exception(Exception e){
        log.error("发生了未知异常："+e.getMessage());
        return Response.fail(e.getMessage());
    }

    /**
     * 拦截业务异常
     */
    @ExceptionHandler(BaseException.class)
    public Response<String> baseException(BaseException e){
        log.warn("发生了异常"+e.getMsg());
        return Response.fail(e.getMsg());
    }
    /**
     * 参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<String> methodArgumentNotValidExceptionException(MethodArgumentNotValidException e){
        return Response.fail("数据不合法");
    }

}
