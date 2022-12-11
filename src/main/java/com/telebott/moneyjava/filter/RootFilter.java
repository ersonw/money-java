package com.telebott.moneyjava.filter;

import com.alibaba.fastjson.JSONObject;
import com.telebott.moneyjava.redis.AuthRedis;
import com.telebott.moneyjava.table.User;
import com.telebott.moneyjava.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter(filterName = "rootFilter", urlPatterns = {"/api/*"})
@Slf4j
public class RootFilter implements Filter {
    private AuthRedis authRedis;
    @Override
    public void init(FilterConfig filterConfig){
        authRedis = Utils.getAuthRedis();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        log.info("Filter rootFilter success");
    }
    public String getCookie(Cookie[] cookies){
        if (cookies !=null){
            for (Cookie cookie: cookies){
                if (StringUtils.equals(cookie.getName(), "X-Token")){
                    if (StringUtils.isNotEmpty(cookie.getValue())) return cookie.getValue();
                }
            }
        }
        return null;
    }
    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            CustomHttpServletRequest request = new CustomHttpServletRequest((HttpServletRequest) req);
            String token = request.getHeader("X-Token");
            if (StringUtils.isNotEmpty(token)){
                User user = authRedis.findByToken(token);
                if (user != null){
                    request.addHeader("user", JSONObject.toJSONString(user));
//                    authRedis.put(user);
                }
            }
            chain.doFilter(request, response);
        } else {
            chain.doFilter(req, response);
        }
    }
    @Override
    public void destroy() {

    }
}
