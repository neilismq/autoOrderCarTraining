package com.bj.zzq.utils;

import java.io.Serializable;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/29
 * @Description:
 */
public class CommonResponse<T> implements Serializable {
    private String code;
    private String message;
    private T body;

    public String getCode() {
        return code;
    }

    public CommonResponse setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommonResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getBody() {
        return body;
    }

    public CommonResponse setBody(T body) {
        this.body = body;
        return this;
    }

    public static CommonResponse okInstance() {
        CommonResponse<Serializable> response = new CommonResponse<>();
        response.setCode("200");
        response.setMessage("成功");
        return response;
    }

    public static CommonResponse errorInstance() {
        CommonResponse<Serializable> response = new CommonResponse<>();
        response.setCode("300");
        response.setMessage("失败");
        return response;
    }
}
