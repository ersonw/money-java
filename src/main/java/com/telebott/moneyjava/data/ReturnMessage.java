package com.telebott.moneyjava.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnMessage<T> {

    private Integer code;//错误码

    private String message;//提示信息

    private T data;//返回具体内容

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public ReturnMessage(Integer code, String message, T data) {
        super();
        this.code = code;
        this.message = message;
        this.data= data;
    }
}
