package com.learn.common.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private String msg;
    public BaseException(){

    }
    public BaseException(String msg){
        this.msg=msg;
    }


}
