package com.telebott.moneyjava.config;

public class BusinessException extends RuntimeException {

    private Integer code = 404;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    public BusinessException(Integer code,String message) {
        super(message);
        this.code = code;
    }
    public BusinessException(String message) {
        super(message);
    }
}
