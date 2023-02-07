package com.telebott.moneyjava.data;

import com.alibaba.fastjson.JSONObject;
import com.telebott.moneyjava.table.AdminUser;
import com.telebott.moneyjava.table.User;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class RequestHeader {
    private String ip;
    private String userAgent;
    private String token;
    private String serverName;
    private String host;
    private int serverPort;
    private String uri;
    private String url;
    private String schema;
    private String query;
    private String referer;
    private String user;
    public AdminUser getAdminUserInterface() {
        if (StringUtils.isNotEmpty(user)){
            return AdminUser.format(user);
        }
        return null;
    }
    public User getUserInterface() {
        if (StringUtils.isNotEmpty(user)){
            return User.format(user);
        }
        return null;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public RequestHeader setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public RequestHeader setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public RequestHeader setToken(String token) {
        this.token = token;
        return this;
    }

    public RequestHeader setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public RequestHeader setHost(String host) {
        this.host = host;
        return this;
    }

    public RequestHeader setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public RequestHeader setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RequestHeader setUrl(String url) {
        this.url = url;
        return this;
    }

    public RequestHeader setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public RequestHeader setQuery(String query) {
        this.query = query;
        return this;
    }

    public RequestHeader setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    public RequestHeader setUser(String user) {
        this.user = user;
        return this;
    }
}
