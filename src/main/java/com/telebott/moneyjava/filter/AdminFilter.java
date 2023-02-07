package com.telebott.moneyjava.filter;

import com.alibaba.fastjson.JSONObject;
import com.telebott.moneyjava.redis.AuthRedis;
import com.telebott.moneyjava.table.AdminUser;
import com.telebott.moneyjava.table.User;
import com.telebott.moneyjava.util.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter(filterName = "adminFilter", urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {
    private AuthRedis authRedis;
    @Override
    public void init(FilterConfig filterConfig){
        authRedis = Utils.getAuthRedis();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            CustomHttpServletRequest request = new CustomHttpServletRequest((HttpServletRequest) req);
            String token = request.getHeader("X-Token");
            if (StringUtils.isNotEmpty(token)){
                AdminUser user = authRedis.findByAdminToken(token);
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
