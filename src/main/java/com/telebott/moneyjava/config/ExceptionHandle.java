package com.telebott.moneyjava.config;

import com.telebott.moneyjava.data.ReturnMessage;
import com.telebott.moneyjava.util.ReturnMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    public ReturnMessage<Object> handle(Exception exception) {
        if(exception instanceof BusinessException) {
            BusinessException businessException = (BusinessException)exception;
            return ReturnMessageUtil.error(businessException.getCode(), businessException.getMessage());
        }else {
            exception.printStackTrace();
            logger.error("系统异常 {}",exception.getMessage());
            return ReturnMessageUtil.error(-1, "系统异常:"+ exception.getMessage());
        }
    }
}
