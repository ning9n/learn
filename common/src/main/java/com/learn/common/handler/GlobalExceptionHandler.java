package com.learn.common.handler;

import com.learn.common.exception.BaseException;
import com.learn.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
    /*
    Spring 会按照以下顺序来查找并执行最匹配的异常处理方法：
    最具体的异常类型：首先会查找最具体的异常类型匹配的方法。
    父类异常类型：如果没有找到匹配的具体异常类型的方法，Spring 会查找父类异常类型的方法。
    通用异常类型：如果仍然没有找到匹配的方法，Spring 会查找通用异常类型的方法，例如 Exception.class。
     */


    /**
     * 拦截业务异常
     */
    @ExceptionHandler(BaseException.class)
    public Response<String> baseException(BaseException e){
        log.warn("发生了异常"+e.getMsg());
        e.printStackTrace();
        return Response.fail(e.getMsg());
    }
    /**
     * 参数异常:jsr303对dto校验、spring mvc 对参数校验
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    public Response<String> methodArgumentNotValidExceptionException(MethodArgumentNotValidException e){
        e.printStackTrace();
        return Response.fail("数据不合法");
    }
    /**
     * 拦截未预料的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<String> exception(Exception e){
        log.error("发生了未知异常："+e.getMessage());
        e.printStackTrace();
        return Response.fail(e.getMessage());
    }

}
