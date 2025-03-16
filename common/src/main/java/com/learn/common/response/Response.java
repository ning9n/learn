package com.learn.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response <T>{
    //0:成功  1：失败
    private Integer code;
    private String msg;
    private T data;


    public static <T> Response<T> ok(T data){
        return new Response<>(0,null,data);
    }
    public static <T> Response<T> fail(String msg){
        return new Response<>(1,msg,null);
    }
}
