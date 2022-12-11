package com.telebott.moneyjava.util;


import com.telebott.moneyjava.data.ReturnMessage;

public class ReturnMessageUtil {

    // 无异常 请求成功并有具体内容返回
    public static ReturnMessage<Object> success(Object object) {
        return new ReturnMessage<Object>(0,"",object);
    }
    // 无异常 请求成功并有具体内容返回
    public static ReturnMessage<Object> success(String message,Object object) {
        return new ReturnMessage<Object>(0,message,object);
    }

    // 无异常 请求成功并无具体内容返回
    public static ReturnMessage<Object> success() {
        return new ReturnMessage<Object>(0,"success",null);
    }

    // 自定义错误异常信息
    public static ReturnMessage<Object> error(Integer code,String msg) {
        return new ReturnMessage<Object>(code,msg,null);
    }
}
