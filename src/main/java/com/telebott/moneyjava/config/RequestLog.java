package com.telebott.moneyjava.config;

import com.telebott.moneyjava.data.RequestHeader;
import com.telebott.moneyjava.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
@Slf4j
public class RequestLog extends HandlerInterceptorAdapter {

    /**
     * 前置检查
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws RuntimeException {
        RequestHeader header = ToolsUtil.getRequestHeaders(request);
        if (handler instanceof ResourceHttpRequestHandler){
            log.error("Resource Http Request Handler: {}",header.getUri());
            return true;
        }
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        if (header.getUser() == null){
//            throw new BusinessException(101, "用户不存在！");
//        }
        Method method = handlerMethod.getMethod();
//        log.info("IP:{},URI:{} \n访问目标:{}.{}" ,ip,header.getUri(),method.getDeclaringClass().getName(),method.getName());
        return true;
    }

    // controller处理完成
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws RuntimeException {
        if (handler instanceof ResourceHttpRequestHandler)return;
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        long startTime = (Long) request.getAttribute("requestStartTime");

        long endTime = System.currentTimeMillis();

        long executeTime = endTime - startTime;

        // log it
        if (executeTime > 1000) {
            log.info("[{}.{}] 执行耗时 : {}ms",method.getDeclaringClass().getName(),method.getName(),executeTime);
        } else {
            log.info("[{}.{}] 执行耗时 : {}ms",method.getDeclaringClass().getSimpleName(),method.getName(),executeTime);
        }

    }
}
